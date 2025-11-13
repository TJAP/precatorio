package br.jus.tjap.precatorio.modulos.calculadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorrecaoDTO {

    private String tipoCalculo;
    private String tempoGraca;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal fatorValor;
    private BigDecimal jurosValor;

    private BigDecimal principalTributavel;
    private BigDecimal principalNaoTributavel;
    private BigDecimal juros;
    private BigDecimal multaCusta;
    private BigDecimal selic;
    private BigDecimal previdencia;
    private BigDecimal total;

    public BigDecimal getTotal() {
        return principalTributavel
                .add(principalNaoTributavel)
                .add(juros)
                .add(multaCusta)
                .add(selic);
    }
}
