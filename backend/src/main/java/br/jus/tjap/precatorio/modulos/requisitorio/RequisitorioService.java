package br.jus.tjap.precatorio.modulos.requisitorio;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.repository.RequisitorioRepository;
import org.springframework.stereotype.Service;

@Service
public class RequisitorioService {

    private final RequisitorioRepository requisitorioRepository;


    public RequisitorioService(RequisitorioRepository requisitorioRepository) {
        this.requisitorioRepository = requisitorioRepository;
    }

    public RequisitorioDTO buscaPorId(Long id){
        var requisitorio = requisitorioRepository.findById(id);

        return requisitorio.orElseThrow().toMetadado();
    }
}
