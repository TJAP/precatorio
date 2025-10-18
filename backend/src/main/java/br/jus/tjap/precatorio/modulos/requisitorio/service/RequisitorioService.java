package br.jus.tjap.precatorio.modulos.requisitorio.service;

import br.jus.tjap.precatorio.modulos.requisitorio.dto.AcordoDiretoDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.entity.AcordoDireto;
import br.jus.tjap.precatorio.modulos.requisitorio.entity.Requisitorio;
import br.jus.tjap.precatorio.modulos.requisitorio.repository.AcordoDiretoRepository;
import br.jus.tjap.precatorio.modulos.requisitorio.repository.RequisitorioRepository;
import br.jus.tjap.precatorio.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequisitorioService {

    private final RequisitorioRepository requisitorioRepository;
    private final AcordoDiretoRepository acordoDiretoRepository;


    public RequisitorioService(RequisitorioRepository requisitorioRepository, AcordoDiretoRepository acordoDiretoRepository) {
        this.requisitorioRepository = requisitorioRepository;
        this.acordoDiretoRepository = acordoDiretoRepository;
    }

    public Requisitorio buscaPorId(Long id){
        var requisitorio = requisitorioRepository.findById(id);
        return requisitorio.orElseThrow();
    }

    public Requisitorio buscaPorNumeroProcesso(String numeroProcesso){
        numeroProcesso = StringUtil.removerFormatacaoNumeroDocumento(numeroProcesso);
        var requisitorio = requisitorioRepository.buscarPorNumeroProcesso(numeroProcesso);
        return requisitorio.orElseThrow();
    }

    public List<AcordoDireto> listarAcordoPorProcesso(Long idPrecatorioTucujuris){
        if(idPrecatorioTucujuris == null){
            return new ArrayList<AcordoDireto>();
        }
        return acordoDiretoRepository.findAllByNumeroProcesso(idPrecatorioTucujuris);
    }

    public Requisitorio salvar(Requisitorio requisitorio){
        return requisitorioRepository.save(requisitorio);
    }

}
