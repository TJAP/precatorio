package br.jus.tjap.precatorio.modulos.calculadora.dto;

import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculoAtualizacaoDTO {

    private static final BigDecimal ZERO = new BigDecimal("0.00");

    private Long idPrecatorio;
    private String numeroProcesso;
    private String tipoCalculoRetornado;
    private RequisitorioDTO requisitorioDTO;

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

    private BigDecimal ipcaValorPrevidenciaCorrigido = ZERO;

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

    private BigDecimal selicValorPrevidenciaCorrigido = ZERO;

    private BigDecimal resultadoValorPrincipalTributavelAtualizado = ZERO;
    private BigDecimal resultadoValorPrincipalNaoTributavelAtualizado = ZERO;
    private BigDecimal resultadoValorJurosAtualizado = ZERO;
    private BigDecimal resultadoValorMultaCustasOutrosAtualizado = ZERO;
    private BigDecimal resultadoValorSelicAtualizado = ZERO;
    private BigDecimal resultadoValorBrutoAtualizado = ZERO;
    private BigDecimal resultadoValorPrevidenciaAtualizado = ZERO;
    private int resultadoNumeroMesesRRA = 0;
    private String resultadoCnpjDevedor;

    // Calculo IRRF
    //private BigDecimal irrfValorCredor = ZERO;
    //private BigDecimal irrfValorPrevidenciaCredor = ZERO;
    //private BigDecimal irrfValorHCLiquido = ZERO;
    //private BigDecimal irrfValorSemHC = ZERO;
    //private BigDecimal irrfValorPenhora = ZERO;
    //private BigDecimal irrfValorCessao = ZERO;

    //private BigDecimal valorLiquidoCredor = ZERO;

    private BigDecimal arredondar(BigDecimal valor) {
        if (valor == null) {
            return UtilCalculo.escala(BigDecimal.ZERO, 2);
        }
        return UtilCalculo.escala(valor, 2);
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
}
