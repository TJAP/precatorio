package br.jus.tjap.precatorio.modulos.calculadora.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String cnpjDevedor;
    private LocalDate dataUltimaAtualizacao;
    private LocalDate dataFimAtualizacao;
    private int anoVencimento;
    private LocalDate dataInicioRRA;
    private LocalDate dataFimRRA;
    // Data do período de graça é calculada conforme regra - não enviada
    private BigDecimal valorPrincipalTributavel;
    private BigDecimal valorPrincipalNaoTributavel;
    private BigDecimal valorJuros;
    private BigDecimal valorSelic;
    private BigDecimal valorPrevidencia;
    private BigDecimal custas;
    private BigDecimal multa;
    private BigDecimal outrosReembolsos;

    private boolean temPrioridade;
    private boolean pagamentoParcial;
    private BigDecimal valorPagamentoParcial;

    private BigDecimal percentualHonorario;
    private BigDecimal valorPagoAdvogado;
    private String tributacaoAdvogado;

    private BigDecimal percentualDesagio;
    private boolean acordoAdvogado;
    private boolean acordoCredor;

    private String tipoVinculoCredor;
    private String tipoTributacaoCredor;

    private BigDecimal percentualCessao;
    private BigDecimal valorPenhora;

}
