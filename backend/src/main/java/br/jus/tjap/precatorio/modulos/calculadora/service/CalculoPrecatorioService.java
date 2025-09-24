package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.apibancocentral.BancoCentralService;
import br.jus.tjap.precatorio.modulos.calculadora.dto.*;
import br.jus.tjap.precatorio.modulos.calculadora.entity.TabelaIRRF;
import br.jus.tjap.precatorio.modulos.calculadora.exception.CalculationException;
import br.jus.tjap.precatorio.modulos.calculadora.exception.IndexNotFoundException;
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
    private static final int ESCALA_FATOR = 6;
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

    public static long contarMesesInclusivos(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Datas não podem ser nulas");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim não pode ser anterior à data início");
        }

        // diferença em meses (exclusiva)
        long diffMeses = ChronoUnit.MONTHS.between(
                dataInicio.withDayOfMonth(1),
                dataFim.withDayOfMonth(1)
        );

        // +1 para incluir o mês inicial
        return diffMeses + 1;
    }

    public static void main(String[] args) {
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fim = LocalDate.of(2025, 8, 1);

        long meses = contarMesesInclusivos(inicio, fim);
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
            boolean isFatorEscalaOito,
            boolean isMesMaisUm
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

            long totalMeses = contarMesesInclusivos(dataInicio, dataFim);

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
            CalculoRetornoDTO resultado
    ) {

        boolean temDataAtesGraca = !Objects.isNull(dataInicioAntesGraca);
        boolean temDataDuranteGraca = !Objects.isNull(dataInicioDuranteGraca);
        boolean temDataAposGraca = !Objects.isNull(dataInicioPosGraca);

        BigDecimal totalValoresNaRequisicao =
                req.getValorPrincipalTributavel().add(req.getValorPrincipalNaoTributavel())
                        .add(req.getValorJurosTributavel().add(req.getValorJurosNaoTributavel()))
                        .add(req.getMulta().add(req.getCustas()).add(req.getOutrosReembolsos()))
                        .add(req.getValorSelicJuros());

        BigDecimal selicTaxaSelicAntesGraca =
                temDataAtesGraca ?
                        bancoCentralService.somarSelic(YearMonth.from(dataInicioAntesGraca), YearMonth.from(dataFimAntesGraca))
                        : UM;
        BigDecimal selicFatorIPCADuranteGraca =
                temDataDuranteGraca ?
                        bancoCentralService.multiplicarIPCA(YearMonth.from(dataInicioDuranteGraca), YearMonth.from(dataFimDuranteGraca))
                        : ZERO;
        BigDecimal selictTaxaSelicAposGraca =
                temDataAposGraca ?
                        bancoCentralService.somarSelic(YearMonth.from(dataInicioPosGraca), YearMonth.from(dataFimPosGraca))
                        : UM;

        resultado.setSelicAntesGracaTaxa(temDataAtesGraca ? selicTaxaSelicAntesGraca : ZERO);
        resultado.setSelicDuranteGracaFatorIPCA(UtilCalculo.escala(selicFatorIPCADuranteGraca, 7));
        resultado.setSelicPosGracaTaxa(selictTaxaSelicAposGraca);

        // Calculo Selic Antes da graça
        BigDecimal valorSelicAntesGraca = !temDataAtesGraca ? BigDecimal.ZERO : totalValoresNaRequisicao
                .subtract(req.getValorSelicJuros())
                .multiply(selicTaxaSelicAntesGraca)
                .divide(CEM);
        resultado.setSelicAntesGracaSelicValorCorrigido(UtilCalculo.escala(valorSelicAntesGraca, 2));

        // Calculo Selic Durante Graça
        BigDecimal valorSelicDurantePrincipalTributavel = req.getValorPrincipalTributavel().multiply(selicFatorIPCADuranteGraca);
        BigDecimal valorSelicDurantePrincipalNaoTributavel = req.getValorPrincipalNaoTributavel().multiply(selicFatorIPCADuranteGraca);
        BigDecimal valorSelicDuranteJuros = req.getValorJurosTributavel().add(req.getValorJurosNaoTributavel()).multiply(selicFatorIPCADuranteGraca);
        BigDecimal valorSelicDuranteMultaCusta = req.getCustas().add(req.getMulta()).add(req.getOutrosReembolsos()).multiply(selicFatorIPCADuranteGraca);
        BigDecimal valorSelicDuranteSelic = req.getValorSelicJuros().add(valorSelicAntesGraca).multiply(selicFatorIPCADuranteGraca);
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
                .divide(CEM);
        BigDecimal valorTotalSelicAposGraca = valorSelicAposGraca.add(valorCalculoSemDuranteGraca);
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

    public CalculoRetornoDTO calcularPagamento(CalculoRetornoDTO atualizacao, boolean temHC){

        // Constantes
        final BigDecimal PERCENTUAL_HC = new BigDecimal("0.10");
        final BigDecimal REGIME_TRIBUTACAO_PJ = new BigDecimal("1.5");
        // Percentuais e valores iniciais
        BigDecimal percentualHC = temHC ? PERCENTUAL_HC : BigDecimal.ZERO;

        BigDecimal totalMesesRRA = BigDecimal.TEN;// BigDecimal.valueOf(
        //contarMesesInclusivos(LocalDate.of(2024, 7, 1), LocalDate.of(2025, 9, 1))
        //);

        BigDecimal ipcaTotalAtualizado = UtilCalculo.coalesceBigDecimal(
                atualizacao.getIpcaAntesGracaTotalAtualizado(),
                atualizacao.getIpcaDuranteGracaTotalAtualizado(),
                atualizacao.getIpcaPosGracaTotalAtualizado()
        );

        // Determinar valores tributáveis e não tributáveis
        BigDecimal valorTributavelBase;
        BigDecimal valorNaoTributavelBase;

        switch (atualizacao.getTipoCalculoRetornado()) {
            case "IPCA" -> {
                valorTributavelBase = UtilCalculo.coalesceBigDecimal(
                        atualizacao.getIpcaAntesGracaPrincipalTributavelCorrigido(),
                        atualizacao.getIpcaDuranteGracaPrincipalTributavelCorrigido(),
                        atualizacao.getIpcaPosGracaPrincipalTributavelCorrigido()
                );
                valorNaoTributavelBase = ipcaTotalAtualizado.subtract(valorTributavelBase);
            }
            case "SELIC" -> {
                valorTributavelBase = atualizacao.getSelicDuranteGracaPrincipalTributavelCorrigido();
                valorNaoTributavelBase = atualizacao.getSelicPosGracaTotalAtualizado()
                        .subtract(valorTributavelBase);
            }
            default -> {
                // Se não for IPCA nem SELIC, devolve sem alterações
                return atualizacao;
            }
        }

        // Regime tributação advogado / credor
        BigDecimal valorRegimeTributacaoHC = BigDecimal.ZERO;
        String tipoTributacao = "PF";//atualizacao.getRequisitorioDTO().getIdTipoTributacaoAdvCredor();

        if ("PJ".equals(tipoTributacao)) {
            valorRegimeTributacaoHC = REGIME_TRIBUTACAO_PJ;
        } else if ("PF".equals(tipoTributacao)) {
            List<TabelaIRRF> tabelaIrrf = tabelaIRRFRepository.findAll();
            // Mapeamento usando totalMesesRRA
            List<TabelaIRRFDTO> tabelaDTO = tabelaIrrf.stream()
                    .map(t -> t.toCalculoCorrigido(totalMesesRRA))
                    .toList();

            var total = tabelaDTO.get(3);
            valorRegimeTributacaoHC = valorRegimeTributacaoHC.add(total.getValorAliquotaCalculado());
        }



        // Cálculo do HC
        BigDecimal valorHCSobreAtualizacao =
                atualizacao.getValorGlobalAtualizado().multiply(percentualHC);

        BigDecimal valorParteCredorRetiradoHC =
                atualizacao.getValorGlobalAtualizado().subtract(valorHCSobreAtualizacao);

        // Atualiza o DTO conforme necessidade (exemplo fictício)
        atualizacao.setIrrfValorHCLiquido(valorRegimeTributacaoHC);
        //atualizacao.setValorParteCredorRetiradoHC(valorParteCredorRetiradoHC);
        //atualizacao.setValorRegimeTributacaoHC(valorRegimeTributacaoHC);
        //atualizacao.setValorTributavelBase(valorTributavelBase);
        //atualizacao.setValorNaoTributavelBase(valorNaoTributavelBase);

        return atualizacao;
    }

    public CalculoRetornoDTO calcularAtualizacao(CalculoRequest req) {
        validateRequest(req);
        var resultado = new CalculoRetornoDTO();
        RequisitorioDTO dto = requisitorioRepository.findById(1335L).get().toMetadado();
        resultado.setRequisitorioDTO(dto);

        // PERÍODOS base
        LocalDate dataFinalGraca = LocalDate.of(req.getAnoVencimento(),12,31);
        LocalDate dataInicialGraca = UtilCalculo.calculaDataIncioGraca(req.getAnoVencimento(), dataFinalGraca);
        LocalDate dataAtualizacao = req.getDataUltimaAtualizacao().plusMonths(1);
        LocalDate dataHoje = LocalDate.now();

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
                    dataInicioDuranteGraca = dataInicialGraca;
                    dataFimDuranteGraca = dataHoje.minusMonths(1);
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
                req.getValorJurosTributavel().add(req.getValorJurosNaoTributavel()),
                req.getCustas().add(req.getMulta()).add(req.getOutrosReembolsos()),
                req.getValorSelicJuros(),
                true,
                true,
                true
        );
        resultado.preencherIpcaAntes(antesGraca);

        PeriodoResultado duranteGraca = calcularPeriodoIPCA(
                dataInicioDuranteGraca,
                dataFimDuranteGraca,
                resultado.getIpcaAntesGracaPrincipalTributavelCorrigido(),
                resultado.getIpcaAntesGracaPrincipalNaoTributavelCorrigido(),
                resultado.getIpcaAntesGracaValorJurosCorrigido(),
                resultado.getIpcaAntesGracaCustasMultaCorrigido(),
                resultado.getIpcaAntesGracaSelicCorrigido(),
                false,
                false,
                false
        );
        resultado.preencherIpcaDurante(duranteGraca);


        PeriodoResultado duranteApos = calcularPeriodoIPCA(
                dataInicioPosGraca,
                dataFimPosGraca,
                resultado.getIpcaDuranteGracaPrincipalTributavelCorrigido(),
                resultado.getIpcaDuranteGracaPrincipalNaoTributavelCorrigido(),
                resultado.getIpcaDuranteGracaValorJurosCorrigido(),
                resultado.getIpcaDuranteGracaCustasMultaCorrigido(),
                resultado.getIpcaDuranteGracaSelicCorrigido(),
                true,
                false,
                false
        );
        resultado.preencherIpcaDepois(duranteApos);

        // calculo SELIC
        calcularPeriodoSelic(
                dataInicioAntesGraca,
                dataFimAntesGraca,
                dataInicioDuranteGraca,
                dataFimDuranteGraca,
                dataInicioPosGraca,
                dataFimPosGraca,
                req,
                resultado
        );

        BigDecimal maiorValorIpca = resultado.getIpcaAntesGracaTotalAtualizado().max(resultado.getIpcaDuranteGracaTotalAtualizado())
                .max(resultado.getIpcaPosGracaTotalAtualizado());

        boolean ipcaZero = maiorValorIpca.compareTo(BigDecimal.ZERO) == 0;

        BigDecimal menor = ipcaZero ? resultado.getSelicPosGracaTotalAtualizado() : maiorValorIpca.min(resultado.getSelicPosGracaTotalAtualizado());
        String tipo = menor.equals(maiorValorIpca) ? "IPCA" : "SELIC";

        resultado.setValorGlobalAtualizado(UtilCalculo.escala(menor, 2));
        resultado.setTipoCalculoRetornado(tipo);

        resultado = calcularPagamento(resultado, true);

        return resultado;
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



    @Deprecated
    public CalculoResponse calcularAntigo(CalculoRequest req) {
        try {
            validateRequest(req);
            var resultado = new CalculoResponse();

            // PERÍODOS base
            YearMonth ultimaAtualizacaoYm = YearMonth.from(req.getDataUltimaAtualizacao());
            YearMonth periodoInicio = ultimaAtualizacaoYm.plusMonths(1);

            // --- Pré-EC113 (até nov/2021) ---
            // calcular fatores/juros apenas uma vez (evita chamadas repetidas)
            BigDecimal fatorCorrecao = bancoCentralService.multiplicarIPCAE(periodoInicio, EC113_DATA_CORTE);
            BigDecimal percJurosPeriodo = bancoCentralService.somarPoupanca(periodoInicio, EC113_DATA_CORTE);

            // --- Período de graça ---
            LocalDate inscricao = UtilCalculo.obterDataInscricao(req.getAnoVencimento());
            YearMonth inicioGraca = YearMonth.from(inscricao);
            YearMonth candidateFimGraca = inicioGraca.plusMonths(18);
            YearMonth fimGraca = candidateFimGraca.isBefore(EC113_DATA_CORTE) ? candidateFimGraca : EC113_DATA_CORTE;

            // Define as datas para calculo
            resultado.setDataInicial(periodoInicio);
            resultado.setDataFinal(fimGraca);

            BigDecimal percJurosPeriodoGraca = ZERO;
            if (req.getAnoVencimento() <= 2022 && !inicioGraca.isAfter(fimGraca)) {
                percJurosPeriodoGraca = bancoCentralService.somarPoupanca(inicioGraca, fimGraca);
            }

            BigDecimal percJurosFinal = percJurosPeriodo.subtract(percJurosPeriodoGraca);

            // --- Aplicar fator de correção e calcular atualizações iniciais ---
            BigDecimal principalTribAtual = UtilCalculo.calculaAtualizacaoJuros(req.getValorPrincipalTributavel(), fatorCorrecao);
            BigDecimal principalNaoTribAtual = UtilCalculo.calculaAtualizacaoJuros(req.getValorPrincipalNaoTributavel(), fatorCorrecao);
            BigDecimal jurosTribAtual = UtilCalculo.calculaAtualizacaoJuros(req.getValorJurosTributavel(), fatorCorrecao);
            BigDecimal jurosNaoTribAtual = UtilCalculo.calculaAtualizacaoJuros(req.getValorJurosNaoTributavel(), fatorCorrecao);

            // Custas/Multas/Outros
            BigDecimal somaCustas = UtilCalculo.manterValorZeroSeNulo(req.getCustas())
                    .add(UtilCalculo.manterValorZeroSeNulo(req.getMulta()))
                    .add(UtilCalculo.manterValorZeroSeNulo(req.getOutrosReembolsos()));

            BigDecimal custasAtualizadas = UtilCalculo.calculaAtualizacaoJuros(somaCustas, fatorCorrecao);

            BigDecimal totalPrincipalAtualizado = principalTribAtual.add(principalNaoTribAtual);
            BigDecimal totalJurosAtualizado = jurosTribAtual.add(jurosNaoTribAtual);

            String tipoCalculoUsado = periodoInicio.isAfter(EC113_DATA_CORTE) ? "SELIC" : "IPCA";

            BigDecimal totalComJurosTributavelNoPeriodo = req.getTipoNaturezaRenda().equals("RRA") ?
                    ZERO :
                    percJurosFinal.multiply(principalTribAtual).divide(CEM, 10, RoundingMode.HALF_UP);

            BigDecimal totalComJurosNaoTributavelNoPeriodo = req.getTipoNaturezaRenda().equals("RRA") ?
                    totalPrincipalAtualizado.multiply(percJurosFinal).divide(CEM) :
                    principalNaoTribAtual.multiply(percJurosFinal).divide(CEM);


            BigDecimal totaisJurosTributavel = jurosTribAtual.add(totalComJurosTributavelNoPeriodo);
            BigDecimal totaisJurosNaoTributavel = jurosNaoTribAtual.add(totalComJurosNaoTributavelNoPeriodo);
            BigDecimal totaisJurosAplicado = totaisJurosTributavel.add(totaisJurosNaoTributavel);
            BigDecimal totalAntesEc = totalPrincipalAtualizado.add(totaisJurosAplicado.add(custasAtualizadas))
                    .setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP);

            // --- pós-EC113 (depois de nov/2021) ---
            resultado = calcularAtualizacaoEC113(
                    principalTribAtual,
                    principalNaoTribAtual,
                    custasAtualizadas,
                    req.getDataUltimaAtualizacao(),
                    req.getAnoVencimento(),
                    inscricao,
                    resultado,
                    totaisJurosTributavel,
                    totaisJurosNaoTributavel,
                    req.getValorSelicPrincipal(),
                    req.getValorSelicJuros()
            );

            // --- Preencher campos finais da resposta (com formatações de escala) ---
            resultado.setNumeroProcesso(req.getNumeroProcesso());
            resultado.setTipoJurosPrecatorio(tipoCalculoUsado);
            resultado.setFatorCorrecaoMonetaria(fatorCorrecao.setScale(ESCALA_INDICE, RoundingMode.HALF_UP));
            resultado.setPercJurosPeriodo(percJurosPeriodo.setScale(ESCALA_TAXA, RoundingMode.HALF_UP));
            resultado.setPercJurosPeriodoDeGraca(percJurosPeriodoGraca.setScale(ESCALA_TAXA, RoundingMode.HALF_UP));
            resultado.setPercJurosPrincipal(percJurosFinal.setScale(ESCALA_TAXA, RoundingMode.HALF_UP));

            resultado.setPrincipalTributavelCorrigido(principalTribAtual);
            resultado.setPrincipalNaoTributavelCorrigido(principalNaoTribAtual);

            resultado.setJurosTributavelCorrigido(jurosTribAtual);
            resultado.setJurosNaoTributavelCorrigido(jurosNaoTribAtual);

            resultado.setJurosPeriodoTributavel(totalComJurosTributavelNoPeriodo.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));
            resultado.setJurosPeriodoNaoTributavel(totalComJurosNaoTributavelNoPeriodo.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));

            resultado.setTotaisJurosTributavel(totaisJurosTributavel.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));
            resultado.setTotaisJurosNaoTributavel(totaisJurosNaoTributavel.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));

            resultado.setPrincipalAtualizado(totalPrincipalAtualizado);
            resultado.setJurosAplicados(totaisJurosAplicado.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));

            resultado.setCustasMultasOutrosAtualizados(custasAtualizadas.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));

            resultado.setTotalAtualizadaAntesEC113(totalAntesEc);
            resultado.setTotaisPrecatorioAtualizado(resultado.getTotalAtualizadaPosEC113().add(resultado.getSelicValorSubtotal()));

            return resultado;

        } catch (IndexNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new CalculationException("Erro no cálculo do precatório: " + e.getMessage(), e);
        }
    }
    @Deprecated
    private CalculoResponse calcularAtualizacaoEC113(
            BigDecimal principalTributavel,
            BigDecimal principalNaoTributavel,
            BigDecimal custas,
            LocalDate dataUltimaAtualizacao,
            Integer anoVencimento,
            LocalDate dataFinalPrimeiraAtualizacao,
            CalculoResponse retorno,
            BigDecimal totalJurosTributavel,
            BigDecimal totalJurosNaoTributavel,
            BigDecimal valorSelicPrincipal,
            BigDecimal valorSelicJuros) {

        // Datas (delegadas ao util)
        LocalDate dataInicioAntesGraca = UtilCalculo.calcularDataInicioAntesGraca(anoVencimento, dataUltimaAtualizacao, dataFinalPrimeiraAtualizacao);
        LocalDate dataFimAntesGraca = UtilCalculo.calcularDataFimAntesGraca(anoVencimento, dataUltimaAtualizacao, DATA_FINAL_CALCULO, dataFinalPrimeiraAtualizacao);

        LocalDate dataInicioDuranteGraca = UtilCalculo.calcularDataInicioDuranteGraca(anoVencimento, dataUltimaAtualizacao, DATA_FINAL_CALCULO, dataFinalPrimeiraAtualizacao);
        LocalDate dataFimDuranteGraca = UtilCalculo.calcularDataFinalDuranteGraca(anoVencimento, dataUltimaAtualizacao, DATA_FINAL_CALCULO, dataFinalPrimeiraAtualizacao);

        LocalDate dataInicioPosGraca = UtilCalculo.calcularDataInicioPosGraca(anoVencimento, dataUltimaAtualizacao, DATA_FINAL_CALCULO, dataFimDuranteGraca);
        LocalDate dataFimPosGraca = UtilCalculo.calcularDataFinalPosGraca(anoVencimento, DATA_FINAL_CALCULO, dataFimDuranteGraca, dataFimAntesGraca);

        YearMonth inicioAntesGraca = YearMonth.from(dataInicioAntesGraca);
        YearMonth fimAntesGraca = YearMonth.from(dataFimAntesGraca);

        YearMonth inicioDuranteGraca = YearMonth.from(dataInicioDuranteGraca);
        YearMonth fimDuranteGraca = YearMonth.from(dataFimDuranteGraca);

        YearMonth inicioAposGraca = YearMonth.from(dataInicioPosGraca);
        YearMonth fimApoGraca = YearMonth.from(dataFimPosGraca);

        // set datas na resposta
        retorno.setDataInicialAntesGraca(dataInicioAntesGraca);
        retorno.setDataFinalAntesGraca(dataFimAntesGraca);

        retorno.setDataInicialCorrecaoDuranteGraca(dataInicioDuranteGraca);
        retorno.setDataFinalCorreccaoDuranteGraca(dataFimDuranteGraca);

        retorno.setDataInicialPosGraca(dataInicioPosGraca);
        retorno.setDataFinalPosGraca(dataFimPosGraca);

        // SELIC antes da graça (soma aditiva)
        BigDecimal selicAntesGraca = bancoCentralService.somarSelic(inicioAntesGraca, fimAntesGraca);
        retorno.setSelicTaxaAntesGraca(selicAntesGraca.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));

        // IPCA durante a graça (multiplicativo)
        BigDecimal ipcaDuranteGraca = bancoCentralService.multiplicarIPCAE(inicioDuranteGraca, fimDuranteGraca);

        // SELIC pós graça
        BigDecimal selicPosGraca = bancoCentralService.somarSelic(inicioAposGraca, fimApoGraca);

        // aplicar IPCA sobre principais/juros/custas
        BigDecimal selicTotalTributavel = UtilCalculo.calculaAtualizacaoJuros(principalTributavel.add(totalJurosTributavel), ipcaDuranteGraca);
        BigDecimal selicTotalNaoTributavel = UtilCalculo.calculaAtualizacaoJuros(principalNaoTributavel, ipcaDuranteGraca);

        // aplicar juros os valores
        BigDecimal jurosAtualizadoIpca = UtilCalculo.calculaAtualizacaoJuros(totalJurosNaoTributavel, ipcaDuranteGraca);
        BigDecimal custasAtualizadoIpca = UtilCalculo.calculaAtualizacaoJuros(custas, ipcaDuranteGraca);

        BigDecimal totalAtualizadoEC113 = selicTotalTributavel
                .add(selicTotalNaoTributavel)
                .add(jurosAtualizadoIpca)
                .add(custasAtualizadoIpca);

        retorno.setTotalAtualizadaPosEC113(totalAtualizadoEC113.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));

        // SELIC total (fator agregado)
        BigDecimal selicTotalFator = selicAntesGraca.add(selicPosGraca);

        BigDecimal selicPrincipal = selicTotalTributavel.add(selicTotalNaoTributavel).multiply(selicTotalFator).divide(CEM);
        BigDecimal selicJuros = jurosAtualizadoIpca.multiply(selicTotalFator).divide(CEM);
        BigDecimal subtotalSelic = selicPrincipal.add(selicJuros);

        // preencher campos selic na resposta
        retorno.setSelicTaxa(selicPosGraca);
        retorno.setSelicPrincipalTributavel(selicTotalTributavel.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));
        retorno.setSelicFatorIpcaDuranteGraca(ipcaDuranteGraca.setScale(7, RoundingMode.HALF_UP));
        retorno.setSelicMultasCustasAtualizada(custasAtualizadoIpca.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));
        retorno.setSelicJurosAtualizadoIpca(jurosAtualizadoIpca.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));
        retorno.setSelicPrincipalNaoTributavel(selicTotalNaoTributavel.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));
        retorno.setSelicValorSubtotal(subtotalSelic.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));
        retorno.setSelicValorJuros(selicJuros.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));
        retorno.setSelicValorPrincipal(selicPrincipal.setScale(ESCALA_DEFAULT, RoundingMode.HALF_UP));

        return retorno;
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

