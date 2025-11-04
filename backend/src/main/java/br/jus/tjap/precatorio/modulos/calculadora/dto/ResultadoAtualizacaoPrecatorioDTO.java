package br.jus.tjap.precatorio.modulos.calculadora.dto;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoAtualizacaoPrecatorioDTO {

    private Long id;

    private RequisitorioDTO requisitorioDTO;
    private String tipoIndiceAplicado;
    private LocalDate dataInicioCalculo;
    private LocalDate dataFimCalculo;
    private BigDecimal valorPrincipalTributavelAtualizado;
    private BigDecimal valorPrincipalNaoTributavelAtualizado;
    private BigDecimal valorJurosAtualizado;
    private BigDecimal valorMultaCustasOutrosAtualizado;
    private BigDecimal valorSelicAtualizado;
    private BigDecimal valorBrutoAtualizado;
    private BigDecimal valorPrevidenciaAtualizado;
    private BigDecimal valorFatorAntesAtualizado;
    private BigDecimal valorFatorDuranteAtualizado;
    private BigDecimal valorFatorDepoisAtualizado;
    private BigDecimal valorTaxaAntesAtualizado;
    private BigDecimal valorTaxaDuranteAtualizado;
    private BigDecimal valorTaxaDepoisAtualizado;
    private Long valorNumeroMesesRRA;

}
