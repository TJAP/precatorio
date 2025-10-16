package br.jus.tjap.precatorio.modulos.calculadora.controller;

import br.jus.tjap.precatorio.modulos.calculadora.dto.*;
import br.jus.tjap.precatorio.modulos.calculadora.service.CalculoPrecatorioService;
import br.jus.tjap.precatorio.modulos.calculadora.service.PagamentoPrecatorioService;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.DadosDeducaoDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.entity.Requisitorio;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @GetMapping("/precatorios/calculo/pdf/{id}")
    @Operation(
            summary = "Calcula atualização monetária e pagamento",
            description = "Retorna o extrato de calculo em PDF",
            operationId = "calcularCorrecaoMonetariaPDF")
    public ResponseEntity<byte[]> gerarPDFCalculoPOrIdPrecatorio(@PathVariable Long id) {
        ResumoCalculoDocumentoDTO documentoDTO = new ResumoCalculoDocumentoDTO();
        CalculoRequisitorioDTO resultado = montaCalculoAtualizacao(id);
        var relatorio = documentoDTO.montarResumoDocumento(resultado);
        resultado.setDadosDocumentoCalculo(relatorio);
        resultado.setBase64DocumentoCalculo(Base64.getEncoder().encodeToString(reportJsService.getRelatorioResumosCalculo(relatorio)));
        byte[] conteudo = reportJsService.getRelatorioResumosCalculo(relatorio);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=calculo-processo-\"" + resultado.getRequisitorioDTO().getIdProcesso() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(conteudo.length)
                .body(conteudo);
    }

    @GetMapping("/precatorios/calculo/{id}")
    @Operation(
            summary = "Calcula atualização monetária e pagamento",
            description = "Retorna o extrato de calculo em PDF",
            operationId = "calcularCorrecaoMonetariaPDF")
    public ResponseEntity<Response<CalculoRequisitorioDTO>> gerarPDFCalculo(@PathVariable Long id) {
        ResumoCalculoDocumentoDTO documentoDTO = new ResumoCalculoDocumentoDTO();
        CalculoRequisitorioDTO resultado = montaCalculoAtualizacao(id);
        var relatorio = documentoDTO.montarResumoDocumento(resultado);
        resultado.setDadosDocumentoCalculo(relatorio);
        resultado.setBase64DocumentoCalculo(Base64.getEncoder().encodeToString(reportJsService.getRelatorioResumosCalculo(relatorio)));
        return ResponseFactory.ok(resultado);
    }

    @GetMapping("/precatorios/processo/{id}")
    @Operation(
            summary = "Retorna a requisição do calculo por processo",
            description = "Retorna a requisição do calculo por processo",
            operationId = "gerarRequisicaoCalculoPorProcesso")
    public ResponseEntity<Response<CalculoRequest>> gerarRequisicaoCalculoPorProcesso(@PathVariable Long id) {
        var requisitorio = requisitorioService.buscaPorId(id);
        var processosDeducoes = processoDeducaoService.listaProcessoDeducaoPorProcessoOrigem(requisitorio.getIdProcesso());
        var acordos = requisitorioService.listarAcordoPorProcesso(requisitorio.getNumProcessoTucujuris());
        requisitorio.setProcessoDeducaos(processosDeducoes);
        requisitorio.setAcordoDiretos(acordos);
        var requisitorioDTO = requisitorio.toMetadado();
        return ResponseFactory.ok(montaRequest(requisitorioDTO));
    }

    @PostMapping("/precatorios/processo")
    @Operation(
            summary = "Salva a requisição do calculo por processo",
            description = "Retorna a requisição do calculo por processo",
            operationId = "gerarRequisicaoCalculoPorProcesso")
    public ResponseEntity<Response<RequisitorioDTO>> salvarRequisicaoPelaRequest(@RequestBody CalculoRequest req) {

        CalculoRequisitorioDTO calculo = new CalculoRequisitorioDTO();
        ResumoCalculoDocumentoDTO documentoDTO = new ResumoCalculoDocumentoDTO();

        if(req.getIdPrecatorio() == null){
            throw new RuntimeException("Informe o numero do precatorio");
        }

        Requisitorio requisitorio = requisitorioService.buscaPorId(req.getIdPrecatorio());
        requisitorio.setDtUltimaAtualizacaoPlanilha(req.getDataUltimaAtualizacao());
        requisitorio.setDtFimAtualizacaoPlanilha(req.getDataFimAtualizacao());
        requisitorio.setAnoVencimento(req.getAnoVencimento());
        requisitorio.setDtInicioRRA(req.getDataInicioRRA());
        requisitorio.setDtFimRRA(req.getDataFimRRA());
        requisitorio.setVlPrincipalTributavelCorrigido(req.getValorPrincipalTributavel());
        requisitorio.setVlPrincipalNaoTributavelCorrigido(req.getValorPrincipalNaoTributavel());
        requisitorio.setVlJurosAplicado(req.getValorJuros());
        requisitorio.setVlSelic(req.getValorSelic());
        requisitorio.setVlPrevidencia(req.getValorPrevidencia());
        requisitorio.setVlDevolucaoCusta(req.getCustas());
        requisitorio.setVlPagamentoMulta(req.getMulta());

        Requisitorio requi = requisitorioService.salvar(requisitorio);
/*
        calculo = montaCalculoAtualizacao(requi.getId());
        var relatorio = documentoDTO.montarResumoDocumento(calculo);
        calculo.setDadosDocumentoCalculo(relatorio);

        calculo.setBase64DocumentoCalculo(Base64.getEncoder().encodeToString(reportJsService.getRelatorioResumosCalculo(relatorio)));
*/
        return ResponseFactory.ok(requi.toMetadado());
    }

    private CalculoRequest montaRequest(RequisitorioDTO requisitorioDTO){

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
        if(!requisitorioDTO.getAcordos().isEmpty()){
            req.setPercentualDesagio(requisitorioDTO.getAcordos().getFirst().getPercentualDesagio());
            for(var acordo : requisitorioDTO.getAcordos()){
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
        if(!requisitorioDTO.getProcessoDeducaos().isEmpty()){
            ObjectMapper mapper = new ObjectMapper();
            for(var deducao : requisitorioDTO.getProcessoDeducaos()){
                DadosDeducaoDTO dados = null;
                try {
                    dados = mapper.readValue(deducao.getDadosDeducao(), DadosDeducaoDTO.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                if(deducao.getTipoDeducao() == 1){
                    req.setValorPenhora(UtilCalculo.manterValorZeroSeNulo(dados.getValor()));
                } else if(deducao.getTipoDeducao() == 2){
                    if(!UtilCalculo.isNotNullOrZero(dados.getPorcentagemCessao())){
                        req.setPercentualCessao(dados.getPorcentagemCessao());
                    }
                }
            }

        }

        return req;
    }

    private CalculoRequisitorioDTO montaCalculoAtualizacao(Long idRequisitorio) {

        var requisitorio = requisitorioService.buscaPorId(idRequisitorio);
        var processosDeducoes = processoDeducaoService.listaProcessoDeducaoPorProcessoOrigem(requisitorio.getIdProcesso());
        var acordos = requisitorioService.listarAcordoPorProcesso(requisitorio.getNumProcessoTucujuris());
        requisitorio.setProcessoDeducaos(processosDeducoes);
        requisitorio.setAcordoDiretos(acordos);

        var requisitorioDTO = requisitorio.toMetadado();

        var resultado = new CalculoRequisitorioDTO();
        resultado.setIdRequisitorio(idRequisitorio);
        var req  = montaRequest(requisitorioDTO);

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


