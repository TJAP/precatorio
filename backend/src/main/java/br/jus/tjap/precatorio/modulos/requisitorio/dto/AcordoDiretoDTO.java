package br.jus.tjap.precatorio.modulos.requisitorio.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcordoDiretoDTO {

    private Long id;
    private String numeroProcesso;
    private String nomeParte;
    private String documentoParte;
    private String tipoParte;
    private BigDecimal percentualHonorario;
    private String tipoTributacao;
    private String banco;
    private String agencia;
    private String conta;
    private String digito;
    private BigDecimal percentualDesagio;
}
