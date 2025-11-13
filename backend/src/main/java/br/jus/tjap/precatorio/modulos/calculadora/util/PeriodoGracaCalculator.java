package br.jus.tjap.precatorio.modulos.calculadora.util;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class PeriodoGracaCalculator {

    private static final LocalDate DATA_ATE_PRIMEIRO_CALCULO = LocalDate.of(2021, 11, 1);

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

    public static Periodo calcularPeriodoEntreDezembro2021AhJulho2025(LocalDate dataInicioAtualizacao,LocalDate dataFinalAtualizacao,int anoVencimento) {

        // Limites globais do cálculo (período total possível)
        final LocalDate DATA_INICIO_SEGUNDO_CALCULO = LocalDate.of(2021, Month.DECEMBER, 1);
        final LocalDate DATA_FIM_SEGUNDO_CALCULO = LocalDate.of(2025, Month.JULY, 1);

        // Obtém o período de graça (Lei 21) conforme o ano de vencimento
        PeriodoLimite periodoLei21 = PERIODOS_GRACA.get(anoVencimento);
        if (periodoLei21 == null) {
            throw new IllegalArgumentException("Ano de vencimento não mapeado: " + anoVencimento);
        }

        LocalDate INICIO_GRACA_LEI21 = periodoLei21.limiteInferior();
        LocalDate FIM_GRACA_LEI21 = periodoLei21.limiteSuperior();

        // Se o intervalo informado não intersecta o segundo cálculo, nada é calculado
        if (dataFinalAtualizacao.isBefore(DATA_INICIO_SEGUNDO_CALCULO) ||
                dataInicioAtualizacao.isAfter(DATA_FIM_SEGUNDO_CALCULO)) {
            return new Periodo(null, null, null, null, null, null);
        }

        // Define o intervalo efetivo (interseção com o segundo cálculo)
        LocalDate inicioEfetivo = dataInicioAtualizacao.isBefore(DATA_INICIO_SEGUNDO_CALCULO)
                ? DATA_INICIO_SEGUNDO_CALCULO
                : dataInicioAtualizacao;

        LocalDate fimEfetivo = dataFinalAtualizacao.isAfter(DATA_FIM_SEGUNDO_CALCULO)
                ? DATA_FIM_SEGUNDO_CALCULO
                : dataFinalAtualizacao;

        LocalDate inicioAntes = null, fimAntes = null;
        LocalDate inicioDurante = null, fimDurante = null;
        LocalDate inicioApos = null, fimApos = null;

        // Antes da graça — entre início do cálculo e início do período da Lei 21
        if (inicioEfetivo.isBefore(INICIO_GRACA_LEI21)) {
            inicioAntes = inicioEfetivo;
            fimAntes = INICIO_GRACA_LEI21.minusMonths(1);
        }

        // Durante a graça — período mapeado no ano de vencimento
        if (!fimEfetivo.isBefore(INICIO_GRACA_LEI21)) {
            inicioDurante = INICIO_GRACA_LEI21;
            fimDurante = fimEfetivo.isBefore(FIM_GRACA_LEI21)
                    ? fimEfetivo
                    : FIM_GRACA_LEI21;
        }

        // Após a graça — se o cálculo ultrapassar o fim da Lei 21
        if (fimEfetivo.isAfter(FIM_GRACA_LEI21)) {
            inicioApos = FIM_GRACA_LEI21.plusMonths(1);
            fimApos = fimEfetivo.isBefore(DATA_FIM_SEGUNDO_CALCULO) ? fimEfetivo : DATA_FIM_SEGUNDO_CALCULO;
        }

        return new Periodo(inicioAntes, fimAntes, inicioDurante, fimDurante, inicioApos, fimApos);
    }

    public static Periodo calcularPeriodoAposAgosto2025(LocalDate dataInicioAtualizacao,LocalDate dataFinalAtualizacao,int anoVencimento) {

        // Limites globais do cálculo (período total possível)
        final LocalDate DATA_INICIO_TERCEIRO_CALCULO = LocalDate.of(2025, Month.AUGUST, 1);
        final LocalDate DATA_FIM_TERCEIRO_CALCULO = (dataFinalAtualizacao != null)
                ? dataFinalAtualizacao
                : LocalDate.now().minusMonths(1);

        // Obtém o período de graça (Lei 21) conforme o ano de vencimento
        PeriodoLimite periodoLei25 = PERIODOS_GRACA.get(anoVencimento);
        if (periodoLei25 == null) {
            throw new IllegalArgumentException("Ano de vencimento não mapeado: " + anoVencimento);
        }

        LocalDate INICIO_GRACA_LEI25 = periodoLei25.limiteInferior();
        LocalDate FIM_GRACA_LEI25 = periodoLei25.limiteSuperior();

        // Se o intervalo informado não intersecta o segundo cálculo, nada é calculado
        if (dataFinalAtualizacao.isBefore(DATA_INICIO_TERCEIRO_CALCULO) ||
                dataInicioAtualizacao.isAfter(DATA_FIM_TERCEIRO_CALCULO)) {
            return new Periodo(null, null, null, null, null, null);
        }

        // Calcula os blocos (antes, durante, após)
        LocalDate inicioAntes = null, fimAntes = null;
        LocalDate inicioDurante = null, fimDurante = null;
        LocalDate inicioApos = null, fimApos = null;

        // --- ANTES DA GRAÇA ---
        // Só exibe se hoje ainda estiver antes do início da graça
        if (DATA_FIM_TERCEIRO_CALCULO.isBefore(INICIO_GRACA_LEI25)) {
            inicioAntes = dataInicioAtualizacao.isBefore(INICIO_GRACA_LEI25)
                    ? dataInicioAtualizacao
                    : INICIO_GRACA_LEI25;
            fimAntes = INICIO_GRACA_LEI25.minusMonths(1);
        }

        // --- DURANTE A GRAÇA ---
        // Exibe se a data atual estiver dentro da faixa da Lei 21
        if (!DATA_FIM_TERCEIRO_CALCULO.isBefore(INICIO_GRACA_LEI25) && !DATA_FIM_TERCEIRO_CALCULO.isAfter(FIM_GRACA_LEI25)) {
            inicioDurante = INICIO_GRACA_LEI25;
            fimDurante = FIM_GRACA_LEI25;
        }

        // --- APÓS A GRAÇA ---
        // Só exibe se o cálculo atual já atingiu agosto/2025
        if (!DATA_FIM_TERCEIRO_CALCULO.isBefore(DATA_INICIO_TERCEIRO_CALCULO)) {
            inicioApos = DATA_INICIO_TERCEIRO_CALCULO;
            fimApos = DATA_FIM_TERCEIRO_CALCULO.isAfter(inicioApos)
                    ? DATA_FIM_TERCEIRO_CALCULO
                    : inicioApos;
        }

        return new Periodo(inicioAntes, fimAntes, inicioDurante, fimDurante, inicioApos, fimApos);
    }

}
