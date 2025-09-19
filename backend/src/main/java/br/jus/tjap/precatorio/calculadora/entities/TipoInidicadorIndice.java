package br.jus.tjap.precatorio.calculadora.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema="public", name="indicador_financeiro_tipo")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
public class TipoInidicadorIndice {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="descricao")
    private String descricao;

    @Column(name="correcao_monetaria")
    private Boolean correcaoMonetaria;
}
