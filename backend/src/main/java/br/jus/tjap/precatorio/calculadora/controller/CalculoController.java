package br.jus.tjap.precatorio.calculadora.controller;

import br.jus.tjap.precatorio.calculadora.dto.*;
import br.jus.tjap.precatorio.calculadora.service.CalculoPrecatorioService;
import br.jus.tjap.precatorio.calculadora.service.PagamentoPrecatorioService;
import br.jus.tjap.precatorio.modelo.ApiVersions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
    public ResponseEntity<CalculoRetornoDTO> calcularCorrecaoMonetaria(@RequestBody CalculoRequest req) {
        CalculoRetornoDTO resp = calculoJurosService.calcularAtualizacao(req);
        resp.preencherIpcaAntesComEscala();
        resp.preencherIpcaDuranteComEscala();
        resp.preencherIpcaDepoisComEscala();
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/precatorios/calculo-tributo")
    @Operation(
            summary = "Calcula Previdência e Imposto de Renda (IR) de acordo com as bases previstas",
            description = "Retorna o extrato de calculo",
            operationId = "calcularTributos")
    public ResponseEntity<CalculoTributoResponse> calcularTributos(@RequestBody CalculoTributoRequest req) {
        CalculoTributoResponse resp = pagamentoPrecatorioService.calcularTributo(req);
        return ResponseEntity.ok(resp);
    }


}

