package br.jus.tjap.precatorio.calculadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoRequest {

    private String numeroProcesso;
    private LocalDate dataUltimaAtualizacao;
    private int anoVencimento;
    // Data do período de graça é calculada conforme regra - não enviada
    private BigDecimal valorPrincipalTributavel;
    private BigDecimal valorPrincipalNaoTributavel;
    private BigDecimal valorJurosTributavel;
    private BigDecimal valorJurosNaoTributavel;

    private BigDecimal valorSelicPrincipal;
    private BigDecimal valorSelicJuros;
    private String tipoSelicTributacao;

    private BigDecimal custas;
    private BigDecimal multa;
    private BigDecimal outrosReembolsos;
    private String tipoIndice;
    private String tipoNaturezaRenda;

    private BigDecimal saldoRemanescente;
}
