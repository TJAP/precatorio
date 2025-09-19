package br.jus.tjap.precatorio.calculadora.apibancocentral;

import br.jus.tjap.precatorio.calculadora.util.UtilCalculo;
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

    public BancoCentralService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    private List<BancoCentralResponse> buscarSerie(Long codigoIndice, LocalDate dataInicial, LocalDate dataFinal) {
        if(dataInicial == null || dataFinal == null){
            return Collections.emptyList();
        }
        try{
            String url = String.format(BASE_URL, codigoIndice, DateUtil.formatarData(dataInicial), DateUtil.formatarData(dataFinal));
            BancoCentralResponse[] response = restTemplate.getForObject(url, BancoCentralResponse[].class);
            return Arrays.asList(response);
        }catch (RuntimeException e){// Verificar se mês/ano são iguais ao mês/ano atual

            YearMonth hoje = YearMonth.now();
            YearMonth inicioYM = YearMonth.from(dataInicial);
            YearMonth fimYM = YearMonth.from(dataFinal);

            boolean dataIniIgualHoje = inicioYM.equals(hoje);
            boolean dataFimIgualHoje = fimYM.equals(hoje);

            String url = String.format(BASE_URL, codigoIndice,
                    DateUtil.formatarData(dataIniIgualHoje ? inicioYM.minusMonths(1).atDay(1) : dataInicial),
                    DateUtil.formatarData(dataFimIgualHoje ? fimYM.minusMonths(1).atDay(1) : dataFinal)
            );
            BancoCentralResponse[] response = restTemplate.getForObject(url, BancoCentralResponse[].class);
            return Arrays.asList(response);
        }

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
        if (cacheIndices.containsKey(cacheKey)) {
            return cacheIndices.get(cacheKey);
        }

        Map<YearMonth, BigDecimal> map = new HashMap<>();
        var indiceBancoCentral = buscarSerie(tipoIndice, inicio.atDay(1), fim.atDay(1));
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
