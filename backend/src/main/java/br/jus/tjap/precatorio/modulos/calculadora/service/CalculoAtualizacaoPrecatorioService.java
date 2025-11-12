package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.apibancocentral.BancoCentralService;
import br.jus.tjap.precatorio.modulos.calculadora.dto.*;
import br.jus.tjap.precatorio.modulos.calculadora.exception.CalculationException;
import br.jus.tjap.precatorio.modulos.calculadora.util.CalculoUtil;
import br.jus.tjap.precatorio.modulos.calculadora.util.PeriodoGracaCalculator;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.service.RequisitorioService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class CalculoAtualizacaoPrecatorioService {

    // extraido da planilha da Secretaria de Precatorio
    private static final BigDecimal ZERO = new BigDecimal("0.00");
    private static final BigDecimal UM = BigDecimal.ONE;
    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final int ESCALA_FATOR = 7;
    private static final int ESCALA_INDICE = 7;

    private static final BigDecimal FATOR_DOIS_PORCENTO = BigDecimal.valueOf(0.1666666667);

    private final BancoCentralService bancoCentralService;
    private final RequisitorioService requisitorioService;

    public CalculoAtualizacaoPrecatorioService(BancoCentralService bancoCentralService, RequisitorioService requisitorioService) {
        this.bancoCentralService = bancoCentralService;
        this.requisitorioService = requisitorioService;
    }

    public static void main(String[] args) {
    }

    public ResultadoAtualizacaoPrecatorioDTO controlarTipoCalculo(RequisitorioDTO dto) {
        var atualizacao = new ResultadoAtualizacaoPrecatorioDTO();
        var calculo = new CorrecaoDTO();
        List<CorrecaoDTO> calculos1 = new ArrayList<>();
        List<CorrecaoDTO> calculos2 = new ArrayList<>();
        List<CorrecaoDTO> calculos3 = new ArrayList<>();
        List<CorrecaoDTO> calculosTotal = new ArrayList<>();

        // Periodo até Novembro de 2021
        calculos1 = calcularPeriodoAteNovembro2021(dto);
        calculos2 = calcularPeriodoEntreDezembro2021AhJulho2025(dto, calculos1.getLast());
        calculos3 = calcularPeriodoAposAgosto2025(dto, calculos2.getLast());
        calculosTotal.addAll(calculos1);
        calculosTotal.addAll(calculos2);
        calculosTotal.addAll(calculos3);

        var request = new CalculoRequest();

        request.set
        // Periodo entre Dezembro de 2021 a Julho de 2025


        return atualizacao;
    }

    public ResultadoAtualizacaoPrecatorioDTO atualizacaoPrecatorio(RequisitorioDTO requisitorioDTO) {

        var atualizacao = new CalculoAtualizacaoDTO();

        var controle = controlarTipoCalculo(requisitorioDTO);

        // --- Determinação dos períodos principais ---
        final var dataAtualMenoUmMes = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        final var dataFinalGraca = LocalDate.of(requisitorioDTO.getAnoVencimento(), 12, 31);
        final var dataInicialGraca = UtilCalculo.calculaDataIncioGraca(requisitorioDTO.getAnoVencimento(), dataFinalGraca);
        final var dataAtualizacao = requisitorioDTO.getDtUltimaAtualizacaoPlanilha().plusMonths(1);
        final var dataHoje = Objects.requireNonNullElse(requisitorioDTO.getDtFimAtualizacaoPlanilha(), dataAtualMenoUmMes);

        // --- Determina períodos antes, durante e depois da graça ---
        final var periodos = calcularPeriodos(dataAtualizacao, dataInicialGraca, dataFinalGraca, dataHoje);

        // --- IPCA: Antes, Durante, Depois ---
        final var antesGraca = calcularPeriodoIPCA(
                periodos.antesInicio(), periodos.antesFim(),
                CalculoUtil.zeroSeNulo(requisitorioDTO.getVlPrincipalTributavelCorrigido()),
                CalculoUtil.zeroSeNulo(requisitorioDTO.getVlPrincipalNaoTributavelCorrigido()),
                CalculoUtil.zeroSeNulo(requisitorioDTO.getVlJurosAplicado()),
                CalculoUtil.zeroSeNulo(requisitorioDTO.getVlDevolucaoCusta()).add(CalculoUtil.zeroSeNulo(requisitorioDTO.getVlPagamentoMulta())),
                CalculoUtil.zeroSeNulo(requisitorioDTO.getVlSelic()),
                true, true
        );
        atualizacao.preencherIpcaAntes(antesGraca);
        final var duranteGraca = calcularPeriodoIPCA(
                periodos.duranteInicio(), periodos.duranteFim(),
                atualizacao.getIpcaAntesGracaPrincipalTributavelCorrigido(),
                atualizacao.getIpcaAntesGracaPrincipalNaoTributavelCorrigido(),
                atualizacao.getIpcaAntesGracaValorJurosCorrigido(),
                atualizacao.getIpcaAntesGracaCustasMultaCorrigido(),
                atualizacao.getIpcaAntesGracaSelicCorrigido(),
                false, false
        );
        atualizacao.preencherIpcaDurante(duranteGraca);
        final var aposGraca = calcularPeriodoIPCA(
                periodos.depoisInicio(), periodos.depoisFim(),
                atualizacao.getIpcaDuranteGracaPrincipalTributavelCorrigido(),
                atualizacao.getIpcaDuranteGracaPrincipalNaoTributavelCorrigido(),
                atualizacao.getIpcaDuranteGracaValorJurosCorrigido(),
                atualizacao.getIpcaDuranteGracaCustasMultaCorrigido(),
                atualizacao.getIpcaDuranteGracaSelicCorrigido(),
                true, false
        );
        atualizacao.preencherIpcaDepois(aposGraca);

        // --- Previdência corrigida pelo IPCA ---
        final var fatorIpcaTotal = antesGraca.getIpcaFator()
                .multiply(duranteGraca.getIpcaFator())
                .multiply(aposGraca.getIpcaFator());
        atualizacao.setIpcaValorPrevidenciaCorrigido(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrevidencia()).multiply(fatorIpcaTotal));

        // --- SELIC ---
        calcularPeriodosSelic(
                periodos.antesInicio(), periodos.antesFim(),
                periodos.duranteInicio(), periodos.duranteFim(),
                periodos.depoisInicio(), periodos.depoisFim(),
                requisitorioDTO, atualizacao
        );

        atualizacao.setSelicValorPrevidenciaCorrigido(
                UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrevidencia())
                        .multiply(BigDecimal.ONE)
                        .multiply(atualizacao.getSelicDuranteGracaFatorIPCA())
        );

        // --- Determina qual índice é mais vantajoso (IPCA x SELIC) ---
        definirResultadoFinal(atualizacao, requisitorioDTO);
        var resultado = new ResultadoAtualizacaoPrecatorioDTO(
                requisitorioDTO.getId(),
                //requisitorioDTO,
                atualizacao.getTipoCalculoRetornado(),
                requisitorioDTO.getDtUltimaAtualizacaoPlanilha(),
                dataHoje,
                atualizacao.getResultadoValorPrincipalTributavelAtualizado(),
                atualizacao.getResultadoValorPrincipalNaoTributavelAtualizado(),
                atualizacao.getResultadoValorJurosAtualizado(),
                atualizacao.getResultadoValorMultaCustasOutrosAtualizado(),
                atualizacao.getResultadoValorSelicAtualizado(),
                atualizacao.getResultadoValorBrutoAtualizado(),
                atualizacao.getResultadoValorPrevidenciaAtualizado(),
                atualizacao.getResultadoFatorAntesAtualizado(),
                atualizacao.getResultadoFatorDuranteAtualizado(),
                atualizacao.getResultadoFatorDepoisAtualizado(),
                atualizacao.getResultadoTaxaAntesAtualizado(),
                atualizacao.getResultadoTaxaDuranteAtualizado(),
                atualizacao.getResultadoTaxaDepoisAtualizado(),
                atualizacao.getResultadoNumeroMesesRRA());
        return resultado;
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
        BigDecimal mesesFator = BigDecimal.ZERO;

        // verifica se existem as datas para consultar indice
        if (dataInicio != null || dataFim != null) {
            ipcaFator = bancoCentralService.multiplicarIPCA(YearMonth.from(dataInicio), YearMonth.from(dataFim));

            long totalMeses = UtilCalculo.contarMesesInclusivos(dataInicio, dataFim);

            mesesFator = FATOR_DOIS_PORCENTO.multiply(BigDecimal.valueOf(totalMeses));
        }

        BigDecimal valorIpcaFatorMostrar = ipcaFator.setScale(isFatorEscalaOito ? ESCALA_FATOR : ESCALA_INDICE, RoundingMode.HALF_UP);

        if (!temTotalMeses) {
            mesesFator = BigDecimal.ZERO;
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

        pr.setFatorJuros(mesesFator);
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
                                )), 7)
                        : ZERO;
        BigDecimal selicFatorIPCADuranteGraca =
                temDataDuranteGraca ?
                        bancoCentralService.multiplicarIPCA(YearMonth.from(dataInicioDuranteGraca), YearMonth.from(dataFimDuranteGraca))
                        : ZERO;
        BigDecimal selictTaxaSelicAposGraca =
                temDataAposGraca ?
                        UtilCalculo.escala(bancoCentralService.somarSelic(YearMonth.from(dataInicioPosGraca), YearMonth.from(dataFimPosGraca)), 7)
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
        BigDecimal valorSelicDurantePrincipalTributavel = UtilCalculo.isNotNullOrZero(selicFatorIPCADuranteGraca) ? req.getValorPrincipalTributavel().multiply(selicFatorIPCADuranteGraca) : req.getValorPrincipalTributavel();
        BigDecimal valorSelicDurantePrincipalNaoTributavel = UtilCalculo.isNotNullOrZero(selicFatorIPCADuranteGraca) ? req.getValorPrincipalNaoTributavel().multiply(selicFatorIPCADuranteGraca) : req.getValorPrincipalNaoTributavel();
        BigDecimal valorSelicDuranteJuros = UtilCalculo.isNotNullOrZero(selicFatorIPCADuranteGraca) ? req.getValorJuros().multiply(selicFatorIPCADuranteGraca) : req.getValorJuros();
        BigDecimal valorSelicDuranteMultaCusta = UtilCalculo.isNotNullOrZero(selicFatorIPCADuranteGraca) ? req.getCustas().add(req.getMulta()).add(req.getOutrosReembolsos()).multiply(selicFatorIPCADuranteGraca) : req.getCustas().add(req.getMulta()).add(req.getOutrosReembolsos());
        BigDecimal valorSelicDuranteSelic = UtilCalculo.isNotNullOrZero(selicFatorIPCADuranteGraca) ? req.getValorSelic().add(valorSelicAntesGraca).multiply(selicFatorIPCADuranteGraca) : req.getValorSelic().add(valorSelicAntesGraca);
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
        BigDecimal valorSelicAposGraca = valorSelicTotalDuranteGranca
                .subtract(valorSelicDuranteSelic)
                .multiply(selictTaxaSelicAposGraca)
                .divide(CEM)
                .add(valorSelicDuranteSelic);
        BigDecimal valorTotalSelicAposGraca = valorSelicAposGraca.add(valorCalculoSemDuranteGraca).subtract(valorSelicDuranteSelic);
        resultado.setSelicPosGracaSelicValorCorrigido(UtilCalculo.escala(valorSelicAposGraca, 2));
        resultado.setSelicPosGracaTotalAtualizado(UtilCalculo.escala(valorTotalSelicAposGraca, 2));

    }

    private void calcularPeriodosSelic(
            LocalDate dataInicioAntesGraca,
            LocalDate dataFimAntesGraca,
            LocalDate dataInicioDuranteGraca,
            LocalDate dataFimDuranteGraca,
            LocalDate dataInicioPosGraca,
            LocalDate dataFimPosGraca,
            RequisitorioDTO req,
            CalculoAtualizacaoDTO resultado
    ) {

        boolean temDataAtesGraca = !Objects.isNull(dataInicioAntesGraca);
        boolean temDataDuranteGraca = !Objects.isNull(dataInicioDuranteGraca);
        boolean temDataAposGraca = !Objects.isNull(dataInicioPosGraca);

        BigDecimal valorTributavel = CalculoUtil.zeroSeNulo(req.getVlPrincipalTributavelCorrigido());
        BigDecimal valorNaoTributavel = CalculoUtil.zeroSeNulo(req.getVlPrincipalNaoTributavelCorrigido());
        BigDecimal valorJuros = CalculoUtil.zeroSeNulo(req.getVlJurosAplicado());
        BigDecimal valorMultaCustasOutros = CalculoUtil.zeroSeNulo(req.getVlPagamentoMulta()).add(CalculoUtil.zeroSeNulo(req.getVlDevolucaoCusta()));
        BigDecimal valorSelic = CalculoUtil.zeroSeNulo(req.getVlSelic());

        BigDecimal totalValoresNaRequisicao =
                valorTributavel.add(valorNaoTributavel)
                        .add(valorJuros)
                        .add(valorMultaCustasOutros)
                        .add(valorSelic);

        BigDecimal selicTaxaSelicAntesGraca =
                temDataAtesGraca ?
                        UtilCalculo.escala(bancoCentralService.somarSelic(YearMonth.from(dataInicioAntesGraca),
                                YearMonth.from(
                                        dataFimAntesGraca.isAfter(LocalDate.now().minusMonths(1)) ? LocalDate.now().minusMonths(1) : dataFimAntesGraca
                                )), 7)
                        : ZERO;
        BigDecimal selicFatorIPCADuranteGraca =
                temDataDuranteGraca ?
                        bancoCentralService.multiplicarIPCA(YearMonth.from(dataInicioDuranteGraca), YearMonth.from(dataFimDuranteGraca))
                        : ZERO;
        BigDecimal selictTaxaSelicAposGraca =
                temDataAposGraca ?
                        UtilCalculo.escala(bancoCentralService.somarSelic(YearMonth.from(dataInicioPosGraca), YearMonth.from(dataFimPosGraca)), 7)
                        : ZERO;

        resultado.setSelicAntesGracaTaxa(temDataAtesGraca ? selicTaxaSelicAntesGraca : ZERO);
        resultado.setSelicDuranteGracaFatorIPCA(UtilCalculo.escala(selicFatorIPCADuranteGraca, 7));
        resultado.setSelicPosGracaTaxa(selictTaxaSelicAposGraca);

        // Calculo Selic Antes da graça
        BigDecimal valorSelicAntesGraca = !temDataAtesGraca ? BigDecimal.ZERO : totalValoresNaRequisicao
                .subtract(UtilCalculo.manterValorZeroSeNulo(req.getVlSelic()))
                .multiply(selicTaxaSelicAntesGraca)
                .divide(CEM);
        resultado.setSelicAntesGracaSelicValorCorrigido(UtilCalculo.escala(valorSelicAntesGraca, 2));

        // Calculo Selic Durante Graça
        BigDecimal valorSelicDurantePrincipalTributavel = UtilCalculo.isNotNullOrZero(selicFatorIPCADuranteGraca) ? valorTributavel.multiply(selicFatorIPCADuranteGraca) : valorTributavel;
        BigDecimal valorSelicDurantePrincipalNaoTributavel = UtilCalculo.isNotNullOrZero(selicFatorIPCADuranteGraca) ? valorNaoTributavel.multiply(selicFatorIPCADuranteGraca) : valorNaoTributavel;
        BigDecimal valorSelicDuranteJuros = UtilCalculo.isNotNullOrZero(selicFatorIPCADuranteGraca) ? valorJuros.multiply(selicFatorIPCADuranteGraca) : valorJuros;
        BigDecimal valorSelicDuranteMultaCusta = UtilCalculo.isNotNullOrZero(selicFatorIPCADuranteGraca) ? valorMultaCustasOutros.multiply(selicFatorIPCADuranteGraca) : valorMultaCustasOutros;
        BigDecimal valorSelicDuranteSelic = UtilCalculo.isNotNullOrZero(selicFatorIPCADuranteGraca) ? valorSelic.add(valorSelicAntesGraca).multiply(selicFatorIPCADuranteGraca) : valorSelic;
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
        BigDecimal valorSelicAposGraca = valorSelicTotalDuranteGranca
                .subtract(valorSelicDuranteSelic)
                .multiply(selictTaxaSelicAposGraca)
                .divide(CEM)
                .add(valorSelicDuranteSelic);
        BigDecimal valorTotalSelicAposGraca = valorSelicAposGraca.add(valorCalculoSemDuranteGraca).subtract(valorSelicDuranteSelic);
        resultado.setSelicPosGracaSelicValorCorrigido(UtilCalculo.escala(valorSelicAposGraca, 2));
        resultado.setSelicPosGracaTotalAtualizado(UtilCalculo.escala(valorTotalSelicAposGraca, 2));

    }


    // Funcionando
    @Deprecated
    public CalculoAtualizacaoDTO calcularAtualizacao(CalculoRequest req) {
        validateRequest(req);
        var atualizacao = new CalculoAtualizacaoDTO();

        // PERÍODOS base
        LocalDate dataFinalGraca = LocalDate.of(req.getAnoVencimento(), 12, 31);
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
            if (dataAtualizacao.isAfter(dataInicialGraca) && dataAtualizacao.isBefore(dataFinalGraca)) {
                dataInicioDuranteGraca = dataAtualizacao;
            } else {
                dataInicioDuranteGraca = dataInicialGraca;
            }
            dataFimDuranteGraca = dataFinalGraca;
        } else {
            if (dataAtualizacao.isBefore(dataFinalGraca) && temPeriodoAntesGraca) {
                if (dataFinalGraca.isAfter(dataHoje)) {
                    if (!dataInicialGraca.isAfter(dataHoje.minusMonths(1))) {
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
        if (dataHoje.isAfter(dataFinalGraca)) {
            if (dataAtualizacao.isAfter(dataFinalGraca)) {
                dataInicioPosGraca = dataAtualizacao;
            } else {
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
                        , 2)
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
                        , 2)
        );

        BigDecimal maiorValorIpca = atualizacao.getIpcaAntesGracaTotalAtualizado().max(atualizacao.getIpcaDuranteGracaTotalAtualizado())
                .max(atualizacao.getIpcaPosGracaTotalAtualizado());

        boolean ipcaZero = maiorValorIpca.compareTo(BigDecimal.ZERO) == 0;

        BigDecimal menor = ipcaZero ? atualizacao.getSelicPosGracaTotalAtualizado() : maiorValorIpca.min(atualizacao.getSelicPosGracaTotalAtualizado());
        String tipo = menor.equals(maiorValorIpca) ? "IPCA" : "SELIC";

        atualizacao.setResultadoValorBrutoAtualizado(UtilCalculo.escala(menor, 2));

        if (tipo.equals("IPCA")) {

            atualizacao.setResultadoValorPrincipalTributavelAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaPrincipalTributavelCorrigido(), 2));
            atualizacao.setResultadoValorPrincipalNaoTributavelAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaPrincipalNaoTributavelCorrigido(), 2));
            atualizacao.setResultadoValorJurosAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaValorJurosCorrigido(), 2));
            atualizacao.setResultadoValorMultaCustasOutrosAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaCustasMultaCorrigido(), 2));
            atualizacao.setResultadoValorSelicAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaSelicCorrigido(), 2));
            atualizacao.setResultadoValorBrutoAtualizado(UtilCalculo.escala(atualizacao.getIpcaPosGracaTotalAtualizado(), 2));
            atualizacao.setResultadoValorPrevidenciaAtualizado(UtilCalculo.escala(atualizacao.getIpcaValorPrevidenciaCorrigido(), 2));

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

        } else {
            atualizacao.setResultadoValorPrincipalTributavelAtualizado(UtilCalculo.escala(atualizacao.getSelicDuranteGracaPrincipalTributavelCorrigido(), 2));
            atualizacao.setResultadoValorPrincipalNaoTributavelAtualizado(UtilCalculo.escala(atualizacao.getSelicDuranteGracaPrincipalNaoTributavelCorrigido(), 2));
            atualizacao.setResultadoValorJurosAtualizado(UtilCalculo.escala(atualizacao.getSelicDuranteGracaValorJurosCorrigido(), 2));
            atualizacao.setResultadoValorMultaCustasOutrosAtualizado(UtilCalculo.escala(atualizacao.getSelicDuranteGracaCustasMultaCorrigido(), 2));
            atualizacao.setResultadoValorSelicAtualizado(UtilCalculo.escala(atualizacao.getSelicPosGracaSelicValorCorrigido(), 2));
            atualizacao.setResultadoValorBrutoAtualizado(UtilCalculo.escala(atualizacao.getSelicPosGracaTotalAtualizado(), 2));
            atualizacao.setResultadoValorPrevidenciaAtualizado(UtilCalculo.escala(atualizacao.getSelicValorPrevidenciaCorrigido(), 2));

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
            atualizacao.setResultadoValorSelicAtualizadoDizima(atualizacao.getSelicPosGracaSelicValorCorrigido());
            atualizacao.setResultadoValorBrutoAtualizadoDizima(atualizacao.getSelicPosGracaTotalAtualizado());
            atualizacao.setResultadoValorPrevidenciaAtualizadoDizima(atualizacao.getSelicValorPrevidenciaCorrigido());
        }

        atualizacao.setResultadoNumeroMesesRRA(CalculoUtil.calcularMesesPeriodo(req.getDataInicioRRA(), req.getDataFimRRA()));
        atualizacao.setResultadoCnpjDevedor(req.getCnpjDevedor());
        atualizacao.setTipoCalculoRetornado(tipo);

        return atualizacao;
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

    private Periodos calcularPeriodos(LocalDate dataAtualizacao, LocalDate dataInicialGraca, LocalDate dataFinalGraca, LocalDate dataHoje) {

        LocalDate antesInicio = null, antesFim = null;
        LocalDate duranteInicio = null, duranteFim = null;
        LocalDate depoisInicio = null, depoisFim = null;

        // Antes
        if (dataAtualizacao.isBefore(dataInicialGraca)) {
            antesInicio = dataAtualizacao;
            antesFim = dataInicialGraca.minusMonths(1);
        }

        // Durante
        boolean dentroDurante = !dataAtualizacao.isBefore(dataInicialGraca) && !dataAtualizacao.isAfter(dataFinalGraca);
        if (dentroDurante) {
            duranteInicio = dataAtualizacao.isAfter(dataInicialGraca) ? dataAtualizacao : dataInicialGraca;
            duranteFim = dataFinalGraca;
        } else if (dataAtualizacao.isBefore(dataFinalGraca) && dataFinalGraca.isAfter(dataHoje.minusMonths(1))) {
            duranteInicio = dataInicialGraca;
            duranteFim = dataHoje.minusMonths(1);
        }

        // Depois
        if (dataHoje.isAfter(dataFinalGraca)) {
            depoisInicio = dataAtualizacao.isAfter(dataFinalGraca) ? dataAtualizacao : dataFinalGraca.plusMonths(1);
            depoisFim = dataHoje;
        }

        return new Periodos(antesInicio, antesFim, duranteInicio, duranteFim, depoisInicio, depoisFim);
    }

    private void definirResultadoFinal(CalculoAtualizacaoDTO atualizacao, RequisitorioDTO req) {

        BigDecimal maiorIpca = Stream.of(
                atualizacao.getIpcaAntesGracaTotalAtualizado(),
                atualizacao.getIpcaDuranteGracaTotalAtualizado(),
                atualizacao.getIpcaPosGracaTotalAtualizado()
        ).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        boolean ipcaZero = maiorIpca.compareTo(BigDecimal.ZERO) == 0;
        BigDecimal menor = ipcaZero
                ? atualizacao.getSelicPosGracaTotalAtualizado()
                : maiorIpca.min(atualizacao.getSelicPosGracaTotalAtualizado());

        String tipo = menor.equals(maiorIpca) ? "IPCA" : "SELIC";
        atualizacao.setTipoCalculoRetornado(tipo);
        atualizacao.setResultadoValorBrutoAtualizado(UtilCalculo.escala(menor, 2));

        if ("IPCA".equals(tipo)) {
            preencherResultadoFinalIPCA(atualizacao);
        } else {
            preencherResultadoFinalSELIC(atualizacao);
        }

        atualizacao.setResultadoNumeroMesesRRA(CalculoUtil.calcularMesesPeriodo(req.getDtInicioRRA(), req.getDtFimRRA()));
        atualizacao.setResultadoCnpjDevedor(req.getDocumentoDevedor());
    }

    private void preencherResultadoFinalIPCA(CalculoAtualizacaoDTO atualizacao) {
        atualizacao.setResultadoValorPrincipalTributavelAtualizado(atualizacao.getIpcaPosGracaPrincipalTributavelCorrigido());
        atualizacao.setResultadoValorPrincipalNaoTributavelAtualizado(atualizacao.getIpcaPosGracaPrincipalNaoTributavelCorrigido());
        atualizacao.setResultadoValorJurosAtualizado(atualizacao.getIpcaPosGracaValorJurosCorrigido());
        atualizacao.setResultadoValorMultaCustasOutrosAtualizado(atualizacao.getIpcaPosGracaCustasMultaCorrigido());
        atualizacao.setResultadoValorSelicAtualizado(atualizacao.getIpcaPosGracaSelicCorrigido());
        atualizacao.setResultadoValorBrutoAtualizado(atualizacao.getIpcaPosGracaTotalAtualizado());
        atualizacao.setResultadoValorPrevidenciaAtualizado(atualizacao.getIpcaValorPrevidenciaCorrigido());

        atualizacao.setResultadoFatorAntesAtualizado(atualizacao.getIpcaAntesGracaFator());
        atualizacao.setResultadoFatorDuranteAtualizado(atualizacao.getIpcaDuranteGracaFator());
        atualizacao.setResultadoFatorDepoisAtualizado(atualizacao.getIpcaPosGracaFator());

        atualizacao.setResultadoTaxaAntesAtualizado(atualizacao.getIpcaAntesGracaFatorJuros());
        atualizacao.setResultadoTaxaDuranteAtualizado(atualizacao.getIpcaDuranteGracaFatorJuros());
        atualizacao.setResultadoTaxaDepoisAtualizado(atualizacao.getIpcaPosGracaFatorJuros());

        //atualizacao.setResultadoValorPrincipalTributavelAtualizadoDizima(atualizacao.getIpcaPosGracaPrincipalTributavelCorrigido());
        //atualizacao.setResultadoValorPrincipalNaoTributavelAtualizadoDizima(atualizacao.getIpcaPosGracaPrincipalNaoTributavelCorrigido());
        //atualizacao.setResultadoValorJurosAtualizadoDizima(atualizacao.getIpcaPosGracaValorJurosCorrigido());
        //atualizacao.setResultadoValorMultaCustasOutrosAtualizadoDizima(atualizacao.getIpcaPosGracaCustasMultaCorrigido());
        //atualizacao.setResultadoValorSelicAtualizadoDizima(atualizacao.getIpcaPosGracaSelicCorrigido());
        //atualizacao.setResultadoValorBrutoAtualizadoDizima(atualizacao.getIpcaPosGracaTotalAtualizado());
        //atualizacao.setResultadoValorPrevidenciaAtualizadoDizima(atualizacao.getIpcaValorPrevidenciaCorrigido());
    }

    private void preencherResultadoFinalSELIC(CalculoAtualizacaoDTO atualizacao) {
        atualizacao.setResultadoValorPrincipalTributavelAtualizado(atualizacao.getSelicDuranteGracaPrincipalTributavelCorrigido());
        atualizacao.setResultadoValorPrincipalNaoTributavelAtualizado(atualizacao.getSelicDuranteGracaPrincipalNaoTributavelCorrigido());
        atualizacao.setResultadoValorJurosAtualizado(atualizacao.getSelicDuranteGracaValorJurosCorrigido());
        atualizacao.setResultadoValorMultaCustasOutrosAtualizado(atualizacao.getSelicDuranteGracaCustasMultaCorrigido());
        atualizacao.setResultadoValorSelicAtualizado(atualizacao.getSelicPosGracaSelicValorCorrigido());
        atualizacao.setResultadoValorBrutoAtualizado(atualizacao.getSelicPosGracaTotalAtualizado());
        atualizacao.setResultadoValorPrevidenciaAtualizado(atualizacao.getSelicValorPrevidenciaCorrigido());

        atualizacao.setResultadoFatorAntesAtualizado(BigDecimal.valueOf(1.0000000));
        atualizacao.setResultadoFatorDuranteAtualizado(atualizacao.getSelicDuranteGracaFatorIPCA());
        atualizacao.setResultadoFatorDepoisAtualizado(BigDecimal.valueOf(1.0000000));

        atualizacao.setResultadoTaxaAntesAtualizado(atualizacao.getSelicAntesGracaTaxa());
        atualizacao.setResultadoTaxaDuranteAtualizado(BigDecimal.ZERO);
        atualizacao.setResultadoTaxaDepoisAtualizado(atualizacao.getSelicPosGracaTaxa());

        //atualizacao.setResultadoValorPrincipalTributavelAtualizadoDizima(atualizacao.getSelicDuranteGracaPrincipalTributavelCorrigido());
        //atualizacao.setResultadoValorPrincipalNaoTributavelAtualizadoDizima(atualizacao.getSelicDuranteGracaPrincipalNaoTributavelCorrigido());
        //atualizacao.setResultadoValorJurosAtualizadoDizima(atualizacao.getSelicDuranteGracaValorJurosCorrigido());
        //atualizacao.setResultadoValorMultaCustasOutrosAtualizadoDizima(atualizacao.getSelicDuranteGracaCustasMultaCorrigido());
        //atualizacao.setResultadoValorSelicAtualizadoDizima(atualizacao.getSelicPosGracaSelicValorCorrigido());
        //atualizacao.setResultadoValorBrutoAtualizadoDizima(atualizacao.getSelicPosGracaTotalAtualizado());
        //atualizacao.setResultadoValorPrevidenciaAtualizadoDizima(atualizacao.getSelicValorPrevidenciaCorrigido());
    }


    // Nova implementação
    private List<CorrecaoDTO> calcularPeriodoAteNovembro2021(RequisitorioDTO requisitorioDTO) {

        final var dataInicioAtualizacao = requisitorioDTO.getDtUltimaAtualizacaoPlanilha().plusMonths(1);
        final var periodo = PeriodoGracaCalculator.calcularPeriodoAntesNovembro2021(
                dataInicioAtualizacao,
                requisitorioDTO.getAnoVencimento()
        );

        // Adiciona os valores do calculo para o metodo calcularPeriodoBasico
        List<CorrecaoDTO> periodosCalculados = new ArrayList<>();
        CorrecaoDTO requisitorio = new CorrecaoDTO("Primeiro", null, null, null, null,
                UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrincipalTributavelCorrigido()),
                UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrincipalNaoTributavelCorrigido()),
                UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlJurosAplicado()),
                UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlDevolucaoCusta()).add(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPagamentoMulta())),
                UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlSelic()),
                UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrevidencia()),
                null);

        if (periodo.isNulo()) {
            periodosCalculados.add(requisitorio);
            return periodosCalculados;
        }

        CorrecaoDTO calculoAnterior = new CorrecaoDTO();

        // Antes da Graça
        if (periodo.inicioAntes() != null && periodo.fimAntes() != null) {
            var primeiro = calcularPeriodoAteNovembro2021(requisitorio, periodo.inicioAntes(), periodo.fimAntes());
            periodosCalculados.add(primeiro);
            calculoAnterior = primeiro;
        } else {
            calculoAnterior = requisitorio;
        }

        // Durante a Graça
        if (periodo.inicioDurante() != null && periodo.fimDurante() != null) {
            var durante = calcularPeriodoAteNovembro2021(calculoAnterior, periodo.inicioDurante(), periodo.fimDurante());
            periodosCalculados.add(durante);
            calculoAnterior = durante;
        } else {
            calculoAnterior = requisitorio;
        }

        // Após a Graça
        if (periodo.inicioApos() != null && periodo.fimApos() != null) {
            var apos = calcularPeriodoAteNovembro2021(calculoAnterior, periodo.inicioApos(), periodo.fimApos());
            periodosCalculados.add(apos);
            calculoAnterior = apos;
        }

        return periodosCalculados;
    }

    private CorrecaoDTO calcularPeriodoAteNovembro2021(CorrecaoDTO correcaoRequisitorio, LocalDate inicio, LocalDate fim) {

        BigDecimal fator = BigDecimal.ONE;
        BigDecimal juros = BigDecimal.ZERO;

        if (inicio != null && fim != null) {

            fator = bancoCentralService.multiplicarIPCAE(YearMonth.from(inicio), YearMonth.from(fim));
            juros = bancoCentralService.somarPoupanca(YearMonth.from(inicio), YearMonth.from(fim))
                    .divide(BigDecimal.valueOf(100));
        }

        var correcao = new CorrecaoDTO();
        correcao.setDataInicio(inicio);
        correcao.setDataFim(fim);
        correcao.setFatorValor(fator);
        correcao.setJurosValor(juros);

        correcao.setPrincipalTributavel(correcaoRequisitorio.getPrincipalTributavel().multiply(fator));
        correcao.setPrincipalNaoTributavel(correcaoRequisitorio.getPrincipalNaoTributavel().multiply(fator));
        correcao.setJuros(
                correcaoRequisitorio.getJuros().multiply(fator)
                        .add(correcao.getPrincipalTributavel().add(correcao.getPrincipalNaoTributavel())
                                .multiply(juros))
        );
        correcao.setMultaCusta(correcaoRequisitorio.getMultaCusta().multiply(fator));
        correcao.setSelic(correcaoRequisitorio.getSelic().multiply(fator));
        correcao.setTotal(correcao.getTotal());

        correcao.setPrevidencia(correcaoRequisitorio.getPrevidencia().multiply(fator));

        return correcao;
    }

    private List<CorrecaoDTO> calcularPeriodoEntreDezembro2021AhJulho2025(RequisitorioDTO requisitorioDTO, CorrecaoDTO requisitorio) {

        final var dataInicioAtualizacao = requisitorioDTO.getDtUltimaAtualizacaoPlanilha();
        final var periodo = PeriodoGracaCalculator.calcularPeriodoEntreDezembro2021AhJulho2025(
                dataInicioAtualizacao,
                requisitorioDTO.getDtFimAtualizacaoPlanilha(),
                requisitorioDTO.getAnoVencimento()
        );

        // Adiciona os valores do calculo para o metodo calcularPeriodoBasico
        List<CorrecaoDTO> periodosCalculados = new ArrayList<>();

        if (periodo.isNulo()) {
            periodosCalculados.add(requisitorio);
            return periodosCalculados;
        }

        CorrecaoDTO calculoAnterior = new CorrecaoDTO();

        // Antes da Graça
        if (periodo.inicioAntes() != null && periodo.fimAntes() != null) {
            var primeiro = calcularPeriodoEntreDezembro2021AhJulho2025(requisitorio, periodo.inicioAntes(), periodo.fimAntes(), false);
            primeiro.setTipoCalculo("Segundo");
            periodosCalculados.add(primeiro);
            calculoAnterior = primeiro;
        } else {
            calculoAnterior = requisitorio;
        }

        // Durante a Graça
        if (periodo.inicioDurante() != null && periodo.fimDurante() != null) {
            var durante = calcularPeriodoEntreDezembro2021AhJulho2025(calculoAnterior, periodo.inicioDurante(), periodo.fimDurante(), true);
            durante.setTipoCalculo("Segundo");
            periodosCalculados.add(durante);
            calculoAnterior = durante;
        } else {
            calculoAnterior = requisitorio;
        }

        // Após a Graça
        if (periodo.inicioApos() != null && periodo.fimApos() != null) {
            var apos = calcularPeriodoEntreDezembro2021AhJulho2025(calculoAnterior, periodo.inicioApos(), periodo.fimApos(), false);
            apos.setTipoCalculo("Segundo");
            periodosCalculados.add(apos);
            calculoAnterior = apos;
        }

        return periodosCalculados;
    }

    private CorrecaoDTO calcularPeriodoEntreDezembro2021AhJulho2025(CorrecaoDTO correcaoRequisitorio, LocalDate inicio, LocalDate fim, boolean duranteGraca) {

        BigDecimal fator = BigDecimal.ONE;
        BigDecimal taxaSelic = BigDecimal.ZERO;

        if (inicio != null && fim != null) {
            if (duranteGraca) {
                fator = bancoCentralService.multiplicarIPCAE(YearMonth.from(inicio), YearMonth.from(fim));
            }
            if (!duranteGraca) {
                taxaSelic = bancoCentralService.somarSelic(YearMonth.from(inicio), YearMonth.from(fim));
            }
        }

        var correcao = new CorrecaoDTO();
        correcao.setDataInicio(inicio);
        correcao.setDataFim(fim);
        correcao.setFatorValor(fator);
        correcao.setJurosValor(taxaSelic);

        correcao.setPrincipalTributavel(correcaoRequisitorio.getPrincipalTributavel().multiply(fator));
        correcao.setPrincipalNaoTributavel(correcaoRequisitorio.getPrincipalNaoTributavel().multiply(fator));
        correcao.setJuros(correcaoRequisitorio.getJuros().multiply(fator));
        correcao.setMultaCusta(correcaoRequisitorio.getMultaCusta().multiply(fator));

        var selic = BigDecimal.ZERO;
        if (duranteGraca) {
            selic = correcaoRequisitorio.getSelic().multiply(fator);
        } else {
            selic = correcaoRequisitorio.getTotal().subtract(correcaoRequisitorio.getSelic()).multiply(taxaSelic.divide(BigDecimal.valueOf(100))).add(correcaoRequisitorio.getSelic());
        }

        correcao.setSelic(selic);

        correcao.setTotal(correcao.getTotal());

        correcao.setPrevidencia(correcaoRequisitorio.getPrevidencia().multiply(fator));

        return correcao;
    }

    private List<CorrecaoDTO> calcularPeriodoAposAgosto2025(RequisitorioDTO requisitorioDTO, CorrecaoDTO requisitorio) {

        final var dataInicioAtualizacao = requisitorioDTO.getDtUltimaAtualizacaoPlanilha().plusMonths(1);
        final var periodo = PeriodoGracaCalculator.calcularPeriodoAposAgosto2025(
                dataInicioAtualizacao,
                requisitorioDTO.getDtFimAtualizacaoPlanilha(),
                requisitorioDTO.getAnoVencimento()
        );

        // Adiciona os valores do calculo para o metodo calcularPeriodoBasico
        List<CorrecaoDTO> periodosCalculados = new ArrayList<>();

        if (periodo.isNulo()) {
            periodosCalculados.add(requisitorio);
            return periodosCalculados;
        }

        CorrecaoDTO calculoAnterior = new CorrecaoDTO();

        // Antes da Graça
        if (periodo.inicioAntes() != null && periodo.fimAntes() != null) {
            var primeiroIpca = calcularPeriodoAposAgosto2025(requisitorio, periodo.inicioAntes(), periodo.fimAntes(), TipoCalculo.IPCA, false);
            var primeiroSelic = calcularPeriodoAposAgosto2025(requisitorio, periodo.inicioAntes(), periodo.fimAntes(), TipoCalculo.SELIC, false);
            periodosCalculados.add(primeiroIpca);
            periodosCalculados.add(primeiroSelic);
            calculoAnterior = primeiroIpca;
        } else {
            calculoAnterior = requisitorio;
        }

        // Durante a Graça
        if (periodo.inicioDurante() != null && periodo.fimDurante() != null) {
            var duranteIpca = calcularPeriodoAposAgosto2025(calculoAnterior, periodo.inicioDurante(), periodo.fimDurante(), TipoCalculo.IPCA, true);
            var duranteSelic = calcularPeriodoAposAgosto2025(calculoAnterior, periodo.inicioDurante(), periodo.fimDurante(), TipoCalculo.SELIC, true);
            periodosCalculados.add(duranteIpca);
            periodosCalculados.add(duranteSelic);
            calculoAnterior = duranteIpca;
        } else {
            calculoAnterior = requisitorio;
        }

        // Após a Graça
        if (periodo.inicioApos() != null && periodo.fimApos() != null) {
            var aposIpca = calcularPeriodoAposAgosto2025(calculoAnterior, periodo.inicioApos(), periodo.fimApos(), TipoCalculo.IPCA, false);
            var aposSelic = calcularPeriodoAposAgosto2025(calculoAnterior, periodo.inicioApos(), periodo.fimApos(), TipoCalculo.SELIC, false);
            periodosCalculados.add(aposIpca);
            periodosCalculados.add(aposSelic);
            calculoAnterior = aposIpca;
        }

        return periodosCalculados;
    }

    private CorrecaoDTO calcularPeriodoAposAgosto2025(CorrecaoDTO correcaoRequisitorio, LocalDate inicio, LocalDate fim, TipoCalculo tipoCalculo, boolean durante) {

        BigDecimal fator = BigDecimal.ONE;
        BigDecimal juros = BigDecimal.ZERO;


        if (inicio != null && fim != null && tipoCalculo.equals(TipoCalculo.IPCA)) {
            fator = BigDecimal.valueOf( 1.0025907); //bancoCentralService.multiplicarIPCA(YearMonth.from(inicio), YearMonth.from(fim));
            long totalMesesJurosPoupanca = UtilCalculo.calcularPeriodoMeses(inicio, fim);
            if (!durante) {
                juros = FATOR_DOIS_PORCENTO.multiply(BigDecimal.valueOf(totalMesesJurosPoupanca));
            }
        }

        if (inicio != null && fim != null && tipoCalculo.equals(TipoCalculo.SELIC)) {
            if (durante) {
                fator = bancoCentralService.multiplicarIPCA(YearMonth.from(inicio), YearMonth.from(fim));
            }
            if (!durante) {
                juros = bancoCentralService.somarSelic(YearMonth.from(inicio), YearMonth.from(fim));
            }
        }

        var correcao = new CorrecaoDTO();
        correcao.setTipoCalculo(tipoCalculo.descricao);
        correcao.setDataInicio(inicio);
        correcao.setDataFim(fim);
        correcao.setFatorValor(fator);
        correcao.setJurosValor(juros);

        correcao.setPrincipalTributavel(correcaoRequisitorio.getPrincipalTributavel().multiply(fator));
        correcao.setPrincipalNaoTributavel(correcaoRequisitorio.getPrincipalNaoTributavel().multiply(fator));
        //=(AB35*E16)+((G16+I16)*F16)/100
        var jurosCorrigido = BigDecimal.ZERO;
        var selicCorrigido = BigDecimal.ZERO;

        if (tipoCalculo.equals(TipoCalculo.SELIC)) {
            jurosCorrigido = correcaoRequisitorio.getJuros().multiply(fator);
            if(durante){
                selicCorrigido = correcaoRequisitorio.getSelic().multiply(fator);
            } else {
                selicCorrigido = correcaoRequisitorio.getTotal().subtract(correcaoRequisitorio.getSelic()).multiply(juros.divide(BigDecimal.valueOf(100))).add(correcaoRequisitorio.getSelic());
            }
        } else {

            selicCorrigido = correcaoRequisitorio.getSelic().multiply(fator);
            if(durante){
                jurosCorrigido = correcaoRequisitorio.getJuros().multiply(fator);
            } else {
            jurosCorrigido = correcaoRequisitorio.getJuros().multiply(fator)
                    .add(
                            correcao.getPrincipalTributavel().add(correcao.getPrincipalNaoTributavel())
                                    .multiply(juros).divide(BigDecimal.valueOf(100))
                    );
            }
        }

        correcao.setJuros(jurosCorrigido);
        correcao.setMultaCusta(correcaoRequisitorio.getMultaCusta().multiply(fator));
        correcao.setSelic(selicCorrigido);
        correcao.setTotal(correcao.getTotal());
        correcao.setPrevidencia(correcaoRequisitorio.getPrevidencia().multiply(fator));

        return correcao;
    }

    private record Periodos(
            LocalDate antesInicio, LocalDate antesFim,
            LocalDate duranteInicio, LocalDate duranteFim,
            LocalDate depoisInicio, LocalDate depoisFim
    ) {
    }

    private enum TipoCalculo {
        IPCA("IPCA"),
        IPCA_E("IPCA-E"),
        SELIC("SELIC"),
        POUCANCA("POUCANCA");

        private final String descricao;

        TipoCalculo(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

}