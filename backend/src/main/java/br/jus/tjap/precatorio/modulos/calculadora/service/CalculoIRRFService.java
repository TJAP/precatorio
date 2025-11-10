package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.dto.*;
import br.jus.tjap.precatorio.modulos.calculadora.entity.TabelaIRRF;
import br.jus.tjap.precatorio.modulos.calculadora.repository.TabelaIRRFRepository;
import br.jus.tjap.precatorio.modulos.calculadora.util.CalculoUtil;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CalculoIRRFService {

    private static final Logger logger = LoggerFactory.getLogger(CalculoIRRFService.class);

    private final TabelaIRRFRepository tabelaIRRFRepository;

    public CalculoIRRFService(TabelaIRRFRepository tabelaIRRFRepository) {
        this.tabelaIRRFRepository = tabelaIRRFRepository;
    }

    private final BigDecimal DESCONTO_SIMPLIFICADO = BigDecimal.valueOf(607.2);

    public ResultadoPagamentoImpostoDTO calculoImposto(ResultadoCalculoPrecatorioDTO calculo) {

        var tributo = new ResultadoPagamentoImpostoDTO();

        BigDecimal baseTributavelHonorarioValor = calculo.getRateio().getValorHonorarioBrutoAtualizado();
        String baseTributavelHonorarioTipo = calculo.getRateio().getTipoTributacaoAdvogado();
        BigDecimal baseTributavelHonorarioImposto = BigDecimal.ZERO;

        String baseTributavelCredorTipoCalculo = calculo.getRateio().getTipoTributacaoCredor();
        BigDecimal baseTributavelCredorValor = calculo.getRateio().getValorCredorPrincipalTributavelAtualizado();
        String baseTributavelCredorTipo = calculo.getRateio().getTipoTributacaoCredor();
        BigDecimal baseTributavelCredorImposto = BigDecimal.ZERO;
        BigDecimal baseTributavelCredorPrevidencia = BigDecimal.ZERO;

        // regra para mostrar o valor base de imposto do advogado
        if (!calculo.getRateio().isHouveAcordoAdvogado() && calculo.getRateio().isHouveAcordoCredor()) {
            baseTributavelHonorarioValor = BigDecimal.ZERO;
        } else if (calculo.getRateio().isHouveAcordoAdvogado()) {
            baseTributavelHonorarioValor = calculo.getRateio().getValorDesagioHonorarioBrutoAtualizado();
        }
        // TODO - Verificar o valor de 1.5 para incluir em uma tabela de parametro
        if (baseTributavelHonorarioTipo.equals("PF") || baseTributavelHonorarioTipo.equals("Pessoa Física")) {
            baseTributavelHonorarioImposto = calcularIRPFProgressivoAdvogado(baseTributavelHonorarioValor);
        } else if (baseTributavelHonorarioTipo.equals("PJ")) {
            baseTributavelHonorarioImposto =
                    baseTributavelHonorarioValor.multiply(BigDecimal.valueOf(1.5))
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        tributo.setBaseTributavelHonorarioValor(baseTributavelHonorarioValor);
        tributo.setBaseTributavelHonorarioTipo(baseTributavelHonorarioTipo);
        tributo.setBaseTributavelHonorarioImposto(UtilCalculo.escala(baseTributavelHonorarioImposto, 2));

        if (calculo.getRequisitorio().getTipoVinculoCredor().equals("Com vinculo")) {
            // IF(AND(L85="Com Vinculo";D69="NÃO";J69="SIM");L73;
            if (!calculo.getRateio().isHouveAcordoAdvogado() && calculo.getRateio().isHouveAcordoCredor()) {
                baseTributavelCredorValor = calculo.getRateio().getValorDesagioCredorPrincipalTributavelAtualizado();
                // IF(AND(L85="Com Vinculo";D69="NÃO";J69="NÃO");L58;
            } else if (calculo.getRateio().isHouveAcordoAdvogado() && !calculo.getRateio().isHouveAcordoCredor()) {
                baseTributavelCredorValor = BigDecimal.ZERO;
                // IF(AND(L85="Com Vinculo";D69="SIM";J69="NÃO");0;
            } else if (calculo.getRateio().isHouveAcordoAdvogado() && calculo.getRateio().isHouveAcordoCredor()) {
                baseTributavelCredorValor = calculo.getRateio().getValorDesagioCredorPrincipalTributavelAtualizado();
            }

        } else if (calculo.getRequisitorio().getTipoVinculoCredor().equals("Sem vinculo")) {
            // IF(AND(L85="Sem Vinculo";D69="NÃO";J69="SIM");L73+L75+L77;
            if (!calculo.getRateio().isHouveAcordoAdvogado() && calculo.getRateio().isHouveAcordoCredor()) {
                baseTributavelCredorValor = calculo.getRateio().getValorDesagioCredorPrincipalTributavelAtualizado()
                        .add(calculo.getRateio().getValorDesagioCredorJurosAtualizado())
                        .add(calculo.getRateio().getValorDesagioCredorSelicAtualizado());
                // IF(AND(L85="Sem Vinculo";D69="NÃO";J69="NÃO");L58+L60+L62;
            } else if (!calculo.getRateio().isHouveAcordoAdvogado() && !calculo.getRateio().isHouveAcordoCredor()) {
                baseTributavelCredorValor = calculo.getRateio().getValorCredorPrincipalTributavelAtualizado()
                        .add(calculo.getRateio().getValorCredorJurosAtualizado())
                        .add(calculo.getRateio().getValorCredorSelicAtualizado());
                // IF(AND(L85="Com Vinculo";D69="SIM";J69="NÃO");0;
            } else if (calculo.getRateio().isHouveAcordoAdvogado() && !calculo.getRateio().isHouveAcordoCredor()) {
                baseTributavelCredorValor = BigDecimal.ZERO;
                // IF(AND(L85="Sem Vinculo";D69="SIM";J69="SIM");L73+L75+L77;
            } else if (calculo.getRateio().isHouveAcordoAdvogado() && calculo.getRateio().isHouveAcordoCredor()) {
                baseTributavelCredorValor = calculo.getRateio().getValorDesagioCredorPrincipalTributavelAtualizado()
                        .add(calculo.getRateio().getValorDesagioCredorJurosAtualizado())
                        .add(calculo.getRateio().getValorDesagioCredorSelicAtualizado());
            }
        }

        if (calculo.getRateio().isHouveAcordoAdvogado() && !calculo.getRateio().isHouveAcordoCredor()) {
            baseTributavelCredorPrevidencia = BigDecimal.ZERO;
        } else if (calculo.getPrioridade().getValorBrutoAtualizado().compareTo(BigDecimal.ZERO) != 0) {
            if (calculo.getRateio().isHouveAcordoCredor()) {
                baseTributavelCredorPrevidencia = calculo.getPrioridade().getValorPrevidenciaAtualizado().multiply(
                        calculo.getRateio().getPercentualDesagio()
                ).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                baseTributavelCredorPrevidencia = calculo.getPrioridade().getValorPrevidenciaAtualizado();
            }
        } else if (calculo.getPrioridade().getValorBrutoAtualizado().compareTo(BigDecimal.ZERO) == 0) {
            if (calculo.getRateio().isHouveAcordoCredor()) {
                baseTributavelCredorPrevidencia = calculo.getAtualizacao().getValorPrevidenciaAtualizado().multiply(
                        calculo.getRateio().getPercentualDesagio()
                ).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                baseTributavelCredorPrevidencia = calculo.getAtualizacao().getValorPrevidenciaAtualizado();
            }
        }

        tributo.setBaseTributavelCredorTipoCalculo(baseTributavelCredorTipoCalculo);
        tributo.setBaseTributavelCredorValor(baseTributavelCredorValor);
        tributo.setBaseTributavelCredorTipo(baseTributavelCredorTipo);
        tributo.setBaseTributavelCredorPrevidencia(baseTributavelCredorPrevidencia);

        calculo.setImposto(tributo);
        baseTributavelCredorImposto = calcularIRRF(calculo);
        tributo.setBaseTributavelCredorImposto(baseTributavelCredorImposto);

        return tributo;
    }

    private BigDecimal calcularIRRF(ResultadoCalculoPrecatorioDTO calculo) {

        BigDecimal baseCalculo;
        BigDecimal resultado = BigDecimal.ZERO;

        var temPrevidencia = calculo.getRequisitorio().getVlPrevidencia() != null
                && calculo.getRequisitorio().getVlPrevidencia().compareTo(BigDecimal.ZERO) > 0;

        var temRRA = Optional.ofNullable(calculo.getRequisitorio().getNumeroMesesRRA())
                .orElse(0) > 0;

        var tipoTributacaoOpt = calculo.getRequisitorio().getTipoCredor()
                .getTipoTributacoesDTO()
                .stream()
                .filter(t -> t.isAtivo()
                        && t.isComPrevidencia() == temPrevidencia
                        && t.isComRRA() == temRRA)
                .findFirst();

        if (tipoTributacaoOpt.isEmpty()) {
            throw new RuntimeException("Nenhum tipo de tributação compatível encontrado para o credor "
                    + calculo.getRequisitorio().getDocumentoCredor());
        }

        var tipoTributacao = tipoTributacaoOpt.get();


        switch (tipoTributacao.getCodigo()) {

            case "PF_TABELA" -> {
                baseCalculo = calculo.getImposto().getBaseTributavelCredorValor().subtract(
                        CalculoUtil.maior(calculo.getImposto().getBaseTributavelCredorPrevidencia(), DESCONTO_SIMPLIFICADO)
                );
                resultado = calcularIRPFProgressivoCredor(calculo, baseCalculo);
            }

            case "RRA" -> {
                // Rendimento recebido acumuladamente (precatórios)
                baseCalculo = calculo.getImposto().getBaseTributavelCredorValor()
                        .subtract(calculo.getImposto().getBaseTributavelCredorPrevidencia())
                        .subtract(calculo.getImposto().getBaseTributavelHonorarioValor() != null ? calculo.getImposto().getBaseTributavelHonorarioValor() : BigDecimal.ZERO);
                resultado = calcularIRPFProgressivoCredor(calculo, baseCalculo);
            }

            case "PJ_CESSAO" -> {
                resultado = CalculoUtil.aplicarPercentual(calculo.getImposto().getBaseTributavelCredorValor(),1);
            }

            case "PJ_SERVICOS" -> {
                resultado = CalculoUtil.aplicarPercentual(calculo.getImposto().getBaseTributavelCredorValor(),1.5);
            }

            case "PJ_LUCRO" -> {
                resultado = CalculoUtil.aplicarPercentual(calculo.getImposto().getBaseTributavelCredorValor(),5);
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

    private BigDecimal calcularIRPFProgressivoAdvogado(BigDecimal baseCalculo) {


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

    private BigDecimal calcularIRPFProgressivoCredor(ResultadoCalculoPrecatorioDTO calculo, BigDecimal baseCalculo) {

        List<TabelaIRRF> tabela = tabelaIRRFRepository.findAll();
        List<TabelaIRRF> tabelaAjustada = new ArrayList<>();

        var totalMesesRRA = calculo.getAtualizacao().getValorNumeroMesesRRA();
        if(calculo.getPrioridade().isTemPrioridade()){
            totalMesesRRA = calculo.getPrioridade().getNumeroPrioridadeRRA().longValue();
        }

        BigDecimal mesesRRA = Optional.ofNullable(totalMesesRRA)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ONE); // 1 mês padrão se não for RRA

        boolean isRRA = totalMesesRRA != null && totalMesesRRA > 0;

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
