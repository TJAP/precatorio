package br.jus.tjap.precatorio.modulos.requisitorio.dto;


import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitorioRequestDTO {
    private long id;
    private String idProcesso; 
    private long idNaturezaCredito;
    private BigDecimal vlGlobalRequisicao;
    private BigDecimal vlPrincipalTributavelCorrigido;
    private BigDecimal vlPrincipalNaoTributavelCorrigido;
    private Integer indiceAtualizacao;
    private BigDecimal vlJurosAplicado;
    private LocalDate dtUltimaAtualizacaoPlanilha;
    private BigDecimal valorCausa;
}
