package br.jus.tjap.precatorio.modulos.calculadora.util;

import br.jus.tjap.precatorio.modulos.calculadora.exception.IndexNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

public class UtilCalculo {

    private static final YearMonth EC113_DATA_CORTE = YearMonth.of(2021, 11); // nov/2021
    private static final BigDecimal ZERO = new BigDecimal("0.00");

    public static BigDecimal coalesceBigDecimal(BigDecimal valor1, BigDecimal valor2, BigDecimal valor3) {
        if (isNotNullOrZero(valor3)) {
            return valor3;
        }
        if (isNotNullOrZero(valor2)) {
            return valor2;
        }
        if (isNotNullOrZero(valor1)) {
            return valor1;
        }
        return BigDecimal.ZERO; // se todos forem null ou zero
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

    public static BigDecimal manterValorUmSeNulo(BigDecimal valor) {
        if (valor == null) {
            return BigDecimal.ONE; // ou lançar exceção se preferir
        }
        return valor.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : valor;
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

    /**
     * Calcula juros atualizados (aplica fator e escala).
     */
    public static BigDecimal calculaAtualizacaoJuros(BigDecimal jurosOriginal, BigDecimal fatorCorrecao) {
        if (!isValorValido(jurosOriginal)) {
            return ZERO;
        }
        return multiplicaEhArredonda(jurosOriginal, fatorCorrecao);
    }

    public static BigDecimal multiplicaEhArredonda(BigDecimal value, BigDecimal factor) {
        BigDecimal safe = UtilCalculo.manterValorZeroSeNulo(value);
        return safe.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    public static boolean isValorValido(Object valor) {
        if (valor == null) {
            return false;
        }
        if (valor instanceof String) {
            String s = ((String) valor).trim();
            return !s.isEmpty() && !s.equals("0") && new BigDecimal(s).compareTo(ZERO) != 0;
        }
        if (valor instanceof BigDecimal) {
            return ((BigDecimal) valor).compareTo(ZERO) != 0;
        }
        return false;
    }

    public static LocalDate obterDataInscricao(Integer anoVencimento){
        if(anoVencimento == null){
            return LocalDate.now();
        }

        if(anoVencimento < 2023){
            return LocalDate.of(anoVencimento -1, 7, 1);
        } else {
            return LocalDate.of(anoVencimento -1, 4, 2);
        }
    }

    public static LocalDate calculaDataIncioGraca(int anoVencimento, LocalDate dataFim){
        int mesesCalculoLei = 23;
        int dia = 1;
        if(anoVencimento <= 2022){
            mesesCalculoLei = 18;
        } else if(anoVencimento >= 2023 || anoVencimento <= 2026){
            mesesCalculoLei = 21;
            dia = 2;
        } else {
            mesesCalculoLei = 23;
        }
        YearMonth mesAno = YearMonth.from(dataFim.minusMonths(mesesCalculoLei - 1));
        return mesAno.atDay(dia);
    }

    public static LocalDate calcularDataInicioAntesGraca(int anoVencimento, LocalDate dataAtualizacao, LocalDate dataInscricao) {
        // Condição 1: C10 >= 2023
        if (anoVencimento >= 2023) {

            // início do mês de m10
            LocalDate inicioMesM10 = dataAtualizacao.withDayOfMonth(1);

            // início do mês anterior a p10
            LocalDate inicioMesAnteriorP10 = dataInscricao.minusMonths(1).withDayOfMonth(1);

            // Condição 2: início do mês de m10 < início do mês anterior de p10
            if (inicioMesM10.isBefore(inicioMesAnteriorP10)) {

                // Se início do mês de M10 <= 30/11/2021
                LocalDate limite = LocalDate.of(2021, 11, 30);
                if (!inicioMesM10.isAfter(limite)) {
                    return LocalDate.of(2021, 12, 1); // 01/12/2021
                } else {
                    // Primeiro dia do mês seguinte a m10
                    return inicioMesM10.plusMonths(1);
                }
            }
        }
        // Caso não atenda às condições, retorna null (representa "-")
        return null;
    }

    public static LocalDate calcularDataFimAntesGraca(int anoVencimento, LocalDate dataUltimaAtualizacao, LocalDate dataFimCalculo, LocalDate dataInscricao) {
        // Condição 1: C10 >= 2023
        if (anoVencimento >= 2023) {

            // início do mês de n10
            LocalDate inicioMesN10 = dataFimCalculo.withDayOfMonth(1);

            // referência dia 2 do mês anterior a p10
            LocalDate dia2MesAnteriorP10 = dataInscricao.minusMonths(1).withDayOfMonth(2);

            if (inicioMesN10.isAfter(dia2MesAnteriorP10)) {

                // início do mês de m10
                LocalDate inicioMesM10 = dataUltimaAtualizacao.withDayOfMonth(1);

                // início do mês anterior a p10
                LocalDate inicioMesAnteriorP10 = dataInscricao.minusMonths(1).withDayOfMonth(1);

                if (!inicioMesM10.isBefore(inicioMesAnteriorP10)) {
                    // inícioMesM10 >= inícioMesAnteriorP10
                    return null; // representando "-"
                } else {
                    // último dia do mês anterior a p10
                    LocalDate ultimoDiaMesAnteriorP10 = dataInscricao.minusMonths(1).withDayOfMonth(31);
                    return ultimoDiaMesAnteriorP10;
                }

            } else {
                // retorna N10 (mesmo dia)
                return dataFimCalculo;
            }
        }

        return null; // representa "-"
    }

    public static LocalDate calcularDataInicioDuranteGraca(int anoVencimento, LocalDate dataUltimaAtualizacao, LocalDate dataFimCalculo, LocalDate dataInscricao) {
        // Se N10 == null ou zero → retorna "-"
        if (dataFimCalculo == null) return null;

        // início do mês
        LocalDate inicioMesM10 = dataUltimaAtualizacao.withDayOfMonth(1);
        LocalDate inicioMesN10 = dataFimCalculo.withDayOfMonth(1);
        LocalDate inicioMesP10 = dataInscricao.withDayOfMonth(1);

        // C10 >= 2023
        if (anoVencimento >= 2023) {
            if (inicioMesN10.isBefore(inicioMesP10)) {
                return null; // "-"
            } else {
                LocalDate limiteM10 = LocalDate.of(dataInscricao.getYear() + 1, 12, 1);
                if (inicioMesM10.isBefore(limiteM10)) {
                    LocalDate dia2P10 = dataInscricao.withDayOfMonth(2);
                    if (inicioMesM10.isAfter(dia2P10)) {
                        return inicioMesM10.plusMonths(1); // primeiro dia mês seguinte M10
                    } else {
                        return dia2P10; // dia 2 do mês de P10
                    }
                } else {
                    return null; // "-"
                }
            }

        } else if (anoVencimento == 2021) {
            LocalDate limite = LocalDate.of(2021, 11, 1);
            if (inicioMesM10.isBefore(limite)) {
                return LocalDate.of(2021, 12, 1);
            } else {
                return null; // "-"
            }

        } else if (anoVencimento == 2022) {
            LocalDate limite2021 = LocalDate.of(2021, 11, 1);
            if (inicioMesM10.isBefore(limite2021)) {
                return LocalDate.of(2021, 12, 1);
            } else {
                LocalDate limite2022 = LocalDate.of(2022, 12, 1);
                if (inicioMesM10.isBefore(limite2022)) {
                    return inicioMesM10.plusMonths(1); // primeiro dia mês seguinte M10
                } else {
                    return null; // "-"
                }
            }

        } else {
            return null; // "-"
        }
    }

    public static LocalDate calcularDataFinalDuranteGraca(int anoVencimento, LocalDate dataUltimaAtualizacao, LocalDate dataFimCalculo, LocalDate dataInscricao) {
        // Se N10 == null ou zero → retorna "-"
        if (dataFimCalculo == null) return null;

        LocalDate inicioMesM10 = dataUltimaAtualizacao.withDayOfMonth(1);
        LocalDate inicioMesN10 = dataFimCalculo.withDayOfMonth(1);
        LocalDate inicioMesP10 = dataInscricao.withDayOfMonth(1);

        if (anoVencimento >= 2023) {
            LocalDate dataInicioAnoSeguinte = LocalDate.of(dataInscricao.getYear() + 1, 12, 1);
            LocalDate dataFimAnoSeguinte = LocalDate.of(dataInscricao.getYear() + 1, 12, 31);

            if (!inicioMesN10.isBefore(dataInicioAnoSeguinte)) {
                // inicioMesN10 >= 01/12 ano+1
                if (!inicioMesM10.isAfter(dataFimAnoSeguinte)) {
                    // inicioMesM10 <= 31/12 ano+1
                    return dataFimAnoSeguinte;
                } else {
                    if (!inicioMesN10.isAfter(inicioMesP10)) {
                        // inicioMesN10 <= inicioMesP10
                        return dataFimCalculo; // retorna N10 (mesmo dia)
                    } else {
                        return null; // "-"
                    }
                }
            } else {
                // Senão: inicioMesN10 < 01/12 ano+1
                if (!inicioMesN10.isBefore(inicioMesP10)) {
                    // inicioMesN10 >= inicioMesP10
                    return dataFimCalculo; // retorna N10 (mesmo dia)
                } else {
                    return null; // "-"
                }
            }

        } else if (anoVencimento == 2021) {
            LocalDate limite = LocalDate.of(2021, 11, 1);
            if (inicioMesM10.isBefore(limite)) {
                return LocalDate.of(2021, 12, 1);
            } else {
                return null; // "-"
            }

        } else if (anoVencimento == 2022) {
            LocalDate limite = LocalDate.of(2022, 12, 1);
            if (inicioMesM10.isBefore(limite)) {
                return LocalDate.of(2022, 12, 31);
            } else {
                return null; // "-"
            }

        } else {
            return null; // "-"
        }
    }

    public static LocalDate calcularDataInicioPosGraca(int anoVencimento, LocalDate dataUltimaAtualizacao, LocalDate dataFimCalculo, LocalDate dataFinalDuranteGraca) {
        // Se N10 == null ou zero → retorna "-"
        if (dataFimCalculo == null) return null;

        // Data de corte: 30/11/2021
        LocalDate dataCorte = EC113_DATA_CORTE.atDay(1);

        // Primeiro dia do mês seguinte de M10
        LocalDate mesSeguinteM10 = dataUltimaAtualizacao.plusMonths(1).withDayOfMonth(1);

        var at10 = "-";

        if (!mesSeguinteM10.isAfter(dataCorte)) {
            at10 = dataCorte.toString(); // ou return dataCorte para devolver LocalDate
        }

        // Início do mês de N10 e BT10
        LocalDate inicioMesN10 = dataFimCalculo.withDayOfMonth(1);
        LocalDate inicioMesM10 = dataUltimaAtualizacao.withDayOfMonth(1);
        LocalDate inicioMesBT10 = dataFinalDuranteGraca != null ? dataFinalDuranteGraca.withDayOfMonth(1) : null;

        // Se C10 >= 2023
        if (anoVencimento >= 2023) {
            if (dataFinalDuranteGraca != null) {
                // Se início do mês de N10 <= início do mês de BT10
                if (!inicioMesN10.isAfter(inicioMesBT10)) {
                    return null; // "-"
                } else {
                    return inicioMesBT10.plusMonths(1); // primeiro dia do mês seguinte de BT10
                }
            } else {
                return null; // "-"
            }

        } else {
            // C10 < 2023
            if (dataFinalDuranteGraca != null) {
                return inicioMesBT10.plusMonths(1); // primeiro dia do mês seguinte de BT10
            } else {
                // Se AT10 = "-"
                if ("-".equals(at10)) {
                    return inicioMesM10.plusMonths(1); // primeiro dia do mês seguinte de M10
                } else {
                    return LocalDate.of(2021, 12, 1);
                }
            }
        }
    }

    public static LocalDate calcularDataFinalPosGraca(int anoVencimento, LocalDate dataFimCalculo, LocalDate dataFinalDuranteGraca, LocalDate dataFinalAntesGraca) {
        // Se N10 == null → "-"
        if (dataFimCalculo == null) {
            return null;
        }

        LocalDate inicioMesN10 = dataFimCalculo.withDayOfMonth(1);

        if (anoVencimento >= 2023) {
            if (dataFinalDuranteGraca != null) {
                LocalDate inicioMesBT10 = dataFinalDuranteGraca.withDayOfMonth(1);

                if (!inicioMesN10.isAfter(inicioMesBT10)) {
                    return null; // "-"
                } else {
                    return dataFimCalculo; // retorna N10 (mesmo dia)
                }
            } else {
                return null; // "-"
            }

        } else {
            // C10 < 2023
            if (dataFinalAntesGraca != null && dataFinalDuranteGraca != null) {
                return dataFinalDuranteGraca.withDayOfMonth(1).plusMonths(1); // primeiro dia do mês seguinte de BT10
            } else {
                return dataFimCalculo; // retorna N10 (mesmo dia)
            }
        }
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
}
