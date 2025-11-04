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
import java.util.ArrayList;
import java.util.List;
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
    private String alvaraDescricaoCredor = "Transferência para o Credor Principal";

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
    private String sucessaoPercentual;

    private String cessaoNome;
    private String cessaoNumeroDocumento;
    private String cessaoBanco;
    private String cessaoTipoConta;
    private String cessaoAgencia;
    private String cessaoConta;
    private String cessaoDVConta;
    private String cessaoPercentual;

    private String previdenciaNome = "Transferência para previdência";
    private String previdenciaBanco;
    private String previdenciaTipoConta;
    private String previdenciaAgencia;
    private String previdenciaConta;
    private String previdenciaDVConta;

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

    private List<PessoasPrecatorioDTO> pessoasPrecatorio = new ArrayList<>();

    public ResumoCalculoDocumentoDTO montarResumoDocumento(CalculoRequisitorioDTO resumo) {
        ResumoCalculoDocumentoDTO doc = new ResumoCalculoDocumentoDTO();

        doc.setProcessoPrecatorio(StringUtil.formataNumeroProcesso(resumo.getRequisitorioDTO().getIdProcesso()));
        doc.setProcessoPrecatorioOrigem(StringUtil.formataNumeroProcesso(resumo.getRequisitorioDTO().getNumProcessoTucujuris()));
        doc.setDevedorNome(resumo.getRequisitorioDTO().getEnteDevedorDTO().getNome());
        doc.setDevedorDocumento(resumo.getRequisitorioDTO().getEnteDevedorDTO().getCnpj());
        doc.setDevedorContaJudicial(resumo.getRequisitorioDTO().getEnteDevedorDTO().getNumeroConta());
        doc.setCredorNome(resumo.getRequisitorioDTO().getNomeCredor());
        doc.setCredorDocumento(resumo.getRequisitorioDTO().getDocumentoCredor());
        if (resumo.getRequisitorioDTO().getNascimentoCredor() != null) {
            doc.setCredorNascimento(StringUtil.formataDataDMY(resumo.getRequisitorioDTO().getNascimentoCredor()));
        } else {
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
        doc.setIdadeCredor(Period.between(Objects.isNull(resumo.getRequisitorioDTO().getNascimentoCredor()) ? LocalDate.now().minusYears(1) : resumo.getRequisitorioDTO().getNascimentoCredor(), LocalDate.now()).getYears());
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
        doc.setVariavelFatorAntesGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoFatorAntesAtualizado(), 5));
        doc.setVariavelFatorDuranteGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoFatorDuranteAtualizado(), 5));
        doc.setVariavelFatorAposGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoFatorDepoisAtualizado(), 5));
        doc.setVariavelTaxaAntesGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoTaxaAntesAtualizado(), 5));
        doc.setVariavelTaxaDuranteGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoTaxaDuranteAtualizado(), 5));
        doc.setVariavelTaxaAposGraca(UtilCalculo.escala(resumo.getCalculoAtualizacaoDTO().getResultadoTaxaDepoisAtualizado(), 5));
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
        doc.setAtualizacaoAcordoDireto(
                (resumo.getRequest().isAcordoAdvogado() || resumo.getRequest().isAcordoCredor()) ?
                        resumo.getRequest().getPercentualDesagio().toString() + "%" : "NÃO"
        );
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

        if (resumo.getRequest().getValorPenhora().compareTo(resumo.getCalculoPagamentoDTO().getPenhoraValor()) > 0) {
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

        if (!resumo.getRequisitorioDTO().getPrioridades().isEmpty()) {
            doc.setTemPrioridade("Sim");
        }

        if (!resumo.getRequisitorioDTO().getAcordos().isEmpty()) {
            doc.setTemAcordo("Sim");
        }

        if (UtilCalculo.isNotNullOrZero(resumo.getRequest().getPercentualCessao())) {
            doc.setTemCessaoCredito("Sim");
        }

        doc.setIndicePrecatorio(resumo.getCalculoAtualizacaoDTO().getTipoCalculoRetornado());
        doc.setDesagioPercentual(resumo.getCalculoPagamentoDTO().getPercentualDesagio().toString());
        doc.setDesagioHonorarioValorBase(RelatorioUtil.formatarValorMoeda(resumo.getCalculoPagamentoDTO().getValorDesagioHonorarioAtualizado()));
        doc.setDesagioCredorValorBase(RelatorioUtil.formatarValorMoeda(resumo.getCalculoPagamentoDTO().getValorDesagioCredorAtualizado()));

        doc.setAdvCredorNome(resumo.getRequisitorioDTO().getNomeCredorAdv());

        doc.setDataCorrente(StringUtil.formataDataDMY(LocalDate.now()));
        doc.setUsuarioCalculo("Não implementado (em fase de homologação)");


        if (UtilCalculo.isNotNullOrZero(resumo.getRequest().getValorPenhora())) {
            doc.setDescricaoAlvaraPenhora(resumo.getRequest().getDescricaoAlvaraPenhora());
        }

        if (Objects.nonNull(resumo.getRequest().getAdvCredorBanco())) {
            var advogado = new PessoasPrecatorioDTO();
            advogado.setTipoPessoa("Advogado Credor");
            advogado.setNome(resumo.getRequest().getAdvCredorNome());
            advogado.setNumeroDocumento(resumo.getRequest().getAdvCredorNumeroDocumento());
            advogado.setNomeBanco(resumo.getRequest().getAdvCredorBanco());
            advogado.setAgencia(resumo.getRequest().getAdvCredorAgencia());
            advogado.setConta(resumo.getRequest().getAdvCredorConta());
            advogado.setDvConta(resumo.getRequest().getAdvCredorDVConta());
            advogado.setTipoConta(resumo.getRequest().getAdvCredorTipoConta());
            advogado.setPercentual(resumo.getCalculoPagamentoDTO().getPercentualHonorario().toString() + "%");
            //advogado.setValorTotal(RelatorioUtil.formatarValorMoeda(resumo.getCalculoPagamentoDTO().getValorHonorarioBrutoAtualizado()));
            pessoasPrecatorio.add(advogado);
            doc.setAlvaraDescricaoAdvogado(
                    resumo.getRequest().getAdvCredorNome() +
                            ", Banco: " + resumo.getRequest().getAdvCredorBanco() +
                            ", Agencia: " + resumo.getRequest().getAdvCredorAgencia() +
                            ", Conta: " + resumo.getRequest().getAdvCredorConta() + "-" + resumo.getRequest().getAdvCredorDVConta() +
                            ", Tipo conta: " + resumo.getRequest().getAdvCredorTipoConta()
            );
        }

        if (Objects.nonNull(resumo.getRequest().getCredorBanco())) {
            var credor = new PessoasPrecatorioDTO();
            credor.setTipoPessoa("Credor");
            credor.setNome(resumo.getRequest().getCredorNome());
            credor.setNumeroDocumento(resumo.getRequest().getCredorNumeroDocumento());
            credor.setNomeBanco(resumo.getRequest().getCredorBanco());
            credor.setAgencia(resumo.getRequest().getCredorAgencia());
            credor.setConta(resumo.getRequest().getCredorConta());
            credor.setDvConta(resumo.getRequest().getCredorDVConta());
            credor.setTipoConta(resumo.getRequest().getCredorTipoConta());
            credor.setPercentual(resumo.getCalculoPagamentoDTO().getPercentualParteCredor().toString() + "%");
            //credor.setValorTotal(RelatorioUtil.formatarValorMoeda(resumo.getCalculoPagamentoDTO().getValorCredorBrutoAtualizado()));
            pessoasPrecatorio.add(credor);
            doc.setAlvaraDescricaoCredor(
                    resumo.getRequest().getCredorNome() +
                            ", Banco: " + resumo.getRequest().getCredorBanco() +
                            ", Agencia: " + resumo.getRequest().getCredorAgencia() +
                            ", Conta: " + resumo.getRequest().getCredorConta() + "-" + resumo.getRequest().getCredorDVConta() +
                            ", Tipo conta: " + resumo.getRequest().getCredorTipoConta()
            );
        }

        if (resumo.getRequest().isTemSucessao()) {
            var sucessao = new PessoasPrecatorioDTO();
            sucessao.setTipoPessoa("Sucessor");
            sucessao.setNome(resumo.getRequest().getSucessaoNome());
            sucessao.setNumeroDocumento(resumo.getRequest().getSucessaoNumeroDocumento());
            sucessao.setNomeBanco(resumo.getRequest().getSucessaoBanco());
            sucessao.setAgencia(resumo.getRequest().getSucessaoAgencia());
            sucessao.setConta(resumo.getRequest().getSucessaoConta());
            sucessao.setDvConta(resumo.getRequest().getSucessaoDVConta());
            sucessao.setTipoConta(resumo.getRequest().getSucessaoTipoConta());
            sucessao.setPercentual(resumo.getRequest().getSucessaoPercentual().toString() + "%");
            //sucessao.setValorTotal(RelatorioUtil.formatarValorMoeda(resumo.getCalculoPagamentoDTO().getValorCredorBrutoAtualizado()));
            pessoasPrecatorio.add(sucessao);
            var dadosSucessao = resumo.getRequest().getSucessaoNome() +
                    ", Banco: " + resumo.getRequest().getSucessaoBanco() +
                    ", Agencia: " + resumo.getRequest().getSucessaoAgencia() +
                    ", Conta: " + resumo.getRequest().getSucessaoConta() + "-" + resumo.getRequest().getSucessaoDVConta() +
                    ", Tipo conta: " + resumo.getRequest().getSucessaoTipoConta();
            doc.setSucessaoNome(dadosSucessao);
            doc.setSucessaoPercentual(resumo.getRequest().getSucessaoPercentual().toString() + "%");
            doc.setTemSucessao("Sim");
        } else {
            doc.setSucessaoNome("Transferência para o Sucessor");
            doc.setSucessaoPercentual("0%");
        }

        if (resumo.getRequest().isTemCessao()) {
            var cessao = new PessoasPrecatorioDTO();
            cessao.setTipoPessoa("Cessão");
            cessao.setNome(resumo.getRequest().getCessaoNome());
            cessao.setNumeroDocumento(resumo.getRequest().getCessaoNumeroDocumento());
            cessao.setNomeBanco(resumo.getRequest().getCessaoBanco());
            cessao.setAgencia(resumo.getRequest().getCessaoAgencia());
            cessao.setConta(resumo.getRequest().getCessaoConta());
            cessao.setDvConta(resumo.getRequest().getCessaoDVConta());
            cessao.setTipoConta(resumo.getRequest().getCessaoTipoConta());
            cessao.setPercentual(resumo.getRequest().getCessaoPercentual().toString() + "%");
            //cessao.setValorTotal(RelatorioUtil.formatarValorMoeda(
            //        resumo.getCalculoPagamentoDTO().getValorCredorBrutoAtualizado().multiply(resumo.getRequest().getCessaoPercentual()).divide(BigDecimal.valueOf(100))
            //        ));
            pessoasPrecatorio.add(cessao);
            var dadosCessao = resumo.getRequest().getCessaoNome() +
                    ", Banco: " + resumo.getRequest().getCessaoBanco() +
                    ", Agencia: " + resumo.getRequest().getCessaoAgencia() +
                    ", Conta: " + resumo.getRequest().getCessaoConta() + "-" + resumo.getRequest().getCessaoDVConta() +
                    ", Tipo conta: " + resumo.getRequest().getCessaoTipoConta();
            doc.setCessaoPercentual(Objects.nonNull(resumo.getRequest().getCessaoPercentual().toString()) ? resumo.getRequest().getCessaoPercentual().toString() : "0" + "%");
            doc.setCessaoNome(dadosCessao);
            doc.setTemCessaoCredito("Sim");
        } else {
            doc.setCessaoNome("Transferência para o Cessionário");
            doc.setCessaoPercentual("0%");
        }
        doc.setPessoasPrecatorio(pessoasPrecatorio);

        if (resumo.getRequisitorioDTO().getTipoCredor().getTipoPessoa().equalsIgnoreCase("PF")) {
            if (resumo.getRequisitorioDTO().getTipoCredor().getId() == 2 || resumo.getRequisitorioDTO().getTipoCredor().getId() == 3 || resumo.getRequisitorioDTO().getTipoCredor().getId() == 4) {
                doc.setPrevidenciaNome("INSS - DARF");
            } else {

                doc.setPrevidenciaNome(resumo.getRequisitorioDTO().getEnteDevedorDTO().getPrevidenciaDTO().getNome());
                doc.setPrevidenciaBanco(resumo.getRequisitorioDTO().getEnteDevedorDTO().getPrevidenciaDTO().getBanco());
                doc.setPrevidenciaAgencia(resumo.getRequisitorioDTO().getEnteDevedorDTO().getPrevidenciaDTO().getAgencia());
                doc.setPrevidenciaConta(resumo.getRequisitorioDTO().getEnteDevedorDTO().getPrevidenciaDTO().getConta());
                doc.setPrevidenciaDVConta(resumo.getRequisitorioDTO().getEnteDevedorDTO().getPrevidenciaDTO().getDigitoConta());
                doc.setPrevidenciaTipoConta(resumo.getRequisitorioDTO().getEnteDevedorDTO().getPrevidenciaDTO().getTipoConta());
            }
        }

        return doc;
    }
}