package br.jus.tjap.precatorio.calculadora.controller;

import br.jus.tjap.precatorio.calculadora.dto.CalculoRequest;
import br.jus.tjap.precatorio.calculadora.dto.CalculoResponse;
import br.jus.tjap.precatorio.calculadora.dto.CalculoRetornoDTO;
import br.jus.tjap.precatorio.calculadora.service.CalculoPrecatorioService;
import br.jus.tjap.precatorio.modelo.ApiVersions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(ApiVersions.V1 + "/calculos")
@Tag(name = "Calculadora", description = "Operações relacionadas a calculos")
public class CalculoController {

    private final CalculoPrecatorioService service;

    public CalculoController(CalculoPrecatorioService service, PrecatorioService precatorioService) {
        this.service = service;
        this.precatorioService = precatorioService;
    }

    @PostMapping("/precatorios/preview")
    public ResponseEntity<CalculoResponse> preview(@RequestBody CalculoRequest req) {
        CalculoResponse resp = service.calcularAntigo(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/precatorios/calculo")
    @Operation(summary = "Calcula atualização monetária", description = "Retorna o extrato de calculo")
    public ResponseEntity<CalculoRetornoDTO> calcular(@RequestBody CalculoRequest req) {
        CalculoRetornoDTO resp = service.calcularAtualizacao(req);
        resp.preencherIpcaAntesComEscala();
        resp.preencherIpcaDuranteComEscala();
        resp.preencherIpcaDepoisComEscala();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/precatorios/{id}")
    public ResponseEntity<?> buscarPrecatorio(@PathVariable Long id) {
        var precatorioFinded = precatorioService.precatorioPorId(id);

        if (precatorioFinded.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroDTO("Precatorio Não existe",404)); // ou criar DTO de erro com mensagem
        }

        var precatorio = precatorioFinded.get();
        CalculoRequest req = new CalculoRequest();
        req.setAnoVencimento(2021);
        req.setDataUltimaAtualizacao(precatorio.getDtUltimaAtualizacaoPlanilha());
        req.setNumeroProcesso(precatorio.getNumProcessoTucujuris());
        req.setValorJurosNaoTributavel(precatorio.getVlJurosAplicado());
        req.setValorPrincipalNaoTributavel(precatorio.getVlPrincipalNaoTributavelCorrigido());
        req.setValorPrincipalTributavel(precatorio.getVlPrincipalTributavelCorrigido());
        req.setValorJurosTributavel(precatorio.getVlJurosAplicado());
        req.setValorSelicJuros(BigDecimal.ZERO);
        req.setValorSelicJuros(BigDecimal.ZERO);
        req.setTipoSelicTributacao("");
        req.setCustas(BigDecimal.ZERO);
        req.setMulta(BigDecimal.ZERO);
        req.setOutrosReembolsos(BigDecimal.ZERO);
        CalculoRetornoDTO precatorioCalculado = service.calcularNovo(req);

        precatorioCalculado.preencherIpcaAntesComEscala();
        precatorioCalculado.preencherIpcaDuranteComEscala();
        precatorioCalculado.preencherIpcaDepoisComEscala();
        precatorioCalculado.preencherSelicDepoisComEscala();
        

        PrecatorioCalculoDTO resp = new PrecatorioCalculoDTO();
        resp.setIdPrecatorio(precatorio.getId());
        resp.setValorPrincipalTributavel(precatorioCalculado.getIpcaAntesGracaPrincipalTributavelCorrigido());
        resp.setValorPrincipalNaoTributavel(precatorioCalculado.getIpcaAntesGracaPrincipalNaoTributavelCorrigido());
        resp.setValorJurosTributavel(precatorioCalculado.getIpcaAntesGracaValorJurosCorrigido());
        resp.setValorJurosNaoTributavel(precatorioCalculado.getIpcaDuranteGracaValorJurosCorrigido());
        resp.setValorSelicPrincipal(precatorioCalculado.getSelicAntesGracaSelicValorCorrigido());
        resp.setValorSelicJuros(precatorioCalculado.getSelicDuranteGracaSelicCorrigido());
        resp.setCustas(precatorioCalculado.getIpcaAntesGracaCustasMultaCorrigido());
        resp.setMulta(precatorioCalculado.getIpcaAntesGracaCustasMultaCorrigido());
        resp.setOutrosReembolsos(BigDecimal.ZERO);
        resp.setDataAntesGracaDataInicio(precatorioCalculado.getDataAntesGracaDataInicio());
        resp.setDataAntesGracaDataFim(precatorioCalculado.getDataAntesGracaDataFim());
        resp.setDataDuranteGracaDataInicio(precatorioCalculado.getDataDuranteGracaDataInicio());
        resp.setDataDuranteGracaDataFim(precatorioCalculado.getDataDuranteGracaDataFim());
        resp.setDataPosGracaDataInicio(precatorioCalculado.getDataPosGracaDataInicio());
        resp.setDataPosGracaDataFim(precatorioCalculado.getDataPosGracaDataFim());

        return ResponseEntity.ok(resp);
    }


}

