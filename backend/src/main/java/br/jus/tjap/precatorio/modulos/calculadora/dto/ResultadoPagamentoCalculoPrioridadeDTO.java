package br.jus.tjap.precatorio.modulos.calculadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoPagamentoCalculoPrioridadeDTO {

    private boolean temPrioridade = Boolean.FALSE;
    private BigDecimal valorBasePrioridade = BigDecimal.ZERO;
    private BigDecimal percentualPrioridade = BigDecimal.ZERO;

    private boolean temPagamentoParcial = Boolean.FALSE;
    private BigDecimal valorBaseParcialPago = BigDecimal.ZERO;
    private BigDecimal percentualParcialPago = BigDecimal.ZERO;

    private boolean houvePrioridadeOuPagamentoParcial = Boolean.FALSE;

    private BigDecimal valorPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorBrutoAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrevidenciaAtualizado = BigDecimal.ZERO;
    private BigDecimal numeroPrioridadeRRA = BigDecimal.ZERO;
}
