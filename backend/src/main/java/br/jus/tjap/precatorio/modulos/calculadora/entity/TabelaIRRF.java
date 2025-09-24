package br.jus.tjap.precatorio.modulos.calculadora.entity;

import br.jus.tjap.precatorio.modulos.calculadora.dto.TabelaIRRFDTO;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private BigDecimal valorAliquota;

    @Column(name = "deducao")
    private BigDecimal valorDeducao;

    public TabelaIRRFDTO toCalculoCorrigido(BigDecimal totalMeses){
        var tabela = new TabelaIRRFDTO();

        if(!UtilCalculo.isNotNullOrZero(totalMeses)){
            totalMeses = BigDecimal.ZERO;
        }

        tabela.setValorFaixaInicial(this.valorFaixaInicial);
        tabela.setValorFaixaFinal(this.valorFaixaFinal);
        tabela.setValorAliquota(this.valorAliquota);
        tabela.setValorDeducao(this.valorDeducao);

        tabela.setValorFaixaInicialCalculado(this.valorFaixaInicial.multiply(totalMeses));
        tabela.setValorFaixaFinalCalculado(this.valorFaixaFinal.multiply(totalMeses));
        tabela.setValorAliquotaCalculado(this.valorAliquota);
        tabela.setValorDeducaoCalculado(this.valorDeducao.multiply(totalMeses));

        return tabela;
    }
}
