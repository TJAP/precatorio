package br.jus.tjap.precatorio.modulos.requisitorio.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.*;
import br.jus.tjap.precatorio.util.DateUtil;
import br.jus.tjap.precatorio.util.StringUtil;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;

@Entity
@Table(name = Requisitorio.TABLE_NAME, schema="precatorio")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class TipoRequisitorio implements Serializable {
    public static final String TABLE_NAME = "precatorio";

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_" + TABLE_NAME)
    @SequenceGenerator(name = "gen_" + TABLE_NAME, sequenceName = "precatorio.sq_" + TABLE_NAME, allocationSize = 1)

    @Column(name = "id_tipo_orbigacao")
    private long idTipoOrbigacao;
    @Column(name = "id_tipo_previdencia")
    private Long idTipoPrevidencia;
    @Column(name = "id_tipo_titulo")
    private Long idTipoTitulo;
    @Column(name = "tp_precatorio")
    private Integer tipoPrecatorio;
    @Column(name = "ds_tipo_obrigacao")
    private String dsTipoObrigacao;
    @Column(name = "nome_banco_credor")
    private String nomeBancoCredor;
    @Column(name = "agencia_bancaria_credor")
    private String agenciaCredor;
    @Column(name = "conta_corrente_credor")
    private String contaCorrenteCredor;
    @Column(name = "agencia_bancaria_adv_credor")
    private String agenciaAdvCredor;
    @Column(name = "conta_corrente_adv_credor")
    private String contaCorrenteAdvCredor;
    @Column(name = "situcao_funcional_credor")
    private String situacaoFuncionalCredor;
    @Column(name = "percent_honor_adv_credor")
    private Integer percentHonorAdvCredor;
    @Column(name = "tp_tributacao_adv_credor")
    private String tpTributacaoAdvCredor;
    @Column(name = "nome_banco_adv_credor")
    private String nomeBancoAdvCredor;

    /**
     * Method to convert the TipoRequisicao entity to the TipoRequisicaoDTO
     * @return TipoRequisicaoDTO
     */
    public TipoRequisitorioDTO toMetadado(){
        
        // Create a new TipoRequisicaoDTO
        TipoRequisitorioDTO dto = new TipoRequisitorioDTO();
        
        // Set the properties of the DTO
        dto.setIdTipoOrbigacao(this.idTipoOrbigacao);
        dto.setIdTipoPrevidencia(this.idTipoPrevidencia);
        dto.setIdTipoTitulo(this.idTipoTitulo);
        dto.setTipoPrecatorio(this.tipoPrecatorio);
        dto.setDsTipoObrigacao(this.dsTipoObrigacao);
        dto.setNomeBancoCredor(this.nomeBancoCredor);
        dto.setAgenciaCredor(this.agenciaCredor);
        dto.setContaCorrenteCredor(this.contaCorrenteCredor);
        dto.setAgenciaAdvCredor(this.agenciaAdvCredor);
        dto.setContaCorrenteAdvCredor(this.contaCorrenteAdvCredor);
        dto.setSituacaoFuncionalCredor(this.situacaoFuncionalCredor);
        dto.setPercentHonorAdvCredor(this.percentHonorAdvCredor);
        
        // Return the DTO
        return dto;
    }
}
