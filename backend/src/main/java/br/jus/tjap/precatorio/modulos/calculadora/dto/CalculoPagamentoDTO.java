package br.jus.tjap.precatorio.modulos.calculadora.dto;

import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoPagamentoDTO {

    private String numeroCNPJCredor;
    private Long idPrecatorio;

    private BigDecimal valorPrincipalTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
    private BigDecimal valorJurosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorMultaCustasOutrosAtualizado = BigDecimal.ZERO;
    private BigDecimal valorSelicAtualizado = BigDecimal.ZERO;
    private BigDecimal valorBrutoAtualizado = BigDecimal.ZERO;
    private BigDecimal valorPrevidenciaAtualizado = BigDecimal.ZERO;

    @JsonIgnore
    private BigDecimal valorPrincipalTributavelAtualizadoDizima = BigDecimal.ZERO;
    @JsonIgnore
    private BigDecimal valorPrincipalNaoTributavelAtualizadoDizima = BigDecimal.ZERO;
    @JsonIgnore
    private BigDecimal valorJurosAtualizadoDizima = BigDecimal.ZERO;
    @JsonIgnore
    private BigDecimal valorMultaCustasOutrosAtualizadoDizima = BigDecimal.ZERO;
    @JsonIgnore
    private BigDecimal valorSelicAtualizadoDizima = BigDecimal.ZERO;
    @JsonIgnore
    private BigDecimal valorBrutoAtualizadoDizima = BigDecimal.ZERO;
    @JsonIgnore
    private BigDecimal valorPrevidenciaAtualizadoDizima = BigDecimal.ZERO;

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
    private BigDecimal numeroPrioridadeRRA = BigDecimal.ZERO;

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

    private String tributacaoAdvogado;
    private String tipoVinculoCredor;
    private String tipoTributacaoCredor;

    private BigDecimal baseTributavelHonorarioValor = BigDecimal.ZERO;
    private String baseTributavelHonorarioTipo;
    private BigDecimal baseTributavelHonorarioImposto = BigDecimal.ZERO;

    private String baseTributavelCredorTipoCalculo;
    private BigDecimal baseTributavelCredorValor = BigDecimal.ZERO;
    private String baseTributavelCredorTipo;
    private BigDecimal baseTributavelCredorImposto = BigDecimal.ZERO;
    private BigDecimal baseTributavelCredorPrevidencia = BigDecimal.ZERO;

    private BigDecimal cessaoPercentual = BigDecimal.ZERO;
    private BigDecimal cessaoBaseValor = BigDecimal.ZERO;
    private BigDecimal penhoraValor = BigDecimal.ZERO;
    private BigDecimal penhoraBaseValor = BigDecimal.ZERO;

    private BigDecimal saldoRemanescentePercentual = BigDecimal.ZERO;
    private BigDecimal saldoRemanescentePrincipalTributavel = BigDecimal.ZERO;
    private BigDecimal saldoRemanescentePrincipalNaoTributavel = BigDecimal.ZERO;
    private BigDecimal saldoRemanescenteJuros = BigDecimal.ZERO;
    private BigDecimal saldoRemanescenteMultasCustasOutros = BigDecimal.ZERO;
    private BigDecimal saldoRemanescenteSelic = BigDecimal.ZERO;
    private BigDecimal saldoRemanescenteTotal = BigDecimal.ZERO;
    private BigDecimal saldoRemanescenteTotalRRA = BigDecimal.ZERO;
    private BigDecimal saldoRemanescentePrevidencia = BigDecimal.ZERO;

    public void preencherResultadoCalculo(CalculoTributoRequest req){
        this.idPrecatorio = req.getIdPrecatorio();
        this.valorPrincipalTributavelAtualizadoDizima = req.getValorPrincipalTributavelAtualizado();
        this.valorPrincipalNaoTributavelAtualizadoDizima = req.getValorPrincipalNaoTributavelAtualizado();
        this.valorJurosAtualizadoDizima = req.getValorJurosAtualizado();
        this.valorMultaCustasOutrosAtualizadoDizima = req.getValorMultaCustaOutrosAtualizado();
        this.valorSelicAtualizadoDizima = req.getValorSelicAtualizada();
        this.valorBrutoAtualizadoDizima = req.getValorTotalAtualizada();
        this.valorPrevidenciaAtualizadoDizima = req.getValorPrevidenciaAtualizada();

        this.valorPrincipalTributavelAtualizado = UtilCalculo.escala(req.getValorPrincipalTributavelAtualizado(),2);
        this.valorPrincipalNaoTributavelAtualizado = UtilCalculo.escala(req.getValorPrincipalNaoTributavelAtualizado(),2);
        this.valorJurosAtualizado = UtilCalculo.escala(req.getValorJurosAtualizado(),2);
        this.valorMultaCustasOutrosAtualizado = UtilCalculo.escala(req.getValorMultaCustaOutrosAtualizado(),2);
        this.valorSelicAtualizado = UtilCalculo.escala(req.getValorSelicAtualizada(),2);
        this.valorBrutoAtualizado = UtilCalculo.escala(req.getValorTotalAtualizada(),2);
        this.valorPrevidenciaAtualizado = UtilCalculo.escala(req.getValorPrevidenciaAtualizada(),2);
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
        this.tributacaoAdvogado = req.getTributacaoAdvogado();
        this.tipoVinculoCredor = req.getTipoVinculoCredor();
        this.tipoTributacaoCredor = req.getTipoTributacaoCredor();
        this.cessaoPercentual = req.getPercentualCessao();
        this.penhoraValor = req.getValorPenhora();
    }

}
