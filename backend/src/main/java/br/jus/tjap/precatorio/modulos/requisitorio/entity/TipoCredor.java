package br.jus.tjap.precatorio.modulos.requisitorio.entity;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.TipoCredorDTO;
import br.jus.tjap.precatorio.modulos.tabelasbasicas.entity.TipoTributacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tipo_credor_precatorio")
public class TipoCredor {

    @Id
    private Long id;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "tipo_pessoa")
    private String tipoPessoa;

    @Column(name = "ativo")
    private boolean ativo;

    @OneToMany(mappedBy = "tipoCredor", fetch = FetchType.LAZY)
    private List<TipoTributacao> tipoTributacoes;

    public TipoCredorDTO toMetadado(){
        var dto = new TipoCredorDTO();

        dto.setId(this.id);
        dto.setDescricao(this.descricao);
        dto.setTipoPessoa(this.tipoPessoa);
        dto.setAtivo(this.ativo);
        dto.setTipoTributacoesDTO(this.tipoTributacoes.stream().map(TipoTributacao::toMetadado).toList());

        return dto;
    }

}
