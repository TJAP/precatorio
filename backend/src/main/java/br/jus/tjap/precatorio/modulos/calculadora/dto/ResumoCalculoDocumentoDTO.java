package br.jus.tjap.precatorio.modulos.calculadora.dto;

import br.jus.tjap.precatorio.modulos.calculadora.util.RelatorioUtil;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumoCalculoDocumentoDTO {

    private Long idPrecatorio;
    // dados precatório
    private int ordemPagamento;
    private String processoPrecatorio;
    private String processoPrecatorioOrigem;
    private String devedorNome;
    private String devedorDocumento;
    private String devedorContaJudicial;

    private String credorNome;
    private String credorDocumento;
    private String credorNascimento;
    private int idadeCredor;
    private String credorBanco;
    private String credorAgencia;
    private String credorConta;
    private String credorDV;

    private String advCredorNome;
    private String advCredorBanco;
    private String advCredorAgencia;
    private String advCredorConta;
    private String advCredorDV;
    private String alvaraDescricaoAdvogado = "Transferência para o Advogado";

    private String temPrioridade = "Não";
    private String temAcordo = "Não";
    private String temPagamentoParcial = "Não";
    private String temCessaoCredito = "Não";
    private String temSucessao = "Não";
    private String temPenhora = "Não";
    private String indicePrecatorio = "IPCA";

    private String descricaoAlvaraPenhora = "Transferência para Vara";

    private String sucessaoNome;
    private String sucessaoNumeroDocumento;
    private String sucessaoBanco;
    private String sucessaoTipoConta;
    private String sucessaoAgencia;
    private String sucessaoConta;
    private String sucessaoDVConta;

    private String cessaoNome;
    private String cessaoNumeroDocumento;
    private String cessaoBanco;
    private String cessaoTipoConta;
    private String cessaoAgencia;
    private String cessaoConta;
    private String cessaoDVConta;

    // dados do requisitório
    private String requisitorioValorPrincipalTributavel;
    private String requisitorioValorPrincipalNaoTributavel;
    private String requisitorioValorJuros;
    private String requisitorioValorMulta;
    private String requisitorioValorCust;
    private String requisitorioValorOutros;
    private String requisitorioCustaMuoltaOutros;
    private String requisitorioValorSelic;
    private String requisitorioValorTotal;
    private String requisitorioUltimaAtualizacao;
    private String requisitorioTipoAcaoPrecatorio;
    private String requisitorioValorPrevidencia;
    private long requisitorioNumeroRRA;
    private int requisitorioAnoVencimento;

    // indice e fatores utilizados
    private String variavelDataFimCalculo;
    private BigDecimal variavelFatorAntesGraca;
    private BigDecimal variavelFatorDuranteGraca;
    private BigDecimal variavelFatorAposGraca;
    private BigDecimal variavelTaxaAntesGraca;
    private BigDecimal variavelTaxaDuranteGraca;
    private BigDecimal variavelTaxaAposGraca;

    // valores atualizados
    private String atualizacaoValorPrincipalTributavel;
    private String atualizacaoValorPrincipalNaoTributavel;
    private String atualizacaoValorJuros;
    private String atualizacaoValorMultaCustOutros;
    private String atualizacaoValorSelic;
    private String atualizacaoValorTotal;
    private long atualizacaoNumeroRRA;
    private String atualizacaoValorPrevidencia;
    private String atualizacaoValorLimitePrioridade;
    private String atualizacaoLimitacaoPagamento;
    private String atualizacaoAcordoDireto;

    // tributação
    private String tributacaoHonorarioPercentual;
    private String tributacaoHonorarioMontanteDesembolso;
    private String tributacaoHonorarioBaseTributacao;
    private String tributacaoHonorarioTipoTributacao;
    private String tributacaoHonorarioImposto;
    private String tributacaoCredorPercentual;
    private String tributacaoCredorMontanteDesembolso;
    private String tributacaoCredorBaseTributacao;
    private String tributacaoCredorTipoTributacao;
    private String tributacaoCredorImposto;
    private String tributacaoCredorPrevidencia;

    private String desagioPercentual;
    private String desagioHonorarioValorBase;
    private String desagioCredorValorBase;

    // resumo do alvará
    private String alvaraDevedorNome;
    private String alvaraIRRFCredor;
    private String alvaraOrgaoPrevidenciaNome;
    private String alvaraValorPrevidencia;
    private String alvaraValorHonorarioContratualLiquido;
    private String alvaraIRRFHonorario;
    private String alvaraValorPenhora;
    private String alvaraValorCessao;
    private String alvaraValorLiquidoCredor;
    private String alvaraValorTotalSomado;

    // saldo remanescente
    // valores atualizados
    private String remanescentePercentual;
    private String remanescenteValorPrincipalTributavel;
    private String remanescenteValorPrincipalNaoTributavel;
    private String remanescenteValorJuros;
    private String remanescenteValorMultaCustOutros;
    private String remanescenteValorSelic;
    private String remanescenteValorTotal;
    private long remanescenteNumeroRRA;
    private String remanescenteValorPrevidencia;

    private String dataCorrente;
    private String usuarioCalculo;

    public ResumoCalculoDocumentoDTO montarResumoDocumento(CalculoRequisitorioDTO resumo){
        ResumoCalculoDocumentoDTO doc = new ResumoCalculoDocumentoDTO();

        doc.setProcessoPrecatorio(StringUtil.formataNumeroProcesso(resumo.getRequisitorioDTO().getIdProcesso()));
        doc.setProcessoPrecatorioOrigem(StringUtil.formataNumeroProcesso(resumo.getRequisitorioDTO().getNumProcessoTucujuris()));
        doc.setDevedorNome(resumo.getRequisitorioDTO().getEnteDevedorDTO().getNome());
        doc.setDevedorDocumento(resumo.getRequisitorioDTO().getEnteDevedorDTO().getCnpj());
        doc.setDevedorContaJudicial(resumo.getRequisitorioDTO().getEnteDevedorDTO().getNumeroConta());
        doc.setCredorNome(resumo.getRequisitorioDTO().getNomeCredor());
        doc.setCredorDocumento(resumo.getRequisitorioDTO().getDocumentoCredor());
        if(resumo.getRequisitorioDTO().getNascimentoCredor() != null){
            doc.setCredorNascimento(StringUtil.formataDataDMY(resumo.getRequisitorioDTO().getNascimentoCredor()));
        }else{
            doc.setCredorNascimento("Não possui");
        }
        // TODO - Verificar o banco do usuário
        doc.setCredorBanco(resumo.getRequisitorioDTO().getNomeBancoCredor());
        doc.setCredorAgencia(resumo.getRequisitorioDTO().getAgenciaCredor());
        doc.setCredorConta(resumo.getRequisitorioDTO().getContaCorrenteCredor());
        doc.setCredorDV("0");
        doc.setAdvCredorBanco(resumo.getRequisitorioDTO().getNomeBancoAdvCredor());
        doc.setAdvCredorAgencia(resumo.getRequisitorioDTO().getAgenciaAdvCredor());
        doc.setAdvCredorConta(resumo.getRequisitorioDTO().getContaCorrenteAdvCredor());
        doc.setAdvCredorDV("0");
        doc.setIdadeCredor(Period.between(Objects.isNull(resumo.getRequisitorioDTO().getNascimentoCredor())? LocalDate.now().minusYears(1) : resumo.getRequisitorioDTO().getNascimentoCredor(), LocalDate.now()).getYears());
        doc.setRequisitorioValorPrincipalTributavel(RelatorioUtil.formatarValorMoeda(resumo.getRequest().getValorPrincipalTributavel()));
        doc.setRequisitorioValorPrincipalNaoTributavel(RelatorioUtil.formatarValorMoeda(resumo.getRequest().getValorPrincipalNaoTributavel()));
        doc.setRequisitorioValorJuros(RelatorioUtil.formatarValorMoeda(resumo.getRequest().getValorJuros()));
        doc.setRequisitorioValorMulta(RelatorioUtil.formatarValorMoeda(resumo.getRequest().getMulta()));
        doc.setRequisitorioValorCust(RelatorioUtil.formatarValorMoeda(resumo.getRequest().getCustas()));
        doc.setRequisitorioValorOutros(RelatorioUtil.formatarValorMoeda(resumo.getRequest().getOutrosReembolsos()));
        doc.setRequisitorioCustaMuoltaOutros(RelatorioUtil.formatarValorMoeda(
                resumo.getRequest().getMulta()
                        .add(resumo.getRequest().getCustas())
                        .add(resumo.getRequest().getOutrosReembolsos())
        ));
        doc.setRequisitorioValorSelic(RelatorioUtil.formatarValorMoeda(resumo.getRequest().getValorSelic()));
        doc.setRequisitorioValorTotal(RelatorioUtil.formatarValorMoeda(
                resumo.getRequest().getValorPrincipalTributavel()
                        .add(resumo.getRequest().getValorPrincipalNaoTributavel())
                        .add(resumo.getRequest().getValorJuros())
                        .add(resumo.getRequest().getMulta())
                        .add(resumo.getRequest().getCustas())
                        .add(resumo.getRequest().getOutrosReembolsos())
                        .add(resumo.getRequest().getValorSelic())
        ));
        doc.setRequisitorioValorPrevidencia(RelatorioUtil.formatarValorMoeda(resumo.getRequest().getValorPrevidencia()));
        doc.setRequisitorioNumeroRRA(resumo.getCalculoAtualizacaoDTO().getResultadoNumeroMesesRRA());
        doc.setRequisitorioUltimaAtualizacao(StringUtil.formataDataDMY(resumo.getRequisitorioDTO().getDtUltimaAtualizacaoPlanilha()));
        doc.setRequisitorioTipoAcaoPrecatorio(resumo.getRequisitorioDTO().getDsTipoObrigacao());
        doc.setRequisitorioAnoVencimento(resumo.getRequisitorioDTO().getAnoVencimento());
        doc.setVariavelDataFimCalculo(StringUtil.formataDataDMY(resumo.getCalculoAtualizacaoDTO().getDataFimAtualizacao()));
        doc.setVariavelFatorAntesGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoFatorAntesAtualizado(),5));
        doc.setVariavelFatorDuranteGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoFatorDuranteAtualizado(),5));
        doc.setVariavelFatorAposGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoFatorDepoisAtualizado(),5));
        doc.setVariavelTaxaAntesGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoTaxaAntesAtualizado(),5));
        doc.setVariavelTaxaDuranteGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoTaxaDuranteAtualizado(),5));
        doc.setVariavelTaxaAposGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoTaxaDepoisAtualizado(),5));
        doc.setAtualizacaoValorPrincipalTributavel(RelatorioUtil.formatarValorMoeda(resumo.getCalculoAtualizacaoDTO().getResultadoValorPrincipalTributavelAtualizado()));
        doc.setAtualizacaoValorPrincipalNaoTributavel(RelatorioUtil.formatarValorMoeda(resumo.getCalculoAtualizacaoDTO().getResultadoValorPrincipalNaoTributavelAtualizado()));
        doc.setAtualizacaoValorJuros(RelatorioUtil.formatarValorMoeda(resumo.getCalculoAtualizacaoDTO().getResultadoValorJurosAtualizado()));
        doc.setAtualizacaoValorMultaCustOutros(RelatorioUtil.formatarValorMoeda(resumo.getCalculoAtualizacaoDTO().getResultadoValorMultaCustasOutrosAtualizado()));
        doc.setAtualizacaoValorSelic(RelatorioUtil.formatarValorMoeda(resumo.getCalculoAtualizacaoDTO().getResultadoValorSelicAtualizado()));
        doc.setAtualizacaoValorTotal(RelatorioUtil.formatarValorMoeda(resumo.getCalculoAtualizacaoDTO().getResultadoValorBrutoAtualizado()));
        doc.setAtualizacaoNumeroRRA(resumo.getCalculoAtualizacaoDTO().getResultadoNumeroMesesRRA());
        doc.setAtualizacaoValorPrevidencia(RelatorioUtil.formatarValorMoeda(resumo.getCalculoAtualizacaoDTO().getResultadoValorPrevidenciaAtualizado()));
        doc.setAtualizacaoValorLimitePrioridade(RelatorioUtil.formatarValorMoeda(resumo.getCalculoPagamentoDTO().getValorBasePrioridade()));
        doc.setAtualizacaoLimitacaoPagamento(resumo.getRequest().isPagamentoParcial() ? StringUtil.formatarValorMoeda(resumo.getRequest().getPagamentoValorParcial()) : "NÃO");
        doc.setAtualizacaoAcordoDireto(resumo.getCalculoResumoDTO().getAtualizacaoAcordoDireto());
        doc.setTributacaoHonorarioPercentual(resumo.getCalculoResumoDTO().getTributacaoHonorarioPercentual().setScale(0, RoundingMode.HALF_UP).toString());
        doc.setTributacaoHonorarioMontanteDesembolso(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getTributacaoHonorarioMontanteDesembolso()));
        doc.setTributacaoHonorarioBaseTributacao(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getTributacaoHonorarioBaseTributacao()));
        doc.setTributacaoHonorarioTipoTributacao(resumo.getCalculoResumoDTO().getTributacaoHonorarioTipoTributacao());
        doc.setTributacaoHonorarioImposto(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getTributacaoHonorarioImposto()));
        doc.setTributacaoCredorPercentual(resumo.getCalculoResumoDTO().getTributacaoCredorPercentual().setScale(0, RoundingMode.HALF_UP).toString());
        doc.setTributacaoCredorMontanteDesembolso(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getTributacaoCredorMontanteDesembolso()));
        doc.setTributacaoCredorBaseTributacao(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getTributacaoCredorBaseTributacao()));
        doc.setTributacaoCredorTipoTributacao(resumo.getCalculoResumoDTO().getTributacaoCredorTipoTributacao());
        doc.setTributacaoCredorImposto(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getTributacaoCredorImposto()));
        doc.setTributacaoCredorPrevidencia(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getTributacaoCredorPrevidencia()));

        doc.setAlvaraDevedorNome(resumo.getCalculoResumoDTO().getAlvaraDevedorNome());
        doc.setAlvaraIRRFCredor(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getAlvaraIRRFCredor()));
        doc.setAlvaraOrgaoPrevidenciaNome(
                UtilCalculo.isNotNullOrZero(resumo.getCalculoResumoDTO().getTributacaoCredorPrevidencia()) ?
                        resumo.getCalculoResumoDTO().getAlvaraOrgaoPrevidenciaNome() : "Sem previdência"
        );
        doc.setAlvaraValorPrevidencia(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getAlvaraValorPrevidencia()));

        doc.setAlvaraValorHonorarioContratualLiquido(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getAlvaraValorHonorarioContratualLiquido()));
        doc.setAlvaraIRRFHonorario(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getAlvaraIRRFHonorario()));

        doc.setAlvaraValorPenhora(RelatorioUtil.formatarValorMoeda(resumo.getRequest().getValorPenhora()));

        if(resumo.getRequest().getValorPenhora().compareTo(resumo.getCalculoPagamentoDTO().getPenhoraValor()) > 0){
            doc.setAlvaraValorPenhora(RelatorioUtil.formatarValorMoeda(resumo.getCalculoPagamentoDTO().getCessaoBaseValor()));
        }

        doc.setAlvaraValorCessao(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getAlvaraValorCessao()));
        doc.setAlvaraValorLiquidoCredor(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getAlvaraValorLiquidoCredor()));
        doc.setAlvaraValorTotalSomado(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getAlvaraValorTotalSomado()));
        doc.setRemanescentePercentual(resumo.getCalculoResumoDTO().getRemanescentePercentual().toString());
        doc.setRemanescenteValorPrincipalTributavel(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getRemanescenteValorPrincipalTributavel()));
        doc.setRemanescenteValorPrincipalNaoTributavel(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getRemanescenteValorPrincipalNaoTributavel()));
        doc.setRemanescenteValorJuros(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getRemanescenteValorJuros()));
        doc.setRemanescenteValorMultaCustOutros(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getRemanescenteValorMultaCustOutros()));
        doc.setRemanescenteValorSelic(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getRemanescenteValorSelic()));
        doc.setRemanescenteValorTotal(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getRemanescenteValorTotal()));
        doc.setRemanescenteNumeroRRA(resumo.getCalculoResumoDTO().getRemanescenteNumeroRRA());
        doc.setRemanescenteValorPrevidencia(RelatorioUtil.formatarValorMoeda(resumo.getCalculoResumoDTO().getRemanescenteValorPrevidencia()));

        if(!resumo.getRequisitorioDTO().getPrioridades().isEmpty()){
            doc.setTemPrioridade("Sim");
        }

        if(!resumo.getRequisitorioDTO().getAcordos().isEmpty()){
            doc.setTemAcordo("Sim");
        }

        if(UtilCalculo.isNotNullOrZero(resumo.getRequest().getPercentualCessao())){
            doc.setTemCessaoCredito("Sim");
        }

        doc.setIndicePrecatorio(resumo.getCalculoAtualizacaoDTO().getTipoCalculoRetornado());
        doc.setDesagioPercentual(resumo.getCalculoPagamentoDTO().getPercentualDesagio().toString());
        doc.setDesagioHonorarioValorBase(RelatorioUtil.formatarValorMoeda(resumo.getCalculoPagamentoDTO().getValorDesagioHonorarioAtualizado()));
        doc.setDesagioCredorValorBase(RelatorioUtil.formatarValorMoeda(resumo.getCalculoPagamentoDTO().getValorDesagioCredorAtualizado()));

        doc.setAdvCredorNome(resumo.getRequisitorioDTO().getNomeCredorAdv());

        doc.setDataCorrente(StringUtil.formataDataDMY(LocalDate.now()));
        doc.setUsuarioCalculo("Não implementado (em fase de homologação)");


        if(UtilCalculo.isNotNullOrZero(resumo.getRequest().getValorPenhora())){
           doc.setDescricaoAlvaraPenhora(resumo.getRequest().getDescricaoAlvaraPenhora());
        }

        doc.setAlvaraDescricaoAdvogado(
                resumo.get;
        );

        if(resumo.getRequest().isTemSucessao()){
            var dadosSucessao = resumo.getRequest().getSucessaoNome()+ ", Banco: "+
                    resumo.getRequest().getSucessaoBanco()+ ", Agencia: "+
                    resumo.getRequest().getSucessaoAgencia()+ ", Conta: "+
                    resumo.getRequest().getSucessaoConta()+ "-" +resumo.getRequest().getSucessaoDVConta()+ ", Tipo conta: "+
                    resumo.getRequest().getSucessaoTipoConta();
            doc.setSucessaoNome(dadosSucessao);
        }else{
            doc.setSucessaoNome("Transferência para o Sucessor");
        }
        doc.setTemSucessao(resumo.getRequest().isTemSucessao()?"Sim":"Não");

        if(resumo.getRequest().isTemCessao()){
            var dadosCessao = resumo.getRequest().getCessaoNome()+ ", Banco: "+
                    resumo.getRequest().getCessaoBanco()+ ", Agencia: "+
                    resumo.getRequest().getCessaoAgencia()+ ", Conta: "+
                    resumo.getRequest().getCessaoConta()+ "-" +resumo.getRequest().getCessaoDVConta()+ ", Tipo conta: "+
                    resumo.getRequest().getCessaoTipoConta();
            doc.setCessaoNome(dadosCessao);
        } else {
            doc.setCessaoNome("Transferência para o Cessionário");
        }

        return doc;
    }
}