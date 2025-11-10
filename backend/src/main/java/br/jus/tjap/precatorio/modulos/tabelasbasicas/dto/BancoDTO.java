package br.jus.tjap.precatorio.modulos.tabelasbasicas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BancoDTO {

    private Integer codigo;
    private String descricao;
}
