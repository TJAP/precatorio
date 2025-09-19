package br.jus.tjap.precatorio.calculadora.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(schema="public", name="indicador_indice")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
public class IndicadorIndice {

    @Id
    @Column(name="id")
    private Long id;

    @JoinColumn(name = "fk_indicador_financeiro_tipo")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private TipoInidicadorIndice tipoInidicadorIndice;

    @Column(name="dt_ini_vigencia")
    private LocalDate dataInicioVigencia;

    @Column(name="dt_fim_vigencia")
    private LocalDate dataFimVigencia;

    @Column(name="valor")
    private BigDecimal valor;
}
