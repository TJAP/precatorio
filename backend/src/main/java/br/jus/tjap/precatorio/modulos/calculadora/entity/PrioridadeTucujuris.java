package br.jus.tjap.precatorio.modulos.calculadora.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "public", name = "processo_precatorio_prioridade")
public class PrioridadeTucujuris {

    @Id
    private Long id;

    @Column(name = "fk_processo_precatorio")
    private Long idProcessoTucujuris;

    @Column(name = "fk_tipo_prioridade")
    private Long idTipoPrioridade;

    @Column(name = "dt_credito_preferencial")
    private LocalTime dtCreditoPreferencial;

}
