package br.jus.tjap.precatorio.modulos.calculadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoPagamentoImpostoDTO {

    private String tributacaoAdvogado;
    private String tipoVinculoCredor;
    private String tipoTributacaoCredor;

    private BigDecimal baseTributavelHonorarioValor = BigDecimal.ZERO;
    private String baseTributavelHonorarioTipo;
    private BigDecimal baseTributavelHonorarioImposto = BigDecimal.ZERO;

    private String baseTributavelCredorTipoCalculo;
    private BigDecimal baseTributavelCredorValor = BigDecimal.ZERO;
    private String baseTributavelCredorTipo;
    private BigDecimal baseTributavelCredorImposto = BigDecimal.ZERO;
    private BigDecimal baseTributavelCredorPrevidencia = BigDecimal.ZERO;
}
