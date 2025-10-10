package br.jus.tjap.precatorio.modulos.requisitorio.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessoDeducaoDTO {

    private Long id;
    private Integer tipoDeducao;
    private String numeroProcessoOrigem;
    private String nomePessoaOrigem;
    private String documentoPessoaOrigem;
    private String numeroProcessoDestino;
    private String nomePessoaDestino;
    private String documentoPessoaDestino;
    private String numeroDocumentoRequisicaoDeducao;
    private Map<String, Object> dadosDeducao;
}
