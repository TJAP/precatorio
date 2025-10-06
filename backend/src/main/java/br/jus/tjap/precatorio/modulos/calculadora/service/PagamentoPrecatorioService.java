package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoRequisitorioDTO;
import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoTributoRequest;
import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoPagamentoDTO;
import br.jus.tjap.precatorio.modulos.calculadora.entity.TabelaIRRF;
import br.jus.tjap.precatorio.modulos.calculadora.repository.TabelaIRRFRepository;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.tabelasbasicas.repository.EnteDevedorRepository;
import jdk.jshell.execution.Util;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PagamentoPrecatorioService {

    private final TabelaIRRFRepository tabelaIRRFRepository;
    private final EnteDevedorRepository enteDevedorRepository;

    private final BigDecimal DESCONTO_SIMPLIFICADO = BigDecimal.valueOf(607.2);

    public PagamentoPrecatorioService(
            TabelaIRRFRepository tabelaIRRFRepository,
            EnteDevedorRepository enteDevedorRepository) {
        this.tabelaIRRFRepository = tabelaIRRFRepository;
        this.enteDevedorRepository = enteDevedorRepository;
    }

    private long calcularMeses(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) return 0L;
        return (fim.getYear() - inicio.getYear()) * 12 + (fim.getMonthValue() - inicio.getMonthValue());
    }

    private BigDecimal calcularDesagio(CalculoTributoRequest req) {
        if (req.getPercentualDesagio() == null) return BigDecimal.ZERO;
        BigDecimal perc = req.getPercentualDesagio().divide(BigDecimal.valueOf(100));
        return req.getValorPrincipalTributavelAtualizado().add(req.getValorPrincipalNaoTributavelAtualizado()).multiply(perc);
    }

    private BigDecimal calcularPrioridade(BigDecimal valorTotal, BigDecimal limitePrioridade) {
        if (valorTotal.compareTo(limitePrioridade) > 0) {
            return limitePrioridade; // só paga até o limite
        }
        return valorTotal; // valor menor que limite
    }

    private BigDecimal calcularPagamentoParcial(BigDecimal valorTotal, BigDecimal valorPagamentoParcial) {
        if (valorPagamentoParcial == null || valorPagamentoParcial.compareTo(BigDecimal.ZERO) <= 0) {
            return valorTotal; // sem parcial -> mantém valor cheio
        }
        return valorPagamentoParcial.min(valorTotal); // paga o que foi informado como parcial
    }

    public static BigDecimal calcularINSS(BigDecimal salario) {
        BigDecimal desconto = BigDecimal.ZERO;

        BigDecimal faixa1 = new BigDecimal("1412.00");
        BigDecimal faixa2 = new BigDecimal("2666.68");
        BigDecimal faixa3 = new BigDecimal("4000.03");
        BigDecimal teto   = new BigDecimal("7786.02");

        BigDecimal[] aliquotas = {
                new BigDecimal("0.075"), // 7,5%
                new BigDecimal("0.09"),  // 9%
                new BigDecimal("0.12"),  // 12%
                new BigDecimal("0.14")   // 14%
        };

        if (salario.compareTo(faixa1) <= 0) {
            desconto = salario.multiply(aliquotas[0]);
        } else {
            // faixa 1
            desconto = faixa1.multiply(aliquotas[0]);

            if (salario.compareTo(faixa2) <= 0) {
                desconto = desconto.add(salario.subtract(faixa1).multiply(aliquotas[1]));
            } else {
                desconto = desconto.add(faixa2.subtract(faixa1).multiply(aliquotas[1]));

                if (salario.compareTo(faixa3) <= 0) {
                    desconto = desconto.add(salario.subtract(faixa2).multiply(aliquotas[2]));
                } else {
                    desconto = desconto.add(faixa3.subtract(faixa2).multiply(aliquotas[2]));

                    if (salario.compareTo(teto) <= 0) {
                        desconto = desconto.add(salario.subtract(faixa3).multiply(aliquotas[3]));
                    } else {
                        desconto = desconto.add(teto.subtract(faixa3).multiply(aliquotas[3]));
                    }
                }
            }
        }

        return desconto.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularIR(BigDecimal base, long qtdMesesRRA) {
        if (base.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

        // busca faixa da tabela
        TabelaIRRF faixa = tabelaIRRFRepository
                .findAll().stream()
                .filter(f -> base.compareTo(f.getValorFaixaInicial()) >= 0 &&
                        base.compareTo(f.getValorFaixaFinal()) <= 0)
                .findFirst()
                .orElse(null);

        if (faixa == null) return BigDecimal.ZERO;

        // aplica IR = base * aliquota - dedução
        BigDecimal imposto = base.multiply(faixa.getValorAliquota())
                .subtract(faixa.getValorDeducao());

        // Se RRA: multiplica pelo número de meses
        if (qtdMesesRRA > 0) {
            imposto = imposto.multiply(BigDecimal.valueOf(qtdMesesRRA));
        }

        return imposto.max(BigDecimal.ZERO);
    }

    private BigDecimal calcularIRPFProgressivoAdvogado(CalculoPagamentoDTO req,BigDecimal baseCalculo) {

        BigDecimal valorRetorno = BigDecimal.ZERO;

        List<TabelaIRRF> tabelaProgressivaAdvogado = new ArrayList<>();
        List<TabelaIRRF> tabela = tabelaIRRFRepository.findAll();
        // montar tabela progreccisa advogado
        BigDecimal valorCalculoProgrecisaAdvogado = BigDecimal.ZERO;

        if(req.getValorPrioridadePrevidenciaAtualizado() == null && req.getValorPrioridadePrevidenciaAtualizado().compareTo(BigDecimal.ZERO) != 0){
            valorCalculoProgrecisaAdvogado = BigDecimal.valueOf(req.getNumeroMesesRRA());
        } else {
            valorCalculoProgrecisaAdvogado = req.getValorPrioridadePrevidenciaAtualizado();
        }

        for (TabelaIRRF faixa : tabela){
            TabelaIRRF faixaAjustada = new TabelaIRRF();
            faixaAjustada.setId(faixa.getId());
            faixaAjustada.setValorAliquota(faixa.getValorAliquota());
            faixaAjustada.setValorDeducao(faixa.getValorDeducao());

            faixaAjustada.setValorFaixaInicial(faixa.getValorFaixaInicial().multiply(valorCalculoProgrecisaAdvogado));
            faixaAjustada.setValorFaixaFinal(faixa.getValorFaixaFinal().multiply(valorCalculoProgrecisaAdvogado));

            tabelaProgressivaAdvogado.add(faixaAjustada);
        }

        for (TabelaIRRF faixa : tabelaProgressivaAdvogado) {
            if (baseCalculo.compareTo(faixa.getValorFaixaInicial()) >= 0 &&
                    baseCalculo.compareTo(faixa.getValorFaixaFinal()) <= 0) {

                return baseCalculo.multiply(faixa.getValorAliquota())
                        .subtract(faixa.getValorDeducao())
                        .setScale(2, RoundingMode.HALF_UP);
            }
        }

        // Caso acima do teto
        TabelaIRRF ultimaFaixa = tabela.getLast();
        valorRetorno = baseCalculo.subtract(DESCONTO_SIMPLIFICADO)
                .multiply(ultimaFaixa.getValorAliquota()).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP)
                .subtract(ultimaFaixa.getValorDeducao());

        return valorRetorno;
    }

    private BigDecimal calcularIRPFProgressivoCredor(CalculoPagamentoDTO req,BigDecimal baseCalculo) {

        List<TabelaIRRF> tabela = tabelaIRRFRepository.findAll();
        List<TabelaIRRF> tabelaProgressivaCredor = new ArrayList<>();
        BigDecimal valorCalculoProgressivo = BigDecimal.ZERO;

        if(!req.isTemPrioridade()){
            valorCalculoProgressivo = BigDecimal.valueOf(req.getNumeroMesesRRA());
        } else {
            valorCalculoProgressivo = req.getValorPrioridadePrevidenciaAtualizado();
        }

        for (TabelaIRRF faixa : tabela){
            TabelaIRRF faixaAjustada = new TabelaIRRF();
            faixaAjustada.setId(faixa.getId());
            faixaAjustada.setValorAliquota(faixa.getValorAliquota());

            faixaAjustada.setValorFaixaInicial(faixa.getValorFaixaInicial().multiply(valorCalculoProgressivo));
            faixaAjustada.setValorFaixaFinal(faixa.getValorFaixaFinal().multiply(valorCalculoProgressivo));
            faixaAjustada.setValorDeducao(faixa.getValorDeducao().multiply(valorCalculoProgressivo));

            tabelaProgressivaCredor.add(faixaAjustada);
        }

        for (TabelaIRRF faixa : tabelaProgressivaCredor) {
            boolean acimaMin = faixa.getValorFaixaInicial() == null || baseCalculo.compareTo(faixa.getValorFaixaInicial()) >= 0;
            boolean abaixoMax = faixa.getValorFaixaFinal() == null || baseCalculo.compareTo(faixa.getValorFaixaFinal()) <= 0;
            if (acimaMin && abaixoMax) {
                BigDecimal valor = baseCalculo.multiply(faixa.getValorAliquota()).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
                BigDecimal menos = valor.subtract(faixa.getValorDeducao());
                return menos;
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calcularIRRF(CalculoPagamentoDTO req) {

        BigDecimal baseCalculo;
        BigDecimal resultado = BigDecimal.ZERO;

        String tipoContribuinte = req.getTipoTributacaoCredor() != null ? req.getTipoTributacaoCredor().trim() : "";

        switch (tipoContribuinte) {

            case "PF" -> {
                baseCalculo = req.getBaseTributavelCredorValor().subtract(
                        UtilCalculo.maior(req.getBaseTributavelCredorPrevidencia(), DESCONTO_SIMPLIFICADO)
                );
                resultado = calcularIRPFProgressivoCredor(req,baseCalculo);
            }

            case "RRA" -> {
                // Rendimento recebido acumuladamente (precatórios)
                baseCalculo = req.getBaseTributavelCredorValor()
                        .subtract(req.getBaseTributavelCredorPrevidencia())
                        .subtract(req.getBaseTributavelHonorarioValor() != null ? req.getBaseTributavelHonorarioValor() : BigDecimal.ZERO);
                resultado = calcularIRPFProgressivoCredor(req,baseCalculo);
            }

            case "PJ-Cessao" -> {
                resultado = req.getBaseTributavelCredorValor().multiply(UtilCalculo.pct(1)).setScale(2, RoundingMode.HALF_UP);
            }

            case "PJ-Servicos" -> {
                resultado = req.getBaseTributavelCredorValor().multiply(UtilCalculo.pct(1.5)).setScale(2, RoundingMode.HALF_UP);
            }

            case "Isento" -> {
                resultado = BigDecimal.ZERO;
            }

            default -> {
                resultado = BigDecimal.ZERO;
            }
        }

        return resultado.max(BigDecimal.ZERO);
    }

    private void calcularPrioridadeEhPagamentoParcial(CalculoPagamentoDTO req){

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

        var enteDevedor = enteDevedorRepository.findByCnpj(req.getNumeroCNPJCredor());
        if(req.isTemPrioridade()){
            valorBasePrioridade = enteDevedor.getLimitePrioridade();
            percentualPrioridade = valorBasePrioridade
                    .multiply(BigDecimal.valueOf(100))
                    .divide(req.getValorBrutoAtualizadoDizima(),12,RoundingMode.HALF_UP);
        }

        if(req.isTemPagamentoParcial()){
            valorBasePagamentoParcial = req.getValorBaseParcialPago();
            percentualPagamentoParcial = valorBasePagamentoParcial
                    .multiply(BigDecimal.valueOf(100))
                    .divide(req.getValorBrutoAtualizadoDizima(),12,RoundingMode.HALF_UP);
        }

        req.setValorBasePrioridade(valorBasePrioridade);
        req.setPercentualPrioridade(percentualPrioridade.setScale(4,RoundingMode.HALF_UP));

        req.setValorBaseParcialPago(valorBasePagamentoParcial);
        req.setPercentualParcialPago(percentualPagamentoParcial.setScale(4,RoundingMode.HALF_UP));

        if(req.isTemPrioridade() && !req.isTemPagamentoParcial()){
            req.setNumeroMesesRRA(0L);
            req.setHouvePrioridadeOuPagamentoParcial(true);
            valorFinalPrincipalTributavelAtualizado =
                    req.getValorPrincipalTributavelAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalPrincipalNaoTributavelAtualizado =
                    req.getValorPrincipalNaoTributavelAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalJurosAtualizado =
                    req.getValorJurosAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalMultaCustasOutrosAtualizado =
                    req.getValorMultaCustasOutrosAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalSelicAtualizado =
                    req.getValorSelicAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalPrevidenciaAtualizado =
                    req.getValorPrevidenciaAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalBrutoAtualizado =
                    req.getValorBrutoAtualizadoDizima().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
        } else if(req.isTemPagamentoParcial()){
            req.setNumeroMesesRRA(0L);
            req.setHouvePrioridadeOuPagamentoParcial(true);
            valorFinalPrincipalTributavelAtualizado =
                    req.getValorPrincipalTributavelAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalPrincipalNaoTributavelAtualizado =
                    req.getValorPrincipalNaoTributavelAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalJurosAtualizado =
                    req.getValorJurosAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalMultaCustasOutrosAtualizado =
                    req.getValorMultaCustasOutrosAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalSelicAtualizado =
                    req.getValorSelicAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalPrevidenciaAtualizado =
                    req.getValorPrevidenciaAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalBrutoAtualizado =
                    req.getValorBrutoAtualizadoDizima().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
        }

        req.setValorPrioridadePrincipalTributavelAtualizado(valorFinalPrincipalTributavelAtualizado);
        req.setValorPrioridadePrincipalNaoTributavelAtualizado(valorFinalPrincipalNaoTributavelAtualizado);
        req.setValorPrioridadeJurosAtualizado(valorFinalJurosAtualizado);
        req.setValorPrioridadeMultaCustasOutrosAtualizado(valorFinalMultaCustasOutrosAtualizado);
        req.setValorPrioridadeSelicAtualizado(valorFinalSelicAtualizado);
        req.setValorPrioridadeBrutoAtualizado(valorFinalBrutoAtualizado);
        req.setValorPrioridadePrevidenciaAtualizado(valorFinalPrevidenciaAtualizado);

    }

    private void calcularRateio(CalculoPagamentoDTO req){

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

        if(req.isTemPrioridade() || req.isTemPagamentoParcial()){
            valorPrincipalTributavelAtualizado = req.getValorPrioridadePrincipalTributavelAtualizado();
            valorPrincipalNaoTributavelAtualizado = req.getValorPrioridadePrincipalNaoTributavelAtualizado();
            valorJurosAtualizado = req.getValorPrioridadeJurosAtualizado();
            valorMultaCustasOutrosAtualizado = req.getValorPrioridadeMultaCustasOutrosAtualizado();
            valorSelicAtualizado = req.getValorPrioridadeSelicAtualizado();
            valorBrutoAtualizado = req.getValorPrioridadeBrutoAtualizado();
        }

        BigDecimal parteHonorario = valorBrutoAtualizado.multiply(req.getPercentualHonorario()
                .divide(BigDecimal.valueOf(100))).add(req.getValorPagoAdvogado());
        BigDecimal parteRestoCredor = valorBrutoAtualizado.subtract(parteHonorario);
        BigDecimal percentualAdvogado = parteHonorario.divide(valorBrutoAtualizado,mc).multiply(BigDecimal.valueOf(100));
        BigDecimal percentualCredor = parteRestoCredor.divide(valorBrutoAtualizado,mc).multiply(BigDecimal.valueOf(100));

        req.setPercentualParteAdvogado(UtilCalculo.escala(percentualAdvogado,2));
        req.setPercentualParteCredor(UtilCalculo.escala(percentualCredor,2));

        valorHonorarioPrincipalTributavelAtualizado = valorPrincipalTributavelAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100),8,RoundingMode.HALF_UP);
        valorHonorarioPrincipalNaoTributavelAtualizado = valorPrincipalNaoTributavelAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100),8,RoundingMode.HALF_UP);
        valorHonorarioJurosAtualizado = valorJurosAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100),8,RoundingMode.HALF_UP);
        valorHonorarioMultaCustasOutrosAtualizado = valorMultaCustasOutrosAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100),8,RoundingMode.HALF_UP);
        valorHonorarioSelicAtualizado = valorSelicAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100),8,RoundingMode.HALF_UP);
        valorHonorarioBrutoAtualizado = valorBrutoAtualizado
                .multiply(percentualAdvogado)
                .divide(BigDecimal.valueOf(100),8,RoundingMode.HALF_UP);



        valorCredorPrincipalTributavelAtualizado = valorPrincipalTributavelAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100),15,RoundingMode.HALF_UP);
        valorCredorPrincipalNaoTributavelAtualizado = valorPrincipalNaoTributavelAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100),15,RoundingMode.HALF_UP);
        valorCredorJurosAtualizado = valorJurosAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100),15,RoundingMode.HALF_UP);
        valorCredorMultaCustasOutrosAtualizado = valorMultaCustasOutrosAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100),15,RoundingMode.HALF_UP);
        valorCredorSelicAtualizado = valorSelicAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100),15,RoundingMode.HALF_UP);
        valorCredorBrutoAtualizado = valorBrutoAtualizado
                .multiply(percentualCredor)
                .divide(BigDecimal.valueOf(100),15,RoundingMode.HALF_UP);

        req.setValorHonorarioPrincipalTributavelAtualizado(UtilCalculo.escala(valorHonorarioPrincipalTributavelAtualizado,2));
        req.setValorHonorarioPrincipalNaoTributavelAtualizado(UtilCalculo.escala(valorHonorarioPrincipalNaoTributavelAtualizado,2));
        req.setValorHonorarioJurosAtualizado(UtilCalculo.escala(valorHonorarioJurosAtualizado,2));
        req.setValorHonorarioMultaCustasOutrosAtualizado(UtilCalculo.escala(valorHonorarioMultaCustasOutrosAtualizado,2));
        req.setValorHonorarioSelicAtualizado(UtilCalculo.escala(valorHonorarioSelicAtualizado,2));
        req.setValorHonorarioBrutoAtualizado(UtilCalculo.escala(valorHonorarioBrutoAtualizado,2));

        req.setValorCredorPrincipalTributavelAtualizado(UtilCalculo.escala(valorCredorPrincipalTributavelAtualizado,2));
        req.setValorCredorPrincipalNaoTributavelAtualizado(UtilCalculo.escala(valorCredorPrincipalNaoTributavelAtualizado,2));
        req.setValorCredorJurosAtualizado(UtilCalculo.escala(valorCredorJurosAtualizado,2));
        req.setValorCredorMultaCustasOutrosAtualizado(UtilCalculo.escala(valorCredorMultaCustasOutrosAtualizado,2));
        req.setValorCredorSelicAtualizado(UtilCalculo.escala(valorCredorSelicAtualizado,2));
        req.setValorCredorBrutoAtualizado(UtilCalculo.escala(valorCredorBrutoAtualizado,2));

    }

    private void calculoAcordoDireto(CalculoPagamentoDTO req){

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

        if(req.isHouveAcordoCredor() && Objects.nonNull(req.getPercentualDesagio())){
            valorDesagioCredorPrincipalTributavelAtualizado =
                    req.getValorCredorPrincipalTributavelAtualizado().subtract(
                            req.getValorCredorPrincipalTributavelAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );

            valorDesagioCredorPrincipalNaoTributavelAtualizado =
                    req.getValorCredorPrincipalNaoTributavelAtualizado().subtract(
                            req.getValorCredorPrincipalNaoTributavelAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );
            valorDesagioCredorJurosAtualizado =
                    req.getValorCredorJurosAtualizado().subtract(
                            req.getValorCredorJurosAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );
            valorDesagioCredorMultaCustasOutrosAtualizado =
                    req.getValorCredorMultaCustasOutrosAtualizado().subtract(
                            req.getValorCredorMultaCustasOutrosAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );
            valorDesagioCredorSelicAtualizado =
                    req.getValorCredorSelicAtualizado().subtract(
                            req.getValorCredorSelicAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );
            valorDesagioCredorBrutoAtualizado =
                    req.getValorCredorBrutoAtualizado().subtract(
                            req.getValorCredorBrutoAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );
            valorDesagioCredorTotalAtualizado =
                    req.getValorCredorBrutoAtualizado().subtract(valorDesagioCredorBrutoAtualizado);

            req.setValorDesagioCredorPrincipalTributavelAtualizado(
                    UtilCalculo.escala(valorDesagioCredorPrincipalTributavelAtualizado,2)
            );
            req.setValorDesagioCredorPrincipalNaoTributavelAtualizado(
                    UtilCalculo.escala(valorDesagioCredorPrincipalNaoTributavelAtualizado,2)
            );
            req.setValorDesagioCredorJurosAtualizado(
                    UtilCalculo.escala(valorDesagioCredorJurosAtualizado,2)
            );
            req.setValorDesagioCredorMultaCustasOutrosAtualizado(
                    UtilCalculo.escala(valorDesagioCredorMultaCustasOutrosAtualizado,2)
            );
            req.setValorDesagioCredorSelicAtualizado(
                    UtilCalculo.escala(valorDesagioCredorSelicAtualizado,2)
            );
            req.setValorDesagioCredorBrutoAtualizado(
                    UtilCalculo.escala(valorDesagioCredorBrutoAtualizado,2)
            );
            req.setValorDesagioCredorAtualizado(
                    UtilCalculo.escala(valorDesagioCredorTotalAtualizado,2)
            );
        }

        if(req.isHouveAcordoAdvogado() && Objects.nonNull(req.getPercentualDesagio())){
            valorDesagioAdvogadoPrincipalTributavelAtualizado =
                    req.getValorHonorarioPrincipalTributavelAtualizado().subtract(
                            req.getValorHonorarioPrincipalTributavelAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );

            valorDesagioAdvogadoPrincipalNaoTributavelAtualizado =
                    req.getValorHonorarioPrincipalNaoTributavelAtualizado().subtract(
                            req.getValorHonorarioPrincipalNaoTributavelAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );
            valorDesagioAdvogadoJurosAtualizado =
                    req.getValorHonorarioJurosAtualizado().subtract(
                            req.getValorHonorarioJurosAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );
            valorDesagioAdvogadoMultaCustasOutrosAtualizado =
                    req.getValorHonorarioMultaCustasOutrosAtualizado().subtract(
                            req.getValorHonorarioMultaCustasOutrosAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );
            valorDesagioAdvogadoSelicAtualizado =
                    req.getValorHonorarioSelicAtualizado().subtract(
                            req.getValorHonorarioSelicAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );
            valorDesagioAdvogadoBrutoAtualizado =
                    req.getValorHonorarioBrutoAtualizado().subtract(
                            req.getValorHonorarioBrutoAtualizado().multiply(
                                    req.getPercentualDesagio()
                            ).divide(BigDecimal.valueOf(100),12, RoundingMode.HALF_UP)
                    );
            valorDesagioAdvogadoTotalAtualizado =
                    req.getValorHonorarioBrutoAtualizado().subtract(valorDesagioAdvogadoBrutoAtualizado);

            req.setValorDesagioHonorarioPrincipalTributavelAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoPrincipalTributavelAtualizado,2)
            );
            req.setValorDesagioHonorarioPrincipalNaoTributavelAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoPrincipalNaoTributavelAtualizado,2)
            );
            req.setValorDesagioHonorarioJurosAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoJurosAtualizado,2)
            );
            req.setValorDesagioHonorarioMultaCustasOutrosAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoMultaCustasOutrosAtualizado,2)
            );
            req.setValorDesagioHonorarioSelicAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoSelicAtualizado,2)
            );
            req.setValorDesagioHonorarioBrutoAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoBrutoAtualizado,2)
            );
            req.setValorDesagioHonorarioAtualizado(
                    UtilCalculo.escala(valorDesagioAdvogadoTotalAtualizado,2)
            );
        }
    }

    private void calculoImposto(CalculoPagamentoDTO req){

        BigDecimal baseTributavelHonorarioValor = req.getValorHonorarioBrutoAtualizado();
        String baseTributavelHonorarioTipo = req.getTributacaoAdvogado();
        BigDecimal baseTributavelHonorarioImposto = BigDecimal.ZERO;

        String baseTributavelCredorTipoCalculo = req.getTipoTributacaoCredor();
        BigDecimal baseTributavelCredorValor = req.getValorCredorPrincipalTributavelAtualizado();
        String baseTributavelCredorTipo = req.getTipoTributacaoCredor();
        BigDecimal baseTributavelCredorImposto = BigDecimal.ZERO;
        BigDecimal baseTributavelCredorPrevidencia = BigDecimal.ZERO;

        // regra para mostrar o valor base de imposto do advogado
        if(!req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()){
            baseTributavelHonorarioValor = BigDecimal.ZERO;
        } else if(req.isHouveAcordoAdvogado()){
            baseTributavelHonorarioValor = req.getValorDesagioHonorarioBrutoAtualizado();
        }
        // TODO - Verificar o valor de 1.5 para incluir em uma tabela de parametro
        if(baseTributavelHonorarioTipo.equals("PF")){
            baseTributavelHonorarioImposto = calcularIRPFProgressivoAdvogado(req, baseTributavelHonorarioValor);
        } else if(baseTributavelHonorarioTipo.equals("PJ")){
            baseTributavelHonorarioImposto =
                    baseTributavelHonorarioValor.multiply(BigDecimal.valueOf(1.5))
                            .divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
        }

        req.setBaseTributavelHonorarioValor(baseTributavelHonorarioValor);
        req.setBaseTributavelHonorarioTipo(baseTributavelHonorarioTipo);
        req.setBaseTributavelHonorarioImposto(UtilCalculo.escala(baseTributavelHonorarioImposto,2));

        if(req.getTipoVinculoCredor().equals("Com vinculo")){
            // IF(AND(L85="Com Vinculo";D69="NÃO";J69="SIM");L73;
            if(!req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()){
                baseTributavelCredorValor = req.getValorDesagioCredorPrincipalTributavelAtualizado();
            // IF(AND(L85="Com Vinculo";D69="NÃO";J69="NÃO");L58;
            } else if(req.isHouveAcordoAdvogado() && !req.isHouveAcordoCredor()){
                baseTributavelCredorValor = BigDecimal.ZERO;
            // IF(AND(L85="Com Vinculo";D69="SIM";J69="NÃO");0;
            } else if(req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()){
                baseTributavelCredorValor = req.getValorDesagioCredorPrincipalTributavelAtualizado();
            }

        } else if(req.getTipoVinculoCredor().equals("Sem vinculo")){
            // IF(AND(L85="Sem Vinculo";D69="NÃO";J69="SIM");L73+L75+L77;
            if(!req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()){
                baseTributavelCredorValor = req.getValorDesagioCredorPrincipalTributavelAtualizado()
                        .add(req.getValorDesagioCredorJurosAtualizado())
                        .add(req.getValorDesagioCredorSelicAtualizado());
            // IF(AND(L85="Sem Vinculo";D69="NÃO";J69="NÃO");L58+L60+L62;
            } else if(!req.isHouveAcordoAdvogado() && !req.isHouveAcordoCredor()){
                baseTributavelCredorValor = req.getValorCredorPrincipalTributavelAtualizado()
                        .add(req.getValorCredorJurosAtualizado())
                        .add(req.getValorCredorSelicAtualizado());
            // IF(AND(L85="Com Vinculo";D69="SIM";J69="NÃO");0;
            } else if(req.isHouveAcordoAdvogado() && !req.isHouveAcordoCredor()){
                baseTributavelCredorValor = BigDecimal.ZERO;
            // IF(AND(L85="Sem Vinculo";D69="SIM";J69="SIM");L73+L75+L77;
            } else if(req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()){
                baseTributavelCredorValor = req.getValorDesagioCredorPrincipalTributavelAtualizado()
                        .add(req.getValorDesagioCredorJurosAtualizado())
                        .add(req.getValorDesagioCredorSelicAtualizado());
            }
        }

        if(req.isHouveAcordoAdvogado() && !req.isHouveAcordoCredor()){
            baseTributavelCredorPrevidencia = BigDecimal.ZERO;
        } else if(req.getValorPrioridadeBrutoAtualizado().compareTo(BigDecimal.ZERO) != 0){
            if(req.isHouveAcordoCredor()){
                baseTributavelCredorPrevidencia = req.getValorPrioridadePrevidenciaAtualizado().multiply(
                        req.getPercentualDesagio()
                ).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            } else {
                baseTributavelCredorPrevidencia = req.getValorPrioridadePrevidenciaAtualizado();
            }
        } else if(req.getValorPrioridadeBrutoAtualizado().compareTo(BigDecimal.ZERO) == 0){
            if(req.isHouveAcordoCredor()){
                baseTributavelCredorPrevidencia = req.getValorPrevidenciaAtualizado().multiply(
                        req.getPercentualDesagio()
                ).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            } else {
                baseTributavelCredorPrevidencia = req.getValorPrevidenciaAtualizado();
            }
        }



        req.setBaseTributavelCredorTipoCalculo(baseTributavelCredorTipoCalculo);
        req.setBaseTributavelCredorValor(UtilCalculo.escala(baseTributavelCredorValor,2));
        req.setBaseTributavelCredorTipo(baseTributavelCredorTipo);
        req.setBaseTributavelCredorPrevidencia(UtilCalculo.escala(baseTributavelCredorPrevidencia,2));

        baseTributavelCredorImposto = calcularIRRF(req);
        req.setBaseTributavelCredorImposto(baseTributavelCredorImposto);

    }

    /*
    p51 = valorPrioridadeBrutoAtualizado
    =IF(AND(D69="SIM";J69="NÃO");0;
IF(AND(P51<>0;J69="SIM");D53*D67;
IF(AND(P51<>0;J69="NÃO");D53;
IF(AND(P51=0;J69="SIM");D42*D67;
IF(AND(P51=0;J69="NÃO");D42;"")))))
     */


    public CalculoPagamentoDTO calcularTributo(CalculoTributoRequest req) {

        var resultado = new CalculoPagamentoDTO();
        MathContext mc = new MathContext(4, RoundingMode.HALF_UP);

        resultado.preencherResultadoCalculo(req);
        resultado.preencherVariaveisDeCalculo(req);

        calcularPrioridadeEhPagamentoParcial(resultado);

        calcularRateio(resultado);

        calculoAcordoDireto(resultado);

        calculoImposto(resultado);


        // 0. Soma o bruto

/*

        BigDecimal parteHonorario = totalBruto.multiply(req.getPercentualHonorario().divide(BigDecimal.valueOf(100))).add(req.getValorPagoAdvogado());

        BigDecimal parteRestoCredor = totalBruto.subtract(parteHonorario);

        BigDecimal percentualAdvogado = parteHonorario.divide(totalBruto,mc).multiply(BigDecimal.valueOf(100));

        BigDecimal percentualCredor = parteRestoCredor.divide(totalBruto,mc).multiply(BigDecimal.valueOf(100));

        resultado.setPercentualParteAdvogado(UtilCalculo.escala(percentualAdvogado,2));
        resultado.setPercentualParteCredor(UtilCalculo.escala(percentualCredor,2));
        resultado.setValorParteAdvogado(UtilCalculo.escala(parteHonorario,2));
        resultado.setValorParteCredor(UtilCalculo.escala(parteRestoCredor,2));

        resultado.setValorParteTributavelCredor(
                UtilCalculo.escala(req.getValorPrincipalTributavelAtualizado().multiply(percentualCredor).divide(BigDecimal.valueOf(100),mc),2)
        );
        resultado.setValorParteNaoTributavelCredor(
                UtilCalculo.escala(req.getValorPrincipalNaoTributavelAtualizado().multiply(percentualCredor).divide(BigDecimal.valueOf(100),mc),2)
        );
        resultado.setValorJurosCredor(
                UtilCalculo.escala(req.getValorJurosAtualizado().multiply(percentualCredor).divide(BigDecimal.valueOf(100),mc),2)
        );
        resultado.setValorSelicCredor(
                UtilCalculo.escala(req.getValorSelicAtualizada().multiply(percentualCredor).divide(BigDecimal.valueOf(100),mc),2)
        );

        // 1. Separar partes do credor e do advogado
        BigDecimal parteAdvogado = req.getValorPrincipalTributavelAtualizado()
                .multiply(req.getPercentualHonorario().divide(BigDecimal.valueOf(100))).add(req.getValorPagoAdvogado());
        BigDecimal parteCredor = req.getValorPrincipalTributavelAtualizado()
                .subtract(parteAdvogado);

*/

/*
        resultado.setTipoCalculo(
                PagamentoUtil.determinarResultado(
                        new PagamentoUtil.DadosCalculoDTO(
                                req.getTipoTributacaoCredor(),
                                req.getTipoVinculoCredor(),
                                req.getNumeroMesesRRA() > 0,
                                req.getValorPrevidenciaAtualizada() != null
                        )
                )
        );

        // 2. Base de IR do credor
        BigDecimal baseIrCredor;
        if (true) {
            baseIrCredor = parteCredor
                    .add(req.getValorJurosAtualizado())
                    .add(req.getValorSelicAtualizada());
        } else {
            baseIrCredor = parteCredor.subtract(req.getValorPrevidenciaAtualizada());
        }

        // 3. Se RRA: dividir por quantidade meses para aplicar faixa
        BigDecimal valorParaFaixa = baseIrCredor;
        if (req.getNumeroMesesRRA() > 0) {
            valorParaFaixa = baseIrCredor.divide(
                    BigDecimal.valueOf(req.getNumeroMesesRRA()),
                    2, RoundingMode.HALF_UP);
        }

        // 4. Aplicar tabela IRRF para credor
        BigDecimal irCredor = calcularIR(valorParaFaixa, req.getNumeroMesesRRA());

        resultado.setBaseTributavelCredor(UtilCalculo.escala(irCredor,2));

        // 5. Previdência do credor (simplesmente repassa)
        BigDecimal previdenciaCredor = req.getValorPrevidenciaAtualizada();

        // 6. Base IR do advogado
        BigDecimal baseIrAdvogado = parteAdvogado; // salvo exceções
        BigDecimal irAdvogado = calcularIR(baseIrAdvogado, 0);
*/
        return resultado;
    }


}

