package br.jus.tjap.precatorio.calculadora.dto;

import br.jus.tjap.precatorio.calculadora.util.UtilCalculo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoRetornoDTO {

    private static final BigDecimal ZERO = new BigDecimal("0.00");

    private Long idPrecatorio;
    private String numeroProcesso;
    private String tipoCalculoRetornado;

    private LocalDate dataAntesGracaDataInicio;
    private LocalDate dataAntesGracaDataFim;
    private LocalDate dataDuranteGracaDataInicio;
    private LocalDate dataDuranteGracaDataFim;
    private LocalDate dataPosGracaDataInicio;
    private LocalDate dataPosGracaDataFim;

    // IPCA + 2%
    private BigDecimal ipcaAntesGracaFator = ZERO;
    private BigDecimal ipcaAntesGracaFatorJuros = ZERO;
    private BigDecimal ipcaAntesGracaPrincipalTributavelCorrigido = ZERO;
    private BigDecimal ipcaAntesGracaPrincipalNaoTributavelCorrigido = ZERO;
    private BigDecimal ipcaAntesGracaValorJurosCorrigido = ZERO;
    private BigDecimal ipcaAntesGracaCustasMultaCorrigido = ZERO;
    private BigDecimal ipcaAntesGracaSelicCorrigido = ZERO;
    private BigDecimal ipcaAntesGracaTotalAtualizado = ZERO;

    private BigDecimal ipcaDuranteGracaFator = ZERO;
    private BigDecimal ipcaDuranteGracaFatorJuros = ZERO;
    private BigDecimal ipcaDuranteGracaPrincipalTributavelCorrigido = ZERO;
    private BigDecimal ipcaDuranteGracaPrincipalNaoTributavelCorrigido = ZERO;
    private BigDecimal ipcaDuranteGracaValorJurosCorrigido = ZERO;
    private BigDecimal ipcaDuranteGracaCustasMultaCorrigido = ZERO;
    private BigDecimal ipcaDuranteGracaSelicCorrigido = ZERO;
    private BigDecimal ipcaDuranteGracaTotalAtualizado = ZERO;

    private BigDecimal ipcaPosGracaFator = ZERO;
    private BigDecimal ipcaPosGracaFatorJuros = ZERO;
    private BigDecimal ipcaPosGracaPrincipalTributavelCorrigido = ZERO;
    private BigDecimal ipcaPosGracaPrincipalNaoTributavelCorrigido = ZERO;
    private BigDecimal ipcaPosGracaValorJurosCorrigido = ZERO;
    private BigDecimal ipcaPosGracaCustasMultaCorrigido = ZERO;
    private BigDecimal ipcaPosGracaSelicCorrigido = ZERO;
    private BigDecimal ipcaPosGracaTotalAtualizado = ZERO;

    // SELIC
    private BigDecimal selicAntesGracaTaxa = ZERO;
    private BigDecimal selicAntesGracaSelicValorCorrigido = ZERO;

    private BigDecimal selicDuranteGracaFatorIPCA = ZERO;
    private BigDecimal selicDuranteGracaPrincipalTributavelCorrigido = ZERO;
    private BigDecimal selicDuranteGracaPrincipalNaoTributavelCorrigido = ZERO;
    private BigDecimal selicDuranteGracaValorJurosCorrigido = ZERO;
    private BigDecimal selicDuranteGracaCustasMultaCorrigido = ZERO;
    private BigDecimal selicDuranteGracaSelicCorrigido = ZERO;
    private BigDecimal selicDuranteGracaTotalAtualizado = ZERO;

    private BigDecimal selicPosGracaTaxa = ZERO;
    private BigDecimal selicPosGracaSelicValorCorrigido = ZERO;
    private BigDecimal selicPosGracaTotalAtualizado = ZERO;

    private BigDecimal valorGlobalAtualizado = ZERO;

    // Calculo previdenciário
    // Tipo regime: RPPS Regime Próprio de Previdência Social, um sistema de previdência para servidores públicos,
    // RGPS Regime Geral de Previdência Social, o sistema para trabalhadores do setor privado e outros, gerido pelo INSS
    private BigDecimal prevValorBase = ZERO;

    private BigDecimal arredondar(BigDecimal valor) {
        if (valor == null) {
            return UtilCalculo.escala(BigDecimal.ZERO,2);
        }
        return UtilCalculo.escala(valor,2);
    }

    public void preencherIpcaAntes(PeriodoResultado pr) {
        this.dataAntesGracaDataInicio = pr.getDataInicio();
        this.dataAntesGracaDataFim = pr.getDataFim();
        this.ipcaAntesGracaFator = pr.getIpcaFator();
        this.ipcaAntesGracaFatorJuros = pr.getFatorJuros();
        this.ipcaAntesGracaPrincipalTributavelCorrigido = pr.getPrincipalTributavel();
        this.ipcaAntesGracaPrincipalNaoTributavelCorrigido = pr.getPrincipalNaoTributavel();
        this.ipcaAntesGracaValorJurosCorrigido = pr.getValorJuros();
        this.ipcaAntesGracaCustasMultaCorrigido = pr.getCustasMulta();
        this.ipcaAntesGracaSelicCorrigido = pr.getSelic();
        this.ipcaAntesGracaTotalAtualizado = pr.getTotalAtualizado();
    }

    public void preencherIpcaDurante(PeriodoResultado pr) {
        this.dataDuranteGracaDataInicio = pr.getDataInicio();
        this.dataDuranteGracaDataFim = pr.getDataFim();
        this.ipcaDuranteGracaFator = pr.getIpcaFator();
        this.ipcaDuranteGracaFatorJuros = pr.getFatorJuros();
        this.ipcaDuranteGracaPrincipalTributavelCorrigido = pr.getPrincipalTributavel();
        this.ipcaDuranteGracaPrincipalNaoTributavelCorrigido = pr.getPrincipalNaoTributavel();
        this.ipcaDuranteGracaValorJurosCorrigido = pr.getValorJuros();
        this.ipcaDuranteGracaCustasMultaCorrigido = pr.getCustasMulta();
        this.ipcaDuranteGracaSelicCorrigido = pr.getSelic();
        this.ipcaDuranteGracaTotalAtualizado = pr.getTotalAtualizado();
    }

    public void preencherIpcaDepois(PeriodoResultado pr) {
        this.dataPosGracaDataInicio = pr.getDataInicio();
        this.dataPosGracaDataFim = pr.getDataFim();
        this.ipcaPosGracaFator = pr.getIpcaFator();
        this.ipcaPosGracaFatorJuros = pr.getFatorJuros();
        this.ipcaPosGracaPrincipalTributavelCorrigido = pr.getPrincipalTributavel();
        this.ipcaPosGracaPrincipalNaoTributavelCorrigido = pr.getPrincipalNaoTributavel();
        this.ipcaPosGracaValorJurosCorrigido = pr.getValorJuros();
        this.ipcaPosGracaCustasMultaCorrigido = pr.getCustasMulta();
        this.ipcaPosGracaSelicCorrigido = pr.getSelic();
        this.ipcaPosGracaTotalAtualizado = pr.getTotalAtualizado();
    }

    public void preencherIpcaAntesComEscala() {
        this.ipcaAntesGracaPrincipalTributavelCorrigido = arredondar(this.ipcaAntesGracaPrincipalTributavelCorrigido);
        this.ipcaAntesGracaPrincipalNaoTributavelCorrigido = arredondar(this.ipcaAntesGracaPrincipalNaoTributavelCorrigido);
        this.ipcaAntesGracaValorJurosCorrigido = arredondar(this.ipcaAntesGracaValorJurosCorrigido);
        this.ipcaAntesGracaCustasMultaCorrigido = arredondar(this.ipcaAntesGracaCustasMultaCorrigido);
        this.ipcaAntesGracaSelicCorrigido = arredondar(this.ipcaAntesGracaSelicCorrigido);
        this.ipcaAntesGracaTotalAtualizado = arredondar(this.ipcaAntesGracaTotalAtualizado);
    }

    public void preencherIpcaDuranteComEscala() {
        this.ipcaDuranteGracaPrincipalTributavelCorrigido = arredondar(this.ipcaDuranteGracaPrincipalTributavelCorrigido);
        this.ipcaDuranteGracaPrincipalNaoTributavelCorrigido = arredondar(this.ipcaDuranteGracaPrincipalNaoTributavelCorrigido);
        this.ipcaDuranteGracaValorJurosCorrigido = arredondar(this.ipcaDuranteGracaValorJurosCorrigido);
        this.ipcaDuranteGracaCustasMultaCorrigido = arredondar(this.ipcaDuranteGracaCustasMultaCorrigido);
        this.ipcaDuranteGracaSelicCorrigido = arredondar(this.ipcaDuranteGracaSelicCorrigido);
        this.ipcaDuranteGracaTotalAtualizado = arredondar(this.ipcaDuranteGracaTotalAtualizado);
    }

    public void preencherIpcaDepoisComEscala() {
        this.ipcaPosGracaPrincipalTributavelCorrigido = arredondar(this.ipcaPosGracaPrincipalTributavelCorrigido);
        this.ipcaPosGracaPrincipalNaoTributavelCorrigido = arredondar(this.ipcaPosGracaPrincipalNaoTributavelCorrigido);
        this.ipcaPosGracaValorJurosCorrigido = arredondar(this.ipcaPosGracaValorJurosCorrigido);
        this.ipcaPosGracaCustasMultaCorrigido = arredondar(this.ipcaPosGracaCustasMultaCorrigido);
        this.ipcaPosGracaSelicCorrigido = arredondar(this.ipcaPosGracaSelicCorrigido);
        this.ipcaPosGracaTotalAtualizado = arredondar(this.ipcaPosGracaTotalAtualizado);
    }

    public Long getIdPrecatorio() {
        return idPrecatorio;
    }

    public void setIdPrecatorio(Long idPrecatorio) {
        this.idPrecatorio = idPrecatorio;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getTipoCalculoRetornado() {
        return tipoCalculoRetornado;
    }

    public void setTipoCalculoRetornado(String tipoCalculoRetornado) {
        this.tipoCalculoRetornado = tipoCalculoRetornado;
    }

    public LocalDate getDataAntesGracaDataInicio() {
        return dataAntesGracaDataInicio;
    }

    public void setDataAntesGracaDataInicio(LocalDate dataAntesGracaDataInicio) {
        this.dataAntesGracaDataInicio = dataAntesGracaDataInicio;
    }

    public LocalDate getDataAntesGracaDataFim() {
        return dataAntesGracaDataFim;
    }

    public void setDataAntesGracaDataFim(LocalDate dataAntesGracaDataFim) {
        this.dataAntesGracaDataFim = dataAntesGracaDataFim;
    }

    public LocalDate getDataDuranteGracaDataInicio() {
        return dataDuranteGracaDataInicio;
    }

    public void setDataDuranteGracaDataInicio(LocalDate dataDuranteGracaDataInicio) {
        this.dataDuranteGracaDataInicio = dataDuranteGracaDataInicio;
    }

    public LocalDate getDataDuranteGracaDataFim() {
        return dataDuranteGracaDataFim;
    }

    public void setDataDuranteGracaDataFim(LocalDate dataDuranteGracaDataFim) {
        this.dataDuranteGracaDataFim = dataDuranteGracaDataFim;
    }

    public LocalDate getDataPosGracaDataInicio() {
        return dataPosGracaDataInicio;
    }

    public void setDataPosGracaDataInicio(LocalDate dataPosGracaDataInicio) {
        this.dataPosGracaDataInicio = dataPosGracaDataInicio;
    }

    public LocalDate getDataPosGracaDataFim() {
        return dataPosGracaDataFim;
    }

    public void setDataPosGracaDataFim(LocalDate dataPosGracaDataFim) {
        this.dataPosGracaDataFim = dataPosGracaDataFim;
    }

    public BigDecimal getIpcaAntesGracaFator() {
        return ipcaAntesGracaFator;
    }

    public void setIpcaAntesGracaFator(BigDecimal ipcaAntesGracaFator) {
        this.ipcaAntesGracaFator = ipcaAntesGracaFator;
    }

    public BigDecimal getIpcaAntesGracaFatorJuros() {
        return ipcaAntesGracaFatorJuros;
    }

    public void setIpcaAntesGracaFatorJuros(BigDecimal ipcaAntesGracaFatorJuros) {
        this.ipcaAntesGracaFatorJuros = ipcaAntesGracaFatorJuros;
    }

    public BigDecimal getIpcaAntesGracaPrincipalTributavelCorrigido() {
        return ipcaAntesGracaPrincipalTributavelCorrigido;
    }

    public void setIpcaAntesGracaPrincipalTributavelCorrigido(BigDecimal ipcaAntesGracaPrincipalTributavelCorrigido) {
        this.ipcaAntesGracaPrincipalTributavelCorrigido = ipcaAntesGracaPrincipalTributavelCorrigido;
    }

    public BigDecimal getIpcaAntesGracaPrincipalNaoTributavelCorrigido() {
        return ipcaAntesGracaPrincipalNaoTributavelCorrigido;
    }

    public void setIpcaAntesGracaPrincipalNaoTributavelCorrigido(BigDecimal ipcaAntesGracaPrincipalNaoTributavelCorrigido) {
        this.ipcaAntesGracaPrincipalNaoTributavelCorrigido = ipcaAntesGracaPrincipalNaoTributavelCorrigido;
    }

    public BigDecimal getIpcaAntesGracaValorJurosCorrigido() {
        return ipcaAntesGracaValorJurosCorrigido;
    }

    public void setIpcaAntesGracaValorJurosCorrigido(BigDecimal ipcaAntesGracaValorJurosCorrigido) {
        this.ipcaAntesGracaValorJurosCorrigido = ipcaAntesGracaValorJurosCorrigido;
    }

    public BigDecimal getIpcaAntesGracaCustasMultaCorrigido() {
        return ipcaAntesGracaCustasMultaCorrigido;
    }

    public void setIpcaAntesGracaCustasMultaCorrigido(BigDecimal ipcaAntesGracaCustasMultaCorrigido) {
        this.ipcaAntesGracaCustasMultaCorrigido = ipcaAntesGracaCustasMultaCorrigido;
    }

    public BigDecimal getIpcaAntesGracaSelicCorrigido() {
        return ipcaAntesGracaSelicCorrigido;
    }

    public void setIpcaAntesGracaSelicCorrigido(BigDecimal ipcaAntesGracaSelicCorrigido) {
        this.ipcaAntesGracaSelicCorrigido = ipcaAntesGracaSelicCorrigido;
    }

    public BigDecimal getIpcaAntesGracaTotalAtualizado() {
        return ipcaAntesGracaTotalAtualizado;
    }

    public void setIpcaAntesGracaTotalAtualizado(BigDecimal ipcaAntesGracaTotalAtualizado) {
        this.ipcaAntesGracaTotalAtualizado = ipcaAntesGracaTotalAtualizado;
    }

    public BigDecimal getIpcaDuranteGracaFator() {
        return ipcaDuranteGracaFator;
    }

    public void setIpcaDuranteGracaFator(BigDecimal ipcaDuranteGracaFator) {
        this.ipcaDuranteGracaFator = ipcaDuranteGracaFator;
    }

    public BigDecimal getIpcaDuranteGracaFatorJuros() {
        return ipcaDuranteGracaFatorJuros;
    }

    public void setIpcaDuranteGracaFatorJuros(BigDecimal ipcaDuranteGracaFatorJuros) {
        this.ipcaDuranteGracaFatorJuros = ipcaDuranteGracaFatorJuros;
    }

    public BigDecimal getIpcaDuranteGracaPrincipalTributavelCorrigido() {
        return ipcaDuranteGracaPrincipalTributavelCorrigido;
    }

    public void setIpcaDuranteGracaPrincipalTributavelCorrigido(BigDecimal ipcaDuranteGracaPrincipalTributavelCorrigido) {
        this.ipcaDuranteGracaPrincipalTributavelCorrigido = ipcaDuranteGracaPrincipalTributavelCorrigido;
    }

    public BigDecimal getIpcaDuranteGracaPrincipalNaoTributavelCorrigido() {
        return ipcaDuranteGracaPrincipalNaoTributavelCorrigido;
    }

    public void setIpcaDuranteGracaPrincipalNaoTributavelCorrigido(BigDecimal ipcaDuranteGracaPrincipalNaoTributavelCorrigido) {
        this.ipcaDuranteGracaPrincipalNaoTributavelCorrigido = ipcaDuranteGracaPrincipalNaoTributavelCorrigido;
    }

    public BigDecimal getIpcaDuranteGracaValorJurosCorrigido() {
        return ipcaDuranteGracaValorJurosCorrigido;
    }

    public void setIpcaDuranteGracaValorJurosCorrigido(BigDecimal ipcaDuranteGracaValorJurosCorrigido) {
        this.ipcaDuranteGracaValorJurosCorrigido = ipcaDuranteGracaValorJurosCorrigido;
    }

    public BigDecimal getIpcaDuranteGracaCustasMultaCorrigido() {
        return ipcaDuranteGracaCustasMultaCorrigido;
    }

    public void setIpcaDuranteGracaCustasMultaCorrigido(BigDecimal ipcaDuranteGracaCustasMultaCorrigido) {
        this.ipcaDuranteGracaCustasMultaCorrigido = ipcaDuranteGracaCustasMultaCorrigido;
    }

    public BigDecimal getIpcaDuranteGracaSelicCorrigido() {
        return ipcaDuranteGracaSelicCorrigido;
    }

    public void setIpcaDuranteGracaSelicCorrigido(BigDecimal ipcaDuranteGracaSelicCorrigido) {
        this.ipcaDuranteGracaSelicCorrigido = ipcaDuranteGracaSelicCorrigido;
    }

    public BigDecimal getIpcaDuranteGracaTotalAtualizado() {
        return ipcaDuranteGracaTotalAtualizado;
    }

    public void setIpcaDuranteGracaTotalAtualizado(BigDecimal ipcaDuranteGracaTotalAtualizado) {
        this.ipcaDuranteGracaTotalAtualizado = ipcaDuranteGracaTotalAtualizado;
    }

    public BigDecimal getIpcaPosGracaFator() {
        return ipcaPosGracaFator;
    }

    public void setIpcaPosGracaFator(BigDecimal ipcaPosGracaFator) {
        this.ipcaPosGracaFator = ipcaPosGracaFator;
    }

    public BigDecimal getIpcaPosGracaFatorJuros() {
        return ipcaPosGracaFatorJuros;
    }

    public void setIpcaPosGracaFatorJuros(BigDecimal ipcaPosGracaFatorJuros) {
        this.ipcaPosGracaFatorJuros = ipcaPosGracaFatorJuros;
    }

    public BigDecimal getIpcaPosGracaPrincipalTributavelCorrigido() {
        return ipcaPosGracaPrincipalTributavelCorrigido;
    }

    public void setIpcaPosGracaPrincipalTributavelCorrigido(BigDecimal ipcaPosGracaPrincipalTributavelCorrigido) {
        this.ipcaPosGracaPrincipalTributavelCorrigido = ipcaPosGracaPrincipalTributavelCorrigido;
    }

    public BigDecimal getIpcaPosGracaPrincipalNaoTributavelCorrigido() {
        return ipcaPosGracaPrincipalNaoTributavelCorrigido;
    }

    public void setIpcaPosGracaPrincipalNaoTributavelCorrigido(BigDecimal ipcaPosGracaPrincipalNaoTributavelCorrigido) {
        this.ipcaPosGracaPrincipalNaoTributavelCorrigido = ipcaPosGracaPrincipalNaoTributavelCorrigido;
    }

    public BigDecimal getIpcaPosGracaValorJurosCorrigido() {
        return ipcaPosGracaValorJurosCorrigido;
    }

    public void setIpcaPosGracaValorJurosCorrigido(BigDecimal ipcaPosGracaValorJurosCorrigido) {
        this.ipcaPosGracaValorJurosCorrigido = ipcaPosGracaValorJurosCorrigido;
    }

    public BigDecimal getIpcaPosGracaCustasMultaCorrigido() {
        return ipcaPosGracaCustasMultaCorrigido;
    }

    public void setIpcaPosGracaCustasMultaCorrigido(BigDecimal ipcaPosGracaCustasMultaCorrigido) {
        this.ipcaPosGracaCustasMultaCorrigido = ipcaPosGracaCustasMultaCorrigido;
    }

    public BigDecimal getIpcaPosGracaSelicCorrigido() {
        return ipcaPosGracaSelicCorrigido;
    }

    public void setIpcaPosGracaSelicCorrigido(BigDecimal ipcaPosGracaSelicCorrigido) {
        this.ipcaPosGracaSelicCorrigido = ipcaPosGracaSelicCorrigido;
    }

    public BigDecimal getIpcaPosGracaTotalAtualizado() {
        return ipcaPosGracaTotalAtualizado;
    }

    public void setIpcaPosGracaTotalAtualizado(BigDecimal ipcaPosGracaTotalAtualizado) {
        this.ipcaPosGracaTotalAtualizado = ipcaPosGracaTotalAtualizado;
    }

    public BigDecimal getSelicAntesGracaTaxa() {
        return selicAntesGracaTaxa;
    }

    public void setSelicAntesGracaTaxa(BigDecimal selicAntesGracaTaxa) {
        this.selicAntesGracaTaxa = selicAntesGracaTaxa;
    }

    public BigDecimal getSelicAntesGracaSelicValorCorrigido() {
        return selicAntesGracaSelicValorCorrigido;
    }

    public void setSelicAntesGracaSelicValorCorrigido(BigDecimal selicAntesGracaSelicValorCorrigido) {
        this.selicAntesGracaSelicValorCorrigido = selicAntesGracaSelicValorCorrigido;
    }

    public BigDecimal getSelicDuranteGracaFatorIPCA() {
        return selicDuranteGracaFatorIPCA;
    }

    public void setSelicDuranteGracaFatorIPCA(BigDecimal selicDuranteGracaFatorIPCA) {
        this.selicDuranteGracaFatorIPCA = selicDuranteGracaFatorIPCA;
    }

    public BigDecimal getSelicDuranteGracaPrincipalTributavelCorrigido() {
        return selicDuranteGracaPrincipalTributavelCorrigido;
    }

    public void setSelicDuranteGracaPrincipalTributavelCorrigido(BigDecimal selicDuranteGracaPrincipalTributavelCorrigido) {
        this.selicDuranteGracaPrincipalTributavelCorrigido = selicDuranteGracaPrincipalTributavelCorrigido;
    }

    public BigDecimal getSelicDuranteGracaPrincipalNaoTributavelCorrigido() {
        return selicDuranteGracaPrincipalNaoTributavelCorrigido;
    }

    public void setSelicDuranteGracaPrincipalNaoTributavelCorrigido(BigDecimal selicDuranteGracaPrincipalNaoTributavelCorrigido) {
        this.selicDuranteGracaPrincipalNaoTributavelCorrigido = selicDuranteGracaPrincipalNaoTributavelCorrigido;
    }

    public BigDecimal getSelicDuranteGracaValorJurosCorrigido() {
        return selicDuranteGracaValorJurosCorrigido;
    }

    public void setSelicDuranteGracaValorJurosCorrigido(BigDecimal selicDuranteGracaValorJurosCorrigido) {
        this.selicDuranteGracaValorJurosCorrigido = selicDuranteGracaValorJurosCorrigido;
    }

    public BigDecimal getSelicDuranteGracaCustasMultaCorrigido() {
        return selicDuranteGracaCustasMultaCorrigido;
    }

    public void setSelicDuranteGracaCustasMultaCorrigido(BigDecimal selicDuranteGracaCustasMultaCorrigido) {
        this.selicDuranteGracaCustasMultaCorrigido = selicDuranteGracaCustasMultaCorrigido;
    }

    public BigDecimal getSelicDuranteGracaSelicCorrigido() {
        return selicDuranteGracaSelicCorrigido;
    }

    public void setSelicDuranteGracaSelicCorrigido(BigDecimal selicDuranteGracaSelicCorrigido) {
        this.selicDuranteGracaSelicCorrigido = selicDuranteGracaSelicCorrigido;
    }

    public BigDecimal getSelicDuranteGracaTotalAtualizado() {
        return selicDuranteGracaTotalAtualizado;
    }

    public void setSelicDuranteGracaTotalAtualizado(BigDecimal selicDuranteGracaTotalAtualizado) {
        this.selicDuranteGracaTotalAtualizado = selicDuranteGracaTotalAtualizado;
    }

    public BigDecimal getSelicPosGracaTaxa() {
        return selicPosGracaTaxa;
    }

    public void setSelicPosGracaTaxa(BigDecimal selicPosGracaTaxa) {
        this.selicPosGracaTaxa = selicPosGracaTaxa;
    }

    public BigDecimal getSelicPosGracaSelicValorCorrigido() {
        return selicPosGracaSelicValorCorrigido;
    }

    public void setSelicPosGracaSelicValorCorrigido(BigDecimal selicPosGracaSelicValorCorrigido) {
        this.selicPosGracaSelicValorCorrigido = selicPosGracaSelicValorCorrigido;
    }

    public BigDecimal getSelicPosGracaTotalAtualizado() {
        return selicPosGracaTotalAtualizado;
    }

    public void setSelicPosGracaTotalAtualizado(BigDecimal selicPosGracaTotalAtualizado) {
        this.selicPosGracaTotalAtualizado = selicPosGracaTotalAtualizado;
    }

    public BigDecimal getValorGlobalAtualizado() {
        return valorGlobalAtualizado;
    }

    public void setValorGlobalAtualizado(BigDecimal valorGlobalAtualizado) {
        this.valorGlobalAtualizado = valorGlobalAtualizado;
    }

    public BigDecimal getPrevValorBase() {
        return prevValorBase;
    }

    public void setPrevValorBase(BigDecimal prevValorBase) {
        this.prevValorBase = prevValorBase;
    }
}
