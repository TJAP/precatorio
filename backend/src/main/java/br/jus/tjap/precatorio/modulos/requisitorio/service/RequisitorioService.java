package br.jus.tjap.precatorio.modulos.requisitorio.service;

import br.jus.tjap.precatorio.modulos.tabelasbasicas.dto.BancoDTO;
import br.jus.tjap.precatorio.modulos.calculadora.entity.PagamentoTucujuris;
import br.jus.tjap.precatorio.modulos.calculadora.entity.PrioridadeTucujuris;
import br.jus.tjap.precatorio.modulos.calculadora.repository.PagamentoTucujurisRepository;
import br.jus.tjap.precatorio.modulos.calculadora.repository.PrioridadeTucujurisRepository;
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
    private final PrioridadeTucujurisRepository prioridadeTucujurisRepository;
    private final PagamentoTucujurisRepository pagamentoTucujurisRepository;


    public RequisitorioService(
            RequisitorioRepository requisitorioRepository,
            AcordoDiretoRepository acordoDiretoRepository,
            PrioridadeTucujurisRepository prioridadeTucujurisRepository,
            PagamentoTucujurisRepository pagamentoTucujurisRepository) {
        this.requisitorioRepository = requisitorioRepository;
        this.acordoDiretoRepository = acordoDiretoRepository;
        this.prioridadeTucujurisRepository = prioridadeTucujurisRepository;
        this.pagamentoTucujurisRepository = pagamentoTucujurisRepository;
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

    public List<PrioridadeTucujuris> listarPrioridadesTucujuris(Long idPrecatorioTucujuris){
        if(idPrecatorioTucujuris == null){
            return new ArrayList<PrioridadeTucujuris>();
        }
        return prioridadeTucujurisRepository.findAllByIdProcessoTucujuris(idPrecatorioTucujuris);
    }

    public List<PagamentoTucujuris> listarPagamentoTucujuris(Long idPrecatorioTucujuris){
        if(idPrecatorioTucujuris == null){
            return new ArrayList<PagamentoTucujuris>();
        }
        return pagamentoTucujurisRepository.findAllByIdPagamentoTucujuris(idPrecatorioTucujuris);
    }

    public BancoDTO recuperaBanco(Long idBanco) {
        Object result = pagamentoTucujurisRepository.getBanco(idBanco);

        if (result != null) {
            Object[] row = (Object[]) result;
            Integer codigo = ((Number) row[0]).intValue();
            String descricao = (String) row[1];
            return new BancoDTO(codigo, descricao);
        }

        return null;
    }

    public Requisitorio salvar(Requisitorio requisitorio){
        return requisitorioRepository.save(requisitorio);
    }

}
