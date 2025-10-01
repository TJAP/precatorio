package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoTributoRequest;
import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoTributoResponse;
import br.jus.tjap.precatorio.modulos.calculadora.entity.TabelaIRRF;
import br.jus.tjap.precatorio.modulos.calculadora.repository.TabelaIRRFRepository;
import br.jus.tjap.precatorio.modulos.calculadora.util.PagamentoUtil;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

@Service
public class PagamentoPrecatorioService {

    private final TabelaIRRFRepository tabelaIRRFRepository;

    public PagamentoPrecatorioService(TabelaIRRFRepository tabelaIRRFRepository) {
        this.tabelaIRRFRepository = tabelaIRRFRepository;
    }

    private BigDecimal calcularIR(BigDecimal base, int qtdMesesRRA) {
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

    public CalculoTributoResponse calcularTributo(CalculoTributoRequest req) {

        var resultado = new CalculoTributoResponse();

        MathContext mc = new MathContext(4, RoundingMode.HALF_UP);

        // 0. Soma o bruto
        BigDecimal totalBruto = req.getValorPrincipalTributavelAtualizado().add(
                req.getValorPrincipalNaoTributavelAtualizado()
        ).add(
                req.getValorJurosAtualizado()
        ).add(
                req.getValorSelicAtualizada()
        ).add(
                req.getValorPrevidenciaAtualizada()
        );

        BigDecimal parteHonorario = totalBruto.multiply(req.getPercentualHonorarios().divide(BigDecimal.valueOf(100))).add(req.getValorFixoHonorarios());

        BigDecimal parteRestoCredor = totalBruto.subtract(parteHonorario);

        BigDecimal percentualAdvogado = parteHonorario.divide(totalBruto,mc).multiply(BigDecimal.valueOf(100));

        BigDecimal percentualCredor = parteRestoCredor.divide(totalBruto,mc).multiply(BigDecimal.valueOf(100));

        resultado.setTotalBruto(UtilCalculo.escala(totalBruto,2));
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
                .multiply(req.getPercentualHonorarios().divide(BigDecimal.valueOf(100))).add(req.getValorFixoHonorarios());
        BigDecimal parteCredor = req.getValorPrincipalTributavelAtualizado()
                .subtract(parteAdvogado);


        resultado.setTipoCalculo(
                PagamentoUtil.determinarResultado(
                        new PagamentoUtil.DadosCalculoDTO(
                                req.getTipoTributacaoCredor(),
                                req.getTipoVinculo(),
                                req.getQuantidadeMesesRRA() > 0,
                                req.getValorPrevidenciaAtualizada() != null
                        )
                )
        );

        // 2. Base de IR do credor
        BigDecimal baseIrCredor;
        if (req.isIncluirJurosSelicNaBase()) {
            baseIrCredor = parteCredor
                    .add(req.getValorJurosAtualizado())
                    .add(req.getValorSelicAtualizada());
        } else {
            baseIrCredor = parteCredor.subtract(req.getValorPrevidenciaAtualizada());
        }

        // 3. Se RRA: dividir por quantidade meses para aplicar faixa
        BigDecimal valorParaFaixa = baseIrCredor;
        if (req.getQuantidadeMesesRRA() > 0) {
            valorParaFaixa = baseIrCredor.divide(
                    BigDecimal.valueOf(req.getQuantidadeMesesRRA()),
                    2, RoundingMode.HALF_UP);
        }

        // 4. Aplicar tabela IRRF para credor
        BigDecimal irCredor = calcularIR(valorParaFaixa, req.getQuantidadeMesesRRA());

        resultado.setBaseTributavelCredor(UtilCalculo.escala(irCredor,2));

        // 5. Previdência do credor (simplesmente repassa)
        BigDecimal previdenciaCredor = req.getValorPrevidenciaAtualizada();

        // 6. Base IR do advogado
        BigDecimal baseIrAdvogado = parteAdvogado; // salvo exceções
        BigDecimal irAdvogado = calcularIR(baseIrAdvogado, 0);

        return resultado;
    }
}
