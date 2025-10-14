package br.jus.tjap.precatorio.modulos.requisitorio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadosDeducaoDTO {

    private String banco;
    private String conta;
    private String agencia;
    private String dv;
    private BigDecimal valor;
    private BigDecimal valorDeducao;
    private String tributacao_irrf;
    private String percentual_honorarios;
    private BigDecimal outros_valores_honorarios;
    private String observacao;
    private String descricao;
    private LocalDate data_nascimento_pessoa_destino;
    private BigDecimal porcentagemCessao;

}
