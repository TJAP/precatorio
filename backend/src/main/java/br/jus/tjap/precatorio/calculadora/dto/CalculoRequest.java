package br.jus.tjap.precatorio.calculadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoRequest {

    private String numeroProcesso;
    private LocalDate dataUltimaAtualizacao;
    private int anoVencimento;
    // Data do período de graça é calculada conforme regra - não enviada
    private BigDecimal valorPrincipalTributavel;
    private BigDecimal valorPrincipalNaoTributavel;
    private BigDecimal valorJurosTributavel;
    private BigDecimal valorJurosNaoTributavel;

    private BigDecimal valorSelicPrincipal;
    private BigDecimal valorSelicJuros;
    private String tipoSelicTributacao;

    private BigDecimal custas;
    private BigDecimal multa;
    private BigDecimal outrosReembolsos;
    private String tipoIndice;
    private String tipoNaturezaRenda;

    private BigDecimal saldoRemanescente;

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public LocalDate getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDate dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public int getAnoVencimento() {
        return anoVencimento;
    }

    public void setAnoVencimento(int anoVencimento) {
        this.anoVencimento = anoVencimento;
    }

    public BigDecimal getValorPrincipalTributavel() {
        return valorPrincipalTributavel;
    }

    public void setValorPrincipalTributavel(BigDecimal valorPrincipalTributavel) {
        this.valorPrincipalTributavel = valorPrincipalTributavel;
    }

    public BigDecimal getValorPrincipalNaoTributavel() {
        return valorPrincipalNaoTributavel;
    }

    public void setValorPrincipalNaoTributavel(BigDecimal valorPrincipalNaoTributavel) {
        this.valorPrincipalNaoTributavel = valorPrincipalNaoTributavel;
    }

    public BigDecimal getValorJurosTributavel() {
        return valorJurosTributavel;
    }

    public void setValorJurosTributavel(BigDecimal valorJurosTributavel) {
        this.valorJurosTributavel = valorJurosTributavel;
    }

    public BigDecimal getValorJurosNaoTributavel() {
        return valorJurosNaoTributavel;
    }

    public void setValorJurosNaoTributavel(BigDecimal valorJurosNaoTributavel) {
        this.valorJurosNaoTributavel = valorJurosNaoTributavel;
    }

    public BigDecimal getValorSelicPrincipal() {
        return valorSelicPrincipal;
    }

    public void setValorSelicPrincipal(BigDecimal valorSelicPrincipal) {
        this.valorSelicPrincipal = valorSelicPrincipal;
    }

    public BigDecimal getValorSelicJuros() {
        return valorSelicJuros;
    }

    public void setValorSelicJuros(BigDecimal valorSelicJuros) {
        this.valorSelicJuros = valorSelicJuros;
    }

    public String getTipoSelicTributacao() {
        return tipoSelicTributacao;
    }

    public void setTipoSelicTributacao(String tipoSelicTributacao) {
        this.tipoSelicTributacao = tipoSelicTributacao;
    }

    public BigDecimal getCustas() {
        return custas;
    }

    public void setCustas(BigDecimal custas) {
        this.custas = custas;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getOutrosReembolsos() {
        return outrosReembolsos;
    }

    public void setOutrosReembolsos(BigDecimal outrosReembolsos) {
        this.outrosReembolsos = outrosReembolsos;
    }

    public String getTipoIndice() {
        return tipoIndice;
    }

    public void setTipoIndice(String tipoIndice) {
        this.tipoIndice = tipoIndice;
    }

    public String getTipoNaturezaRenda() {
        return tipoNaturezaRenda;
    }

    public void setTipoNaturezaRenda(String tipoNaturezaRenda) {
        this.tipoNaturezaRenda = tipoNaturezaRenda;
    }

    public BigDecimal getSaldoRemanescente() {
        return saldoRemanescente;
    }

    public void setSaldoRemanescente(BigDecimal saldoRemanescente) {
        this.saldoRemanescente = saldoRemanescente;
    }
}
