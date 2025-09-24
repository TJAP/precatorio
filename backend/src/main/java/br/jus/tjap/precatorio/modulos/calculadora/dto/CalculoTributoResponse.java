package br.jus.tjap.precatorio.modulos.calculadora.dto;

import java.math.BigDecimal;

public record CalculoTributoResponse (

    BigDecimal baseIrCredor,
    BigDecimal irCredor,
    BigDecimal previdenciaCredor,
    BigDecimal baseIrAdvogado,
    BigDecimal irAdvogado
){}
