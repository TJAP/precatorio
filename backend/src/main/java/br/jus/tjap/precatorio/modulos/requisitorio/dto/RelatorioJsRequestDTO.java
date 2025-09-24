package br.jus.tjap.precatorio.modulos.requisitorio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioJsRequestDTO {

    private String dataAtual;
    private boolean minuta;
    private String nomeJuiz;
    private boolean rpv;
    private boolean precatorio;

    // Dados ofício
    private String numeroPrecatorio;
    private String nomeComarca;
    private String nomeVara;
    private String numeroProcesso;
    private String numeroProcessoTucujuris;
    private String tipoDocumento;

    // I dados devedor
    private String nomeDevedor;
    private String documentoDevedor;
    private String nomeDevedorAdv;

    // II Natureza do precatório
    private String naturezaCredito;
    private String creditoPreferencial;

    // III Natureza do obrigação
    private String tipoObrigacao;
    private String dsTipoObrigacao;

    // IV Informações do processo
    private String tipoTitulo;
    private String dataAjuizamento;
    private String dataTransitoJulgadoConhecimento;
    private String dataTransitoJulgadoEmbargos;
    private String dataDecursoPrazo;
    private String dataTransitoJulgado;

    // V dados credor
    private String nomeCredor;
    private String representanteCredor;
    private String credorNaturezaQualificacao;
    private String documentoCredor;
    private String nomeCredorAdv;
    private String documentoCredorAdv;
    private String nascimentoCredor;
    private String bancoCredor;
    private String agenciaCredor;
    private String contaCorrenteCredor;
    private String digitoVerificadorCredor;
    private String nascimentoAdvCredor;
    private String vlPercentualHonorarioAdvCredor;
    private String tipoTributacaoAdvCredor;
    private String nomeCredorRepresentante;
    private String docCredorRepresentante;
    private String nascimentoRepresentanteCredor;
    private String idCredorNaturezaQualificacao;
    private String orgaoVinculoCredor;
    private String situacaoFuncionalCredor;
    private String nomeBancoCredor;
    private String nomeBancoAdvCredor;
    private String agenciaAdvCredor;
    private String contaCorrenteAdvCredor;

    private boolean temRepresentante;
    private boolean temAdvogado;

    // V dados do crédito
    /*private String valorBruto;
    private String dataBaseMonetaria;
    private String dataBaseMoratorios;
    private String indiceAtualizacaoSentenca;
    private String jurosMoratoriosSentenca;
    private String debitoCompensado;
    private String dataBaseAtualizacaoMonetaria;
    private String indiceAtualizacaoCompensado;
    private String jurosMoratorioCompensado;
    private String naturezaDebitoCompensado;
    private String valorRemanescenteSerPago;
    private String especificacaoTributo;*/

    private String vlGlobalRequisicao;
    private String vlPrincipalTributavelCorrigido;
    private String vlPrincipalNaoTributavelCorrigido;
    private String indiceAtualizacao;
    private String idTaxaJurosAplicadas;
    private String vlJurosAplicado;
    private String devolucaoCusta;
    private String vlDevolucaoCusta;
    private String pagamentoMulta;
    private String vlPagamentoMulta;
    private String dtUltimaAtualizacaoPlanilha;
    private String numeroMesesRendimentoAcumulado;
    private String vlPrevidencia;
    private String orgaoPrevidencia;
    private String averbacaoPenhora;
    private String vlAverbacaoPenhora;
    private String sessaoCredito;
    private String vlSessaoCredito;
    private String pagamentoAdministrativo;
    private String vlPagamentoAdministrativo;
}
