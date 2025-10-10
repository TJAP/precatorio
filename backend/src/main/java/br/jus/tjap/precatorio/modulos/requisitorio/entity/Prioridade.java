package br.jus.tjap.precatorio.modulos.requisitorio.entity;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.PrioridadeDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = Prioridade.TABLE_NAME, schema = "precatorio")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "applications", "hibernateLazyInitializer" })
public class Prioridade implements Serializable {

    public static final String TABLE_NAME = "prioridade";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_" + TABLE_NAME)
    @SequenceGenerator(name = "gen_" + TABLE_NAME, sequenceName = "precatorio.sq_" + TABLE_NAME, allocationSize = 1)
    private Long id;

    @Column(name = "id_tipo_prioridade")
    private Integer idTipoPrioridade;

    @Column(name = "dt_credito_preferencia")
    private LocalDate dataLancamento;

    @Column(name = "id_comarca")
    private Integer comarca;

    @JoinColumn(name = "id_precatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    private Requisitorio requisitorio;

    public PrioridadeDTO toDTO(){
        PrioridadeDTO dto = new PrioridadeDTO();

        dto.setId(this.id);
        dto.setDataLancamento(this.dataLancamento);
        dto.setIdTipoPrioridade(this.idTipoPrioridade);
        dto.setComarca(this.comarca);
        return dto;
    }


}
