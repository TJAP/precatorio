package br.jus.tjap.precatorio.modulos.requisitorio.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PessoasPrecatorioDTO {

    private String nome;
    private String numeroDocumento;
    private String tipoPessoa;

    private String nomeBanco;
    private String tipoConta;
    private String agencia;
    private String conta;
    private String dvConta;

    private String percentual;
    private String valor;
    private String processoDestino;
    private String valorTotal;
}
