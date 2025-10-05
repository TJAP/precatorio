package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoTributoRequest;
import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoPagamentoDTO;
import br.jus.tjap.precatorio.modulos.calculadora.entity.TabelaIRRF;
import br.jus.tjap.precatorio.modulos.calculadora.repository.TabelaIRRFRepository;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.tabelasbasicas.repository.EnteDevedorRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class PagamentoPrecatorioService {

    private final TabelaIRRFRepository tabelaIRRFRepository;
    private final EnteDevedorRepository enteDevedorRepository;

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
                    .divide(req.getValorBrutoAtualizado(),12,RoundingMode.HALF_UP);
        }

        if(req.isTemPagamentoParcial()){
            valorBasePagamentoParcial = req.getValorBaseParcialPago();
            percentualPagamentoParcial = valorBasePagamentoParcial
                    .multiply(BigDecimal.valueOf(100))
                    .divide(req.getValorBrutoAtualizado(),12,RoundingMode.HALF_UP);
        }

        req.setValorBasePrioridade(valorBasePrioridade);
        req.setPercentualPrioridade(percentualPrioridade.setScale(4,RoundingMode.HALF_UP));

        req.setValorBaseParcialPago(valorBasePagamentoParcial);
        req.setPercentualParcialPago(percentualPagamentoParcial.setScale(4,RoundingMode.HALF_UP));

        if(req.isTemPrioridade() && !req.isTemPagamentoParcial()){
            req.setNumeroMesesRRA(0L);
            req.setHouvePrioridadeOuPagamentoParcial(true);
            valorFinalPrincipalTributavelAtualizado =
                    req.getValorPrincipalTributavelAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalPrincipalNaoTributavelAtualizado =
                    req.getValorPrincipalNaoTributavelAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalJurosAtualizado =
                    req.getValorJurosAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalMultaCustasOutrosAtualizado =
                    req.getValorMultaCustasOutrosAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalSelicAtualizado =
                    req.getValorSelicAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalPrevidenciaAtualizado =
                    req.getValorPrevidenciaAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalBrutoAtualizado =
                    req.getValorBrutoAtualizado().multiply(percentualPrioridade).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
        } else if(req.isTemPagamentoParcial()){
            req.setNumeroMesesRRA(0L);
            req.setHouvePrioridadeOuPagamentoParcial(true);
            valorFinalPrincipalTributavelAtualizado =
                    req.getValorPrincipalTributavelAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalPrincipalNaoTributavelAtualizado =
                    req.getValorPrincipalNaoTributavelAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalJurosAtualizado =
                    req.getValorJurosAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalMultaCustasOutrosAtualizado =
                    req.getValorMultaCustasOutrosAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalSelicAtualizado =
                    req.getValorSelicAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalPrevidenciaAtualizado =
                    req.getValorPrevidenciaAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
            valorFinalBrutoAtualizado =
                    req.getValorBrutoAtualizado().multiply(percentualPagamentoParcial).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
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

        BigDecimal valorPrincipalTributavelAtualizado = req.getValorPrincipalTributavelAtualizado();
        BigDecimal valorPrincipalNaoTributavelAtualizado = req.getValorPrincipalNaoTributavelAtualizado();
        BigDecimal valorJurosAtualizado = req.getValorJurosAtualizado();
        BigDecimal valorMultaCustasOutrosAtualizado = req.getValorMultaCustasOutrosAtualizado();
        BigDecimal valorSelicAtualizado = req.getValorSelicAtualizado();
        BigDecimal valorBrutoAtualizado = req.getValorBrutoAtualizado();

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
    public CalculoPagamentoDTO calcularTributo(CalculoTributoRequest req) {

        var resultado = new CalculoPagamentoDTO();
        MathContext mc = new MathContext(4, RoundingMode.HALF_UP);

        resultado.preencherResultadoCalculo(req);
        resultado.preencherVariaveisDeCalculo(req);

        calcularPrioridadeEhPagamentoParcial(resultado);

        calcularRateio(resultado);






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

    private void calculoA1(CalculoPagamentoDTO tributo){

        if(tributo.getTipoCalculo().getPrevidenciaDestino().equals("INSS")){

        }

    }

}
