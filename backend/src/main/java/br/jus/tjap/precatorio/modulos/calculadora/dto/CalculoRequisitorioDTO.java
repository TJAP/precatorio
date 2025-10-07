package br.jus.tjap.precatorio.modulos.calculadora.dto;


import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoRequisitorioDTO {

    private Long idRequisitorio;
    private CalculoRequest request;
    private RequisitorioDTO requisitorioDTO;
    private CalculoAtualizacaoDTO calculoAtualizacaoDTO;
    private CalculoPagamentoDTO calculoPagamentoDTO;
    private CalculoResumoDTO calculoResumoDTO;

}
