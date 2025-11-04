package br.jus.tjap.precatorio.modulos.calculadora.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CalculoUtil {

    private CalculoUtil() {}

    public static final int SCALE_PADRAO = 2;
    public static final int SCALE_INTERMEDIARIA = 12;
    public static final RoundingMode ROUND_MODE = RoundingMode.HALF_UP;

    public static BigDecimal pct(double valor) {
        return BigDecimal.valueOf(valor).divide(BigDecimal.valueOf(100), SCALE_PADRAO, ROUND_MODE);
    }

    public static BigDecimal aplicarPercentual(BigDecimal base, double percentual) {
        if (base == null) return BigDecimal.ZERO;
        return base.multiply(pct(percentual)).setScale(SCALE_PADRAO, ROUND_MODE);
    }

    public static BigDecimal aplicarDesconto(BigDecimal base, double percentual) {
        if (base == null) return BigDecimal.ZERO;
        return base.subtract(aplicarPercentual(base, percentual)).setScale(SCALE_PADRAO, ROUND_MODE);
    }

    public static BigDecimal zeroSeNulo(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public static BigDecimal maior(BigDecimal a, BigDecimal b) {
        if (a == null) return b != null ? b : BigDecimal.ZERO;
        if (b == null) return a;
        return a.max(b);
    }

    public static long calcularMesesPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            return 0L;
        }
        LocalDate proximoMesPrimeiroDia = dataFim.plusMonths(1).withDayOfMonth(1);

        long mesesEntre = ChronoUnit.MONTHS.between(dataInicio.withDayOfMonth(1), dataFim.withDayOfMonth(1));

        long anosEntre = ChronoUnit.YEARS.between(dataInicio, proximoMesPrimeiroDia);

        return anosEntre + mesesEntre + 1;
    }
}
