package br.jus.tjap.precatorio.modulos.tabelasbasicas.dto;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.TipoCredorDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.entity.TipoCredor;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoTributacaoDTO {

    private Long id;
    private String codigo;
    private String descricao;
    private boolean aplicaIr;
    private BigDecimal aliquotaPadrao;
    private boolean usaTabelaProgressiva;
    private String origemIsencao;
    private String observacao;
    private boolean ativo;
    private boolean comPrevidencia;
    private boolean comRRA;
    private TipoCredorDTO tipoCredorDTO;
}
