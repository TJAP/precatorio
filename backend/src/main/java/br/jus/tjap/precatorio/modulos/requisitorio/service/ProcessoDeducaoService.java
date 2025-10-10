package br.jus.tjap.precatorio.modulos.requisitorio.service;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.ProcessoDeducaoDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.entity.ProcessoDeducao;
import br.jus.tjap.precatorio.modulos.requisitorio.repository.ProcessoDeducaoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessoDeducaoService {

    private final ProcessoDeducaoRepository processoDeducaoRepository;

    public ProcessoDeducaoService(ProcessoDeducaoRepository processoDeducaoRepository) {
        this.processoDeducaoRepository = processoDeducaoRepository;
    }

    public List<ProcessoDeducao> listaProcessoDeducaoPorProcessoOrigem(String numeroProcessoOrigem){
        var lista = processoDeducaoRepository.findAllByProcessoOrigem(numeroProcessoOrigem);
        if(lista.isEmpty()){
            return new ArrayList<ProcessoDeducao>();
        }
        return lista;
    }

}
