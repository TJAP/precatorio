package br.jus.tjap.precatorio.modulos.calculadora.controller;

import br.jus.tjap.precatorio.modulos.calculadora.dto.*;
import br.jus.tjap.precatorio.modulos.calculadora.service.CalculoPrecatorioService;
import br.jus.tjap.precatorio.modulos.calculadora.service.PagamentoPrecatorioService;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.util.ApiVersions;
import br.jus.tjap.precatorio.util.Response;
import br.jus.tjap.precatorio.util.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersions.V1 + "/calculos")
@Tag(name = "Calculadora", description = "Operações relacionadas a calculos")
public class CalculoController {

    private final CalculoPrecatorioService calculoJurosService;
    private final PagamentoPrecatorioService pagamentoPrecatorioService;

    public CalculoController(CalculoPrecatorioService calculoJurosService,PagamentoPrecatorioService pagamentoPrecatorioService) {
        this.calculoJurosService = calculoJurosService;
        this.pagamentoPrecatorioService =pagamentoPrecatorioService;
    }

    @PostMapping("/precatorios/calculo")
    @Operation(
            summary = "Calcula atualização monetária",
            description = "Retorna o extrato de calculo",
            operationId = "calcularCorrecaoMonetaria")
    public ResponseEntity<Response<CalculoRequisitorioDTO>> calcularCorrecaoMonetaria(@RequestBody CalculoRequest req) {
        var resultado = new CalculoRequisitorioDTO();
        CalculoAtualizacaoDTO resp = calculoJurosService.calcularAtualizacao(req);
        // para atualização
        resp.preencherIpcaAntesComEscala();
        resp.preencherIpcaDuranteComEscala();
        resp.preencherIpcaDepoisComEscala();
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


}

