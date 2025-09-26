package br.jus.tjap.precatorio.modulos.requisitorio.controller;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.TipoRequisitorio;
import br.jus.tjap.precatorio.modulos.requisitorio.service.TipoRequisitorioService;
import br.jus.tjap.precatorio.util.ApiVersions;
import br.jus.tjap.precatorio.util.Response;
import br.jus.tjap.precatorio.util.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(ApiVersions.V1 + "/tipo_requisitorio")
@Tag(name = "TipoRequisitorio", description = "Operações relacionadas aos tipos de Requisitorios")
public class TipoRequisitorioController {
    
}
