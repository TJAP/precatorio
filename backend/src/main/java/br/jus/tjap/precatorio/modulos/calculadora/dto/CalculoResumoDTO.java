package br.jus.tjap.precatorio.modulos.calculadora.dto;

import br.jus.tjap.precatorio.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoResumoDTO {

    private Long idPrecatorio;
    // dados precatório
    private int ordemPagamento;
    private String processoPrecatorio;
    private String devedorNome;
    private String devedorDocumento;
    private String devedorContaJudicial;
    private String credorNome;
    private String credorDocumento;
    private LocalDate credorNascimento;

    // dados do requisitório
    private BigDecimal requisitorioValorPrincipalTributavel;
    private BigDecimal requisitorioValorPrincipalNaoTributavel;
    private BigDecimal requisitorioValorJuros;
    private BigDecimal requisitorioValorMultaCustOutros;
    private BigDecimal requisitorioValorSelic;
    private BigDecimal requisitorioValorTotal;
    private long requisitorioNumeroRRA;
    private LocalDate requisitorioUltimaAtualizacao;
    private String requisitorioTipoAcaoPrecatorio;
    private BigDecimal requisitorioValorPrevidencia;
    private int requisitorioAnoVencimento;

    // indice e fatores utilizados
    private LocalDate variavelDataFimCalculo;
    private BigDecimal variavelFatorAntesGraca;
    private BigDecimal variavelFatorDuranteGraca;
    private BigDecimal variavelFatorAposGraca;
    private BigDecimal variavelTaxaAntesGraca;
    private BigDecimal variavelTaxaDuranteGraca;
    private BigDecimal variavelTaxaAposGraca;

    // valores atualizados
    private BigDecimal atualizacaoValorPrincipalTributavel;
    private BigDecimal atualizacaoValorPrincipalNaoTributavel;
    private BigDecimal atualizacaoValorJuros;
    private BigDecimal atualizacaoValorMultaCustOutros;
    private BigDecimal atualizacaoValorSelic;
    private BigDecimal atualizacaoValorTotal;
    private long atualizacaoNumeroRRA;
    private BigDecimal atualizacaoValorPrevidencia;
    private BigDecimal atualizacaoValorLimitePrioridade;
    private String atualizacaoLimitacaoPagamento;
    private String atualizacaoAcordoDireto;

    // tributação
    private BigDecimal tributacaoHonorarioPercentual;
    private BigDecimal tributacaoHonorarioMontanteDesembolso;
    private BigDecimal tributacaoHonorarioBaseTributacao;
    private String tributacaoHonorarioTipoTributacao;
    private BigDecimal tributacaoHonorarioImposto;
    private BigDecimal tributacaoCredorPercentual;
    private BigDecimal tributacaoCredorMontanteDesembolso;
    private BigDecimal tributacaoCredorBaseTributacao;
    private String tributacaoCredorTipoTributacao;
    private BigDecimal tributacaoCredorImposto;
    private BigDecimal tributacaoCredorPrevidencia;

    // resumo do alvará
    private String alvaraDevedorNome;
    private BigDecimal alvaraIRRFCredor;
    private String alvaraOrgaoPrevidenciaNome;
    private BigDecimal alvaraValorPrevidencia;
    private BigDecimal alvaraValorHonorarioContratualLiquido;
    private BigDecimal alvaraIRRFHonorario;
    private BigDecimal alvaraValorPenhora;
    private BigDecimal alvaraValorCessao;
    private BigDecimal alvaraValorLiquidoCredor;
    private BigDecimal alvaraValorTotalSomado;

    // saldo remanescente
    // valores atualizados
    private BigDecimal remanescenteValorPrincipalTributavel;
    private BigDecimal remanescenteValorPrincipalNaoTributavel;
    private BigDecimal remanescenteValorJuros;
    private BigDecimal remanescenteValorMultaCustOutros;
    private BigDecimal remanescenteValorSelic;
    private BigDecimal remanescenteValorTotal;
    private long remanescenteNumeroRRA;
    private BigDecimal remanescenteValorPrevidencia;


    public void montarDocumentoCalculo(CalculoRequisitorioDTO req){

        // dados precatório
        //this.ordemPagamento = req.;
        this.processoPrecatorio = req.getCalculoAtualizacaoDTO().getNumeroProcesso();;
        this.devedorNome = req.getRequisitorioDTO().getEnteDevedorDTO().getNome();
        this.devedorDocumento = req.getRequisitorioDTO().getEnteDevedorDTO().getCnpj();
        this.devedorContaJudicial = req.getRequisitorioDTO().getEnteDevedorDTO().getNumeroConta();;
        this.credorNome = req.getRequisitorioDTO().getNomeCredor();
        this.credorDocumento = req.getRequisitorioDTO().getDocumentoCredor();
        this.credorNascimento = req.getRequisitorioDTO().getNascimentoCredor();

        // dados do requisitório
        this.requisitorioValorPrincipalTributavel = req.getRequest().getValorPrincipalTributavel();
        this.requisitorioValorPrincipalNaoTributavel = req.getRequest().getValorPrincipalNaoTributavel();
        this.requisitorioValorJuros = req.getRequest().getValorJuros();
        this.requisitorioValorMultaCustOutros = req.getRequest().getMulta().add(req.getRequest().getCustas()).add(req.getRequest().getOutrosReembolsos());
        this.requisitorioValorSelic = req.getRequest().getValorSelic();
        this.requisitorioValorTotal = req.getRequest().getValorPrincipalTributavel()
                .add(req.getRequest().getValorPrincipalNaoTributavel())
                .add(req.getRequest().getValorJuros())
                .add(req.getRequest().getValorSelic())
                .add(this.atualizacaoValorMultaCustOutros);
        this.requisitorioNumeroRRA = req.getCalculoAtualizacaoDTO().getResultadoNumeroMesesRRA();
        this.requisitorioUltimaAtualizacao = req.getRequest().getDataUltimaAtualizacao();
        this.requisitorioTipoAcaoPrecatorio = req.getRequisitorioDTO().getDsTipoObrigacao();
        this.requisitorioValorPrevidencia = req.getRequest().getValorPrevidencia();
        this.requisitorioAnoVencimento = req.getRequest().getAnoVencimento();

        // indice e fatores utilizados
        this.variavelDataFimCalculo = req.getCalculoAtualizacaoDTO().getDataFimAtualizacao();
        this.variavelFatorAntesGraca = req.getCalculoAtualizacaoDTO().getResultadoFatorAntesAtualizado();
        this.variavelFatorDuranteGraca = req.getCalculoAtualizacaoDTO().getResultadoFatorDuranteAtualizado();
        this.variavelFatorAposGraca = req.getCalculoAtualizacaoDTO().getResultadoFatorDepoisAtualizado();
        this.variavelTaxaAntesGraca = req.getCalculoAtualizacaoDTO().getResultadoTaxaAntesAtualizado();
        this.variavelTaxaDuranteGraca = req.getCalculoAtualizacaoDTO().getResultadoTaxaDuranteAtualizado();
        this.variavelTaxaAposGraca = req.getCalculoAtualizacaoDTO().getResultadoTaxaDepoisAtualizado();

        // valores atualizados
        this.atualizacaoValorPrincipalTributavel = req.getCalculoAtualizacaoDTO().getResultadoValorPrincipalTributavelAtualizado();;
        this.atualizacaoValorPrincipalNaoTributavel = req.getCalculoAtualizacaoDTO().getResultadoValorPrincipalNaoTributavelAtualizado();
        this.atualizacaoValorJuros = req.getCalculoAtualizacaoDTO().getResultadoValorJurosAtualizado();
        this.atualizacaoValorMultaCustOutros = req.getCalculoAtualizacaoDTO().getResultadoValorMultaCustasOutrosAtualizado();
        this.atualizacaoValorSelic = req.getCalculoAtualizacaoDTO().getResultadoValorSelicAtualizado();
        this.atualizacaoValorTotal = req.getCalculoAtualizacaoDTO().getResultadoValorBrutoAtualizado();
        this.atualizacaoNumeroRRA = req.getCalculoAtualizacaoDTO().getResultadoNumeroMesesRRA();
        this.atualizacaoValorPrevidencia = req.getCalculoAtualizacaoDTO().getResultadoValorPrevidenciaAtualizado();
        this.atualizacaoValorLimitePrioridade = req.getCalculoPagamentoDTO().getValorBasePrioridade();
        this.atualizacaoLimitacaoPagamento = req.getRequest().isPagamentoParcial() ? StringUtil.formatarValorMoeda(req.getRequest().getValorPagamentoParcial()) : "NÃO";
        this.atualizacaoAcordoDireto = req.getRequest().isAcordoAdvogado() && req.getRequest().isAcordoCredor() ? req.getRequest().getPercentualDesagio().toString() : "NÃO";

        // tributação
        this.tributacaoHonorarioPercentual = req.getCalculoPagamentoDTO().getPercentualHonorario();
        this.tributacaoHonorarioMontanteDesembolso = req.getCalculoPagamentoDTO().getValorHonorarioBrutoAtualizado();
        this.tributacaoHonorarioBaseTributacao = req.getCalculoPagamentoDTO().getValorHonorarioBrutoAtualizado();
        this.tributacaoHonorarioTipoTributacao = req.getCalculoPagamentoDTO().getBaseTributavelHonorarioTipo();
        this.tributacaoHonorarioImposto = req.getCalculoPagamentoDTO().getBaseTributavelHonorarioImposto();
        this.tributacaoCredorPercentual = req.getCalculoPagamentoDTO().getPercentualParteCredor();
        this.tributacaoCredorMontanteDesembolso = req.getCalculoPagamentoDTO().getValorCredorBrutoAtualizado();
        this.tributacaoCredorBaseTributacao = req.getCalculoPagamentoDTO().getValorCredorPrincipalTributavelAtualizado();
        this.tributacaoCredorTipoTributacao = req.getCalculoPagamentoDTO().getTipoTributacaoCredor();
        this.tributacaoCredorImposto = req.getCalculoPagamentoDTO().getBaseTributavelCredorImposto();
        this.tributacaoCredorPrevidencia = req.getCalculoPagamentoDTO().getBaseTributavelCredorPrevidencia();

        // resumo do alvará
        this.alvaraDevedorNome = req.;
        this.alvaraIRRFCredor = req.;
        this.alvaraOrgaoPrevidenciaNome = req.;
        this.alvaraValorPrevidencia = req.;
        this.alvaraValorHonorarioContratualLiquido = req.;
        this.alvaraIRRFHonorario = req.;
        this.alvaraValorPenhora = req.;
        this.alvaraValorCessao = req.;
        this.alvaraValorLiquidoCredor = req.;
        this.alvaraValorTotalSomado = req.;

        // saldo remanescente
        // valores atualizados
        this.remanescenteValorPrincipalTributavel = req.
        this.remanescenteValorPrincipalNaoTributavel = req.
        this.remanescenteValorJuros = req.
        this.remanescenteValorMultaCustOutros = req.
        this.remanescenteValorSelic = req.
        this.remanescenteValorTotal = req.
        this.emanescenteNumeroRRA = req.
        this.remanescenteValorPrevidencia = req.

        return resumo;

    }


}
