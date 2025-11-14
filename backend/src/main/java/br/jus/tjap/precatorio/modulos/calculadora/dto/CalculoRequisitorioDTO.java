package br.jus.tjap.precatorio.modulos.calculadora.dto;


import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoRequisitorioDTO {

    private Long idRequisitorio;
    //@JsonIgnore
    private CalculoRequest request;
    //@JsonIgnore
    private RequisitorioDTO requisitorioDTO;
    //@JsonIgnore
    private CalculoAtualizacaoDTO calculoAtualizacaoDTO;
    //@JsonIgnore
    private CalculoPagamentoDTO calculoPagamentoDTO;
    private CalculoResumoDTO calculoResumoDTO;
    private ResumoCalculoDocumentoDTO dadosDocumentoCalculo;
    private String base64DocumentoCalculo;

}
