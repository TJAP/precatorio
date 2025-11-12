package br.jus.tjap.precatorio.modulos.calculadora.util;

import br.jus.tjap.precatorio.modulos.calculadora.exception.IndexNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

public class UtilCalculo {

    private UtilCalculo(){}

    public static final Map<Integer, PeriodoLimite> PERIODOS = Map.of(
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

    private static final YearMonth EC113_DATA_CORTE = YearMonth.of(2021, 11); // nov/2021
    private static final BigDecimal ZERO = new BigDecimal("0.00");

    public static BigDecimal aplicarPercentual(BigDecimal valor, BigDecimal percentual) {
        return valor.multiply(percentual).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal aplicarDesconto(BigDecimal valor, BigDecimal percentual) {
        return valor.subtract(aplicarPercentual(valor, percentual));
    }

    public static BigDecimal pct(double valor) {
        return BigDecimal.valueOf(valor).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
    }

    public static boolean isNotNullOrZero(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) != 0;
    }

    public static BigDecimal escala(BigDecimal valor, int escala) {
        if(valor.compareTo(BigDecimal.ZERO)==0){
            return BigDecimal.ZERO;
        }
        return valor == null ? BigDecimal.ZERO : valor.setScale(escala, RoundingMode.HALF_UP);
    }

    public static BigDecimal manterValorZeroSeNulo(BigDecimal b) {
        return Objects.isNull(b) ? ZERO : b;
    }

    public static void validarSeTemTodosOsMeses(YearMonth start, YearMonth end, Map<YearMonth, BigDecimal> map, String indexName) {
        YearMonth cur = start;
        while (!cur.isAfter(end)) {
            if (!map.containsKey(cur) || map.get(cur) == null) {
                throw new IndexNotFoundException("Índice " + indexName + " sem dado para " + cur);
            }
            cur = cur.plusMonths(1);
        }
    }

    public static LocalDate calculaDataIncioGraca(int anoVencimento, LocalDate dataFim){
        int mesesCalculoLei = 23;
        int dia = 1;
        if(anoVencimento <= 2022){
            mesesCalculoLei = 18;
        } else if(anoVencimento >= 2023 && anoVencimento <= 2025){
            mesesCalculoLei = 21;
            dia = 2;
        } else {
            mesesCalculoLei = 23;
        }
        YearMonth mesAno = YearMonth.from(dataFim.minusMonths(mesesCalculoLei - 1));
        return mesAno.atDay(dia);
    }

    public static int calcularPeriodoMeses(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) return 0;

        // Equivalente a DATA(ANO(L4); MÊS(L4)+1; 1)
        LocalDate proximoMes = dataFim.plusMonths(1).withDayOfMonth(1);

        // DATADIF(I4; DATA(...); "y")
        int anos = Period.between(dataInicio, proximoMes).getYears();

        // DATADIF(I4; L4; "m")
        Period periodoMeses = Period.between(dataInicio, dataFim);
        int meses = periodoMeses.getYears() * 12 + periodoMeses.getMonths();

        // Fórmula final: (anos) + (meses + 1)
        return anos + (meses + 1);
    }

    public static long contarMesesInclusivos(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            return 0L;
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

    public static BigDecimal maior(BigDecimal a, BigDecimal b) {
        if (a == null) return b != null ? b : BigDecimal.ZERO;
        if (b == null) return a;
        return a.max(b);
    }
}
