package br.jus.tjap.precatorio.modulos.requisitorio.dto;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.ProcessoDeducao;
import br.jus.tjap.precatorio.modulos.tabelasbasicas.dto.EnteDevedorDTO;
import br.jus.tjap.precatorio.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequisitorioDTO {

    private Long id;
    private String idProcesso;
    private String numProcessoTucujuris;
    private String precato;
    private String nomeMagistrado;
    private Long idTipoTitulo;
    private Long idNaturezaCredito;
    private Long idTipoPrevidencia;
    private Long idTipoOrbigacao;
    private String dsTipoObrigacao;
    private LocalDate dtAjuizamento;
    private LocalDate dtDecursoPrazo;
    private LocalDate dtTransitoJulgadoConhecimento;
    private LocalDate dtTransitoJulgadoEmbargos;
    private String numeroPrecatorio;
    private String nomeDevedor;
    private String documentoDevedor;
    private LocalDate nascimentoDevedor;
    private String nomeDevedorAdv;
    private String documentoDevedorAdv;
    private String nomeCredor;
    private String documentoCredor;
    private LocalDate nascimentoCredor;
    private String nomeCredorAdv;
    private String documentoCredorAdv;
    private LocalDate nascimentoAdvCredor;
    private BigDecimal vlPercentualHonorarioAdvCredor;
    private String idTipoTributacaoAdvCredor;
    private String nomeCredorRepresentante;
    private String docCredorRepresentante;
    private LocalDate nascimentoRepresentanteCredor;
    private Long idCredorNaturezaQualificacao;
    private String orgaoVinculoCredor;
    private String situacaoFuncionalCredor;
    private String nomeBancoCredor;
    private String agenciaCredor;
    private String contaCorrenteCredor;
    private String nomeBancoAdvCredor;
    private String agenciaAdvCredor;
    private String contaCorrenteAdvCredor;
    private BigDecimal valorCausa;
    //private SituacaoPrecatorioEnum situacao;
    private BigDecimal vlGlobalRequisicao;
    private BigDecimal vlPrincipalTributavelCorrigido;
    private BigDecimal vlPrincipalNaoTributavelCorrigido;
    private Integer indiceAtualizacao;
    private BigDecimal idTaxaJurosAplicadas;
    private BigDecimal vlJurosAplicado;
    private Boolean retencaoImposto;
    private BigDecimal vlRetencaoImposto;
    private boolean devolucaoCusta;
    private BigDecimal vlDevolucaoCusta;
    private boolean pagamentoMulta;
    private BigDecimal vlPagamentoMulta;
    private LocalDate dtUltimaAtualizacaoPlanilha;
    private LocalDate dtFimAtualizacaoPlanilha;
    private Integer numeroMesesRendimentoAcumulado;
    private Boolean pagamentoPrevidenciario;
    private BigDecimal vlPrevidencia;
    private Integer orgaoPrevidencia;
    private boolean averbacaoPenhora;
    private BigDecimal vlAverbacaoPenhora;
    private boolean sessaoCredito;
    private BigDecimal vlSessaoCredito;
    private boolean pagamentoAdministrativo;
    private BigDecimal vlPagamentoAdministrativo;
    private Integer tipoPrecatorio;
    private LocalDateTime dtAssinatura;
    private String assinador;
    private Integer idPeAssinador;
    private Integer codOrgaoJulgadorPje;
    private Integer codOrgaoJulgadorTucujuris;
    private BigDecimal vlSelic;
    private BigDecimal vlTotalAtualizado;
    @JsonIgnore
    private byte[] arquivoPdf;
    private Long idPrecatorioTucujuris;
    //private List<ValorPrecatorioAtualizadoDTO> valores = new ArrayList<ValorPrecatorioAtualizadoDTO>();
    //private List<PagamentoDTO> pagamentos = new ArrayList<PagamentoDTO>();
    //private List<PrioridadeDTO> prioridades = new ArrayList<PrioridadeDTO>();
    //private List<ArquivoDTO> arquivos = new ArrayList<ArquivoDTO>();
    private LocalDateTime dtCadastro;
    private LocalDateTime dtAtualizacao;
    private String dsJustificativaInvalidade;
    private boolean ativo;
    private String msgErroDistribuicao;

    private LocalDate dtInicioRRA;
    private LocalDate dtFimRRA;
    private Integer anoVencimento;

    private EnteDevedorDTO enteDevedorDTO;
    private List<ProcessoDeducaoDTO> processoDeducaos = new ArrayList<>();
    private List<PrioridadeDTO> prioridades = new ArrayList<>();
    private List<AcordoDiretoDTO> acordos = new ArrayList<>();

    // para requestCalculo
    private String tipoVinculoCredor;
    private String tipoTributacaoCredor;
    private String tipoTributacaoAdvogado;

    public String getNumeroProcessoPJE(){
        return StringUtil.formataNumeroProcesso(getIdProcesso());
    }
}
