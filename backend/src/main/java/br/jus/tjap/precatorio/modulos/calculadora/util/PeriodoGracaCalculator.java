package br.jus.tjap.precatorio.modulos.calculadora.util;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class PeriodoGracaCalculator {

    private static final LocalDate DATA_ATE_PRIMEIRO_CALCULO = LocalDate.of(2021, 11, 1);

    private static final LocalDate DATA_INICIO_SEGUNDO_CALCULO = LocalDate.of(2021, 12, 1);
    private static final LocalDate DATA_FIM_SEGUNDO_CALCULO = LocalDate.of(2025, 7, 1);

    public record Periodo(LocalDate inicioAntes, LocalDate fimAntes,
                          LocalDate inicioDurante, LocalDate fimDurante,
                          LocalDate inicioApos, LocalDate fimApos) {
        public boolean isNulo() {
            return Stream.of(
                    inicioAntes, fimAntes,
                    inicioDurante, fimDurante,
                    inicioApos, fimApos
            ).allMatch(Objects::isNull);
        }
    }

    private static final Map<Integer, PeriodoLimite> PERIODOS_GRACA = Map.of(
            2020, new PeriodoLimite(LocalDate.of(2019, Month.JULY, 1), LocalDate.of(2020, Month.DECEMBER, 31)),
            2021, new PeriodoLimite(LocalDate.of(2020, Month.JULY, 1), LocalDate.of(2021, Month.DECEMBER, 31)),
            2022, new PeriodoLimite(LocalDate.of(2021, Month.JULY, 1), LocalDate.of(2022, Month.DECEMBER, 31)),
            2023, new PeriodoLimite(LocalDate.of(2022, Month.APRIL, 1), LocalDate.of(2023, Month.DECEMBER, 31)),
            2024, new PeriodoLimite(LocalDate.of(2023, Month.APRIL, 1), LocalDate.of(2024, Month.DECEMBER, 31)),
            2025, new PeriodoLimite(LocalDate.of(2024, Month.APRIL, 1), LocalDate.of(2025, Month.DECEMBER, 31)),
            2026, new PeriodoLimite(LocalDate.of(2025, Month.APRIL, 1), LocalDate.of(2026, Month.DECEMBER, 31)),
            2027, new PeriodoLimite(LocalDate.of(2026, Month.FEBRUARY, 1), LocalDate.of(2027, Month.DECEMBER, 31))
    );

    public record PeriodoLimite(LocalDate limiteInferior, LocalDate limiteSuperior) {}

    public static Periodo calcularPeriodoAntesNovembro2021(LocalDate dataInicioAtualizacao, int anoPrecatorio) {

        if(DATA_ATE_PRIMEIRO_CALCULO.isBefore(dataInicioAtualizacao)){
            return new Periodo(null, null, null, null, null, null);
        }

        PeriodoLimite limite = PERIODOS_GRACA.get(anoPrecatorio);
        if (limite == null) {
            throw new IllegalArgumentException("Ano de precatório inválido ou não mapeado: " + anoPrecatorio);
        }

        LocalDate inicioGraca = limite.limiteInferior();
        LocalDate fimGraca = limite.limiteSuperior();

        LocalDate inicioAntes = null, fimAntes = null;
        LocalDate inicioDurante = null, fimDurante = null;
        LocalDate inicioApos = null, fimApos = null;

        // 🟡 Antes da Graça
        if (DATA_ATE_PRIMEIRO_CALCULO.isBefore(inicioGraca)) {
            inicioAntes = dataInicioAtualizacao;
            fimAntes = DATA_ATE_PRIMEIRO_CALCULO;
        }
        // 🔵 Durante a Graça
        else if ((dataInicioAtualizacao.isBefore(fimGraca) || dataInicioAtualizacao.isEqual(fimGraca)) && !DATA_ATE_PRIMEIRO_CALCULO.isBefore(inicioGraca)) {
            inicioDurante = dataInicioAtualizacao.isBefore(inicioGraca) ? inicioGraca : dataInicioAtualizacao;
            fimDurante = DATA_ATE_PRIMEIRO_CALCULO.isAfter(fimGraca) ? fimGraca : DATA_ATE_PRIMEIRO_CALCULO;
        }
        // 🔴 Após a Graça
        if (DATA_ATE_PRIMEIRO_CALCULO.isAfter(fimGraca)) {
            inicioApos = dataInicioAtualizacao.isAfter(fimGraca) ? dataInicioAtualizacao : fimGraca.plusDays(1);
            fimApos = DATA_ATE_PRIMEIRO_CALCULO;
        }

        return new Periodo(inicioAntes, fimAntes, inicioDurante, fimDurante, inicioApos, fimApos);
    }

    public static Periodo calcularPeriodoEntreDezembro2021AhJulho2025(LocalDate dataInicioAtualizacao,LocalDate dataFinalAtualizacao, int anoVencimento){

        PeriodoLimite limite = PERIODOS_GRACA.get(anoVencimento);
        if (limite == null) {
            throw new IllegalArgumentException("Ano de precatório inválido ou não mapeado: " + anoVencimento);
        }

        LocalDate inicioAntes = null, fimAntes = null;
        LocalDate inicioDurante = null, fimDurante = null;
        LocalDate inicioApos = null, fimApos = null;

        PeriodoLimite inicioPeriodoAno = PERIODOS_GRACA.get(anoVencimento);

        LocalDate inicioGraca = limite.limiteInferior();
        LocalDate fimGraca = limite.limiteSuperior();

        LocalDate proximoMes = dataInicioAtualizacao.plusMonths(1);
        LocalDate dataInicio = null;
        LocalDate dataFim = null;

        if (proximoMes.isBefore(DATA_INICIO_SEGUNDO_CALCULO) && !dataFinalAtualizacao.isBefore(DATA_INICIO_SEGUNDO_CALCULO)) {
            dataInicio = DATA_INICIO_SEGUNDO_CALCULO;
        } else if ((proximoMes.isEqual(DATA_INICIO_SEGUNDO_CALCULO) || proximoMes.isAfter(DATA_INICIO_SEGUNDO_CALCULO))
                && (proximoMes.isEqual(DATA_FIM_SEGUNDO_CALCULO) || proximoMes.isBefore(DATA_FIM_SEGUNDO_CALCULO))) {
            dataInicio = proximoMes;
        }

        if (dataInicio != null && dataFinalAtualizacao.isAfter(DATA_FIM_SEGUNDO_CALCULO)) {
            dataFim = DATA_FIM_SEGUNDO_CALCULO;
        } else if (dataInicio != null
                && (dataFinalAtualizacao.isEqual(DATA_INICIO_SEGUNDO_CALCULO) || dataFinalAtualizacao.isAfter(DATA_INICIO_SEGUNDO_CALCULO))
                && (dataFinalAtualizacao.isBefore(DATA_FIM_SEGUNDO_CALCULO) || dataFinalAtualizacao.isEqual(DATA_FIM_SEGUNDO_CALCULO))) {
            dataFim = dataFinalAtualizacao;
        }


        if(DATA_INICIO_SEGUNDO_CALCULO.isBefore(dataInicio)){
            inicioAntes  = dataInicio;
            fimAntes = fimGraca.isBefore(inicioGraca) ? dataFim : inicioGraca.minusMonths(1);
        }

        if (inicioPeriodoAno == null) return new Periodo(null, null,null,null,null,null);

        









        return new Periodo(inicioAntes, fimAntes,inicioDurante,fimDurante,inicioApos,fimApos);
    }
}
