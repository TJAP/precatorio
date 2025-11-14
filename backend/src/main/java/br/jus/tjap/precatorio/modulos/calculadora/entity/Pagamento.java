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

@Entity
@Table(schema = "public", name = "processo_precatorio_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagamento {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="fk_processo_precatorio")
    private Long idPrecatorioTucujuris;

    @Column(name="valor")
    private BigDecimal valor;

    @Column(name="valor_pagamento")
    private BigDecimal valorPagamento;

    @Column(name="dt_pagamento")
    private LocalDate dtPagamento;

    @Column(name = "status_item")
    private Integer statusItem;
}
