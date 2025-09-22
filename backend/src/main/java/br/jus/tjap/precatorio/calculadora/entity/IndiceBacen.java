package br.jus.tjap.precatorio.calculadora.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(schema = "precatorio", name="indice_bacen")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
public class IndiceBacen implements Serializable {

    @Id
    @Column(name="id")
    private Long id;

    @JoinColumn(name = "id_tipo_indice_bacen")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private TipoIndiceBacen tipoInidicadorIndice;

    @Column(name="dt_referencia")
    private LocalDate dataInicioVigencia;

    @Column(name="valor")
    private BigDecimal valor;

    public IndiceBacen(Long id, TipoIndiceBacen tipoInidicadorIndice, LocalDate dataInicioVigencia, BigDecimal valor) {
        this.id = id;
        this.tipoInidicadorIndice = tipoInidicadorIndice;
        this.dataInicioVigencia = dataInicioVigencia;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoIndiceBacen getTipoInidicadorIndice() {
        return tipoInidicadorIndice;
    }

    public void setTipoInidicadorIndice(TipoIndiceBacen tipoInidicadorIndice) {
        this.tipoInidicadorIndice = tipoInidicadorIndice;
    }

    public LocalDate getDataInicioVigencia() {
        return dataInicioVigencia;
    }

    public void setDataInicioVigencia(LocalDate dataInicioVigencia) {
        this.dataInicioVigencia = dataInicioVigencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
