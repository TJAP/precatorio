package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.apibancocentral.BancoCentralService;
import br.jus.tjap.precatorio.modulos.calculadora.dto.*;
import br.jus.tjap.precatorio.modulos.calculadora.entity.TabelaIRRF;
import br.jus.tjap.precatorio.modulos.calculadora.exception.CalculationException;
import br.jus.tjap.precatorio.modulos.calculadora.repository.IndicadorIndiceRepository;
import br.jus.tjap.precatorio.modulos.calculadora.repository.TabelaIRRFRepository;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.repository.RequisitorioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CalculoPrecatorioService {

    private static final YearMonth EC113_DATA_CORTE = YearMonth.of(2021, 11); // nov/2021

    // extraido da planilha da Secretaria de Precatorio
    private static final LocalDate DATA_FINAL_CALCULO = LocalDate.now();
    private static final BigDecimal ZERO = new BigDecimal("0.00");
    private static final BigDecimal UM = BigDecimal.ONE;
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final int ESCALA_DEFAULT = 2;
    private static final int ESCALA_FATOR = 7;
    private static final int ESCALA_INDICE = 7;
    private static final int ESCALA_TAXA = 4;

    private static final BigDecimal FATOR_DOIS_PORCENTO = BigDecimal.valueOf(0.166666667);


    private final IndicadorIndiceRepository indicadorIndiceRepository;

    private final TabelaIRRFRepository tabelaIRRFRepository;
    private final RequisitorioRepository requisitorioRepository;

    private BancoCentralService bancoCentralService;

    public CalculoPrecatorioService(
            IndicadorIndiceRepository indicadorIndiceRepository,
            BancoCentralService bancoCentralService,
            TabelaIRRFRepository tabelaIRRFRepository,
            RequisitorioRepository requisitorioRepository) {
        this.indicadorIndiceRepository = indicadorIndiceRepository;
        this.bancoCentralService = bancoCentralService;
        this.tabelaIRRFRepository = tabelaIRRFRepository;
        this.requisitorioRepository = requisitorioRepository;
    }

    public static void main(String[] args) {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 8, 1);

        long meses = UtilCalculo.contarMesesInclusivos(inicio, fim);
        System.out.println("Meses: " + meses); // saída: 7

        System.out.println("Calculo: " + FATOR_DOIS_PORCENTO.multiply(BigDecimal.valueOf(meses)).setScale(4, RoundingMode.HALF_UP)); // saída: 7
    }



    private PeriodoResultado calcularPeriodoIPCA(
            LocalDate dataInicio,
            LocalDate dataFim,
            BigDecimal valorTributavel,
            BigDecimal valorNaoTributavel,
            BigDecimal valorJuros,
            BigDecimal custasMulta,
            BigDecimal selic,
            boolean temTotalMeses,
            boolean isFatorEscalaOito
    ) {

        PeriodoResultado pr = new PeriodoResultado();
        BigDecimal ipcaFator = UM;
        BigDecimal mesesFator = ZERO;

        // verifica se existem as datas para consultar indice
        if (dataInicio != null || dataFim != null) {
            ipcaFator = bancoCentralService.multiplicarIPCA(
                    YearMonth.from(dataInicio),
                    YearMonth.from(dataFim)
            );

            long totalMeses = UtilCalculo.contarMesesInclusivos(dataInicio, dataFim);

            mesesFator = FATOR_DOIS_PORCENTO.multiply(
                    BigDecimal.valueOf(totalMeses)
            );
        }

        BigDecimal valorIpcaFatorMostrar = ipcaFator.setScale(isFatorEscalaOito ? ESCALA_FATOR : ESCALA_INDICE, RoundingMode.HALF_UP);

        if (!temTotalMeses) {
            mesesFator = ZERO;
        }

        pr.setDataInicio(dataInicio);
        pr.setDataFim(dataFim);
        pr.setIpcaFator(valorIpcaFatorMostrar);

        pr.setPrincipalTributavel(valorTributavel.multiply(ipcaFator));
        pr.setPrincipalNaoTributavel(valorNaoTributavel.multiply(ipcaFator));

        if (temTotalMeses) {
            BigDecimal valorJurosCorrigido = valorJuros.multiply(ipcaFator).add(
                    pr.getPrincipalTributavel().add(pr.getPrincipalNaoTributavel()).multiply(mesesFator).divide(CEM)
            );
            pr.setValorJuros(valorJurosCorrigido);
        } else {
            pr.setValorJuros(valorJuros.multiply(ipcaFator));
        }

        pr.setFatorJuros(mesesFator.setScale(4, RoundingMode.HALF_UP));
        pr.setCustasMulta(custasMulta.multiply(ipcaFator));
        pr.setSelic(selic.multiply(ipcaFator));

        // total atualizado
        pr.setTotalAtualizado(
                pr.getPrincipalTributavel()
                        .add(pr.getPrincipalNaoTributavel())
                        .add(pr.getValorJuros())
                        .add(pr.getCustasMulta())
                        .add(pr.getSelic())
        );

        return pr;
    }

    private void calcularPeriodoSelic(
            LocalDate dataInicioAntesGraca,
            LocalDate dataFimAntesGraca,
            LocalDate dataInicioDuranteGraca,
            LocalDate dataFimDuranteGraca,
            LocalDate dataInicioPosGraca,
            LocalDate dataFimPosGraca,
            CalculoRequest req,
            CalculoAtualizacaoDTO resultado
    ) {

        boolean temDataAtesGraca = !Objects.isNull(dataInicioAntesGraca);
        boolean temDataDuranteGraca = !Objects.isNull(dataInicioDuranteGraca);
        boolean temDataAposGraca = !Objects.isNull(dataInicioPosGraca);

        BigDecimal totalValoresNaRequisicao =
                req.getValorPrincipalTributavel().add(req.getValorPrincipalNaoTributavel())
                        .add(req.getValorJuros())
                        .add(req.getMulta().add(req.getCustas()).add(req.getOutrosReembolsos()))
                        .add(req.getValorSelic());

        BigDecimal selicTaxaSelicAntesGraca =
                temDataAtesGraca ?
                        UtilCalculo.escala(bancoCentralService.somarSelic(YearMonth.from(dataInicioAntesGraca),
                                YearMonth.from(
                                        dataFimAntesGraca.isAfter(LocalDate.now().minusMonths(1)) ? LocalDate.now().minusMonths(1) : dataFimAntesGraca
                                )),4)
                        : ZERO;
        BigDecimal selicFatorIPCADuranteGraca =
                temDataDuranteGraca ?
                        bancoCentralService.multiplicarIPCA(YearMonth.from(dataInicioDuranteGraca), YearMonth.from(dataFimDuranteGraca))
                        : ZERO;
        BigDecimal selictTaxaSelicAposGraca =
                temDataAposGraca ?
                        UtilCalculo.escala(bancoCentralService.somarSelic(YearMonth.from(dataInicioPosGraca), YearMonth.from(dataFimPosGraca)),4)
                        : ZERO;

        resultado.setSelicAntesGracaTaxa(temDataAtesGraca ? selicTaxaSelicAntesGraca : ZERO);
        resultado.setSelicDuranteGracaFatorIPCA(UtilCalculo.escala(selicFatorIPCADuranteGraca, 7));
        resultado.setSelicPosGracaTaxa(selictTaxaSelicAposGraca);

        // Calculo Selic Antes da graça
        BigDecimal valorSelicAntesGraca = !temDataAtesGraca ? BigDecimal.ZERO : totalValoresNaRequisicao
                .subtract(req.getValorSelic())
                .multiply(selicTaxaSelicAntesGraca)
                .divide(CEM);
        resultado.setSelicAntesGracaSelicValorCorrigido(UtilCalculo.escala(valorSelicAntesGraca, 2));

        // Calculo Selic Durante Graça
        BigDecimal valorSelicDurantePrincipalTributavel = req.getValorPrincipalTributavel().multiply(selicFatorIPCADuranteGraca);
        BigDecimal valorSelicDurantePrincipalNaoTributavel = req.getValorPrincipalNaoTributavel().multiply(selicFatorIPCADuranteGraca);
        BigDecimal valorSelicDuranteJuros = req.getValorJuros().multiply(selicFatorIPCADuranteGraca);
        BigDecimal valorSelicDuranteMultaCusta = req.getCustas().add(req.getMulta()).add(req.getOutrosReembolsos()).multiply(selicFatorIPCADuranteGraca);
        BigDecimal valorSelicDuranteSelic = req.getValorSelic().add(valorSelicAntesGraca).multiply(selicFatorIPCADuranteGraca);
        BigDecimal valorSelicTotalDuranteGranca =
                valorSelicDurantePrincipalTributavel
                        .add(valorSelicDurantePrincipalNaoTributavel)
                        .add(valorSelicDuranteJuros)
                        .add(valorSelicDuranteMultaCusta)
                        .add(valorSelicDuranteSelic);
        resultado.setSelicDuranteGracaPrincipalTributavelCorrigido(UtilCalculo.escala(valorSelicDurantePrincipalTributavel, 2));
        resultado.setSelicDuranteGracaPrincipalNaoTributavelCorrigido(UtilCalculo.escala(valorSelicDurantePrincipalNaoTributavel, 2));
        resultado.setSelicDuranteGracaValorJurosCorrigido(UtilCalculo.escala(valorSelicDuranteJuros, 2));
        resultado.setSelicDuranteGracaCustasMultaCorrigido(UtilCalculo.escala(valorSelicDuranteMultaCusta, 2));
        resultado.setSelicDuranteGracaSelicCorrigido(UtilCalculo.escala(valorSelicDuranteSelic, 2));
        resultado.setSelicDuranteGracaTotalAtualizado(UtilCalculo.escala(valorSelicTotalDuranteGranca, 2));

        BigDecimal valorCalculoSemDuranteGraca = valorSelicTotalDuranteGranca.compareTo(BigDecimal.ZERO) == 0 ? totalValoresNaRequisicao : valorSelicTotalDuranteGranca;

        // Calculo Selic Após Graça
        BigDecimal valorSelicAposGraca = valorCalculoSemDuranteGraca
                .subtract(valorSelicDuranteSelic)
                .multiply(selictTaxaSelicAposGraca)
                .divide(CEM)
                .add(valorSelicDuranteSelic);
        BigDecimal valorTotalSelicAposGraca = valorSelicAposGraca.add(valorCalculoSemDuranteGraca).subtract(valorSelicDuranteSelic);
        resultado.setSelicPosGracaSelicValorCorrigido(UtilCalculo.escala(valorSelicAposGraca, 2));
        resultado.setSelicPosGracaTotalAtualizado(UtilCalculo.escala(valorTotalSelicAposGraca, 2));

    }

    public BigDecimal calculoPrevidencia(
            BigDecimal valorPrevidencia,
            BigDecimal valorPrincipalTributavel,
            BigDecimal valorPrincipalNaoTributavel){

        BigDecimal valoPrevidenciaCorrigido =
                valorPrevidencia.add(valorPrincipalNaoTributavel);
        return valoPrevidenciaCorrigido;
    }

    public CalculoAtualizacaoDTO calcularAtualizacao(CalculoRequest req) {
        validateRequest(req);
        var atualizacao = new CalculoAtualizacaoDTO();
        RequisitorioDTO requisitorioDTO = new RequisitorioDTO();
        if(Objects.nonNull(req.getIdPrecatorio())){
            requisitorioDTO = requisitorioRepository.findById(req.getIdPrecatorio()).get().toMetadado();
            atualizacao.setRequisitorioDTO(requisitorioDTO);
        }

        // PERÍODOS base
        LocalDate dataFinalGraca = LocalDate.of(req.getAnoVencimento(),12,31);
        LocalDate dataInicialGraca = UtilCalculo.calculaDataIncioGraca(req.getAnoVencimento(), dataFinalGraca);
        LocalDate dataAtualizacao = req.getDataUltimaAtualizacao().plusMonths(1);
        LocalDate dataHoje = Objects.isNull(req.getDataFimAtualizacao()) ? LocalDate.now() : req.getDataFimAtualizacao().plusMonths(1);

        LocalDate dataInicioAntesGraca = null;
        LocalDate dataFimAntesGraca = null;
        LocalDate dataInicioDuranteGraca = null;
        LocalDate dataFimDuranteGraca = null;
        LocalDate dataInicioPosGraca = null;
        LocalDate dataFimPosGraca = null;

        // --- Antes ---
        boolean temPeriodoAntesGraca = dataAtualizacao.isBefore(dataInicialGraca);
        if (temPeriodoAntesGraca) {
            dataInicioAntesGraca = dataAtualizacao;
            dataFimAntesGraca = dataInicialGraca.minusMonths(1);
        }

        // --- Durante ---
        boolean dentroPeriodoDuranteGraca =
                (dataAtualizacao.isEqual(dataInicialGraca) || dataAtualizacao.isAfter(dataInicialGraca))
                        && (dataAtualizacao.isEqual(dataFinalGraca) || dataAtualizacao.isBefore(dataFinalGraca));

        if (dentroPeriodoDuranteGraca) {
            if(dataAtualizacao.isAfter(dataInicialGraca) && dataAtualizacao.isBefore(dataFinalGraca)){
                dataInicioDuranteGraca = dataAtualizacao;
            } else {
                dataInicioDuranteGraca = dataInicialGraca;
            }
            dataFimDuranteGraca = dataFinalGraca;
        } else {
            if(dataAtualizacao.isBefore(dataFinalGraca) && temPeriodoAntesGraca){
                if(dataFinalGraca.isAfter(dataHoje)){
                    if(!dataInicialGraca.isAfter(dataHoje.minusMonths(1))){
                        dataInicioDuranteGraca = dataInicialGraca;
                        dataFimDuranteGraca = dataHoje.minusMonths(1);
                    }
                } else {
                    dataInicioDuranteGraca = dataInicialGraca;
                    dataFimDuranteGraca = dataFinalGraca;
                }
            }
        }

        // --- Após ---
        if(dataHoje.isAfter(dataFinalGraca)){
            if (dataAtualizacao.isAfter(dataFinalGraca)) {
                dataInicioPosGraca = dataAtualizacao;
            }else {
                dataInicioPosGraca = dataFinalGraca.plusMonths(1);
            }
            dataFimPosGraca = dataHoje.minusMonths(1);
        }

        PeriodoResultado antesGraca = calcularPeriodoIPCA(
                dataInicioAntesGraca,
                dataFimAntesGraca,
                req.getValorPrincipalTributavel(),
                req.getValorPrincipalNaoTributavel(),
                req.getValorJuros(),
                req.getCustas().add(req.getMulta()).add(req.getOutrosReembolsos()),
                req.getValorSelic(),
                true,
                true
        );
        atualizacao.preencherIpcaAntes(antesGraca);

        PeriodoResultado duranteGraca = calcularPeriodoIPCA(
                dataInicioDuranteGraca,
                dataFimDuranteGraca,
                atualizacao.getIpcaAntesGracaPrincipalTributavelCorrigido(),
                atualizacao.getIpcaAntesGracaPrincipalNaoTributavelCorrigido(),
                atualizacao.getIpcaAntesGracaValorJurosCorrigido(),
                atualizacao.getIpcaAntesGracaCustasMultaCorrigido(),
                atualizacao.getIpcaAntesGracaSelicCorrigido(),
                false,
                false
        );
        atualizacao.preencherIpcaDurante(duranteGraca);


        PeriodoResultado aposGraca = calcularPeriodoIPCA(
                dataInicioPosGraca,
                dataFimPosGraca,
                atualizacao.getIpcaDuranteGracaPrincipalTributavelCorrigido(),
                atualizacao.getIpcaDuranteGracaPrincipalNaoTributavelCorrigido(),
                atualizacao.getIpcaDuranteGracaValorJurosCorrigido(),
                atualizacao.getIpcaDuranteGracaCustasMultaCorrigido(),
                atualizacao.getIpcaDuranteGracaSelicCorrigido(),
                true,
                false
        );
        atualizacao.setIpcaValorPrevidenciaCorrigido(
                UtilCalculo.escala(
                        req.getValorPrevidencia()
                                .multiply(antesGraca.getIpcaFator())
                                .multiply(duranteGraca.getIpcaFator())
                                .multiply(aposGraca.getIpcaFator())
                        ,2)
        );
        atualizacao.preencherIpcaDepois(aposGraca);

        // calculo SELIC
        calcularPeriodoSelic(
                dataInicioAntesGraca,
                dataFimAntesGraca,
                dataInicioDuranteGraca,
                dataFimDuranteGraca,
                dataInicioPosGraca,
                dataFimPosGraca,
                req,
                atualizacao
        );

        atualizacao.setSelicValorPrevidenciaCorrigido(
                UtilCalculo.escala(
                        req.getValorPrevidencia()
                                .multiply(UM)
                                .multiply(atualizacao.getSelicDuranteGracaFatorIPCA())
                                .multiply(UM)
                        ,2)
        );

        BigDecimal maiorValorIpca = atualizacao.getIpcaAntesGracaTotalAtualizado().max(atualizacao.getIpcaDuranteGracaTotalAtualizado())
                .max(atualizacao.getIpcaPosGracaTotalAtualizado());

        boolean ipcaZero = maiorValorIpca.compareTo(BigDecimal.ZERO) == 0;

        BigDecimal menor = ipcaZero ? atualizacao.getSelicPosGracaTotalAtualizado() : maiorValorIpca.min(atualizacao.getSelicPosGracaTotalAtualizado());
        String tipo = menor.equals(maiorValorIpca) ? "IPCA" : "SELIC";

        atualizacao.setResultadoValorBrutoAtualizado(UtilCalculo.escala(menor, 2));

        if(tipo.equals("IPCA")){

            atualizacao.setResultadoValorPrincipalTributavelAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaPrincipalTributavelCorrigido(),2));
            atualizacao.setResultadoValorPrincipalNaoTributavelAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaPrincipalNaoTributavelCorrigido(),2));
            atualizacao.setResultadoValorJurosAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaValorJurosCorrigido(),2));
            atualizacao.setResultadoValorMultaCustasOutrosAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaCustasMultaCorrigido(),2));
            atualizacao.setResultadoValorSelicAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaSelicCorrigido(),2));
            atualizacao.setResultadoValorBrutoAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaTotalAtualizado(),2));
            atualizacao.setResultadoValorPrevidenciaAtualizado(UtilCalculo.escala(atualizacao.getIpcaValorPrevidenciaCorrigido(),2));

            atualizacao.setResultadoFatorAntesAtualizado(atualizacao.getIpcaAntesGracaFator());
            atualizacao.setResultadoFatorDuranteAtualizado(atualizacao.getIpcaDuranteGracaFator());
            atualizacao.setResultadoFatorDepoisAtualizado(atualizacao.getIpcaPosGracaFator());

            atualizacao.setResultadoTaxaAntesAtualizado(atualizacao.getIpcaAntesGracaFatorJuros());
            atualizacao.setResultadoTaxaDuranteAtualizado(atualizacao.getIpcaDuranteGracaFatorJuros());
            atualizacao.setResultadoTaxaDepoisAtualizado(atualizacao.getIpcaPosGracaFatorJuros());

            atualizacao.setResultadoValorPrincipalTributavelAtualizadoDizima(atualizacao.getIpcaPosGracaPrincipalTributavelCorrigido());
            atualizacao.setResultadoValorPrincipalNaoTributavelAtualizadoDizima(atualizacao.getIpcaPosGracaPrincipalNaoTributavelCorrigido());
            atualizacao.setResultadoValorJurosAtualizadoDizima(atualizacao.getIpcaPosGracaValorJurosCorrigido());
            atualizacao.setResultadoValorMultaCustasOutrosAtualizadoDizima(atualizacao.getIpcaPosGracaCustasMultaCorrigido());
            atualizacao.setResultadoValorSelicAtualizadoDizima(atualizacao.getIpcaPosGracaSelicCorrigido());
            atualizacao.setResultadoValorBrutoAtualizadoDizima(atualizacao.getIpcaPosGracaTotalAtualizado());
            atualizacao.setResultadoValorPrevidenciaAtualizadoDizima(atualizacao.getIpcaValorPrevidenciaCorrigido());

        }else{
            atualizacao.setResultadoValorPrincipalTributavelAtualizado(UtilCalculo.escala(atualizacao.getSelicDuranteGracaPrincipalTributavelCorrigido(),2));
            atualizacao.setResultadoValorPrincipalNaoTributavelAtualizado(UtilCalculo.escala(atualizacao.getSelicDuranteGracaPrincipalNaoTributavelCorrigido(),2));
            atualizacao.setResultadoValorJurosAtualizado(UtilCalculo.escala(atualizacao.getSelicDuranteGracaValorJurosCorrigido(),2));
            atualizacao.setResultadoValorMultaCustasOutrosAtualizado(UtilCalculo.escala(atualizacao.getSelicDuranteGracaCustasMultaCorrigido(),2));
            atualizacao.setResultadoValorSelicAtualizado(UtilCalculo.escala(atualizacao.getSelicDuranteGracaSelicCorrigido(),2));
            atualizacao.setResultadoValorBrutoAtualizado(UtilCalculo.escala(atualizacao.getSelicPosGracaTotalAtualizado(),2));
            atualizacao.setResultadoValorPrevidenciaAtualizado(UtilCalculo.escala(atualizacao.getSelicValorPrevidenciaCorrigido(),2));

            atualizacao.setResultadoFatorAntesAtualizado(BigDecimal.valueOf(1.0000000));
            atualizacao.setResultadoFatorDuranteAtualizado(atualizacao.getSelicDuranteGracaFatorIPCA());
            atualizacao.setResultadoFatorDepoisAtualizado(BigDecimal.valueOf(1.0000000));

            atualizacao.setResultadoTaxaAntesAtualizado(atualizacao.getSelicAntesGracaTaxa());
            atualizacao.setResultadoTaxaDuranteAtualizado(BigDecimal.ZERO);
            atualizacao.setResultadoTaxaDepoisAtualizado(atualizacao.getSelicPosGracaTaxa());

            atualizacao.setResultadoValorPrincipalTributavelAtualizadoDizima(atualizacao.getSelicDuranteGracaPrincipalTributavelCorrigido());
            atualizacao.setResultadoValorPrincipalNaoTributavelAtualizadoDizima(atualizacao.getSelicDuranteGracaPrincipalNaoTributavelCorrigido());
            atualizacao.setResultadoValorJurosAtualizadoDizima(atualizacao.getSelicDuranteGracaValorJurosCorrigido());
            atualizacao.setResultadoValorMultaCustasOutrosAtualizadoDizima(atualizacao.getSelicDuranteGracaCustasMultaCorrigido());
            atualizacao.setResultadoValorSelicAtualizadoDizima(atualizacao.getSelicDuranteGracaSelicCorrigido());
            atualizacao.setResultadoValorBrutoAtualizadoDizima(atualizacao.getSelicPosGracaTotalAtualizado());
            atualizacao.setResultadoValorPrevidenciaAtualizadoDizima(atualizacao.getSelicValorPrevidenciaCorrigido());
        }

        atualizacao.setResultadoNumeroMesesRRA(calcularMesesPeriodo(req.getDataInicioRRA(), req.getDataFimRRA()));
        atualizacao.setResultadoCnpjDevedor(req.getCnpjDevedor());
        atualizacao.setTipoCalculoRetornado(tipo);

        return atualizacao;
    }

    public static long calcularMesesPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            return 0L;
        }

        // Equivalente a: DATE(YEAR(AO3); MONTH(AO3)+1; 1)
        LocalDate proximoMesPrimeiroDia = dataFim.plusMonths(1).withDayOfMonth(1);

        // DATEDIF(AN3; AO3; "m")
        long mesesEntre = ChronoUnit.MONTHS.between(dataInicio.withDayOfMonth(1), dataFim.withDayOfMonth(1));

        // DATEDIF(AN3; DATE(YEAR(AO3);MONTH(AO3)+1;1);"y")
        long anosEntre = ChronoUnit.YEARS.between(dataInicio, proximoMesPrimeiroDia);

        // Soma os componentes e adiciona +1 conforme a fórmula
        return anosEntre + mesesEntre + 1;
    }

    public BigDecimal calcularValorBasePrevidenciaCorrigido(
            BigDecimal valorBasePrevidencia, BigDecimal fatorIndiceIpca, BigDecimal fatorIndiceIpcaDuranteGraca, BigDecimal dc,
            BigDecimal ae, BigDecimal j,
            String q,
            LocalDate h, LocalDate i,
            Map<YearMonth, BigDecimal> inssCol9,
            Map<YearMonth, BigDecimal> inssCol12
    ) {
        // Se DL <= 0 retorna zero
        if (valorBasePrevidencia == null || valorBasePrevidencia.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // RPPS → ((DL * AU) * BU) * DC
        BigDecimal valorParticipacao = calcularValorPrioridade(
                false,
                ZERO,
                ZERO,
                ZERO,
                ZERO
        );
        if ("RPPS".equalsIgnoreCase(q)) {
            return valorBasePrevidencia.multiply(fatorIndiceIpca).multiply(fatorIndiceIpcaDuranteGraca).multiply(dc);
        }

        // Média da data entre H e I
        LocalDate averageDate = h.plusDays(ChronoUnit.DAYS.between(h, i) / 2);
        YearMonth ym = YearMonth.from(averageDate);

        // Valor INSS coluna 12 se existir, senão coluna 9
        BigDecimal inssValue = inssCol12.getOrDefault(ym, inssCol9.getOrDefault(ym, BigDecimal.ZERO));

        if ("RGPS".equalsIgnoreCase(q)) {
            // compara DL < inssValue * J ?
            BigDecimal inssTimesJ = inssValue.multiply(j);

            BigDecimal base;
            if (valorBasePrevidencia.compareTo(inssTimesJ) < 0) {
                base = ae;
            } else {
                base = inssTimesJ;
            }

            // ((base * AU) * BU) * DC
            return base.multiply(fatorIndiceIpca).multiply(fatorIndiceIpcaDuranteGraca).multiply(dc);
        }

        // Default
        return BigDecimal.ZERO;
    }

    public BigDecimal calcularValorPrioridade(
            Boolean prioridade,
            BigDecimal saldo,
            BigDecimal desembolso,
            BigDecimal totalPrecatorio,
            BigDecimal honorarios) {

        // se B496 = "PRIORIDADE"
        if (prioridade) {
            // se DB496 >= 0
            if (saldo != null && saldo.compareTo(BigDecimal.ZERO) >= 0) {
                // evita divisão por zero
                BigDecimal divisor = totalPrecatorio.subtract(honorarios);
                if (divisor.compareTo(BigDecimal.ZERO) == 0) {
                    return BigDecimal.valueOf(0);
                }
                return desembolso.divide(divisor, 10, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(0);
            }
        } else {
            // se não é PRIORIDADE, retorna 100%
            return BigDecimal.valueOf(100);
        }
    }

    private void validateRequest(CalculoRequest req) {
        if (Objects.isNull(req)) {
            throw new CalculationException("Requisição de cálculo não pode ser nula");
        }
        if (Objects.isNull(req.getDataUltimaAtualizacao())) {
            throw new CalculationException("dataUltimaAtualizacao é obrigatória");
        }
        if (Objects.isNull(req.getAnoVencimento())) {
            throw new CalculationException("anoVencimento é obrigatório");
        }
    }

}

