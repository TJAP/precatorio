package br.jus.tjap.precatorio.calculadora.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoResponse {

    private static final BigDecimal ZERO = new BigDecimal("0.00");

    private String numeroProcesso;

    private YearMonth dataInicial;
    private YearMonth dataFinal;
    private String indicePrecatorio = "IPCA-E";
    private BigDecimal fatorCorrecaoMonetaria; // acumulado IPCA-E
    private String tipoJurosPrecatorio = "Poupança";

    // Indice de calculo
    private BigDecimal percJurosPeriodo; // soma juros poupança período
    private BigDecimal percJurosPeriodoDeGraca; // soma juros poupança período de graç;
    private BigDecimal percJurosPrincipal;



    //@JsonIgnore
    private LocalDate dataInicialAntesGraca;
    //@JsonIgnore
    private LocalDate dataFinalAntesGraca;
    //@JsonIgnore
    private LocalDate dataInicialCorrecaoDuranteGraca;
    //@JsonIgnore
    private LocalDate dataFinalCorreccaoDuranteGraca;
    //@JsonIgnore
    private LocalDate dataInicialPosGraca;
    //@JsonIgnore
    private LocalDate dataFinalPosGraca;






    private BigDecimal principalTributavelCorrigido = ZERO;
    private BigDecimal principalNaoTributavelCorrigido = ZERO;
    //@JsonIgnore
    private BigDecimal totalPrincipalCorrigido = ZERO;

    private BigDecimal jurosTributavelCorrigido = ZERO;
    private BigDecimal jurosNaoTributavelCorrigido = ZERO;
    //@JsonIgnore
    private BigDecimal totalJurosCorrigido = ZERO;

    private BigDecimal custasMultasOutrosAtualizados;

    //@JsonIgnore
    private BigDecimal jurosPeriodoTributavel = ZERO;
    //@JsonIgnore
    private BigDecimal jurosPeriodoNaoTributavel = ZERO;
    //@JsonIgnore
    private BigDecimal totaisJurosTributavel = ZERO;
    //@JsonIgnore
    private BigDecimal totaisJurosNaoTributavel = ZERO;

    private BigDecimal principalAtualizado = ZERO;
    //@JsonIgnore
    private BigDecimal jurosAplicados = ZERO;

    //@JsonIgnore
    private BigDecimal selicTaxaAntesGraca = ZERO;
    //@JsonIgnore
    private BigDecimal selicFatorIpcaDuranteGraca = ZERO;

    private BigDecimal selicPrincipalTributavel = ZERO;
    private BigDecimal selicPrincipalNaoTributavel = ZERO;
    private BigDecimal selicJurosAtualizadoIpca = ZERO;
    private BigDecimal selicMultasCustasAtualizada = ZERO;
    private BigDecimal selicAtualizada = ZERO;
    private BigDecimal selicTaxa = ZERO;

    private BigDecimal selicValorPrincipal = ZERO;
    private BigDecimal selicValorJuros = ZERO;
    private BigDecimal selicValorSubtotal = ZERO;
    private BigDecimal selicTotaisAtualizada = ZERO;

    private BigDecimal totalAtualizadaAntesEC113 = ZERO;
    private BigDecimal totalAtualizadaPosEC113 = ZERO;

    private BigDecimal totaisPrecatorioAtualizado = ZERO;


    private BigDecimal irpfBaseTributavel = ZERO;
    private BigDecimal irpfBaseNaoTributavel = ZERO;
    private BigDecimal irpfTotalBruto = ZERO;
    private BigDecimal irpfMesesRRA = ZERO;
    private BigDecimal irpfPrevidencia = ZERO;
    private BigDecimal irpfHC = ZERO;
    private BigDecimal irpfValorIR = ZERO;
    private BigDecimal irpfValorLiquido = ZERO;

    private BigDecimal desconto = ZERO;


    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public YearMonth getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(YearMonth dataInicial) {
        this.dataInicial = dataInicial;
    }

    public YearMonth getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(YearMonth dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getIndicePrecatorio() {
        return indicePrecatorio;
    }

    public void setIndicePrecatorio(String indicePrecatorio) {
        this.indicePrecatorio = indicePrecatorio;
    }

    public BigDecimal getFatorCorrecaoMonetaria() {
        return fatorCorrecaoMonetaria;
    }

    public void setFatorCorrecaoMonetaria(BigDecimal fatorCorrecaoMonetaria) {
        this.fatorCorrecaoMonetaria = fatorCorrecaoMonetaria;
    }

    public String getTipoJurosPrecatorio() {
        return tipoJurosPrecatorio;
    }

    public void setTipoJurosPrecatorio(String tipoJurosPrecatorio) {
        this.tipoJurosPrecatorio = tipoJurosPrecatorio;
    }

    public BigDecimal getPercJurosPeriodo() {
        return percJurosPeriodo;
    }

    public void setPercJurosPeriodo(BigDecimal percJurosPeriodo) {
        this.percJurosPeriodo = percJurosPeriodo;
    }

    public BigDecimal getPercJurosPeriodoDeGraca() {
        return percJurosPeriodoDeGraca;
    }

    public void setPercJurosPeriodoDeGraca(BigDecimal percJurosPeriodoDeGraca) {
        this.percJurosPeriodoDeGraca = percJurosPeriodoDeGraca;
    }

    public BigDecimal getPercJurosPrincipal() {
        return percJurosPrincipal;
    }

    public void setPercJurosPrincipal(BigDecimal percJurosPrincipal) {
        this.percJurosPrincipal = percJurosPrincipal;
    }

    public LocalDate getDataInicialAntesGraca() {
        return dataInicialAntesGraca;
    }

    public void setDataInicialAntesGraca(LocalDate dataInicialAntesGraca) {
        this.dataInicialAntesGraca = dataInicialAntesGraca;
    }

    public LocalDate getDataFinalAntesGraca() {
        return dataFinalAntesGraca;
    }

    public void setDataFinalAntesGraca(LocalDate dataFinalAntesGraca) {
        this.dataFinalAntesGraca = dataFinalAntesGraca;
    }

    public LocalDate getDataInicialCorrecaoDuranteGraca() {
        return dataInicialCorrecaoDuranteGraca;
    }

    public void setDataInicialCorrecaoDuranteGraca(LocalDate dataInicialCorrecaoDuranteGraca) {
        this.dataInicialCorrecaoDuranteGraca = dataInicialCorrecaoDuranteGraca;
    }

    public LocalDate getDataFinalCorreccaoDuranteGraca() {
        return dataFinalCorreccaoDuranteGraca;
    }

    public void setDataFinalCorreccaoDuranteGraca(LocalDate dataFinalCorreccaoDuranteGraca) {
        this.dataFinalCorreccaoDuranteGraca = dataFinalCorreccaoDuranteGraca;
    }

    public LocalDate getDataInicialPosGraca() {
        return dataInicialPosGraca;
    }

    public void setDataInicialPosGraca(LocalDate dataInicialPosGraca) {
        this.dataInicialPosGraca = dataInicialPosGraca;
    }

    public LocalDate getDataFinalPosGraca() {
        return dataFinalPosGraca;
    }

    public void setDataFinalPosGraca(LocalDate dataFinalPosGraca) {
        this.dataFinalPosGraca = dataFinalPosGraca;
    }

    public BigDecimal getPrincipalTributavelCorrigido() {
        return principalTributavelCorrigido;
    }

    public void setPrincipalTributavelCorrigido(BigDecimal principalTributavelCorrigido) {
        this.principalTributavelCorrigido = principalTributavelCorrigido;
    }

    public BigDecimal getPrincipalNaoTributavelCorrigido() {
        return principalNaoTributavelCorrigido;
    }

    public void setPrincipalNaoTributavelCorrigido(BigDecimal principalNaoTributavelCorrigido) {
        this.principalNaoTributavelCorrigido = principalNaoTributavelCorrigido;
    }

    public BigDecimal getTotalPrincipalCorrigido() {
        return totalPrincipalCorrigido;
    }

    public void setTotalPrincipalCorrigido(BigDecimal totalPrincipalCorrigido) {
        this.totalPrincipalCorrigido = totalPrincipalCorrigido;
    }

    public BigDecimal getJurosTributavelCorrigido() {
        return jurosTributavelCorrigido;
    }

    public void setJurosTributavelCorrigido(BigDecimal jurosTributavelCorrigido) {
        this.jurosTributavelCorrigido = jurosTributavelCorrigido;
    }

    public BigDecimal getJurosNaoTributavelCorrigido() {
        return jurosNaoTributavelCorrigido;
    }

    public void setJurosNaoTributavelCorrigido(BigDecimal jurosNaoTributavelCorrigido) {
        this.jurosNaoTributavelCorrigido = jurosNaoTributavelCorrigido;
    }

    public BigDecimal getTotalJurosCorrigido() {
        return totalJurosCorrigido;
    }

    public void setTotalJurosCorrigido(BigDecimal totalJurosCorrigido) {
        this.totalJurosCorrigido = totalJurosCorrigido;
    }

    public BigDecimal getCustasMultasOutrosAtualizados() {
        return custasMultasOutrosAtualizados;
    }

    public void setCustasMultasOutrosAtualizados(BigDecimal custasMultasOutrosAtualizados) {
        this.custasMultasOutrosAtualizados = custasMultasOutrosAtualizados;
    }

    public BigDecimal getJurosPeriodoTributavel() {
        return jurosPeriodoTributavel;
    }

    public void setJurosPeriodoTributavel(BigDecimal jurosPeriodoTributavel) {
        this.jurosPeriodoTributavel = jurosPeriodoTributavel;
    }

    public BigDecimal getJurosPeriodoNaoTributavel() {
        return jurosPeriodoNaoTributavel;
    }

    public void setJurosPeriodoNaoTributavel(BigDecimal jurosPeriodoNaoTributavel) {
        this.jurosPeriodoNaoTributavel = jurosPeriodoNaoTributavel;
    }

    public BigDecimal getTotaisJurosTributavel() {
        return totaisJurosTributavel;
    }

    public void setTotaisJurosTributavel(BigDecimal totaisJurosTributavel) {
        this.totaisJurosTributavel = totaisJurosTributavel;
    }

    public BigDecimal getTotaisJurosNaoTributavel() {
        return totaisJurosNaoTributavel;
    }

    public void setTotaisJurosNaoTributavel(BigDecimal totaisJurosNaoTributavel) {
        this.totaisJurosNaoTributavel = totaisJurosNaoTributavel;
    }

    public BigDecimal getPrincipalAtualizado() {
        return principalAtualizado;
    }

    public void setPrincipalAtualizado(BigDecimal principalAtualizado) {
        this.principalAtualizado = principalAtualizado;
    }

    public BigDecimal getJurosAplicados() {
        return jurosAplicados;
    }

    public void setJurosAplicados(BigDecimal jurosAplicados) {
        this.jurosAplicados = jurosAplicados;
    }

    public BigDecimal getSelicTaxaAntesGraca() {
        return selicTaxaAntesGraca;
    }

    public void setSelicTaxaAntesGraca(BigDecimal selicTaxaAntesGraca) {
        this.selicTaxaAntesGraca = selicTaxaAntesGraca;
    }

    public BigDecimal getSelicFatorIpcaDuranteGraca() {
        return selicFatorIpcaDuranteGraca;
    }

    public void setSelicFatorIpcaDuranteGraca(BigDecimal selicFatorIpcaDuranteGraca) {
        this.selicFatorIpcaDuranteGraca = selicFatorIpcaDuranteGraca;
    }

    public BigDecimal getSelicPrincipalTributavel() {
        return selicPrincipalTributavel;
    }

    public void setSelicPrincipalTributavel(BigDecimal selicPrincipalTributavel) {
        this.selicPrincipalTributavel = selicPrincipalTributavel;
    }

    public BigDecimal getSelicPrincipalNaoTributavel() {
        return selicPrincipalNaoTributavel;
    }

    public void setSelicPrincipalNaoTributavel(BigDecimal selicPrincipalNaoTributavel) {
        this.selicPrincipalNaoTributavel = selicPrincipalNaoTributavel;
    }

    public BigDecimal getSelicJurosAtualizadoIpca() {
        return selicJurosAtualizadoIpca;
    }

    public void setSelicJurosAtualizadoIpca(BigDecimal selicJurosAtualizadoIpca) {
        this.selicJurosAtualizadoIpca = selicJurosAtualizadoIpca;
    }

    public BigDecimal getSelicMultasCustasAtualizada() {
        return selicMultasCustasAtualizada;
    }

    public void setSelicMultasCustasAtualizada(BigDecimal selicMultasCustasAtualizada) {
        this.selicMultasCustasAtualizada = selicMultasCustasAtualizada;
    }

    public BigDecimal getSelicAtualizada() {
        return selicAtualizada;
    }

    public void setSelicAtualizada(BigDecimal selicAtualizada) {
        this.selicAtualizada = selicAtualizada;
    }

    public BigDecimal getSelicTaxa() {
        return selicTaxa;
    }

    public void setSelicTaxa(BigDecimal selicTaxa) {
        this.selicTaxa = selicTaxa;
    }

    public BigDecimal getSelicValorPrincipal() {
        return selicValorPrincipal;
    }

    public void setSelicValorPrincipal(BigDecimal selicValorPrincipal) {
        this.selicValorPrincipal = selicValorPrincipal;
    }

    public BigDecimal getSelicValorJuros() {
        return selicValorJuros;
    }

    public void setSelicValorJuros(BigDecimal selicValorJuros) {
        this.selicValorJuros = selicValorJuros;
    }

    public BigDecimal getSelicValorSubtotal() {
        return selicValorSubtotal;
    }

    public void setSelicValorSubtotal(BigDecimal selicValorSubtotal) {
        this.selicValorSubtotal = selicValorSubtotal;
    }

    public BigDecimal getSelicTotaisAtualizada() {
        return selicTotaisAtualizada;
    }

    public void setSelicTotaisAtualizada(BigDecimal selicTotaisAtualizada) {
        this.selicTotaisAtualizada = selicTotaisAtualizada;
    }

    public BigDecimal getTotalAtualizadaAntesEC113() {
        return totalAtualizadaAntesEC113;
    }

    public void setTotalAtualizadaAntesEC113(BigDecimal totalAtualizadaAntesEC113) {
        this.totalAtualizadaAntesEC113 = totalAtualizadaAntesEC113;
    }

    public BigDecimal getTotalAtualizadaPosEC113() {
        return totalAtualizadaPosEC113;
    }

    public void setTotalAtualizadaPosEC113(BigDecimal totalAtualizadaPosEC113) {
        this.totalAtualizadaPosEC113 = totalAtualizadaPosEC113;
    }

    public BigDecimal getTotaisPrecatorioAtualizado() {
        return totaisPrecatorioAtualizado;
    }

    public void setTotaisPrecatorioAtualizado(BigDecimal totaisPrecatorioAtualizado) {
        this.totaisPrecatorioAtualizado = totaisPrecatorioAtualizado;
    }

    public BigDecimal getIrpfBaseTributavel() {
        return irpfBaseTributavel;
    }

    public void setIrpfBaseTributavel(BigDecimal irpfBaseTributavel) {
        this.irpfBaseTributavel = irpfBaseTributavel;
    }

    public BigDecimal getIrpfBaseNaoTributavel() {
        return irpfBaseNaoTributavel;
    }

    public void setIrpfBaseNaoTributavel(BigDecimal irpfBaseNaoTributavel) {
        this.irpfBaseNaoTributavel = irpfBaseNaoTributavel;
    }

    public BigDecimal getIrpfTotalBruto() {
        return irpfTotalBruto;
    }

    public void setIrpfTotalBruto(BigDecimal irpfTotalBruto) {
        this.irpfTotalBruto = irpfTotalBruto;
    }

    public BigDecimal getIrpfMesesRRA() {
        return irpfMesesRRA;
    }

    public void setIrpfMesesRRA(BigDecimal irpfMesesRRA) {
        this.irpfMesesRRA = irpfMesesRRA;
    }

    public BigDecimal getIrpfPrevidencia() {
        return irpfPrevidencia;
    }

    public void setIrpfPrevidencia(BigDecimal irpfPrevidencia) {
        this.irpfPrevidencia = irpfPrevidencia;
    }

    public BigDecimal getIrpfHC() {
        return irpfHC;
    }

    public void setIrpfHC(BigDecimal irpfHC) {
        this.irpfHC = irpfHC;
    }

    public BigDecimal getIrpfValorIR() {
        return irpfValorIR;
    }

    public void setIrpfValorIR(BigDecimal irpfValorIR) {
        this.irpfValorIR = irpfValorIR;
    }

    public BigDecimal getIrpfValorLiquido() {
        return irpfValorLiquido;
    }

    public void setIrpfValorLiquido(BigDecimal irpfValorLiquido) {
        this.irpfValorLiquido = irpfValorLiquido;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }
}


