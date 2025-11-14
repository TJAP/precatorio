package br.jus.tjap.precatorio.modulos.tabelasbasicas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoAcaoDTO {

    private Long id;
    private String descricao;
    private String codCnj;
    private String observacao;
    private boolean ativo;
    private TipoTributacaoDTO tipoTributacaoDTO;
}
