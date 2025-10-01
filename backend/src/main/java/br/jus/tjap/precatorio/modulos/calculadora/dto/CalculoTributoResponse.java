package br.jus.tjap.precatorio.modulos.calculadora.dto;

import br.jus.tjap.precatorio.modulos.calculadora.util.PagamentoUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoTributoResponse {

    private BigDecimal totalBruto = BigDecimal.ZERO;
    private BigDecimal percentualParteAdvogado = BigDecimal.ZERO;
    private BigDecimal percentualParteCredor = BigDecimal.ZERO;
    private BigDecimal valorParteAdvogado = BigDecimal.ZERO;
    private BigDecimal valorParteCredor = BigDecimal.ZERO;
    private BigDecimal valorParteTributavelCredor = BigDecimal.ZERO;
    private BigDecimal valorParteNaoTributavelCredor = BigDecimal.ZERO;
    private BigDecimal valorJurosCredor = BigDecimal.ZERO;
    private BigDecimal valorSelicCredor = BigDecimal.ZERO;
    private BigDecimal baseTributavelCredor = BigDecimal.ZERO;

    private PagamentoUtil.ResultadoCalculo tipoCalculo;

}
