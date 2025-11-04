package br.jus.tjap.precatorio.modulos.tabelasbasicas.entity;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.TipoCredor;
import br.jus.tjap.precatorio.modulos.tabelasbasicas.dto.TipoTributacaoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tipo_tributacao_precatorio")
public class TipoTributacao {

    @Id
    private Long id;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "aplica_ir")
    private boolean aplicaIr;

    @Column(name = "aliquota_padrao")
    private BigDecimal aliquotaPadrao;

    @Column(name = "usa_tabela_progressiva")
    private boolean usaTabelaProgressiva;

    @Column(name = "origem_isencao")
    private String origemIsencao;

    @Column(name = "observacao")
    private String observacao;

    @Column(name = "ativo")
    private boolean ativo;

    @Column(name = "com_previdencia")
    private boolean comPrevidencia;

    @Column(name = "com_rra")
    private boolean comRRA;

    @JoinColumn(name = "id_tipo_credor")
    @ManyToOne(fetch = FetchType.LAZY)
    private TipoCredor tipoCredor;

    public TipoTributacaoDTO toMetadado(){
        var dto = new TipoTributacaoDTO();

        dto.setId(this.id);
        dto.setCodigo(this.codigo);
        dto.setDescricao(this.descricao);
        dto.setAplicaIr(this.aplicaIr);
        dto.setAliquotaPadrao(this.aliquotaPadrao);
        dto.setUsaTabelaProgressiva(this.usaTabelaProgressiva);
        dto.setComPrevidencia(this.comPrevidencia);
        dto.setComRRA(this.comRRA);
        dto.setOrigemIsencao(this.origemIsencao);
        dto.setObservacao(this.observacao);
        dto.setAtivo(this.ativo);
        //dto.setTipoCredorDTO(this.tipoCredor.toMetadado());

        return dto;
    }

}
