package br.jus.tjap.precatorio.calculadora.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodoResultado {
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal ipcaFator;
    private BigDecimal fatorJuros;

    private BigDecimal principalTributavel;
    private BigDecimal principalNaoTributavel;
    private BigDecimal valorJuros;
    private BigDecimal custasMulta;
    private BigDecimal selic;
    private BigDecimal totalAtualizado;

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public BigDecimal getIpcaFator() {
        return ipcaFator;
    }

    public void setIpcaFator(BigDecimal ipcaFator) {
        this.ipcaFator = ipcaFator;
    }

    public BigDecimal getFatorJuros() {
        return fatorJuros;
    }

    public void setFatorJuros(BigDecimal fatorJuros) {
        this.fatorJuros = fatorJuros;
    }

    public BigDecimal getPrincipalTributavel() {
        return principalTributavel;
    }

    public void setPrincipalTributavel(BigDecimal principalTributavel) {
        this.principalTributavel = principalTributavel;
    }

    public BigDecimal getPrincipalNaoTributavel() {
        return principalNaoTributavel;
    }

    public void setPrincipalNaoTributavel(BigDecimal principalNaoTributavel) {
        this.principalNaoTributavel = principalNaoTributavel;
    }

    public BigDecimal getValorJuros() {
        return valorJuros;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getCustasMulta() {
        return custasMulta;
    }

    public void setCustasMulta(BigDecimal custasMulta) {
        this.custasMulta = custasMulta;
    }

    public BigDecimal getSelic() {
        return selic;
    }

    public void setSelic(BigDecimal selic) {
        this.selic = selic;
    }

    public BigDecimal getTotalAtualizado() {
        return totalAtualizado;
    }

    public void setTotalAtualizado(BigDecimal totalAtualizado) {
        this.totalAtualizado = totalAtualizado;
    }
}
