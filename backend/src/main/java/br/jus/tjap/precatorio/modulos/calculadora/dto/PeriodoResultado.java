package br.jus.tjap.precatorio.modulos.calculadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodoResultado {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal ipcaFator = BigDecimal.ZERO;
    private BigDecimal fatorJuros = BigDecimal.ZERO;

    private BigDecimal principalTributavel = BigDecimal.ZERO;
    private BigDecimal principalNaoTributavel = BigDecimal.ZERO;
    private BigDecimal valorJuros = BigDecimal.ZERO;
    private BigDecimal custasMulta = BigDecimal.ZERO;
    private BigDecimal selic = BigDecimal.ZERO;
    private BigDecimal totalAtualizado = BigDecimal.ZERO;

}
