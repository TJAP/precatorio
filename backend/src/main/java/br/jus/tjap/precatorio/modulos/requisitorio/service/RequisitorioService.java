package br.jus.tjap.precatorio.modulos.requisitorio.service;

import br.jus.tjap.precatorio.modulos.requisitorio.repository.RequisitorioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import br.jus.tjap.precatorio.modulos.requisitorio.entity.Requisitorio;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.foreign.Linker.Option;
import java.util.Optional;

@Service
public class RequisitorioService{
    private static final Logger logger = LoggerFactory.getLogger(RequisitorioService.class);
    private final RequisitorioRepository requisitorioRepository;

    @Autowired
    public RequisitorioService(RequisitorioRepository requisitorioRepository, Requisitorio requisitorio) {
        this.requisitorioRepository = requisitorioRepository;
    }

    public RequisitorioDTO requisitorioPorId(Long id) {
        return requisitorioRepository.findById(id)
                .map(Requisitorio::toMetadado) // precisa existir esse método na entidade
                .orElseThrow(() -> new RuntimeException("Requisitório não encontrado"));
    }

    
}
