package br.jus.tjap.precatorio.calculadora.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TabelaIRRFDTO {

    private Long id;
    private BigDecimal valorFaixaInicial;
    private BigDecimal valorFaixaFinal;
    private BigDecimal valorAliquota;
    private BigDecimal valorDeducao;

    private BigDecimal valorFaixaInicialCalculado = BigDecimal.ZERO;
    private BigDecimal valorFaixaFinalCalculado = BigDecimal.ZERO;
    private BigDecimal valorAliquotaCalculado = BigDecimal.ZERO;
    private BigDecimal valorDeducaoCalculado = BigDecimal.ZERO;

}
