package br.jus.tjap.precatorio.modulos.calculadora.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "public", name = "processo_precatorio_valor")
public class PagamentoTucujuris {

    @Id
    @Column(name = "id")
    private Long idValorPago;

    @Column(name = "fk_processo_precatorio")
    private Long idProcessoTucujuris;

    @Column(name = "vl_atualizado")
    private BigDecimal valorAtualizado;

    @Column(name = "dt_atualizacao")
    private LocalDate dtAtualizacao;

    @Column(name = "tp_atualizacao")
    private Integer tpAtualizacao;
}
