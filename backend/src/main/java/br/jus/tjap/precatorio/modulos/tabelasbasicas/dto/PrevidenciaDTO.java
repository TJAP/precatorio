package br.jus.tjap.precatorio.modulos.tabelasbasicas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrevidenciaDTO {

    private Long id;
    private String nome; // PF ou PJ
    private String tipo;
    private BigDecimal valorTeto;
    private String banco;
    private String agencia;
    private String conta;
    private String tipoConta;
    private String digitoConta;
    private LocalDateTime dtAtualizacao;
}
