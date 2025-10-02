package br.jus.tjap.precatorio.modulos.tabelasbasicas.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnteDevedorDTO {

    private Long id;
    private String nome;
    private String cnpj;
    private BigDecimal limitePrioridade;
    private String comVinculo;
    private String semVinculo;
    private String numeroConta;
    private String numeroContaAcordo;
}
