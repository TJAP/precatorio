package br.jus.tjap.precatorio.modulos.calculadora.dto;

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
    private Long idPrecatorio;
    private String cnpjDevedor;
    private LocalDate dataUltimaAtualizacao;
    private LocalDate dataFimAtualizacao;
    private int anoVencimento;
    private LocalDate dataInicioRRA;
    private LocalDate dataFimRRA;
    // Data do período de graça é calculada conforme regra - não enviada
    private BigDecimal valorPrincipalTributavel = BigDecimal.ZERO;
    private BigDecimal valorPrincipalNaoTributavel = BigDecimal.ZERO;
    private BigDecimal valorJuros = BigDecimal.ZERO;
    private BigDecimal valorSelic = BigDecimal.ZERO;
    private BigDecimal valorPrevidencia = BigDecimal.ZERO;
    private BigDecimal custas = BigDecimal.ZERO;
    private BigDecimal multa = BigDecimal.ZERO;
    private BigDecimal outrosReembolsos = BigDecimal.ZERO;

    private boolean temPrioridade;
    // pagamento
    private boolean pagamentoParcial;
    private BigDecimal pagamentoValorUltimoAtualizado = BigDecimal.ZERO;
    private BigDecimal pagamentoValorParcial = BigDecimal.ZERO;
    private LocalDate pagamentoDtUltimoAtualizado;

    private BigDecimal percentualHonorario = BigDecimal.ZERO;
    private BigDecimal valorPagoAdvogado = BigDecimal.ZERO;
    private String tributacaoAdvogado;

    private BigDecimal percentualDesagio = BigDecimal.ZERO;
    private boolean acordoAdvogado;
    private boolean acordoCredor;

    private String tipoVinculoCredor;
    private String tipoTributacaoCredor;

    // penhora
    private BigDecimal valorPenhora = BigDecimal.ZERO;
    private String descricaoAlvaraPenhora;

    // cessão
    private boolean temCessao;
    private BigDecimal percentualCessao = BigDecimal.ZERO;
    private String cessaoNome;
    private String cessaoNumeroDocumento;
    private String cessaoPercentual;
    private String cessaoBanco;
    private String cessaoTipoConta;
    private String cessaoAgencia;
    private String cessaoConta;
    private String cessaoDVConta;

    // sucessão
    private boolean temSucessao;
    private String sucessaoNome;
    private String sucessaoNumeroDocumento;
    private String sucessaoBanco;
    private String sucessaoTipoConta;
    private String sucessaoAgencia;
    private String sucessaoConta;
    private String sucessaoDVConta;

}
