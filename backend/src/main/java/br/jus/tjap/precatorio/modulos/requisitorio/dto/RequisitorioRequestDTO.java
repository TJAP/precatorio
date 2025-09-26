package br.jus.tjap.precatorio.modulos.requisitorio.dto;


import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitorioRequestDTO {
    
    private String Processo;
    private String precato;
    private String nomeMagistrado;
    private String numeroPrecatorio;
    private String nomeDevedor;
    private String nomeCredor;
    private String documentoDevedor;
    private String documentoCredor;
    private Date nascimentoDevedor;
    private Date nascimentoCredor;
    private String representanteCredor;
    private String credorNaturezaQualificacao;
    private String naturezaCredito;
    private String creditoPreferencial;
    private String tipoObrigacao;
    private String dsTipoObrigacao;
    private String tipoTitulo;
    private Date dataAjuizamento;
    private Date dataTransitoJulgadoConhecimento;
    private Date dataTransitoJulgadoEmbargos;
    private Date dataDecursoPrazo;
    private Date dataTransitoJulgado; 
}
