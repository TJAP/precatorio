package br.jus.tjap.precatorio.modulos.calculadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoTucujurisDTO {

    private Long idValorPago;
    private BigDecimal valorAtualizado;
    private Long idProcessoTucujuris;
    private LocalDate dtAtualizacao;
    private Integer tpAtualizacao;
    public String getTpAtualizacaoStr(){
        if(this.tpAtualizacao == 1){
            return "Normal";
        }

        if(this.tpAtualizacao == 2){
            return "Acordo Direto";
        }

        return "";
    }

}
