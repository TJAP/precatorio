package br.jus.tjap.precatorio.modulos.calculadora.controller;

import br.jus.tjap.precatorio.modulos.calculadora.dto.*;
import br.jus.tjap.precatorio.modulos.calculadora.service.CalculoPrecatorioService;
import br.jus.tjap.precatorio.modulos.calculadora.service.PagamentoPrecatorioService;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.DadosDeducaoDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.service.RequisitorioService;
import br.jus.tjap.precatorio.modulos.requisitorio.service.ProcessoDeducaoService;
import br.jus.tjap.precatorio.relatorio.service.RelatorioService;
import br.jus.tjap.precatorio.relatorio.service.ReportJsService;
import br.jus.tjap.precatorio.util.ApiVersions;
import br.jus.tjap.precatorio.util.Response;
import br.jus.tjap.precatorio.util.ResponseFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(ApiVersions.V1 + "/calculos")
@Tag(name = "Calculadora", description = "Operações relacionadas a calculos")
public class CalculoController {

    private final RequisitorioService requisitorioService;
    private final CalculoPrecatorioService calculoJurosService;
    private final PagamentoPrecatorioService pagamentoPrecatorioService;
    private final ProcessoDeducaoService processoDeducaoService;
    private final RelatorioService relatorioService;
    private final ReportJsService reportJsService;

    public CalculoController(
            CalculoPrecatorioService calculoJurosService,
            PagamentoPrecatorioService pagamentoPrecatorioService,
            RelatorioService relatorioService,
            RequisitorioService requisitorioService,
            ProcessoDeducaoService processoDeducaoService, ReportJsService reportJsService) {
        this.calculoJurosService = calculoJurosService;
        this.pagamentoPrecatorioService = pagamentoPrecatorioService;
        this.relatorioService = relatorioService;
        this.requisitorioService = requisitorioService;
        this.processoDeducaoService = processoDeducaoService;
        this.reportJsService = reportJsService;
    }

    @GetMapping("/precatorios/calculo/{id}")
    @Operation(
            summary = "Calcula atualização monetária e pagamento",
            description = "Retorna o extrato de calculo em PDF",
            operationId = "calcularCorrecaoMonetariaPDF")
    public ResponseEntity<Response<CalculoRequisitorioDTO>> gerarPDFCalculo(@PathVariable Long id) throws JRException, JsonProcessingException {
        ResumoCalculoDocumentoDTO documentoDTO = new ResumoCalculoDocumentoDTO();
        CalculoRequisitorioDTO resultado = montaCalculo(id);
        var relatorio = documentoDTO.montarResumoDocumento(resultado);
        resultado.setDadosDocumentoCalculo(relatorio);
        resultado.setBase64DocumentoCalculo(Base64.getEncoder().encodeToString(reportJsService.getRelatorioResumosCalculo(relatorio)));
        return ResponseFactory.ok(resultado);
    }

    @GetMapping("/precatorios/processo/{numeroProcesso}")
    @Operation(
            summary = "Retorna a requisição do calculo por processo",
            description = "Retorna a requisição do calculo por processo",
            operationId = "gerarRequisicaoCalculoPorProcesso")
    public ResponseEntity<Response<CalculoRequest>> gerarRequisicaoCalculoPorProcesso(@PathVariable String numeroProcesso) throws JsonProcessingException {
        var requisitorio = requisitorioService.buscaPorNumeroProcesso(numeroProcesso);
        var processosDeducoes = processoDeducaoService.listaProcessoDeducaoPorProcessoOrigem(requisitorio.getIdProcesso());
        var acordos = requisitorioService.listarAcordoPorProcesso(requisitorio.getNumProcessoTucujuris());
        requisitorio.setProcessoDeducaos(processosDeducoes);
        requisitorio.setAcordoDiretos(acordos);

        var requisitorioDTO = requisitorio.toMetadado();

        var req  = new CalculoRequest();

        req.setIdPrecatorio(requisitorioDTO.getId());

        req.setPercentualHonorario(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPercentualHonorarioAdvCredor()));
        req.setPercentualDesagio(BigDecimal.ZERO);
        req.setAcordoAdvogado(Boolean.FALSE);
        req.setAcordoCredor(Boolean.FALSE);
        req.setTipoTributacaoCredor(requisitorioDTO.getTipoTributacaoCredor());
        req.setTributacaoAdvogado(requisitorioDTO.getTipoTributacaoAdvogado());
        req.setTipoVinculoCredor(requisitorioDTO.getTipoVinculoCredor());

        // verifica se tem acordo
        if(!acordos.isEmpty()){
            req.setPercentualDesagio(acordos.getFirst().getPercentualDesagio());
            for(var acordo : acordos){
                if(acordo.getTipoParte().equalsIgnoreCase("Credor Principal")){
                    req.setAcordoCredor(Boolean.TRUE);

                    if(Objects.nonNull(acordo.getTipoTributacao())){
                        if(Objects.nonNull(req.getDataInicioRRA())){
                            req.setTipoTributacaoCredor("RRA");
                        } else {
                            req.setTipoTributacaoCredor(
                                    acordo.getTipoTributacao().equalsIgnoreCase("Pessoa Jurídica - Simples") ? "SN" : "PF"
                            );
                        }
                    }

                } else if(acordo.getTipoParte().equalsIgnoreCase("Honorários Contratuais")){
                    if(Objects.nonNull(acordo.getPercentualHonorario())){
                        req.setPercentualHonorario(UtilCalculo.manterValorZeroSeNulo(acordo.getPercentualHonorario()));
                    }

                    if(Objects.nonNull(acordo.getTipoTributacao())){
                        req.setTributacaoAdvogado(
                                acordo.getTipoTributacao().equalsIgnoreCase("Pessoa Jurídica - Simples") ? "SN" : "PF"
                        );
                    }
                    req.setAcordoAdvogado(Boolean.TRUE);
                }
            }
        }

        req.setNumeroProcesso(requisitorioDTO.getIdProcesso());
        req.setDataUltimaAtualizacao(requisitorioDTO.getDtUltimaAtualizacaoPlanilha());
        req.setCnpjDevedor(requisitorioDTO.getDocumentoDevedor());
        req.setDataFimAtualizacao(LocalDate.of(2025,8,30));
        req.setAnoVencimento(requisitorioDTO.getAnoVencimento());
        req.setDataInicioRRA(requisitorioDTO.getDtInicioRRA());
        req.setDataFimRRA(requisitorioDTO.getDtFimRRA());
        req.setValorPrincipalTributavel(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrincipalTributavelCorrigido()));
        req.setValorPrincipalNaoTributavel(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrincipalNaoTributavelCorrigido()));
        req.setValorJuros(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlJurosAplicado()));
        req.setValorSelic(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlSelic()));
        req.setValorPrevidencia(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrevidencia()));
        req.setCustas(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlDevolucaoCusta()));
        req.setMulta(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPagamentoMulta()));
        req.setOutrosReembolsos(BigDecimal.ZERO);
        req.setTemPrioridade(!requisitorioDTO.getPrioridades().isEmpty());
        // TODO: verifica de onde retirar essa informação
        req.setPagamentoParcial(Boolean.FALSE);
        req.setValorPagamentoParcial(BigDecimal.ZERO);

        req.setValorPagoAdvogado(BigDecimal.ZERO);

        req.setPercentualCessao(BigDecimal.ZERO);
        req.setValorPenhora(BigDecimal.ZERO);
        if(!processosDeducoes.isEmpty()){
            ObjectMapper mapper = new ObjectMapper();
            for(var deducao : processosDeducoes){
                var dados = mapper.readValue(deducao.getDadosDeducao(), DadosDeducaoDTO.class);
                if(deducao.getTipoDeducao() == 1){
                    req.setValorPenhora(UtilCalculo.manterValorZeroSeNulo(dados.getValor()));
                } else if(deducao.getTipoDeducao() == 2){
                    req.setPercentualCessao(dados.getPorcentagemCessao());
                }
            }

        }

        return ResponseFactory.ok(req);
    }

    private CalculoRequisitorioDTO montaCalculo(Long idRequisitorio) throws JsonProcessingException {

        var requisitorio = requisitorioService.buscaPorId(idRequisitorio);
        var processosDeducoes = processoDeducaoService.listaProcessoDeducaoPorProcessoOrigem(requisitorio.getIdProcesso());
        var acordos = requisitorioService.listarAcordoPorProcesso(requisitorio.getNumProcessoTucujuris());
        requisitorio.setProcessoDeducaos(processosDeducoes);
        requisitorio.setAcordoDiretos(acordos);

        var requisitorioDTO = requisitorio.toMetadado();

        var resultado = new CalculoRequisitorioDTO();
        var req  = new CalculoRequest();

        req.setPercentualHonorario(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPercentualHonorarioAdvCredor()));
        req.setPercentualDesagio(BigDecimal.ZERO);
        req.setAcordoAdvogado(Boolean.FALSE);
        req.setAcordoCredor(Boolean.FALSE);
        req.setTipoTributacaoCredor(requisitorioDTO.getTipoTributacaoCredor());
        req.setTributacaoAdvogado(requisitorioDTO.getTipoTributacaoAdvogado());
        req.setTipoVinculoCredor(requisitorioDTO.getTipoVinculoCredor());

        // verifica se tem acordo
        if(!acordos.isEmpty()){
            req.setPercentualDesagio(acordos.getFirst().getPercentualDesagio());
            for(var acordo : acordos){
                if(acordo.getTipoParte().equalsIgnoreCase("Credor Principal")){
                    req.setAcordoCredor(Boolean.TRUE);

                    if(Objects.nonNull(acordo.getTipoTributacao())){
                        if(Objects.nonNull(req.getDataInicioRRA())){
                            req.setTipoTributacaoCredor("RRA");
                        } else {
                        req.setTipoTributacaoCredor(
                                acordo.getTipoTributacao().equalsIgnoreCase("Pessoa Jurídica - Simples") ? "SN" : "PF"
                        );
                        }
                    }

                } else if(acordo.getTipoParte().equalsIgnoreCase("Honorários Contratuais")){
                    if(Objects.nonNull(acordo.getPercentualHonorario())){
                        req.setPercentualHonorario(UtilCalculo.manterValorZeroSeNulo(acordo.getPercentualHonorario()));
                    }

                    if(Objects.nonNull(acordo.getTipoTributacao())){
                        req.setTributacaoAdvogado(
                                acordo.getTipoTributacao().equalsIgnoreCase("Pessoa Jurídica - Simples") ? "SN" : "PF"
                        );
                    }
                    req.setAcordoAdvogado(Boolean.TRUE);
                }
            }
        }

        req.setNumeroProcesso(requisitorioDTO.getIdProcesso());
        req.setDataUltimaAtualizacao(requisitorioDTO.getDtUltimaAtualizacaoPlanilha());
        req.setCnpjDevedor(requisitorioDTO.getDocumentoDevedor());
        req.setDataFimAtualizacao(LocalDate.of(2025,8,30));
        req.setAnoVencimento(requisitorioDTO.getAnoVencimento());
        req.setDataInicioRRA(requisitorioDTO.getDtInicioRRA());
        req.setDataFimRRA(requisitorioDTO.getDtFimRRA());
        req.setValorPrincipalTributavel(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrincipalTributavelCorrigido()));
        req.setValorPrincipalNaoTributavel(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrincipalNaoTributavelCorrigido()));
        req.setValorJuros(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlJurosAplicado()));
        req.setValorSelic(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlSelic()));
        req.setValorPrevidencia(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPrevidencia()));
        req.setCustas(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlDevolucaoCusta()));
        req.setMulta(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPagamentoMulta()));
        req.setOutrosReembolsos(BigDecimal.ZERO);
        req.setTemPrioridade(!requisitorioDTO.getPrioridades().isEmpty());
        // TODO: verifica de onde retirar essa informação
        req.setPagamentoParcial(Boolean.FALSE);
        req.setValorPagamentoParcial(BigDecimal.ZERO);

        req.setValorPagoAdvogado(BigDecimal.ZERO);

        req.setPercentualCessao(BigDecimal.ZERO);
        req.setValorPenhora(BigDecimal.ZERO);
        if(!processosDeducoes.isEmpty()){
            ObjectMapper mapper = new ObjectMapper();
            for(var deducao : processosDeducoes){
                var dados = mapper.readValue(deducao.getDadosDeducao(), DadosDeducaoDTO.class);
                if(deducao.getTipoDeducao() == 1){
                    req.setValorPenhora(UtilCalculo.manterValorZeroSeNulo(dados.getValor()));
                } else if(deducao.getTipoDeducao() == 2){
                    req.setPercentualCessao(dados.getPorcentagemCessao());
                }
            }

        }

        resultado.setRequest(req);
        resultado.setRequisitorioDTO(requisitorioDTO);

        CalculoAtualizacaoDTO atualizacao = calculoJurosService.calcularAtualizacao(req);
        atualizacao.preencherIpcaAntesComEscala();
        atualizacao.preencherIpcaDuranteComEscala();
        atualizacao.preencherIpcaDepoisComEscala();

        atualizacao.setDataUltimaAtualizacao(req.getDataUltimaAtualizacao());
        atualizacao.setDataFimAtualizacao(req.getDataFimAtualizacao());
        resultado.setCalculoAtualizacaoDTO(atualizacao);

        var pagRequest = montarCalculoPagamento(resultado);
        CalculoPagamentoDTO pagamento = pagamentoPrecatorioService.calcularTributo(pagRequest);
        resultado.setCalculoPagamentoDTO(pagamento);

        resultado.setCalculoResumoDTO(new CalculoResumoDTO().montarDocumentoCalculo(resultado));

        return resultado;

    }

    private CalculoTributoRequest montarCalculoPagamento(CalculoRequisitorioDTO resultado){
        CalculoRequest req = resultado.getRequest();
        CalculoAtualizacaoDTO resp = resultado.getCalculoAtualizacaoDTO();
        var pagRequest = new CalculoTributoRequest();

        pagRequest.setPercentualDesagio(req.getPercentualDesagio());
        pagRequest.setAcordoAdvogado(req.isAcordoAdvogado());
        pagRequest.setAcordoCredor(req.isAcordoCredor());

        pagRequest.setValorPrincipalTributavelAtualizado(resp.getResultadoValorPrincipalTributavelAtualizadoDizima());
        pagRequest.setValorPrincipalNaoTributavelAtualizado(resp.getResultadoValorPrincipalNaoTributavelAtualizadoDizima());
        pagRequest.setValorJurosAtualizado(resp.getResultadoValorJurosAtualizadoDizima());
        pagRequest.setValorMultaCustaOutrosAtualizado(resp.getResultadoValorMultaCustasOutrosAtualizadoDizima());
        pagRequest.setValorSelicAtualizada(resp.getResultadoValorSelicAtualizadoDizima());
        pagRequest.setValorTotalAtualizada(resp.getResultadoValorBrutoAtualizadoDizima());
        pagRequest.setValorPrevidenciaAtualizada(resp.getResultadoValorPrevidenciaAtualizadoDizima());

        pagRequest.setNumeroMesesRRA(resp.getResultadoNumeroMesesRRA());
        pagRequest.setTemPrioridade(req.isTemPrioridade());
        pagRequest.setPagamentoParcial(req.isPagamentoParcial());
        pagRequest.setValorPagamentoParcial(req.getValorPagamentoParcial());
        pagRequest.setPercentualHonorario(req.getPercentualHonorario());
        pagRequest.setValorPagoAdvogado(req.getValorPagoAdvogado());
        pagRequest.setTributacaoAdvogado(req.getTributacaoAdvogado());
        pagRequest.setPercentualDesagio(req.getPercentualDesagio());
        pagRequest.setAcordoAdvogado(req.isAcordoAdvogado());
        pagRequest.setAcordoCredor(req.isAcordoCredor());
        pagRequest.setTipoVinculoCredor(req.getTipoVinculoCredor());
        pagRequest.setTipoTributacaoCredor(req.getTipoTributacaoCredor());
        pagRequest.setPercentualCessao(req.getPercentualCessao());
        pagRequest.setValorPenhora(req.getValorPenhora());

        pagRequest.setTributacaoAdvogado(req.getTributacaoAdvogado());

        pagRequest.setCnpjDevedor(req.getCnpjDevedor());
        return pagRequest;
    }



}


