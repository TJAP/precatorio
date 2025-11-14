package br.jus.tjap.precatorio.modulos.calculadora.dto;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoCalculoPrecatorioDTO {

    private RequisitorioDTO requisitorio;
    private ResultadoAtualizacaoPrecatorioDTO atualizacao;
    private ResultadoPagamentoCalculoPrioridadeDTO prioridade;
    private ResultadoPagamentoRateioDTO rateio;
    private ResultadoPagamentoImpostoDTO imposto;
}
