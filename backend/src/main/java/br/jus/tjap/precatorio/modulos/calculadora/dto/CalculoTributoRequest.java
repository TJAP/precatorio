package br.jus.tjap.precatorio.modulos.calculadora.dto;

import java.math.BigDecimal;

public record CalculoTributoRequest (

    BigDecimal valorPrincipalTributavelAtualizado,
    BigDecimal valorPrincipalNaoTributavelAtualizado,
    BigDecimal valorJurosAtualizado,
    BigDecimal valorSelicAtualizada,
    BigDecimal valorPrevidenciaAtualizada,
    BigDecimal percentualHonorarios, // Ex.: 0.2 (20%)
    boolean incluirJurosSelicNaBase,
    int quantidadeMesesRRA
){}
