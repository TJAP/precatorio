package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoTributoRequest;
import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoPagamentoDTO;
import br.jus.tjap.precatorio.modulos.calculadora.entity.Pagamento;
import br.jus.tjap.precatorio.modulos.calculadora.entity.TabelaIRRF;
import br.jus.tjap.precatorio.modulos.calculadora.repository.PagamentoRepository;
import br.jus.tjap.precatorio.modulos.calculadora.repository.TabelaIRRFRepository;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.requisitorio.repository.RequisitorioRepository;
import br.jus.tjap.precatorio.modulos.tabelasbasicas.repository.EnteDevedorRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PagamentoPrecatorioService {

    private final EnteDevedorRepository enteDevedorRepository;
    private final PagamentoRepository pagamentoRepository;
    private final RequisitorioRepository requisitorioRepository;

    private final CalculoIRRFService irrfService;

    public PagamentoPrecatorioService(
            EnteDevedorRepository enteDevedorRepository,
            PagamentoRepository pagamentoRepository,
            RequisitorioRepository requisitorioRepository, CalculoIRRFService irrfService) {
        this.enteDevedorRepository = enteDevedorRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.requisitorioRepository = requisitorioRepository;
        this.irrfService = irrfService;
    }

    // Metodo principal de calculo
    public CalculoPagamentoDTO calcularTributo(CalculoTributoRequest req) {

        var requisitorio = requisitorioRepository.findById(req.getIdPrecatorio())
                .orElseThrow(() -> new RuntimeException(
                        "Requisitório de ID " + req.getIdPrecatorio() + " não encontrado na tabela precatório.")
                );

        var requisitorioDto = requisitorio.toMetadado();

        var resultado = new CalculoPagamentoDTO();
        MathContext mc = new MathContext(4, RoundingMode.HALF_UP);

        resultado.preencherResultadoCalculo(req);
        resultado.preencherVariaveisDeCalculo(req);

        calcularPrioridadeEhPagamentoParcial(resultado);

        calcularRateio(resultado);

        calculoAcordoDireto(resultado);

        resultado = irrfService.calculoImposto(resultado, requisitorioDto);

        calculoCessaoPenhora(resultado);

        calculoSaldoRemanescente(resultado);

        return resultado;
    }

    private void calcularPrioridadeEhPagamentoParcial(CalculoPagamentoDTO req) {

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
        BigDecimal numeroPrioridadeRRA = BigDecimal.valueOf(req.getNumeroMesesRRA());

        var enteDevedor = enteDevedorRepository.findByCnpj(req.getNumeroCNPJCredor());
        if (req.isTemPrioridade()) {
            valorBasePrioridade = enteDevedor.getLimitePrioridade();
            if (valorBasePrioridade.compareTo(req.getValorBrutoAtualizado()) <= 0) {
                percentualPrioridade = valorBasePrioridade
                        .multiply(BigDecimal.valueOf(100))
                        .divide(req.getValorBrutoAtualizadoDizima(), 12, RoundingMode.HALF_UP);

                numeroPrioridadeRRA = BigDecimal.valueOf(req.getNumeroMesesRRA())
                        .multiply(percentualPrioridade);
            }
        }

        if (req.isTemPagamentoParcial()) {
            valorBasePagamentoParcial = req.getValorBaseParcialPago();
            percentualPagamentoParcial = valorBasePagamentoParcial
                    .multiply(BigDecimal.valueOf(100))
                    .divide(req.getValorBrutoAtualizadoDizima(), 12, RoundingMode.HALF_UP);
            numeroPrioridadeRRA = BigDecimal.valueOf(req.getNumeroMesesRRA())
                    .multiply(percentualPagamentoParcial);
        }

        req.setNumeroPrioridadeRRA(numeroPrioridadeRRA);
        req.setValorBasePrioridade(valorBasePrioridade);
        req.setPercentualPrioridade(percentualPrioridade.setScale(4, RoundingMode.HALF_UP));
        req.setValorBaseParcialPago(valorBasePagamentoParcial);
        req.setPercentualParcialPago(percentualPagamentoParcial.setScale(4, RoundingMode.HALF_UP));

        if (req.isTemPrioridade() && !req.isTemPagamentoParcial()) {
            req.setNumeroPrioridadeRRA(BigDecimal.ZERO);
            req.setHouvePrioridadeOuPagamentoParcial(true);
            valorFinalPrincipalTributavelAtualizado =
                    req.getValorPrincipalTributavelAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalPrincipalNaoTributavelAtualizado =
                    req.getValorPrincipalNaoTributavelAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalJurosAtualizado =
                    req.getValorJurosAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalMultaCustasOutrosAtualizado =
                    req.getValorMultaCustasOutrosAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalSelicAtualizado =
                    req.getValorSelicAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalPrevidenciaAtualizado =
                    req.getValorPrevidenciaAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalBrutoAtualizado =
                    req.getValorBrutoAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (req.isTemPagamentoParcial()) {
            req.setNumeroPrioridadeRRA(BigDecimal.ZERO);
            req.setHouvePrioridadeOuPagamentoParcial(true);
            valorFinalPrincipalTributavelAtualizado =
                    req.getValorPrincipalTributavelAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalPrincipalNaoTributavelAtualizado =
                    req.getValorPrincipalNaoTributavelAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalJurosAtualizado =
                    req.getValorJurosAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalMultaCustasOutrosAtualizado =
                    req.getValorMultaCustasOutrosAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalSelicAtualizado =
                    req.getValorSelicAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalPrevidenciaAtualizado =
                    req.getValorPrevidenciaAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            valorFinalBrutoAtualizado =
                    req.getValorBrutoAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        req.setValorPrioridadePrincipalTributavelAtualizado(valorFinalPrincipalTributavelAtualizado);
        req.setValorPrioridadePrincipalNaoTributavelAtualizado(valorFinalPrincipalNaoTributavelAtualizado);
        req.setValorPrioridadeJurosAtualizado(valorFinalJurosAtualizado);
        req.setValorPrioridadeMultaCustasOutrosAtualizado(valorFinalMultaCustasOutrosAtualizado);
        req.setValorPrioridadeSelicAtualizado(valorFinalSelicAtualizado);
        req.setValorPrioridadeBrutoAtualizado(valorFinalBrutoAtualizado);
        req.setValorPrioridadePrevidenciaAtualizado(valorFinalPrevidenciaAtualizado);

    }

    private void calcularRateio(CalculoPagamentoDTO req) {

        MathContext mc = new MathContext(4, RoundingMode.HALF_UP);

        BigDecimal valorPrincipalTributavelAtualizado = req.getValorPrincipalTributavelAtualizadoDizima();
        BigDecimal valorPrincipalNaoTributavelAtualizado = req.getValorPrincipalNaoTributavelAtualizadoDizima();
        BigDecimal valorJurosAtualizado = req.getValorJurosAtualizadoDizima();
        BigDecimal valorMultaCustasOutrosAtualizado = req.getValorMultaCustasOutrosAtualizadoDizima();
        BigDecimal valorSelicAtualizado = req.getValorSelicAtualizadoDizima();
        BigDecimal valorBrutoAtualizado = req.getValorBrutoAtualizadoDizima();

        BigDecimal valorHonorarioPrincipalTributavelAtualizado = BigDecimal.ZERO;
        BigDecimal valorHonorarioPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
        BigDecimal valorHonorarioJurosAtualizado = BigDecimal.ZERO;
        BigDecimal valorHonorarioMultaCustasOutrosAtualizado = BigDecimal.ZERO;
        BigDecimal valorHonorarioSelicAtualizado = BigDecimal.ZERO;
        BigDecimal valorHonorarioBrutoAtualizado = BigDecimal.ZERO;

        BigDecimal valorCredorPrincipalTributavelAtualizado = BigDecimal.ZERO;
        BigDecimal valorCredorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
        BigDecimal valorCredorJurosAtualizado = BigDecimal.ZERO;
        BigDecimal valorCredorMultaCustasOutrosAtualizado = BigDecimal.ZERO;
        BigDecimal valorCredorSelicAtualizado = BigDecimal.ZERO;
        BigDecimal valorCredorBrutoAtualizado = BigDecimal.ZERO;

        if (req.isTemPrioridade() || req.isTemPagamentoParcial()) {
            if (req.getValorBasePrioridade().compareTo(req.getValorBrutoAtualizado()) <= 0) {

                valorPrincipalTributavelAtualizado = req.getValorPrioridadePrincipalTributavelAtualizado();
                valorPrincipalNaoTributavelAtualizado = req.getValorPrioridadePrincipalNaoTributavelAtualizado();
                valorJurosAtualizado = req.getValorPrioridadeJurosAtualizado();
                valorMultaCustasOutrosAtualizado = req.getValorPrioridadeMultaCustasOutrosAtualizado();
                valorSelicAtualizado = req.getValorPrioridadeSelicAtualizado();
                valorBrutoAtualizado = req.getValorPrioridadeBrutoAtualizado();
            }
        }

        BigDecimal parteHonorario = valorBrutoAtualizado.multiply(req.getPercentualHonorario()
                .divide(BigDecimal.valueOf(100))).add(req.getValorPagoAdvogado());
        BigDecimal parteRestoCredor = valorBrutoAtualizado.subtract(parteHonorario);
        BigDecimal percentualAdvogado = parteHonorario.divide(valorBrutoAtualizado, mc).multiply(BigDecimal.valueOf(100));
        BigDecimal percentualCredor = parteRestoCredor.divide(valorBrutoAtualizado, mc).multiply(BigDecimal.valueOf(100));

        req.setPercentualParteAdvogado(UtilCalculo.escala(percentualAdvogado, 2));
        req.setPercentualParteCredor(UtilCalculo.escala(percentualCredor, 2));

        valorHonorarioPrincipalTributavelAtualizado = valorPrincipalTributavelAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        valorHonorarioPrincipalNaoTributavelAtualizado = valorPrincipalNaoTributavelAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        valorHonorarioJurosAtualizado = valorJurosAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        valorHonorarioMultaCustasOutrosAtualizado = valorMultaCustasOutrosAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        valorHonorarioSelicAtualizado = valorSelicAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        valorHonorarioBrutoAtualizado = valorBrutoAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);


        valorCredorPrincipalTributavelAtualizado = valorPrincipalTributavelAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP);
        valorCredorPrincipalNaoTributavelAtualizado = valorPrincipalNaoTributavelAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP);
        valorCredorJurosAtualizado = valorJurosAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP);
        valorCredorMultaCustasOutrosAtualizado = valorMultaCustasOutrosAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP);
        valorCredorSelicAtualizado = valorSelicAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP);
        valorCredorBrutoAtualizado = valorBrutoAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100), 15, RoundingMode.HALF_UP);

        req.setValorHonorarioPrincipalTributavelAtualizado(UtilCalculo.escala(valorHonorarioPrincipalTributavelAtualizado, 2));
        req.setValorHonorarioPrincipalNaoTributavelAtualizado(UtilCalculo.escala(valorHonorarioPrincipalNaoTributavelAtualizado, 2));
        req.setValorHonorarioJurosAtualizado(UtilCalculo.escala(valorHonorarioJurosAtualizado, 2));
        req.setValorHonorarioMultaCustasOutrosAtualizado(UtilCalculo.escala(valorHonorarioMultaCustasOutrosAtualizado, 2));
        req.setValorHonorarioSelicAtualizado(UtilCalculo.escala(valorHonorarioSelicAtualizado, 2));
        req.setValorHonorarioBrutoAtualizado(UtilCalculo.escala(valorHonorarioBrutoAtualizado, 2));

        req.setValorCredorPrincipalTributavelAtualizado(UtilCalculo.escala(valorCredorPrincipalTributavelAtualizado, 2));
        req.setValorCredorPrincipalNaoTributavelAtualizado(UtilCalculo.escala(valorCredorPrincipalNaoTributavelAtualizado, 2));
        req.setValorCredorJurosAtualizado(UtilCalculo.escala(valorCredorJurosAtualizado, 2));
        req.setValorCredorMultaCustasOutrosAtualizado(UtilCalculo.escala(valorCredorMultaCustasOutrosAtualizado, 2));
        req.setValorCredorSelicAtualizado(UtilCalculo.escala(valorCredorSelicAtualizado, 2));
        req.setValorCredorBrutoAtualizado(UtilCalculo.escala(valorCredorBrutoAtualizado, 2));

    }

    private void calculoAcordoDireto(CalculoPagamentoDTO req) {

        BigDecimal valorDesagioAdvogadoPrincipalTributavelAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioAdvogadoPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioAdvogadoJurosAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioAdvogadoMultaCustasOutrosAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioAdvogadoSelicAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioAdvogadoBrutoAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioAdvogadoTotalAtualizado = BigDecimal.ZERO;


        BigDecimal valorDesagioCredorPrincipalTributavelAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioCredorPrincipalNaoTributavelAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioCredorJurosAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioCredorMultaCustasOutrosAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioCredorSelicAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioCredorBrutoAtualizado = BigDecimal.ZERO;
        BigDecimal valorDesagioCredorTotalAtualizado = BigDecimal.ZERO;

        if (req.isHouveAcordoCredor() && Objects.nonNull(req.getPercentualDesagio())) {
            valorDesagioCredorPrincipalTributavelAtualizado =
                    req.getValorCredorPrincipalTributavelAtualizado().subtract(
                            req.getValorCredorPrincipalTributavelAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );

            valorDesagioCredorPrincipalNaoTributavelAtualizado =
                    req.getValorCredorPrincipalNaoTributavelAtualizado().subtract(
                            req.getValorCredorPrincipalNaoTributavelAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );
            valorDesagioCredorJurosAtualizado =
                    req.getValorCredorJurosAtualizado().subtract(
                            req.getValorCredorJurosAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );
            valorDesagioCredorMultaCustasOutrosAtualizado =
                    req.getValorCredorMultaCustasOutrosAtualizado().subtract(
                            req.getValorCredorMultaCustasOutrosAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );
            valorDesagioCredorSelicAtualizado =
                    req.getValorCredorSelicAtualizado().subtract(
                            req.getValorCredorSelicAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );
            valorDesagioCredorBrutoAtualizado =
                    req.getValorCredorBrutoAtualizado().subtract(
                            req.getValorCredorBrutoAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );
            valorDesagioCredorTotalAtualizado =
                    req.getValorCredorBrutoAtualizado().subtract(valorDesagioCredorBrutoAtualizado);

            req.setValorDesagioCredorPrincipalTributavelAtualizado(
                    UtilCalculo.escala(valorDesagioCredorPrincipalTributavelAtualizado, 2)
            );
            req.setValorDesagioCredorPrincipalNaoTributavelAtualizado(
                    UtilCalculo.escala(valorDesagioCredorPrincipalNaoTributavelAtualizado, 2)
            );
            req.setValorDesagioCredorJurosAtualizado(
                    UtilCalculo.escala(valorDesagioCredorJurosAtualizado, 2)
            );
            req.setValorDesagioCredorMultaCustasOutrosAtualizado(
                    UtilCalculo.escala(valorDesagioCredorMultaCustasOutrosAtualizado, 2)
            );
            req.setValorDesagioCredorSelicAtualizado(
                    UtilCalculo.escala(valorDesagioCredorSelicAtualizado, 2)
            );
            req.setValorDesagioCredorBrutoAtualizado(
                    UtilCalculo.escala(valorDesagioCredorBrutoAtualizado, 2)
            );
            req.setValorDesagioCredorAtualizado(
                    UtilCalculo.escala(valorDesagioCredorTotalAtualizado, 2)
            );
        }

        if (req.isHouveAcordoAdvogado() && Objects.nonNull(req.getPercentualDesagio())) {
            valorDesagioAdvogadoPrincipalTributavelAtualizado =
                    req.getValorHonorarioPrincipalTributavelAtualizado().subtract(
                            req.getValorHonorarioPrincipalTributavelAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );

            valorDesagioAdvogadoPrincipalNaoTributavelAtualizado =
                    req.getValorHonorarioPrincipalNaoTributavelAtualizado().subtract(
                            req.getValorHonorarioPrincipalNaoTributavelAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );
            valorDesagioAdvogadoJurosAtualizado =
                    req.getValorHonorarioJurosAtualizado().subtract(
                            req.getValorHonorarioJurosAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );
            valorDesagioAdvogadoMultaCustasOutrosAtualizado =
                    req.getValorHonorarioMultaCustasOutrosAtualizado().subtract(
                            req.getValorHonorarioMultaCustasOutrosAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );
            valorDesagioAdvogadoSelicAtualizado =
                    req.getValorHonorarioSelicAtualizado().subtract(
                            req.getValorHonorarioSelicAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );
            valorDesagioAdvogadoBrutoAtualizado =
                    req.getValorHonorarioBrutoAtualizado().subtract(
                            req.getValorHonorarioBrutoAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                    );
            valorDesagioAdvogadoTotalAtualizado =
                    req.getValorHonorarioBrutoAtualizado().subtract(valorDesagioAdvogadoBrutoAtualizado);

            req.setValorDesagioHonorarioPrincipalTributavelAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoPrincipalTributavelAtualizado, 2)
            );
            req.setValorDesagioHonorarioPrincipalNaoTributavelAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoPrincipalNaoTributavelAtualizado, 2)
            );
            req.setValorDesagioHonorarioJurosAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoJurosAtualizado, 2)
            );
            req.setValorDesagioHonorarioMultaCustasOutrosAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoMultaCustasOutrosAtualizado, 2)
            );
            req.setValorDesagioHonorarioSelicAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoSelicAtualizado, 2)
            );
            req.setValorDesagioHonorarioBrutoAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoBrutoAtualizado, 2)
            );
            req.setValorDesagioHonorarioAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoTotalAtualizado, 2)
            );
        }
    }

    private void calculoCessaoPenhora(CalculoPagamentoDTO req) {

        BigDecimal baseValorCessao = BigDecimal.ZERO;
        BigDecimal baseValorPenhora = BigDecimal.ZERO;
        BigDecimal baseValorPenhoraMultiplo = BigDecimal.ZERO;

        if (!req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor() && UtilCalculo.isNotNullOrZero(req.getCessaoPercentual())) {
            baseValorCessao = req.getValorDesagioCredorBrutoAtualizado()
                    .subtract(req.getBaseTributavelCredorImposto())
                    .subtract(req.getBaseTributavelCredorPrevidencia());
            baseValorPenhora = req.getCessaoPercentual().multiply(baseValorCessao)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        } else if (!req.isHouveAcordoAdvogado() && !req.isHouveAcordoCredor()) {
            baseValorCessao = req.getValorHonorarioBrutoAtualizado()
                    .add(req.getValorCredorBrutoAtualizado())
                    .subtract(req.getBaseTributavelHonorarioValor())
                    .subtract(req.getBaseTributavelCredorImposto())
                    .subtract(req.getBaseTributavelCredorPrevidencia());
            baseValorPenhora = req.getCessaoPercentual().multiply(baseValorCessao)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        } else if (req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()) {
            baseValorCessao = req.getValorDesagioHonorarioBrutoAtualizado()
                    .add(req.getValorDesagioCredorBrutoAtualizado())
                    .subtract(req.getBaseTributavelHonorarioValor())
                    .subtract(req.getBaseTributavelCredorImposto())
                    .subtract(req.getBaseTributavelCredorPrevidencia());
            baseValorPenhora = req.getCessaoPercentual().multiply(baseValorCessao)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        req.setCessaoBaseValor(UtilCalculo.escala(baseValorCessao, 2));
        req.setPenhoraValor(UtilCalculo.escala(baseValorPenhora, 2));
    }

    private void calculoSaldoRemanescente(CalculoPagamentoDTO req) {

        BigDecimal saldoRemanescentePercentual = BigDecimal.ZERO;
        BigDecimal saldoRemanescentePrincipalTributavel = BigDecimal.ZERO;
        BigDecimal saldoRemanescentePrincipalNaoTributavel = BigDecimal.ZERO;
        BigDecimal saldoRemanescenteJuros = BigDecimal.ZERO;
        BigDecimal saldoRemanescenteMultasCustasOutros = BigDecimal.ZERO;
        BigDecimal saldoRemanescenteSelic = BigDecimal.ZERO;
        BigDecimal saldoRemanescenteTotal = BigDecimal.ZERO;
        BigDecimal saldoRemanescenteTotalRRA = BigDecimal.ZERO;
        BigDecimal saldoRemanescentePrevidencia = BigDecimal.ZERO;


        if (req.isTemPrioridade() &&
                req.getValorPrioridadeBrutoAtualizado().compareTo(req.getValorBrutoAtualizado()) < 0 || req.isTemPagamentoParcial()) {
            saldoRemanescentePrincipalTributavel = req.getValorPrincipalTributavelAtualizado().subtract(
                    req.getValorPrioridadePrincipalTributavelAtualizado()
            );
            saldoRemanescentePrincipalNaoTributavel = req.getValorPrincipalNaoTributavelAtualizado().subtract(
                    req.getValorPrioridadePrincipalNaoTributavelAtualizado()
            );
            saldoRemanescenteJuros = req.getValorJurosAtualizado().subtract(
                    req.getValorPrioridadeJurosAtualizado()
            );
            saldoRemanescenteMultasCustasOutros = req.getValorMultaCustasOutrosAtualizado().subtract(
                    req.getValorPrioridadeMultaCustasOutrosAtualizado()
            );
            saldoRemanescenteSelic = req.getValorSelicAtualizado().subtract(
                    req.getValorPrioridadeSelicAtualizado()
            );
            saldoRemanescenteTotal =
                    saldoRemanescentePrincipalTributavel
                            .add(saldoRemanescentePrincipalNaoTributavel)
                            .add(saldoRemanescenteJuros)
                            .add(saldoRemanescenteMultasCustasOutros)
                            .add(saldoRemanescenteSelic);


            saldoRemanescenteTotalRRA = BigDecimal.valueOf(req.getNumeroMesesRRA()).subtract(
                    req.getNumeroPrioridadeRRA()
            );
            saldoRemanescentePrevidencia = req.getValorPrevidenciaAtualizado().subtract(
                    req.getValorPrioridadePrevidenciaAtualizado()
            );
        } else if (!req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()) {
            saldoRemanescentePrincipalTributavel = req.getValorHonorarioPrincipalTributavelAtualizado();
            saldoRemanescentePrincipalNaoTributavel = req.getValorHonorarioPrincipalNaoTributavelAtualizado();
            saldoRemanescenteJuros = req.getValorHonorarioJurosAtualizado();
            saldoRemanescenteMultasCustasOutros = req.getValorHonorarioMultaCustasOutrosAtualizado();
            saldoRemanescenteSelic = req.getValorHonorarioSelicAtualizado();
            saldoRemanescenteTotal = req.getValorHonorarioBrutoAtualizado();

            saldoRemanescenteTotalRRA = BigDecimal.valueOf(req.getNumeroMesesRRA()).multiply(
                    req.getPercentualParcialPago()
            );
        } else if (req.isHouveAcordoAdvogado() && !req.isHouveAcordoCredor()) {
            saldoRemanescentePrincipalTributavel = req.getValorCredorPrincipalTributavelAtualizado();
            saldoRemanescentePrincipalNaoTributavel = req.getValorCredorPrincipalNaoTributavelAtualizado();
            saldoRemanescenteJuros = req.getValorCredorJurosAtualizado();
            saldoRemanescenteMultasCustasOutros = req.getValorCredorMultaCustasOutrosAtualizado();
            saldoRemanescenteSelic = req.getValorCredorSelicAtualizado();
            saldoRemanescenteTotal = req.getValorCredorBrutoAtualizado();

            saldoRemanescenteTotalRRA = BigDecimal.valueOf(req.getNumeroMesesRRA());
            saldoRemanescentePrevidencia = req.getValorPrevidenciaAtualizado();
        }

        saldoRemanescentePercentual = saldoRemanescenteTotal.divide(req.getValorBrutoAtualizado(), 12, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        req.setSaldoRemanescentePercentual(UtilCalculo.escala(saldoRemanescentePercentual, 4));
        req.setSaldoRemanescentePrincipalTributavel(UtilCalculo.escala(saldoRemanescentePrincipalTributavel, 2));
        req.setSaldoRemanescentePrincipalNaoTributavel(UtilCalculo.escala(saldoRemanescentePrincipalNaoTributavel, 2));
        req.setSaldoRemanescenteJuros(UtilCalculo.escala(saldoRemanescenteJuros, 2));
        req.setSaldoRemanescenteMultasCustasOutros(UtilCalculo.escala(saldoRemanescenteMultasCustasOutros, 2));
        req.setSaldoRemanescenteSelic(UtilCalculo.escala(saldoRemanescenteSelic, 2));
        req.setSaldoRemanescenteTotal(UtilCalculo.escala(saldoRemanescenteTotal, 2));
        req.setSaldoRemanescenteTotalRRA(UtilCalculo.escala(saldoRemanescenteTotalRRA, 2));
        req.setSaldoRemanescentePrevidencia(UtilCalculo.escala(saldoRemanescentePrevidencia, 2));
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

