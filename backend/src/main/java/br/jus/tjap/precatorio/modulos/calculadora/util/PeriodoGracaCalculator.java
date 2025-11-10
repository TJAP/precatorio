package br.jus.tjap.precatorio.modulos.calculadora.util;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

public class PeriodoGracaCalculator {

    public record Periodo(LocalDate inicioAntes, LocalDate fimAntes,
                          LocalDate inicioDurante, LocalDate fimDurante,
                          LocalDate inicioApos, LocalDate fimApos) {}

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

    public static Periodo calcularPeriodo(LocalDate y13, LocalDate aa13, int anoPrecatório) {

        PeriodoLimite limite = PERIODOS_GRACA.get(anoPrecatório);
        if (limite == null) {
            throw new IllegalArgumentException("Ano de precatório inválido ou não mapeado: " + anoPrecatório);
        }

        LocalDate inicioGraca = limite.limiteInferior();
        LocalDate fimGraca = limite.limiteSuperior();

        LocalDate inicioAntes = null, fimAntes = null;
        LocalDate inicioDurante = null, fimDurante = null;
        LocalDate inicioApos = null, fimApos = null;

        // 🟡 Antes da Graça
        if (aa13.isBefore(inicioGraca)) {
            inicioAntes = y13;
            fimAntes = aa13;
        }
        // 🔵 Durante a Graça
        else if ((y13.isBefore(fimGraca) || y13.isEqual(fimGraca)) && !aa13.isBefore(inicioGraca)) {
            inicioDurante = y13.isBefore(inicioGraca) ? inicioGraca : y13;
            fimDurante = aa13.isAfter(fimGraca) ? fimGraca : aa13;
        }
        // 🔴 Após a Graça
        if (aa13.isAfter(fimGraca)) {
            inicioApos = y13.isAfter(fimGraca) ? y13 : fimGraca.plusDays(1);
            fimApos = aa13;
        }

        return new Periodo(inicioAntes, fimAntes, inicioDurante, fimDurante, inicioApos, fimApos);
    }
}
