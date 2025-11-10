package br.jus.tjap.precatorio.modulos.calculadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoPagamentoRateioDTO {

    private BigDecimal percentualHonorario = BigDecimal.ZERO;
    private BigDecimal valorPagoAdvogado = BigDecimal.ZERO;
    private BigDecimal percentualParteAdvogado = BigDecimal.ZERO;
    private BigDecimal percentualParteCredor = BigDecimal.ZERO;

    private BigDecimal percentualDesagio = BigDecimal.ZERO;
    private boolean houveAcordoAdvogado = Boolean.FALSE;
    private boolean houveAcordoCredor = Boolean.FALSE;

    private String tipoTributacaoCredor = "ISENTO";
    private String tipoTributacaoAdvogado = "ISENTO";

    private BigDecimal valorHonorarioPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorHonorarioPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorHonorarioJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorHonorarioMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorHonorarioSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorHonorarioBrutoAtualizado = BigDecimal.ZERO;

    private BigDecimal valorCredorPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorCredorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorCredorJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorCredorMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorCredorSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorCredorBrutoAtualizado = BigDecimal.ZERO;

    private BigDecimal valorDesagioHonorarioPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioBrutoAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioAtualizado = BigDecimal.ZERO;

    private BigDecimal valorDesagioCredorPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorBrutoAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorAtualizado = BigDecimal.ZERO;
}
