package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoPagamentoDTO;
import br.jus.tjap.precatorio.modulos.calculadora.entity.TabelaIRRF;
import br.jus.tjap.precatorio.modulos.calculadora.repository.TabelaIRRFRepository;
import br.jus.tjap.precatorio.modulos.calculadora.util.CalculoUtil;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CalculoIRRFService {

    private final TabelaIRRFRepository tabelaIRRFRepository;

    public CalculoIRRFService(TabelaIRRFRepository tabelaIRRFRepository) {
        this.tabelaIRRFRepository = tabelaIRRFRepository;
    }

    private final BigDecimal DESCONTO_SIMPLIFICADO = BigDecimal.valueOf(607.2);

    public CalculoPagamentoDTO calculoImposto(CalculoPagamentoDTO req, RequisitorioDTO requisitorioDTO) {

        BigDecimal baseTributavelHonorarioValor = req.getValorHonorarioBrutoAtualizado();
        String baseTributavelHonorarioTipo = req.getTributacaoAdvogado();
        BigDecimal baseTributavelHonorarioImposto = BigDecimal.ZERO;

        String baseTributavelCredorTipoCalculo = req.getTipoTributacaoCredor();
        BigDecimal baseTributavelCredorValor = req.getValorCredorPrincipalTributavelAtualizado();
        String baseTributavelCredorTipo = req.getTipoTributacaoCredor();
        BigDecimal baseTributavelCredorImposto = BigDecimal.ZERO;
        BigDecimal baseTributavelCredorPrevidencia = BigDecimal.ZERO;

        // regra para mostrar o valor base de imposto do advogado
        if (!req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()) {
            baseTributavelHonorarioValor = BigDecimal.ZERO;
        } else if (req.isHouveAcordoAdvogado()) {
            baseTributavelHonorarioValor = req.getValorDesagioHonorarioBrutoAtualizado();
        }
        // TODO - Verificar o valor de 1.5 para incluir em uma tabela de parametro
        if (baseTributavelHonorarioTipo.equals("PF") || baseTributavelHonorarioTipo.equals("Pessoa Física")) {
            baseTributavelHonorarioImposto = calcularIRPFProgressivoAdvogado(req, baseTributavelHonorarioValor);
        } else if (baseTributavelHonorarioTipo.equals("PJ")) {
            baseTributavelHonorarioImposto =
                    baseTributavelHonorarioValor.multiply(BigDecimal.valueOf(1.5))
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        req.setBaseTributavelHonorarioValor(baseTributavelHonorarioValor);
        req.setBaseTributavelHonorarioTipo(baseTributavelHonorarioTipo);
        req.setBaseTributavelHonorarioImposto(UtilCalculo.escala(baseTributavelHonorarioImposto, 2));

        if (req.getTipoVinculoCredor().equals("Com vinculo")) {
            // IF(AND(L85="Com Vinculo";D69="NÃO";J69="SIM");L73;
            if (!req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()) {
                baseTributavelCredorValor = req.getValorDesagioCredorPrincipalTributavelAtualizado();
                // IF(AND(L85="Com Vinculo";D69="NÃO";J69="NÃO");L58;
            } else if (req.isHouveAcordoAdvogado() && !req.isHouveAcordoCredor()) {
                baseTributavelCredorValor = BigDecimal.ZERO;
                // IF(AND(L85="Com Vinculo";D69="SIM";J69="NÃO");0;
            } else if (req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()) {
                baseTributavelCredorValor = req.getValorDesagioCredorPrincipalTributavelAtualizado();
            }

        } else if (req.getTipoVinculoCredor().equals("Sem vinculo")) {
            // IF(AND(L85="Sem Vinculo";D69="NÃO";J69="SIM");L73+L75+L77;
            if (!req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()) {
                baseTributavelCredorValor = req.getValorDesagioCredorPrincipalTributavelAtualizado()
                        .add(req.getValorDesagioCredorJurosAtualizado())
                        .add(req.getValorDesagioCredorSelicAtualizado());
                // IF(AND(L85="Sem Vinculo";D69="NÃO";J69="NÃO");L58+L60+L62;
            } else if (!req.isHouveAcordoAdvogado() && !req.isHouveAcordoCredor()) {
                baseTributavelCredorValor = req.getValorCredorPrincipalTributavelAtualizado()
                        .add(req.getValorCredorJurosAtualizado())
                        .add(req.getValorCredorSelicAtualizado());
                // IF(AND(L85="Com Vinculo";D69="SIM";J69="NÃO");0;
            } else if (req.isHouveAcordoAdvogado() && !req.isHouveAcordoCredor()) {
                baseTributavelCredorValor = BigDecimal.ZERO;
                // IF(AND(L85="Sem Vinculo";D69="SIM";J69="SIM");L73+L75+L77;
            } else if (req.isHouveAcordoAdvogado() && req.isHouveAcordoCredor()) {
                baseTributavelCredorValor = req.getValorDesagioCredorPrincipalTributavelAtualizado()
                        .add(req.getValorDesagioCredorJurosAtualizado())
                        .add(req.getValorDesagioCredorSelicAtualizado());
            }
        }

        if (req.isHouveAcordoAdvogado() && !req.isHouveAcordoCredor()) {
            baseTributavelCredorPrevidencia = BigDecimal.ZERO;
        } else if (req.getValorPrioridadeBrutoAtualizado().compareTo(BigDecimal.ZERO) != 0) {
            if (req.isHouveAcordoCredor()) {
                baseTributavelCredorPrevidencia = req.getValorPrioridadePrevidenciaAtualizado().multiply(
                        req.getPercentualDesagio()
                ).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                baseTributavelCredorPrevidencia = req.getValorPrioridadePrevidenciaAtualizado();
            }
        } else if (req.getValorPrioridadeBrutoAtualizado().compareTo(BigDecimal.ZERO) == 0) {
            if (req.isHouveAcordoCredor()) {
                baseTributavelCredorPrevidencia = req.getValorPrevidenciaAtualizado().multiply(
                        req.getPercentualDesagio()
                ).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                baseTributavelCredorPrevidencia = req.getValorPrevidenciaAtualizado();
            }
        }

        req.setBaseTributavelCredorTipoCalculo(baseTributavelCredorTipoCalculo);
        req.setBaseTributavelCredorValor(UtilCalculo.escala(baseTributavelCredorValor, 2));
        req.setBaseTributavelCredorTipo(baseTributavelCredorTipo);
        req.setBaseTributavelCredorPrevidencia(UtilCalculo.escala(baseTributavelCredorPrevidencia, 2));

        baseTributavelCredorImposto = calcularIRRF(req, requisitorioDTO);
        req.setBaseTributavelCredorImposto(baseTributavelCredorImposto);

        return req;
    }

    private BigDecimal calcularIRRF(CalculoPagamentoDTO req, RequisitorioDTO requisitorioDTO) {

        BigDecimal baseCalculo;
        BigDecimal resultado = BigDecimal.ZERO;

        var temPrevidencia = requisitorioDTO.getVlPrevidencia() != null
                && requisitorioDTO.getVlPrevidencia().compareTo(BigDecimal.ZERO) > 0;

        var temRRA = Optional.ofNullable(requisitorioDTO.getNumeroMesesRRA())
                .orElse(0) > 0;

        var tipoTributacaoOpt = requisitorioDTO.getTipoCredor()
                .getTipoTributacoesDTO()
                .stream()
                .filter(t -> t.isAtivo()
                        && t.isComPrevidencia() == temPrevidencia
                        && t.isComRRA() == temRRA)
                .findFirst();

        if (tipoTributacaoOpt.isEmpty()) {
            throw new RuntimeException("Nenhum tipo de tributação compatível encontrado para o credor "
                    + requisitorioDTO.getDocumentoCredor());
        }

        var tipoTributacao = tipoTributacaoOpt.get();


        switch (tipoTributacao.getCodigo()) {

            case "PF_TABELA" -> {
                baseCalculo = req.getBaseTributavelCredorValor().subtract(
                        CalculoUtil.maior(req.getBaseTributavelCredorPrevidencia(), DESCONTO_SIMPLIFICADO)
                );
                resultado = calcularIRPFProgressivoCredor(req, baseCalculo);
            }

            case "RRA" -> {
                // Rendimento recebido acumuladamente (precatórios)
                baseCalculo = req.getBaseTributavelCredorValor()
                        .subtract(req.getBaseTributavelCredorPrevidencia())
                        .subtract(req.getBaseTributavelHonorarioValor() != null ? req.getBaseTributavelHonorarioValor() : BigDecimal.ZERO);
                resultado = calcularIRPFProgressivoCredor(req, baseCalculo);
            }

            case "PJ_CESSAO" -> {
                resultado = CalculoUtil.aplicarPercentual(req.getBaseTributavelCredorValor(),1);
            }

            case "PJ_SERVICOS" -> {
                resultado = CalculoUtil.aplicarPercentual(req.getBaseTributavelCredorValor(),1.5);
            }

            case "PJ_LUCRO" -> {
                resultado = CalculoUtil.aplicarPercentual(req.getBaseTributavelCredorValor(),5);
            }

            case "ISENTO" -> {
                resultado = BigDecimal.ZERO;
            }

            default -> {
                resultado = BigDecimal.ZERO;
            }
        }

        return resultado.max(BigDecimal.ZERO);
    }

    private BigDecimal calcularIRPFProgressivoAdvogado(CalculoPagamentoDTO req, BigDecimal baseCalculo) {


        List<TabelaIRRF> tabela = tabelaIRRFRepository.findAll();

        BigDecimal base = baseCalculo.subtract(DESCONTO_SIMPLIFICADO);

        if (base.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        for (TabelaIRRF faixa : tabela) {
            if (faixa.dentroDaFaixa(base)) {
                BigDecimal valor = base.multiply(faixa.getValorAliquota()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal menos = valor.subtract(faixa.getValorDeducao());
                return menos;
            }
        }

        TabelaIRRF ultimaFaixa = tabela.getLast();
        return base
                .multiply(ultimaFaixa.getValorAliquota()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .subtract(ultimaFaixa.getValorDeducao());

    }

    private BigDecimal calcularIRPFProgressivoCredor(CalculoPagamentoDTO req, BigDecimal baseCalculo) {

        List<TabelaIRRF> tabela = tabelaIRRFRepository.findAll();
        List<TabelaIRRF> tabelaAjustada = new ArrayList<>();

        BigDecimal mesesRRA = Optional.ofNullable(req.getNumeroMesesRRA())
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ONE); // 1 mês padrão se não for RRA

        boolean isRRA = req.getNumeroMesesRRA() != null && req.getNumeroMesesRRA() > 0;

        // Ajusta as faixas da tabela conforme número de meses (RRA)
        for (TabelaIRRF faixa : tabela) {
            TabelaIRRF ajustada = new TabelaIRRF();
            ajustada.setValorAliquota(faixa.getValorAliquota());

            if (isRRA) {
                ajustada.setValorFaixaInicial(faixa.getValorFaixaInicial().multiply(mesesRRA));
                ajustada.setValorFaixaFinal(faixa.getValorFaixaFinal() != null
                        ? faixa.getValorFaixaFinal().multiply(mesesRRA)
                        : null);
                ajustada.setValorDeducao(faixa.getValorDeducao().multiply(mesesRRA));
            } else {
                ajustada.setValorFaixaInicial(faixa.getValorFaixaInicial());
                ajustada.setValorFaixaFinal(faixa.getValorFaixaFinal());
                ajustada.setValorDeducao(faixa.getValorDeducao());
            }

            tabelaAjustada.add(ajustada);
        }

        // Calcula o valor do IRRF pela faixa aplicável
        for (TabelaIRRF faixa : tabelaAjustada) {
            boolean acimaMin = faixa.getValorFaixaInicial() == null || baseCalculo.compareTo(faixa.getValorFaixaInicial()) >= 0;
            boolean abaixoMax = faixa.getValorFaixaFinal() == null || baseCalculo.compareTo(faixa.getValorFaixaFinal()) <= 0;

            if (acimaMin && abaixoMax) {
                BigDecimal imposto = baseCalculo.multiply(faixa.getValorAliquota())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                        .subtract(faixa.getValorDeducao());

                // Isenção automática se o imposto <= 0
                if (imposto.compareTo(BigDecimal.ZERO) <= 0) {
                    return BigDecimal.ZERO;
                }

                return imposto.setScale(2, RoundingMode.HALF_UP);
            }
        }

        return BigDecimal.ZERO;
    }
}
