package br.jus.tjap.precatorio.modulos.calculadora.controller;

import br.jus.tjap.precatorio.modulos.calculadora.dto.*;
import br.jus.tjap.precatorio.modulos.calculadora.service.CalculoPrecatorioService;
import br.jus.tjap.precatorio.modulos.calculadora.service.PagamentoPrecatorioService;
import br.jus.tjap.precatorio.modulos.requisitorio.service.RequisitorioService;
import br.jus.tjap.precatorio.modulos.requisitorio.service.ProcessoDeducaoService;
import br.jus.tjap.precatorio.relatorio.service.RelatorioService;
import br.jus.tjap.precatorio.util.ApiVersions;
import br.jus.tjap.precatorio.util.Response;
import br.jus.tjap.precatorio.util.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    public CalculoController(
            CalculoPrecatorioService calculoJurosService,
            PagamentoPrecatorioService pagamentoPrecatorioService,
            RelatorioService relatorioService,
            RequisitorioService requisitorioService,
            ProcessoDeducaoService processoDeducaoService) {
        this.calculoJurosService = calculoJurosService;
        this.pagamentoPrecatorioService = pagamentoPrecatorioService;
        this.relatorioService = relatorioService;
        this.requisitorioService = requisitorioService;
        this.processoDeducaoService = processoDeducaoService;
    }

    @PostMapping("/precatorios/calculo")
    @Operation(
            summary = "Calcula atualização monetária",
            description = "Retorna o extrato de calculo",
            operationId = "calcularCorrecaoMonetaria")
    public ResponseEntity<Response<CalculoRequisitorioDTO>> calcularCorrecaoMonetaria(@RequestBody CalculoRequest req) throws JRException {
        var resultado = new CalculoRequisitorioDTO();
        resultado.setRequest(req);
        CalculoAtualizacaoDTO resp = calculoJurosService.calcularAtualizacao(req);
        // para atualização
        resp.preencherIpcaAntesComEscala();
        resp.preencherIpcaDuranteComEscala();
        resp.preencherIpcaDepoisComEscala();

        resp.setDataUltimaAtualizacao(req.getDataUltimaAtualizacao());
        resp.setDataFimAtualizacao(req.getDataFimAtualizacao());
        resultado.setCalculoAtualizacaoDTO(resp);

        // para pagamento
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
        pagRequest.setTipoTributacaoCredor(req.getTipoTributacaoCredor());
        pagRequest.setTipoVinculoCredor(req.getTipoVinculoCredor());

        pagRequest.setCnpjDevedor(req.getCnpjDevedor());

        var pagamento = pagamentoPrecatorioService.calcularTributo(pagRequest);

        resultado.setCalculoPagamentoDTO(pagamento);

        resultado.setCalculoResumoDTO(new CalculoResumoDTO().montarDocumentoCalculo(resultado));

        Map<String, Object> parametros = null;
/*
        byte[] pdf = relatorioService.gerarRelatorioPdf(
                "template",
                resultado.getCalculoResumoDTO(),
                parametros
        );
*/
        return ResponseFactory.ok(resultado);
    }

    @PostMapping("/precatorios/calculo-tributo")
    @Operation(
            summary = "Calcula Previdência e Imposto de Renda (IR) de acordo com as bases previstas",
            description = "Retorna o extrato de calculo",
            operationId = "calcularTributos")
    public ResponseEntity<Response<CalculoPagamentoDTO>> calcularTributos(@RequestBody CalculoTributoRequest req) {
        CalculoPagamentoDTO resp = pagamentoPrecatorioService.calcularTributo(req);
        return ResponseFactory.ok(resp);
    }

    @PostMapping("/precatorios/calculo/{id}")
    @Operation(
            summary = "Calcula atualização monetária e pagamento",
            description = "Retorna o extrato de calculo em PDF",
            operationId = "calcularCorrecaoMonetariaPDF")
    public ResponseEntity<Response<byte[]>> gerarPDFCalculo(@PathVariable Long id) throws JRException {
        CalculoRequisitorioDTO resultado = montaCalculo(id);
        return null;
    }

    private CalculoRequisitorioDTO montaCalculo(Long idRequisitorio){

        var requisitorio = requisitorioService.buscaPorId(idRequisitorio);
        var processosDeducoes = processoDeducaoService.listaProcessoDeducaoPorProcessoOrigem(requisitorio.getIdProcesso());
        var acordos = requisitorioService.listarAcordoPOrProcesso(requisitorio.getNumProcessoTucujuris());
        requisitorio.setProcessoDeducaos(processosDeducoes);

        var requisitorioDTO = requisitorio.toMetadado();

        var resultado = new CalculoRequisitorioDTO();
        var pagRequest = new CalculoTributoRequest();
        var req  = new CalculoRequest();

        req.setPercentualHonorario(requisitorioDTO.getVlPercentualHonorarioAdvCredor());
        req.setPercentualDesagio(BigDecimal.ZERO);
        req.setAcordoAdvogado(Boolean.FALSE);
        req.setAcordoCredor(Boolean.FALSE);
        req.setTipoTributacaoCredor(requisitorioDTO.getTipoTributacaoCredor());
        req.setTributacaoAdvogado(requisitorioDTO.getTipoTributacaoAdvogado());

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
                        req.setPercentualHonorario(acordo.getPercentualHonorario());
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
        req.setDataFimAtualizacao(requisitorioDTO.getDtUltimaAtualizacaoPlanilha());
        req.setCnpjDevedor(requisitorioDTO.getDocumentoDevedor());
        req.setDataFimAtualizacao(LocalDate.of(2025,8,30));
        req.setAnoVencimento(requisitorioDTO.getAnoVencimento());
        req.setDataInicioRRA(requisitorioDTO.getDtInicioRRA());
        req.setDataFimRRA(requisitorioDTO.getDtFimRRA());
        req.setValorPrincipalTributavel(requisitorioDTO.getVlPrincipalTributavelCorrigido());
        req.setValorPrincipalNaoTributavel(requisitorioDTO.getVlPrincipalNaoTributavelCorrigido());
        req.setValorJuros(requisitorioDTO.getVlJurosAplicado());
        req.setValorSelic(requisitorioDTO.getVlSelic());
        req.setValorPrevidencia(requisitorioDTO.getVlPrevidencia());
        req.setCustas(requisitorioDTO.getVlDevolucaoCusta());
        req.setMulta(requisitorioDTO.getVlPagamentoMulta());
        req.setOutrosReembolsos(BigDecimal.ZERO);
        req.setTemPrioridade(!requisitorioDTO.getPrioridades().isEmpty());
        // TODO: verifica de onde retirar essa informação
        req.setPagamentoParcial(Boolean.FALSE);
        req.setValorPagamentoParcial(BigDecimal.ZERO);

        req.setValorPagoAdvogado(BigDecimal.ZERO);
        req.setTipoVinculoCredor(requisitorioDTO.getTipoVinculoCredor());

        req.setPercentualCessao(BigDecimal.ZERO);
        req.setValorPenhora(BigDecimal.ZERO);
        if(!processosDeducoes.isEmpty()){

            for(var deducao : processosDeducoes){
                if(deducao.getTipoDeducao() == 1){
                    BigDecimal valor_penhora = BigDecimal.ZERO;
                    req.setValorPenhora(valor_penhora);
                }
            }

        }




        resultado.setRequest(req);
        resultado.setRequisitorioDTO(requisitorioDTO);
        CalculoAtualizacaoDTO resp = calculoJurosService.calcularAtualizacao(req);

        return resultado;

    }


}


