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

    @Schema(description = "Valor principal tributável atualizado", example = "1000.00")
    private BigDecimal valorPrincipalTributavelAtualizado = BigDecimal.ZERO;;
    @Schema(description = "Valor principal não tributável atualizado", example = "500.00")
    private BigDecimal valorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;;
    @Schema(description = "Valor dos juros atualizados", example = "50.00")
    private BigDecimal valorJurosAtualizado = BigDecimal.ZERO;;
    @Schema(description = "Valor SELIC atualizado", example = "10.00")
    private BigDecimal valorSelicAtualizada = BigDecimal.ZERO;;
    @Schema(description = "Valor da previdência atualizada", example = "20.00")
    private BigDecimal valorPrevidenciaAtualizada = BigDecimal.ZERO;;
    @Schema(description = "Percentual de honorários", example = "0.2")
    private BigDecimal percentualHonorarios = BigDecimal.ZERO;;
    @Schema(description = "Valor fixo honorário", example = "100.00")
    private BigDecimal valorFixoHonorarios = BigDecimal.ZERO;; // Ex.: 0.2 (20%)
    @Schema(description = "Incluir juros SELIC na base")
    private boolean incluirJurosSelicNaBase;
    @Schema(description = "Quantidade de meses RRA", example = "12")
    private int quantidadeMesesRRA;

    @Schema(description = "Quantidade de meses RRA", example = "PF")
    private String tipoTributacaoAdvogado; // PF, PJ e SN

    @Schema(description = "Quantidade de meses RRA", example = "PF")
    private String tipoTributacaoCredor; // PF, PJ e SN

    @Schema(description = "Quantidade de meses RRA", example = "12")
    private String tipoVinculo;

    @Schema(description = "Quantidade de meses RRA", example = "12")
    private String tipoPrevidenciaCredor;

}
