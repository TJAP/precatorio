package br.jus.tjap.precatorio.modulos.requisitorio.entity;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.RelatorioJsRequestDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import br.jus.tjap.precatorio.modulos.tabelasbasicas.entity.EnteDevedor;
import br.jus.tjap.precatorio.util.DateUtil;
import br.jus.tjap.precatorio.util.StringUtil;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;

@Entity
@Table(name = Requisitorio.TABLE_NAME, schema="precatorio")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Requisitorio implements Serializable {

    public static final String TABLE_NAME = "precatorio";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_" + TABLE_NAME)
    @SequenceGenerator(name = "gen_" + TABLE_NAME, sequenceName = "precatorio.sq_" + TABLE_NAME, allocationSize = 1)
    private Long id;

    //1: OR, 2:RPV
    @Column(name = "tp_precatorio")
    private Integer tipoPrecatorio;

    @Column(name = "id_processo")
    private String idProcesso;
    @Column(name = "num_processo_tucujuris")
    private String numProcessoTucujuris;
    @Column(name = "nome_magistrado")
    private String nomeMagistrado;
    @Column(name = "id_tipo_titulo")
    private Long idTipoTitulo;
    @Column(name = "id_natureza_credito")
    private Long idNaturezaCredito;
    @Column(name = "id_tipo_previdencia")
    private Long idTipoPrevidencia;
    @Column(name = "id_tipo_orbigacao")
    private Long idTipoOrbigacao;
    @Column(name = "ds_tipo_obrigacao")
    private String dsTipoObrigacao;
    @Column(name = "dt_ajuizamento")
    private LocalDate dtAjuizamento;
    @Column(name = "dt_decurso_prazo")
    private LocalDate dtDecursoPrazo;
    @Column(name = "dt_transito_julgado_conhecimento")
    private LocalDate dtTransitoJulgadoConhecimento;
    @Column(name = "dt_transito_julgado_embargos")
    private LocalDate dtTransitoJulgadoEmbargos;

    // dados devedor
    @Column(name = "nome_devedor")
    private String nomeDevedor;
    @Column(name = "doc_devedor")
    private String documentoDevedor;
    @Column(name = "dt_nascimento_devedor")
    private LocalDate nascimentoDevedor;
    @Column(name = "nome_adv_devedor_")
    private String nomeDevedorAdv;
    @Column(name = "doc_adv_devedor")
    private String documentoDevedorAdv;

    // dados credor
    @Column(name = "nome_credor")
    private String nomeCredor;
    @Column(name = "doc_credor")
    private String documentoCredor;
    @Column(name = "dt_nascimento_credor")
    private LocalDate nascimentoCredor;
    @Column(name = "nome_adv_credor")
    private String nomeCredorAdv;
    @Column(name = "doc_adv_credor")
    private String documentoCredorAdv;
    @Column(name = "dt_nascimento_adv_credor")
    private LocalDate nascimentoAdvCredor;
    @Column(name = "percent_honor_adv_credor")
    private BigDecimal vlPercentualHonorarioAdvCredor;
    @Column(name = "tp_tributacao_adv_credor")
    private String idTipoTributacaoAdvCredor;

    @Column(name = "nome_representante_credor")
    private String nomeCredorRepresentante;
    @Column(name = "doc_representante_credor")
    private String docCredorRepresentante;
    @Column(name = "dat_nascimento_representante_credor")
    private LocalDate nascimentoRepresentanteCredor;
    @Column(name = "id_natureza_qualificacao_credor")
    private Long idCredorNaturezaQualificacao;


    @Column(name = "orgao_vinculo_credor")
    private String orgaoVinculoCredor;
    @Column(name = "situcao_funcional_credor")
    private String situacaoFuncionalCredor;
    @Column(name = "nome_banco_credor")
    private String nomeBancoCredor;
    @Column(name = "agencia_bancaria_credor")
    private String agenciaCredor;
    @Column(name = "conta_corrente_credor")
    private String contaCorrenteCredor;
    @Column(name = "nome_banco_adv_credor")
    private String nomeBancoAdvCredor;
    @Column(name = "agencia_bancaria_adv_credor")
    private String agenciaAdvCredor;
    @Column(name = "conta_corrente_adv_credor")
    private String contaCorrenteAdvCredor;

    @Column(name = "vl_causa")
    private BigDecimal valorCausa;
    //@Column(name = "situacao", columnDefinition = "varchar(10) default 'ABERTO'")
    //private SituacaoPrecatorioEnum situacao;

    // novos campos
    @Column(name = "vl_global_requisicao")
    private BigDecimal vlGlobalRequisicao;
    @Column(name = "vl_principal_tributavel_corrigido")
    private BigDecimal vlPrincipalTributavelCorrigido;
    @Column(name = "vl_principal_nao_tributavel_corrigido")
    private BigDecimal vlPrincipalNaoTributavelCorrigido;
    @Column(name = "indice_atualizacao")
    private Integer indiceAtualizacao;
    @Column(name = "id_taxa_juros_aplicada")
    private BigDecimal idTaxaJurosAplicadas;
    @Column(name = "vl_juros_aplicado")
    private BigDecimal vlJurosAplicado;

    @Column(name = "in_devolucao_custa")
    private Boolean devolucaoCusta;
    @Column(name = "vl_devolucao_custa")
    private BigDecimal vlDevolucaoCusta;

    @Column(name = "in_pagamento_multa")
    private Boolean pagamentoMulta;
    @Column(name = "vl_pagamento_multa")
    private BigDecimal vlPagamentoMulta;

    @Column(name = "dt_base_ultima_atualizacao")
    private LocalDate dtUltimaAtualizacaoPlanilha;

    // imposto de renda
    @Column(name = "in_retencao_ir")
    private Boolean retencaoImposto;
    @Column(name = "vl_retencao_ir")
    private BigDecimal vlRetencaoImposto;
    @Column(name = "nm_meses_rendimento_recebido_acumulado")
    private Integer numeroMesesRendimentoAcumulado;

    // previdencia
    @Column(name = "in_pagamento_previdenciario")
    private Boolean pagamentoPrevidenciario;
    @Column(name = "vl_previdencia")
    private BigDecimal vlPrevidencia;
    @Column(name = "orgao_previdencia")
    private Integer orgaoPrevidencia;

    // averbação
    @Column(name = "in_averbacao_penhora")
    private Boolean averbacaoPenhora;
    @Column(name = "vl_averbacao_penhora")
    private BigDecimal vlAverbacaoPenhora;

    // sessão crédito
    @Column(name = "in_sessao_credito")
    private Boolean sessaoCredito;
    @Column(name = "vl__sessao_credito")
    private BigDecimal vlSessaoCredito;

    // pagamento administrativo
    @Column(name = "in_pagamento_administrativo")
    private Boolean pagamentoAdministrativo;
    @Column(name = "vl_pagamento_administrativo")
    private BigDecimal vlPagamentoAdministrativo;

    @Column(name = "dt_assinatura")
    private LocalDateTime dtAssinatura;
    @Column(name = "assinador")
    private String assinador;
    @Column(name = "id_pe_assinador")
    private Integer idPeAssinador;

    @Column(name = "id_orgao_julgador_pje")
    private Integer codOrgaoJulgadorPje;

    @Column(name = "id_orgao_julgador_tucujuris")
    private Integer codOrgaoJulgadorTucujuris;

    @Basic(fetch = FetchType.LAZY)
    //@Type(type="org.hibernate.type.BinaryType")
    @Column(name = "arquivo_pdf")
    @JsonManagedReference
    private byte[] arquivoPdf;

    @Column(name = "id_precatorio_tucujuris")
    private Long idPrecatorioTucujuris;

    @Column(name = "dt_cadastro")
    private LocalDateTime dtCadastro;

    @Column(name = "dt_atualizacao")
    private LocalDateTime dtAtualizacao;

    @Column(name = "ds_justificativa_invalidade")
    private String dsJustificativaInvalidade;

    @Column(name = "ativo")
    private boolean ativo;

    @Column(name = "msg_erro_distribuicao")
    private String msgErroDistribuicao;

    @JoinColumn(name = "id_ente_devedor")
    @ManyToOne
    private EnteDevedor enteDevedor;

    @Transient
    private String numeroPrecatorio;

    @Transient
    private String precato;

    public String getNumeroProcessoPJE(){
        return StringUtil.formataNumeroProcesso(getIdProcesso());
    }


    public RequisitorioDTO toMetadado(){

        var dto = new RequisitorioDTO();

        dto.setId(this.id);
        dto.setIdProcesso(this.idProcesso);
        dto.setNumProcessoTucujuris(this.numProcessoTucujuris);
        dto.setPrecato(this.precato);
        dto.setNomeMagistrado(this.nomeMagistrado);
        dto.setIdTipoTitulo(this.idTipoTitulo);
        dto.setIdNaturezaCredito(this.idNaturezaCredito);
        dto.setIdTipoPrevidencia(this.idTipoPrevidencia);
        dto.setIdTipoOrbigacao(this.idTipoOrbigacao);
        dto.setDsTipoObrigacao(this.dsTipoObrigacao);
        dto.setDtAjuizamento(this.dtAjuizamento);
        dto.setDtDecursoPrazo(this.dtDecursoPrazo);
        dto.setDtTransitoJulgadoConhecimento(this.dtTransitoJulgadoConhecimento);
        dto.setDtTransitoJulgadoEmbargos(this.dtTransitoJulgadoEmbargos);
        dto.setNumeroPrecatorio(this.numeroPrecatorio);
        dto.setNomeDevedor(this.nomeDevedor);
        dto.setDocumentoDevedor(this.documentoDevedor);
        dto.setNascimentoDevedor(this.nascimentoDevedor);
        dto.setNomeDevedorAdv(this.nomeDevedorAdv);
        dto.setDocumentoDevedorAdv(this.documentoDevedorAdv);
        dto.setNomeCredor(this.nomeCredor);
        dto.setDocumentoCredor(this.documentoCredor);
        dto.setNascimentoCredor(this.nascimentoCredor);
        dto.setNomeCredorAdv(this.nomeCredorAdv);
        dto.setDocumentoCredorAdv(this.documentoCredorAdv);
        dto.setNascimentoAdvCredor(this.nascimentoAdvCredor);
        dto.setVlPercentualHonorarioAdvCredor(this.vlPercentualHonorarioAdvCredor);
        dto.setIdTipoTributacaoAdvCredor(this.idTipoTributacaoAdvCredor);
        dto.setNomeCredorRepresentante(this.nomeCredorRepresentante);
        dto.setDocCredorRepresentante(this.docCredorRepresentante);
        dto.setNascimentoRepresentanteCredor(this.nascimentoRepresentanteCredor);
        dto.setIdCredorNaturezaQualificacao(this.idCredorNaturezaQualificacao);
        dto.setOrgaoVinculoCredor(this.orgaoVinculoCredor);
        dto.setSituacaoFuncionalCredor(this.situacaoFuncionalCredor);
        dto.setNomeBancoCredor(this.nomeBancoCredor);
        dto.setAgenciaCredor(this.agenciaCredor);
        dto.setContaCorrenteCredor(this.contaCorrenteCredor);
        dto.setNomeBancoAdvCredor(this.nomeBancoAdvCredor);
        dto.setAgenciaAdvCredor(this.agenciaAdvCredor);
        dto.setContaCorrenteAdvCredor(this.contaCorrenteAdvCredor);
        dto.setValorCausa(this.valorCausa);
        //dto.setSituacao(this.situacao);
        dto.setVlGlobalRequisicao(this.vlGlobalRequisicao);
        dto.setVlPrincipalTributavelCorrigido(this.vlPrincipalTributavelCorrigido);
        dto.setVlPrincipalNaoTributavelCorrigido(this.vlPrincipalNaoTributavelCorrigido);
        dto.setIndiceAtualizacao(this.indiceAtualizacao);
        dto.setIdTaxaJurosAplicadas(this.idTaxaJurosAplicadas);
        dto.setVlJurosAplicado(this.vlJurosAplicado);
        dto.setDevolucaoCusta(this.devolucaoCusta);
        dto.setVlDevolucaoCusta(this.vlDevolucaoCusta);
        dto.setPagamentoMulta(this.pagamentoMulta);
        dto.setVlPagamentoMulta(this.vlPagamentoMulta);
        dto.setDtUltimaAtualizacaoPlanilha(this.dtUltimaAtualizacaoPlanilha);
        dto.setRetencaoImposto(this.retencaoImposto);
        dto.setVlRetencaoImposto(this.vlRetencaoImposto);
        dto.setNumeroMesesRendimentoAcumulado(this.numeroMesesRendimentoAcumulado);
        dto.setPagamentoPrevidenciario(this.pagamentoPrevidenciario);
        dto.setVlPrevidencia(this.vlPrevidencia);
        dto.setOrgaoPrevidencia(this.orgaoPrevidencia);
        dto.setAverbacaoPenhora(this.averbacaoPenhora);
        dto.setVlAverbacaoPenhora(this.vlAverbacaoPenhora);
        dto.setSessaoCredito(this.sessaoCredito);
        dto.setVlSessaoCredito(this.vlSessaoCredito);
        dto.setPagamentoAdministrativo(this.pagamentoAdministrativo);
        dto.setVlPagamentoAdministrativo(this.vlPagamentoAdministrativo);
        dto.setTipoPrecatorio(this.tipoPrecatorio);
        dto.setDtAssinatura(this.dtAssinatura);
        dto.setAssinador(this.assinador);
        dto.setIdPeAssinador(this.idPeAssinador);
        dto.setCodOrgaoJulgadorPje(this.codOrgaoJulgadorPje);
        dto.setCodOrgaoJulgadorTucujuris(this.codOrgaoJulgadorTucujuris);
        dto.setArquivoPdf(this.arquivoPdf);
        dto.setIdPrecatorioTucujuris(this.idPrecatorioTucujuris);
        dto.setDtCadastro(this.dtCadastro);
        dto.setDtAtualizacao(this.dtAtualizacao);
        dto.setDsJustificativaInvalidade(this.dsJustificativaInvalidade);
        dto.setAtivo(this.ativo);
        dto.setMsgErroDistribuicao(this.msgErroDistribuicao);
        dto.setEnteDevedorDTO(this.enteDevedor.toMetadado());

        return dto;
    }

    public RelatorioJsRequestDTO toRelatorio(){
        var relatorio = new RelatorioJsRequestDTO();

        var minuta = this.dtAssinatura == null;

        relatorio.setRpv(this.tipoPrecatorio == 2);
        relatorio.setPrecatorio(this.tipoPrecatorio == 1);
        relatorio.setTipoDocumento(this.tipoPrecatorio == 1 ? "OFÍCIO REQUISITÓRIO DE PRECATÓRIO" : "REQUISIÇÃO DE PEQUENO VALOR");

        relatorio.setDataAtual(DateUtil.localDataFormatada(LocalDate.now(), "dd/MM/yyyy"));

        relatorio.setDataAtual(DateUtil.localDataFormatada(LocalDate.now(), "dd/MM/yyyy"));

        relatorio.setMinuta(minuta);
        relatorio.setNomeJuiz(this.nomeMagistrado);

        relatorio.setNumeroPrecatorio((minuta ? "xxxx" : StringUtil.leftPad(this.id + "", "0", 4)) + "-" + this.codOrgaoJulgadorPje + "" + this.tipoPrecatorio + "/" + this.dtCadastro.getYear());
        //relatorio.setNomeComarca(DeParaPrecatorioTucujuris.deParaComarca(this.lotacaoTucujuris.getComarca()));
        //relatorio.setNomeVara(this.lotacaoTucujuris.getDescricao());
        relatorio.setNumeroProcesso(this.idProcesso);

        relatorio.setNomeDevedor(this.nomeDevedor);
        relatorio.setDocumentoDevedor(this.documentoDevedor);
        relatorio.setNomeDevedorAdv(this.nomeDevedorAdv);

        if (this.idNaturezaCredito != null) {
            relatorio.setNaturezaCredito(this.idNaturezaCredito == 0L ? "Alimentar" : "Comum");
            //relatorio.setCreditoPreferencial(DeParaPrecatorioTucujuris.getPrioridade(this.prioridades));
        } else {
            relatorio.setNaturezaCredito("");
            relatorio.setCreditoPreferencial("");
        }
/*
        if (this.tipoObrigacao.getDescricao() != null) {
            relatorio.setTipoObrigacao(this.tipoObrigacao.getDescricao());
            relatorio.setDsTipoObrigacao(this.dsTipoObrigacao);
        } else {
            relatorio.setTipoObrigacao("");
            relatorio.setDsTipoObrigacao("");
        }
*/
        relatorio.setTipoTitulo(this.idTipoTitulo == 0L ? "Judicial" : "Extrajudicial");
        relatorio.setDataAjuizamento(DateUtil.localDataFormatada(this.dtAjuizamento, "dd/MM/yyyy"));
        relatorio.setDataTransitoJulgadoConhecimento(DateUtil.localDataFormatada(this.dtTransitoJulgadoConhecimento, "dd/MM/yyyy"));
        relatorio.setDataTransitoJulgadoEmbargos(DateUtil.localDataFormatada(this.dtTransitoJulgadoEmbargos, "dd/MM/yyyy"));
        relatorio.setDataDecursoPrazo(DateUtil.localDataFormatada(this.dtDecursoPrazo, "dd/MM/yyyy"));
        relatorio.setDataTransitoJulgado(DateUtil.localDataFormatada(this.dtTransitoJulgadoConhecimento, "dd/MM/yyyy"));

        relatorio.setNomeCredor(this.nomeCredor);
        relatorio.setDocumentoCredor(this.documentoCredor);
        relatorio.setNascimentoCredor(DateUtil.localDataFormatada(this.nascimentoCredor, "dd/MM/yyyy"));
        relatorio.setBancoCredor(this.nomeBancoCredor);
        relatorio.setAgenciaCredor(this.agenciaCredor);
        relatorio.setContaCorrenteCredor(this.contaCorrenteCredor);
        relatorio.setSituacaoFuncionalCredor(this.situacaoFuncionalCredor);
        relatorio.setOrgaoVinculoCredor(this.orgaoVinculoCredor);

        /*
        if (this.tipoQualificacao != null || !this.nomeCredorRepresentante.equals("")) {
            relatorio.setRepresentanteCredor(this.nomeCredorRepresentante);
            relatorio.setDocCredorRepresentante(this.docCredorRepresentante);
            relatorio.setNascimentoRepresentanteCredor(DateUtil.localDataFormatada(this.nascimentoRepresentanteCredor, "dd/MM/yyyy"));
            relatorio.setCredorNaturezaQualificacao(this.tipoQualificacao.getDescricao());
            relatorio.setTemRepresentante(true);
        } else {
            relatorio.setTemRepresentante(false);
        }
*/
        if (this.nomeCredorAdv != null) {
            relatorio.setNomeCredorAdv(this.nomeCredorAdv);
            relatorio.setDocumentoCredorAdv(this.documentoCredorAdv);
            relatorio.setNascimentoAdvCredor(DateUtil.localDataFormatada(this.nascimentoAdvCredor, "dd/MM/yyyy"));
            relatorio.setNomeBancoAdvCredor(this.nomeBancoAdvCredor);
            relatorio.setAgenciaAdvCredor(this.agenciaAdvCredor);
            relatorio.setContaCorrenteAdvCredor(this.contaCorrenteAdvCredor);
            relatorio.setTipoTributacaoAdvCredor(this.idTipoTributacaoAdvCredor);
            relatorio.setVlPercentualHonorarioAdvCredor(StringUtil.formatarValorMoeda(this.vlPercentualHonorarioAdvCredor));
            relatorio.setTemAdvogado(true);
        } else {
            relatorio.setTemAdvogado(false);
        }

        relatorio.setVlGlobalRequisicao(StringUtil.formatarValorMoeda(this.vlGlobalRequisicao));
        relatorio.setVlPrincipalTributavelCorrigido(StringUtil.formatarValorMoeda(this.vlPrincipalTributavelCorrigido));
        relatorio.setVlPrincipalNaoTributavelCorrigido(StringUtil.formatarValorMoeda(this.vlPrincipalNaoTributavelCorrigido));
        //relatorio.setIndiceAtualizacao(DeParaPrecatorioTucujuris.indiceAtualizacao(this.indiceAtualizacao.toString()));
        //relatorio.setIdTaxaJurosAplicadas(DeParaPrecatorioTucujuris.jurosAplicado(this.idTaxaJurosAplicadas.toString()));
        relatorio.setVlJurosAplicado(StringUtil.formatarValorMoeda(this.vlJurosAplicado));
        relatorio.setDevolucaoCusta(this.devolucaoCusta ? "SIM" : "NÃO");
        relatorio.setVlDevolucaoCusta(this.devolucaoCusta ? StringUtil.formatarValorMoeda(this.vlDevolucaoCusta) : "R$ 0,00");
        relatorio.setPagamentoMulta(this.pagamentoMulta ? "SIM" : "NÃO");
        relatorio.setVlPagamentoMulta(this.pagamentoMulta ? StringUtil.formatarValorMoeda(this.vlPagamentoMulta) : "R$ 0,00");
        relatorio.setDtUltimaAtualizacaoPlanilha(DateUtil.localDataFormatada(this.dtUltimaAtualizacaoPlanilha, "dd/MM/yyyy"));
        relatorio.setNumeroMesesRendimentoAcumulado(this.numeroMesesRendimentoAcumulado.toString());
        relatorio.setVlPrevidencia(StringUtil.formatarValorMoeda(this.vlPrevidencia));
        //relatorio.setOrgaoPrevidencia(this.tipoPrevidencia.getDescricao());
        relatorio.setAverbacaoPenhora(this.averbacaoPenhora ? "SIM" : "NÃO");
        relatorio.setVlAverbacaoPenhora(this.averbacaoPenhora ? StringUtil.formatarValorMoeda(this.vlAverbacaoPenhora) : "R$ 0,00");
        relatorio.setSessaoCredito(this.sessaoCredito ? "SIM" : "NÃO");
        relatorio.setVlSessaoCredito(this.sessaoCredito ? StringUtil.formatarValorMoeda(this.vlSessaoCredito) : "R$ 0,00");
        relatorio.setPagamentoAdministrativo(this.pagamentoAdministrativo ? "SIM" : "NÃO");
        relatorio.setVlPagamentoAdministrativo(this.pagamentoAdministrativo ? StringUtil.formatarValorMoeda(this.vlPagamentoAdministrativo) : "R$ 0,00");

        return relatorio;
    }

}

