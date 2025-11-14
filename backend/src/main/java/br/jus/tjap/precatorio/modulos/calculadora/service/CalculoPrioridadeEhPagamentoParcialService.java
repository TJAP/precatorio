package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoPagamentoDTO;
import br.jus.tjap.precatorio.modulos.calculadora.dto.ResultadoAtualizacaoPrecatorioDTO;
import br.jus.tjap.precatorio.modulos.calculadora.dto.ResultadoCalculoPrecatorioDTO;
import br.jus.tjap.precatorio.modulos.calculadora.dto.ResultadoPagamentoCalculoPrioridadeDTO;
import br.jus.tjap.precatorio.modulos.calculadora.entity.Pagamento;
import br.jus.tjap.precatorio.modulos.calculadora.repository.PagamentoRepository;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.requisitorio.service.RequisitorioService;
import br.jus.tjap.precatorio.modulos.tabelasbasicas.repository.EnteDevedorRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculoPrioridadeEhPagamentoParcialService {

    private final EnteDevedorRepository enteDevedorRepository;
    private final RequisitorioService requisitorioService;
    private final PagamentoRepository pagamentoRepository;

    public CalculoPrioridadeEhPagamentoParcialService(EnteDevedorRepository enteDevedorRepository, RequisitorioService requisitorioService, PagamentoRepository pagamentoRepository) {
        this.enteDevedorRepository = enteDevedorRepository;
        this.requisitorioService = requisitorioService;
        this.pagamentoRepository = pagamentoRepository;
    }

    public ResultadoPagamentoCalculoPrioridadeDTO calcularPrioridadeEhPagamentoParcial(ResultadoCalculoPrecatorioDTO calculo) {

        var prioridade = new ResultadoPagamentoCalculoPrioridadeDTO();

        BigDecimal valorBasePrioridade = BigDecimal.ZERO;
        BigDecimal percentualPrioridade = BigDecimal.ZERO;
        BigDecimal valorBasePagamentoParcial = BigDecimal.ZERO;
        BigDecimal percentualPagamentoParcial = BigDecimal.ZERO;

        BigDecimal valorFinalPrincipalTributavelAtualizado = BigDecimal.ZERO;
        BigDecimal valorFinalPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
        BigDecimal valorFinalJurosAtualizado = BigDecimal.ZERO;
        BigDecimal valorFinalMultaCustasOutrosAtualizado = BigDecimal.ZERO;
        BigDecimal valorFinalSelicAtualizado = BigDecimal.ZERO;
        BigDecimal valorFinalBrutoAtualizado = BigDecimal.ZERO;
        BigDecimal valorFinalPrevidenciaAtualizado = BigDecimal.ZERO;
        BigDecimal numeroPrioridadeRRA = BigDecimal.valueOf(calculo.getAtualizacao().getValorNumeroMesesRRA());

        var prioridades = requisitorioService.listarPrioridadesTucujuris(calculo.getRequisitorio().getIdPrecatorioTucujuris());
        var enteDevedor = enteDevedorRepository.findByCnpj(calculo.getRequisitorio().getDocumentoDevedor());
        var pagamentoAtualizado = requisitorioService.listarPagamentoTucujuris(calculo.getRequisitorio().getIdPrecatorioTucujuris());
        var pagamentoEfetuado = somarPagamentosLancados(calculo.getRequisitorio().getIdPrecatorioTucujuris());

        if(UtilCalculo.isNotNullOrZero(pagamentoEfetuado)){
            prioridade.setTemPagamentoParcial(Boolean.TRUE);
            prioridade.setValorBaseParcialPago(pagamentoEfetuado);
        }

        if (!prioridades.isEmpty()) {
            valorBasePrioridade = enteDevedor.getLimitePrioridade();
            if (valorBasePrioridade.compareTo(calculo.getAtualizacao().getValorBrutoAtualizado()) <= 0) {
                percentualPrioridade = valorBasePrioridade
                        .multiply(BigDecimal.valueOf(100))
                        .divide(calculo.getAtualizacao().getValorBrutoAtualizado(), 12, RoundingMode.HALF_UP);

                numeroPrioridadeRRA = BigDecimal.valueOf(calculo.getAtualizacao().getValorNumeroMesesRRA())
                        .multiply(percentualPrioridade);
            }
        }

        if (!pagamentoAtualizado.isEmpty()) {
            valorBasePagamentoParcial = prioridade.getValorBaseParcialPago();
            percentualPagamentoParcial = valorBasePagamentoParcial
                    .multiply(BigDecimal.valueOf(100))
                    .divide(calculo.getAtualizacao().getValorBrutoAtualizado(), 12, RoundingMode.HALF_UP);
            numeroPrioridadeRRA = BigDecimal.valueOf(calculo.getAtualizacao().getValorNumeroMesesRRA())
                    .multiply(percentualPagamentoParcial);
        }



        prioridade.setNumeroPrioridadeRRA(numeroPrioridadeRRA);
        prioridade.setValorBasePrioridade(valorBasePrioridade);
        prioridade.setPercentualPrioridade(percentualPrioridade.setScale(4, RoundingMode.HALF_UP));
        prioridade.setValorBaseParcialPago(valorBasePagamentoParcial);
        prioridade.setPercentualParcialPago(percentualPagamentoParcial.setScale(4, RoundingMode.HALF_UP));

        if (!prioridades.isEmpty() && pagamentoAtualizado.isEmpty()) {
            prioridade.setNumeroPrioridadeRRA(BigDecimal.ZERO);
            prioridade.setHouvePrioridadeOuPagamentoParcial(true);
            valorFinalPrincipalTributavelAtualizado =
                    calculo.getAtualizacao().getValorPrincipalTributavelAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalPrincipalNaoTributavelAtualizado =
                    calculo.getAtualizacao().getValorPrincipalNaoTributavelAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalJurosAtualizado =
                    calculo.getAtualizacao().getValorJurosAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalMultaCustasOutrosAtualizado =
                    calculo.getAtualizacao().getValorMultaCustasOutrosAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalSelicAtualizado =
                    calculo.getAtualizacao().getValorSelicAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalPrevidenciaAtualizado =
                    calculo.getAtualizacao().getValorPrevidenciaAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalBrutoAtualizado =
                    calculo.getAtualizacao().getValorBrutoAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (!pagamentoAtualizado.isEmpty()) {
            prioridade.setNumeroPrioridadeRRA(BigDecimal.ZERO);
            prioridade.setHouvePrioridadeOuPagamentoParcial(true);
            valorFinalPrincipalTributavelAtualizado =
                    calculo.getAtualizacao().getValorPrincipalTributavelAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalPrincipalNaoTributavelAtualizado =
                    calculo.getAtualizacao().getValorPrincipalNaoTributavelAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalJurosAtualizado =
                    calculo.getAtualizacao().getValorJurosAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalMultaCustasOutrosAtualizado =
                    calculo.getAtualizacao().getValorMultaCustasOutrosAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalSelicAtualizado =
                    calculo.getAtualizacao().getValorSelicAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalPrevidenciaAtualizado =
                    calculo.getAtualizacao().getValorPrevidenciaAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalBrutoAtualizado =
                    calculo.getAtualizacao().getValorBrutoAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        prioridade.setValorPrincipalTributavelAtualizado(valorFinalPrincipalTributavelAtualizado);
        prioridade.setValorPrincipalNaoTributavelAtualizado(valorFinalPrincipalNaoTributavelAtualizado);
        prioridade.setValorJurosAtualizado(valorFinalJurosAtualizado);
        prioridade.setValorMultaCustasOutrosAtualizado(valorFinalMultaCustasOutrosAtualizado);
        prioridade.setValorSelicAtualizado(valorFinalSelicAtualizado);
        prioridade.setValorBrutoAtualizado(valorFinalBrutoAtualizado);
        prioridade.setValorPrevidenciaAtualizado(valorFinalPrevidenciaAtualizado);

        return prioridade;
    }

    public BigDecimal somarPagamentosLancados(Long idPrecatorio) {
        var pagamentos = pagamentoRepository.findPagamentoPorIdPrecatorioTucujuris(idPrecatorio);
        if (pagamentos.isPresent()) {
            var valor = BigDecimal.ZERO;
            for (Pagamento pag : pagamentos.get()) {
                valor = valor.add(pag.getValorPagamento());
            }
            return valor;
        } else {
            return BigDecimal.ZERO;
        }
    }
}
