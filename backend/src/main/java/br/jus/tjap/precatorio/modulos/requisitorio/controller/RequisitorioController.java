package br.jus.tjap.precatorio.modulos.requisitorio.controller;

import br.jus.tjap.precatorio.modulos.requisitorio.entity.Requisitorio;
import br.jus.tjap.precatorio.modulos.requisitorio.service.RequisitorioService;
import br.jus.tjap.precatorio.util.ApiVersions;
import br.jus.tjap.precatorio.util.Response;
import br.jus.tjap.precatorio.util.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(ApiVersions.V1 + "/requisitorio")
@Tag(name = "Requisitorio", description = "Operações relacionadas a Requisitorios")
public class RequisitorioController {
    private final RequisitorioService requisitorioService;

    public RequisitorioController(RequisitorioService requisitorioService) {
        this.requisitorioService = requisitorioService;
    }


}
