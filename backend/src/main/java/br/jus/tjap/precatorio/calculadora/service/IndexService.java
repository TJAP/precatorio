package br.jus.tjap.precatorio.calculadora.service;

import java.time.YearMonth;
import java.util.Map;

/**
 * Abstração para buscar séries mensais de índices.
 * Implementar com Feign/RestTemplate para BCB/SGS.
 *
 * Deve retornar um Map<YearMonth, BigDecimal> onde o BigDecimal
 * representa a variação do mês em decimal (ex.: 0.0123 para 1.23%).
 */
public interface IndexService {
    /**
     * Busca variações mensais do índice entre start (inclusive) e end (inclusive).
     * @param indexCode código do índice (ex.: "IPCAE", "POUPANCA", "SELIC")
     */
    Map<YearMonth, java.math.BigDecimal> fetchMonthlyVariations(String indexCode, YearMonth start, YearMonth end);
}
