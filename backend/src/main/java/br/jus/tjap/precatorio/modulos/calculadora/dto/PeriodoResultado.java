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
    private BigDecimal ipcaFator;
    private BigDecimal fatorJuros;

    private BigDecimal principalTributavel;
    private BigDecimal principalNaoTributavel;
    private BigDecimal valorJuros;
    private BigDecimal custasMulta;
    private BigDecimal selic;
    private BigDecimal totalAtualizado;

}
