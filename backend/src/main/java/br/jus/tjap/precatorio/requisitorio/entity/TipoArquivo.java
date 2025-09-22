package br.jus.tjap.precatorio.requisitorio.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = TipoArquivo.TABLE_NAME, schema="precatorio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoArquivo implements Serializable {

    public static final String TABLE_NAME = "tipo_arquivo";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_" + TABLE_NAME)
    @SequenceGenerator(name = "gen_" + TABLE_NAME, sequenceName = "precatorio.sq_" + TABLE_NAME, allocationSize = 1)
    private Long id;

    private String descricao;

    @Column(name = "id_grupo_anexo_tucujuris")
    private Integer idGrupoAnexoTucujuris;

    @Column(name = "id_tipo_documento_pje")
    private String idTipoDocumentoPje;

    @Column(name = "retorna_pje")
    private boolean retornaDoPje;
}

