package br.jus.tjap.precatorio.modulos.requisitorio.entity;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.AcordoDiretoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = AcordoDireto.TABLE_NAME, schema = "precatorio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcordoDireto {

    public static final String TABLE_NAME = "vw_acordo";

    @Id
    private Long id;

    @Column(name="numero_cnj")
    private String numeroProcesso;
    @Column(name="nome")
    private String nomeParte;
    @Column(name="cpfcnpj_tratado")
    private String documentoParte;
    @Column(name="tipo_adesao")
    private String tipoParte;
    @Column(name="percentual_honorarios")
    private BigDecimal percentualHonorario;
    @Column(name="tipo_tributacao")
    private String tipoTributacao;
    @Column(name="banco")
    private String banco;
    @Column(name="agencia")
    private String agencia;
    @Column(name="conta")
    private String conta;
    @Column(name="dv_conta")
    private String digito;
    @Column(name="desagio")
    private BigDecimal percentualDesagio;

    public AcordoDiretoDTO toDTO(){
        AcordoDiretoDTO dto = new AcordoDiretoDTO();

        dto.setId(this.id);
        dto.setNumeroProcesso(this.numeroProcesso);
        dto.setDocumentoParte(this.documentoParte);
        dto.setNomeParte(this.nomeParte);
        dto.setTipoParte(this.tipoParte);
        dto.setPercentualHonorario(this.percentualHonorario);
        dto.setPercentualDesagio(this.percentualDesagio);
        dto.setTipoTributacao(this.tipoTributacao);
        dto.setBanco(this.getBanco());
        dto.setAgencia(this.agencia);
        dto.setConta(this.conta);
        dto.setDigito(this.digito);

        return dto;
    }

}
