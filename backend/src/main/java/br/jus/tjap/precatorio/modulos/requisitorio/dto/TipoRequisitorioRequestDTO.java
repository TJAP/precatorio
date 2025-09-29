package br.jus.tjap.precatorio.modulos.requisitorio.dto;

import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TipoRequisitorioRequestDTO {
    private Integer tipoPrecatorio;
    private String nomeBancoCredor;
    private long idTipoObrigacao;
    private long idTipoPrevidencia;
    private long idTipoTitulo;
    private String dsTipoObrigacao;
    private Boolean sessaoCredito;
}
