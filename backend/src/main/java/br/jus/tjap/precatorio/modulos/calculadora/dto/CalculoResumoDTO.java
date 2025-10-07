package br.jus.tjap.precatorio.modulos.calculadora.dto;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import br.jus.tjap.precatorio.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.angus.mail.imap.OlderTerm;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private BigDecimal remanescentePercentual;
    private BigDecimal remanescenteValorPrincipalTributavel;
    private BigDecimal remanescenteValorPrincipalNaoTributavel;
    private BigDecimal remanescenteValorJuros;
    private BigDecimal remanescenteValorMultaCustOutros;
    private BigDecimal remanescenteValorSelic;
    private BigDecimal remanescenteValorTotal;
    private long remanescenteNumeroRRA;
    private BigDecimal remanescenteValorPrevidencia;

    public CalculoResumoDTO(CalculoResumoDTO calculoResumoDTO) {
    }


    public CalculoResumoDTO montarDocumentoCalculo(CalculoRequisitorioDTO req){

        var resumo = new CalculoResumoDTO();
        var requisitorio = new RequisitorioDTO();
        // dados precatório
        //this.ordemPagamento = req.;
        this.processoPrecatorio = req.getCalculoAtualizacaoDTO().getNumeroProcesso();
        //this.devedorNome = req.getRequisitorioDTO().getEnteDevedorDTO().getNome();
        //this.devedorDocumento = req.getRequisitorioDTO().getEnteDevedorDTO().getCnpj();
        //this.devedorContaJudicial = req.getRequisitorioDTO().getEnteDevedorDTO().getNumeroConta();
        this.devedorNome = "Município de Macapá";
        this.devedorDocumento = req.getRequest().getCnpjDevedor();
        this.devedorContaJudicial = "00011122233";
        //this.credorNome = req.getRequisitorioDTO().getNomeCredor();
        this.credorNome = requisitorio.getNomeCredor();
        this.credorDocumento = requisitorio.getDocumentoCredor();
        this.credorNascimento = requisitorio.getNascimentoCredor();
        //this.credorDocumento = req.getRequisitorioDTO().getDocumentoCredor();
        //this.credorNascimento = req.getRequisitorioDTO().getNascimentoCredor();

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
                .add(this.requisitorioValorMultaCustOutros);
        this.requisitorioNumeroRRA = req.getCalculoAtualizacaoDTO().getResultadoNumeroMesesRRA();
        this.requisitorioUltimaAtualizacao = req.getRequest().getDataUltimaAtualizacao();
        //this.requisitorioTipoAcaoPrecatorio = req.getRequisitorioDTO().getDsTipoObrigacao();
        this.requisitorioTipoAcaoPrecatorio = requisitorio.getDsTipoObrigacao();
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
        this.alvaraDevedorNome = this.devedorNome;
        this.alvaraIRRFCredor = req.getCalculoPagamentoDTO().getBaseTributavelCredorImposto();;
        this.alvaraOrgaoPrevidenciaNome = req.getRequest().getTipoVinculoCredor().equals("Com vinculo") ?
                //req.getRequisitorioDTO().getEnteDevedorDTO().getComVinculo()
                "MACAPAPREV" : req.getRequisitorioDTO().getEnteDevedorDTO().getSemVinculo();
        this.alvaraValorPrevidencia = req.getCalculoPagamentoDTO().getBaseTributavelCredorPrevidencia();
        this.alvaraValorHonorarioContratualLiquido = req.getCalculoPagamentoDTO().getValorHonorarioBrutoAtualizado().subtract(req.getCalculoPagamentoDTO().getBaseTributavelHonorarioImposto());
        this.alvaraIRRFHonorario = req.getCalculoPagamentoDTO().getBaseTributavelHonorarioImposto();
        this.alvaraValorPenhora = req.getRequest().getValorPenhora().compareTo(req.getCalculoPagamentoDTO().getPenhoraBaseValor())<0 ? req.getCalculoPagamentoDTO().getCessaoBaseValor() : req.getRequest().getValorPenhora();
        //=D93*G93
        this.alvaraValorCessao = req.getRequest().getPercentualCessao().multiply(req.getCalculoPagamentoDTO().getCessaoBaseValor()).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);
        //=IFERROR((M133-M136-M137-L147-L146);0)
        this.alvaraValorLiquidoCredor = this.tributacaoHonorarioMontanteDesembolso
                .subtract(this.tributacaoCredorImposto)
                .subtract(this.tributacaoCredorPrevidencia)
                .subtract(this.alvaraValorCessao)
                .subtract(this.alvaraValorPenhora);
        this.alvaraValorTotalSomado = this.alvaraIRRFCredor
                .add(this.alvaraValorPrevidencia)
                .add(this.alvaraValorHonorarioContratualLiquido)
                .add(this.alvaraIRRFHonorario)
                .add(this.alvaraValorPenhora)
                .add(this.alvaraValorCessao)
                .add(this.alvaraValorLiquidoCredor);

        // saldo remanescente
        // valores atualizados
        this.remanescentePercentual = req.getCalculoPagamentoDTO().getSaldoRemanescentePercentual();
        this.remanescenteValorPrincipalTributavel = req.getCalculoPagamentoDTO().getSaldoRemanescentePrincipalTributavel();
        this.remanescenteValorPrincipalNaoTributavel = req.getCalculoPagamentoDTO().getSaldoRemanescentePrincipalNaoTributavel();
        this.remanescenteValorJuros = req.getCalculoPagamentoDTO().getSaldoRemanescenteJuros();
        this.remanescenteValorMultaCustOutros = req.getCalculoPagamentoDTO().getSaldoRemanescenteMultasCustasOutros();
        this.remanescenteValorSelic = req.getCalculoPagamentoDTO().getSaldoRemanescenteSelic();
        this.remanescenteValorTotal = req.getCalculoPagamentoDTO().getSaldoRemanescenteTotal();
        this.remanescenteNumeroRRA = req.getCalculoPagamentoDTO().getSaldoRemanescenteTotalRRA().longValue();
        this.remanescenteValorPrevidencia = req.getCalculoPagamentoDTO().getSaldoRemanescentePrevidencia();


        return this;
    }


}
