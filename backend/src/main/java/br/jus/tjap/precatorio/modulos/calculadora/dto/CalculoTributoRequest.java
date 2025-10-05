package br.jus.tjap.precatorio.modulos.calculadora.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoTributoRequest {

    private String cnpjDevedor;

    private BigDecimal valorPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorMultaCustaOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorSelicAtualizada = BigDecimal.ZERO;
    private BigDecimal valorTotalAtualizada = BigDecimal.ZERO;
    private BigDecimal valorPrevidenciaAtualizada = BigDecimal.ZERO;

    private long numeroMesesRRA;

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
