package br.jus.tjap.precatorio.modulos.calculadora.dto;

import br.jus.tjap.precatorio.modulos.calculadora.util.PagamentoUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoPagamentoDTO {

    private String numeroCNPJCredor;

    private BigDecimal valorPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorBrutoAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrevidenciaAtualizado = BigDecimal.ZERO;

    private boolean temPrioridade = Boolean.FALSE;
    private BigDecimal valorBasePrioridade = BigDecimal.ZERO;
    private BigDecimal percentualPrioridade = BigDecimal.ZERO;

    private boolean temPagamentoParcial = Boolean.FALSE;
    private BigDecimal valorBaseParcialPago = BigDecimal.ZERO;
    private BigDecimal percentualParcialPago = BigDecimal.ZERO;

    private long numeroMesesRRA;

    private boolean houvePrioridadeOuPagamentoParcial = Boolean.FALSE;

    private BigDecimal valorPrioridadePrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrioridadePrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrioridadeJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrioridadeMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrioridadeSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrioridadeBrutoAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrioridadePrevidenciaAtualizado = BigDecimal.ZERO;

    private BigDecimal percentualHonorario = BigDecimal.ZERO;
    private BigDecimal valorPagoAdvogado = BigDecimal.ZERO;
    private BigDecimal percentualParteAdvogado = BigDecimal.ZERO;
    private BigDecimal percentualParteCredor = BigDecimal.ZERO;

    private BigDecimal valorHonorarioPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorHonorarioPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorHonorarioJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorHonorarioMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorHonorarioSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorHonorarioBrutoAtualizado = BigDecimal.ZERO;

    private BigDecimal valorCredorPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorCredorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorCredorJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorCredorMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorCredorSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorCredorBrutoAtualizado = BigDecimal.ZERO;

    private BigDecimal percentualDesagio = BigDecimal.ZERO;
    private boolean houveAcordoAdvogado = Boolean.FALSE;
    private boolean houveAcordoCredor = Boolean.FALSE;

    private BigDecimal valorDesagioHonorarioPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioBrutoAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioHonorarioAtualizado = BigDecimal.ZERO;

    private BigDecimal valorDesagioCredorPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorBrutoAtualizado = BigDecimal.ZERO;
    private BigDecimal valorDesagioCredorAtualizado = BigDecimal.ZERO;

    private BigDecimal baseTributavelCredor = BigDecimal.ZERO;

    private PagamentoUtil.ResultadoCalculo tipoCalculo;

    public void preencherResultadoCalculo(CalculoTributoRequest req){
        this.valorPrincipalTributavelAtualizado = req.getValorPrincipalTributavelAtualizado();
        this.valorPrincipalNaoTributavelAtualizado = req.getValorPrincipalNaoTributavelAtualizado();
        this.valorJurosAtualizado = req.getValorJurosAtualizado();
        this.valorMultaCustasOutrosAtualizado = req.getValorMultaCustaOutrosAtualizado();
        this.valorSelicAtualizado = req.getValorSelicAtualizada();
        this.valorBrutoAtualizado = req.getValorTotalAtualizada();
        this.valorPrevidenciaAtualizado = req.getValorPrevidenciaAtualizada();
    }

    public void preencherVariaveisDeCalculo(CalculoTributoRequest req){
        this.numeroCNPJCredor = req.getCnpjDevedor();
        this.temPrioridade = req.isTemPrioridade();
        this.temPagamentoParcial = req.isPagamentoParcial();
        this.valorBaseParcialPago = req.getValorPagamentoParcial();
        this.numeroMesesRRA = req.getNumeroMesesRRA();
        this.percentualDesagio = req.getPercentualDesagio();
        this.houveAcordoAdvogado = req.isAcordoAdvogado();
        this.houveAcordoCredor = req.isAcordoCredor();
        this.percentualHonorario = req.getPercentualHonorario();
        this.valorPagoAdvogado =req.getValorPagoAdvogado();
    }

}
