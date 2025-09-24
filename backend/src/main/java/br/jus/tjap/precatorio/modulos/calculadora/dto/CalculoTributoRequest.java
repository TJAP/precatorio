package br.jus.tjap.precatorio.modulos.calculadora.dto;

import java.math.BigDecimal;

public record CalculoTributoRequest (

    BigDecimal valorPrincipalTributavelAtualizado,
    BigDecimal valorPrincipalNaoTributavelAtualizado,
    BigDecimal valorJurosAtualizado,
    BigDecimal valorSelicAtualizada,
    BigDecimal valorPrevidenciaAtualizada,
    BigDecimal percentualHonorarios,
    BigDecimal valorFixoHonorarios,// Ex.: 0.2 (20%)
    boolean incluirJurosSelicNaBase,
    int quantidadeMesesRRA
){
    public CalculoTributoRequest {
        if (valorPrincipalTributavelAtualizado == null) {
            valorPrincipalTributavelAtualizado = BigDecimal.ZERO;
        }
        if (valorPrincipalNaoTributavelAtualizado == null) {
            valorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
        }
        if (valorJurosAtualizado == null) {
            valorJurosAtualizado = BigDecimal.ZERO;
        }
        if (valorSelicAtualizada == null) {
            valorSelicAtualizada = BigDecimal.ZERO;
        }
        if (valorPrevidenciaAtualizada == null) {
            valorPrevidenciaAtualizada = BigDecimal.ZERO;
        }
        if (percentualHonorarios == null) {
            percentualHonorarios = BigDecimal.ZERO;
        }
        if (valorFixoHonorarios == null) {
            valorFixoHonorarios = BigDecimal.ZERO;
        }

        // faça o mesmo para outros campos se precisar
    }
}
