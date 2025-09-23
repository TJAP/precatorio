package br.jus.tjap.precatorio.calculadora.service;

import br.jus.tjap.precatorio.calculadora.dto.CalculoTributoRequest;
import br.jus.tjap.precatorio.calculadora.dto.CalculoTributoResponse;
import br.jus.tjap.precatorio.calculadora.entity.TabelaIRRF;
import br.jus.tjap.precatorio.calculadora.repository.TabelaIRRFRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

        // 1. Separar partes do credor e do advogado
        BigDecimal parteAdvogado = req.valorPrincipalTributavelAtualizado()
                .multiply(req.percentualHonorarios());
        BigDecimal parteCredor = req.valorPrincipalTributavelAtualizado()
                .subtract(parteAdvogado);

        // 2. Base de IR do credor
        BigDecimal baseIrCredor;
        if (req.incluirJurosSelicNaBase()) {
            baseIrCredor = parteCredor
                    .add(req.valorJurosAtualizado())
                    .add(req.valorSelicAtualizada());
        } else {
            baseIrCredor = parteCredor.subtract(req.valorPrevidenciaAtualizada());
        }

        // 3. Se RRA: dividir por quantidade meses para aplicar faixa
        BigDecimal valorParaFaixa = baseIrCredor;
        if (req.quantidadeMesesRRA() > 0) {
            valorParaFaixa = baseIrCredor.divide(
                    BigDecimal.valueOf(req.quantidadeMesesRRA()),
                    2, RoundingMode.HALF_UP);
        }

        // 4. Aplicar tabela IRRF para credor
        BigDecimal irCredor = calcularIR(valorParaFaixa, req.quantidadeMesesRRA());

        // 5. Previdência do credor (simplesmente repassa)
        BigDecimal previdenciaCredor = req.valorPrevidenciaAtualizada();

        // 6. Base IR do advogado
        BigDecimal baseIrAdvogado = parteAdvogado; // salvo exceções
        BigDecimal irAdvogado = calcularIR(baseIrAdvogado, 0);

        return new CalculoTributoResponse(baseIrCredor, irCredor, previdenciaCredor, baseIrAdvogado, irAdvogado);
    }
}
