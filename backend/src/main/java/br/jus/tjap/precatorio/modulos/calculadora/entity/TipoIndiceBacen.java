package br.jus.tjap.precatorio.modulos.calculadora.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "precatorio", name="tipo_indice_bacen")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
public class TipoIndiceBacen {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="descricao")
    private String descricao;

}
