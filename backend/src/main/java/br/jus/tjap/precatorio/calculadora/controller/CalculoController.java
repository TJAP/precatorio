package br.jus.tjap.precatorio.calculadora.controller;

import br.jus.tjap.precatorio.calculadora.dto.CalculoRequest;
import br.jus.tjap.precatorio.calculadora.dto.CalculoResponse;
import br.jus.tjap.precatorio.calculadora.dto.CalculoRetornoDTO;
import br.jus.tjap.precatorio.calculadora.service.CalculoPrecatorioService;
import br.jus.tjap.precatorio.modelo.ApiVersions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(ApiVersions.V1 + "/calculos")
public class CalculoController {

    private final CalculoPrecatorioService service;

    public CalculoController(CalculoPrecatorioService service) {
        this.service = service;
    }

    @PostMapping("/precatorios/preview")
    public ResponseEntity<CalculoResponse> preview(@RequestBody CalculoRequest req) {
        CalculoResponse resp = service.calcularAntigo(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/precatorios/calculo")
    public ResponseEntity<CalculoRetornoDTO> calcular(@RequestBody CalculoRequest req) {
        CalculoRetornoDTO resp = service.calcularNovo(req);
        resp.preencherIpcaAntesComEscala();
        resp.preencherIpcaDuranteComEscala();
        resp.preencherIpcaDepoisComEscala();
        return ResponseEntity.ok(resp);
    }


}

