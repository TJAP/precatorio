package br.jus.tjap.precatorio.modulos.requisitorio.dto;

import br.jus.tjap.precatorio.modulos.tabelasbasicas.dto.TipoTributacaoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoCredorDTO {

    private Long id;

    private String descricao;
    private String tipoPessoa;
    private boolean ativo;
    private List<TipoTributacaoDTO> tipoTributacoesDTO;
}
