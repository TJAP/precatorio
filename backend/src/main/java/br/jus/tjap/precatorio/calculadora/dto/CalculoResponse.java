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

}


