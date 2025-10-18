package br.jus.tjap.precatorio.modulos.calculadora.apibancocentral;

import br.jus.tjap.precatorio.modulos.calculadora.entity.IndiceBacen;
import br.jus.tjap.precatorio.modulos.calculadora.repository.IndicadorIndiceRepository;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class BancoCentralService {

    private final IndicadorIndiceRepository indicadorIndiceRepository;

    private static final String BASE_URL =
            "https://api.bcb.gov.br/dados/serie/bcdata.sgs.%s/dados?formato=json&dataInicial=%s&dataFinal=%s";

    // EC113
    private static final Long CODIGO_IPCAE = 10764L;
    // EC136
    private static final Long CODIGO_IPCA = 433L;

    private static final Long CODIGO_POUPANCA = 196L;
    private static final Long CODIGO_SELIC = 4390L;
    private static final BigDecimal ZERO = new BigDecimal("0.00");
    private static final BigDecimal UM = BigDecimal.ONE;

    /**
     * Cache dos índices já carregados para não bater no Banco Central toda hora.
     */
    private static final Map<String, Map<YearMonth, BigDecimal>> cacheIndices = new HashMap<>();

    private RestTemplate restTemplate;

    public BancoCentralService(IndicadorIndiceRepository indicadorIndiceRepository, RestTemplate restTemplate){
        this.indicadorIndiceRepository = indicadorIndiceRepository;
        this.restTemplate = restTemplate;
    }

    private List<BancoCentralResponse> buscarSerie(Long codigoIndice, LocalDate dataInicial, LocalDate dataFinal) {
        if(dataInicial == null || dataFinal == null){
            return Collections.emptyList();
        }
        try{
            String url = String.format(BASE_URL, codigoIndice, DateUtil.formatarData(dataInicial), DateUtil.formatarData(dataFinal));
            return Arrays.asList(Objects.requireNonNull(restTemplate.getForObject(url, BancoCentralResponse[].class)));
        }catch (RuntimeException e){// Verificar se mês/ano são iguais ao mês/ano atual

            YearMonth hoje = YearMonth.now();
            YearMonth inicioYM = YearMonth.from(dataInicial);
            YearMonth fimYM = YearMonth.from(dataFinal);

            boolean dataIniIgualHoje = inicioYM.equals(hoje);
            boolean dataFimIgualHoje = fimYM.equals(hoje);

            String url = String.format(BASE_URL, codigoIndice,
                    DateUtil.formatarData(dataIniIgualHoje ? inicioYM.minusMonths(1).atDay(1) : dataInicial),
                    DateUtil.formatarData(dataFimIgualHoje ? fimYM.minusMonths(1).atDay(1) : hoje.minusMonths(1).atDay(1))
            );
            BancoCentralResponse[] response = restTemplate.getForObject(url, BancoCentralResponse[].class);
            return Arrays.asList(response);
        }

    }

    private List<BancoCentralResponse> buscarSerieBanco(Long codigoIndice, LocalDate dataInicial, LocalDate dataFinal) {
        if(dataInicial == null || dataFinal == null){
            return Collections.emptyList();
        }
        List<BancoCentralResponse> listaIndice = new ArrayList<>();
        var indices = indicadorIndiceRepository.findByTipoIndiceAndPeriodo(codigoIndice, dataInicial, dataFinal);

        for( IndiceBacen ind : indices){
            listaIndice.add(new BancoCentralResponse(DateUtil.formatarData(ind.getDataInicioVigencia()), ind.getValor()) );
        }

        return listaIndice;
    }

    public BigDecimal somarSelic(YearMonth inicio, YearMonth fim) {
        return somarIndice(CODIGO_SELIC, inicio, fim);
    }

    public  BigDecimal somarPoupanca(YearMonth inicio, YearMonth fim) {
        return somarIndice(CODIGO_POUPANCA, inicio, fim);
    }

    public  BigDecimal multiplicarIPCAE(YearMonth inicio, YearMonth fim) {
        return multiplicarIndice(CODIGO_IPCAE, inicio, fim);
    }

    public  BigDecimal multiplicarIPCA(YearMonth inicio, YearMonth fim) {
        return multiplicarIndice(CODIGO_IPCA, inicio, fim);
    }

    // ==== CARGA DE ÍNDICES ====
    private Map<YearMonth, BigDecimal> buscarIndice(Long tipoIndice, YearMonth inicio, YearMonth fim) {
        String cacheKey = tipoIndice + "-" + inicio + "-" + fim;
        /*if (cacheIndices.containsKey(cacheKey)) {
            return cacheIndices.get(cacheKey);
        }*/

        Map<YearMonth, BigDecimal> map = new HashMap<>();
        var indiceBancoCentral = buscarSerieBanco(tipoIndice, inicio.atDay(1), fim.atDay(1));
        for (BancoCentralService.BancoCentralResponse indice : indiceBancoCentral) {
            YearMonth ym = YearMonth.from(DateUtil.parseStringParaLocalDate(indice.getData()));
            map.put(ym, tipoIndice.equals(CODIGO_IPCAE) ?
                        indice.getValor().divide(BigDecimal.valueOf(100)).add(UM)
                    : (tipoIndice.equals(CODIGO_IPCA) ?
                        indice.getValor().divide(BigDecimal.valueOf(100)).add(UM)
                    : indice.getValor()));
        }
        cacheIndices.put(cacheKey, map);
        return map;
    }

    public BigDecimal somarIndice(Long codigoIndice, YearMonth inicio, YearMonth fim) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) return ZERO;
        Map<YearMonth, BigDecimal> indices = buscarIndice(codigoIndice, inicio, fim);
        UtilCalculo.validarSeTemTodosOsMeses(inicio, fim, indices, codigoIndice.toString());

        return indices.entrySet().stream()
                .filter(e -> !e.getKey().isBefore(inicio) && !e.getKey().isAfter(fim))
                .map(Map.Entry::getValue)
                .reduce(ZERO, BigDecimal::add);
    }

    public BigDecimal multiplicarIndice(Long codigoIndice, YearMonth inicio, YearMonth fim) {
        if (inicio == null || fim == null || inicio.isAfter(fim)) return UM;
        Map<YearMonth, BigDecimal> indices = buscarIndice(codigoIndice, inicio, fim);
        //UtilCalculo.validarSeTemTodosOsMeses(inicio, fim, indices, codigoIndice.toString());

        return indices.entrySet().stream()
                .filter(e -> !e.getKey().isBefore(inicio) && !e.getKey().isAfter(fim))
                .map(Map.Entry::getValue)
                .reduce(UM, BigDecimal::multiply);
    }

    private BigDecimal manterValorZeroSeNulo(BigDecimal b) {
        return b == null ? ZERO : b;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BancoCentralResponse {
        private String data;
        private BigDecimal valor;

        BancoCentralResponse(String data, BigDecimal valor){
            this.data = data;
            this.valor = valor;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }
}
