package br.jus.tjap.precatorio.calculadora.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(schema = "precatorio", name = "tabela_irrf")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TabelaIRRF implements Serializable {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name = "faixa_inicial")
    private BigDecimal valorFaixaInicial;

    @Column(name = "faixa_final")
    private BigDecimal valorFaixaFinal;

    @Column(name = "aliquota")
    private BigDecimal valorAliquita;

    @Column(name = "deducao")
    private BigDecimal valorDeducao;
}
