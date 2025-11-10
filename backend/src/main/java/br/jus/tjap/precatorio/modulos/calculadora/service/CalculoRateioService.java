package br.jus.tjap.precatorio.modulos.calculadora.service;

import br.jus.tjap.precatorio.modulos.calculadora.dto.ResultadoAtualizacaoPrecatorioDTO;
import br.jus.tjap.precatorio.modulos.calculadora.dto.ResultadoPagamentoCalculoPrioridadeDTO;
import br.jus.tjap.precatorio.modulos.calculadora.dto.ResultadoPagamentoRateioDTO;
import br.jus.tjap.precatorio.modulos.calculadora.util.UtilCalculo;
import br.jus.tjap.precatorio.modulos.requisitorio.dto.RequisitorioDTO;
import br.jus.tjap.precatorio.modulos.requisitorio.entity.AcordoDireto;
import br.jus.tjap.precatorio.modulos.requisitorio.service.RequisitorioService;
import br.jus.tjap.precatorio.util.StringUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CalculoRateioService {

    private static final Logger logger = LoggerFactory.getLogger(CalculoRateioService.class);

    private final RequisitorioService requisitorioService;

    public CalculoRateioService(RequisitorioService requisitorioService) {
        this.requisitorioService = requisitorioService;
    }

    public ResultadoPagamentoRateioDTO calcularRateioPrecatorio(RequisitorioDTO requisitorioDTO, ResultadoAtualizacaoPrecatorioDTO atualizacao, ResultadoPagamentoCalculoPrioridadeDTO prioridade) {

        try {

            var rateio = new ResultadoPagamentoRateioDTO();

            var acordos = requisitorioService.listarAcordoPorProcesso(requisitorioDTO.getIdPrecatorioTucujuris());

            MathContext mc = new MathContext(4, RoundingMode.HALF_UP);

            BigDecimal valorPrincipalTributavelAtualizado = atualizacao.getValorPrincipalTributavelAtualizado();
            BigDecimal valorPrincipalNaoTributavelAtualizado = atualizacao.getValorPrincipalNaoTributavelAtualizado();
            BigDecimal valorJurosAtualizado = atualizacao.getValorJurosAtualizado();
            BigDecimal valorMultaCustasOutrosAtualizado = atualizacao.getValorMultaCustasOutrosAtualizado();
            BigDecimal valorSelicAtualizado = atualizacao.getValorSelicAtualizado();
            BigDecimal valorBrutoAtualizado = atualizacao.getValorBrutoAtualizado();

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

            if (prioridade.isTemPrioridade() || prioridade.isTemPagamentoParcial()) {
                if (prioridade.getValorBasePrioridade().compareTo(atualizacao.getValorBrutoAtualizado()) <= 0) {

                    valorPrincipalTributavelAtualizado = prioridade.getValorPrincipalTributavelAtualizado();
                    valorPrincipalNaoTributavelAtualizado = prioridade.getValorPrincipalNaoTributavelAtualizado();
                    valorJurosAtualizado = prioridade.getValorJurosAtualizado();
                    valorMultaCustasOutrosAtualizado = prioridade.getValorMultaCustasOutrosAtualizado();
                    valorSelicAtualizado = prioridade.getValorSelicAtualizado();
                    valorBrutoAtualizado = prioridade.getValorBrutoAtualizado();
                }
            }

            // TODO - Verificar o valor pago ao advogado on tem (BigDecimal.ZERO)
            BigDecimal parteHonorario = valorBrutoAtualizado.multiply(UtilCalculo.manterValorZeroSeNulo(requisitorioDTO.getVlPercentualHonorarioAdvCredor())
                    .divide(BigDecimal.valueOf(100))).add(BigDecimal.ZERO);
            BigDecimal parteRestoCredor = valorBrutoAtualizado.subtract(parteHonorario);
            BigDecimal percentualAdvogado = parteHonorario.divide(valorBrutoAtualizado, mc).multiply(BigDecimal.valueOf(100));
            BigDecimal percentualCredor = parteRestoCredor.divide(valorBrutoAtualizado, mc).multiply(BigDecimal.valueOf(100));

            rateio.setPercentualParteAdvogado(UtilCalculo.escala(percentualAdvogado, 2));
            rateio.setPercentualParteCredor(UtilCalculo.escala(percentualCredor, 2));

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

            rateio.setValorHonorarioPrincipalTributavelAtualizado(valorHonorarioPrincipalTributavelAtualizado);
            rateio.setValorHonorarioPrincipalNaoTributavelAtualizado(valorHonorarioPrincipalNaoTributavelAtualizado);
            rateio.setValorHonorarioJurosAtualizado(valorHonorarioJurosAtualizado);
            rateio.setValorHonorarioMultaCustasOutrosAtualizado(valorHonorarioMultaCustasOutrosAtualizado);
            rateio.setValorHonorarioSelicAtualizado(valorHonorarioSelicAtualizado);
            rateio.setValorHonorarioBrutoAtualizado(valorHonorarioBrutoAtualizado);

            rateio.setValorCredorPrincipalTributavelAtualizado(valorCredorPrincipalTributavelAtualizado);
            rateio.setValorCredorPrincipalNaoTributavelAtualizado(valorCredorPrincipalNaoTributavelAtualizado);
            rateio.setValorCredorJurosAtualizado(valorCredorJurosAtualizado);
            rateio.setValorCredorMultaCustasOutrosAtualizado(valorCredorMultaCustasOutrosAtualizado);
            rateio.setValorCredorSelicAtualizado(valorCredorSelicAtualizado);
            rateio.setValorCredorBrutoAtualizado(valorCredorBrutoAtualizado);

            if (Objects.nonNull(requisitorioDTO.getTipoTributacaoAdvogado())) {
                rateio.setTipoTributacaoAdvogado(requisitorioDTO.getTipoTributacaoAdvogado());
            }
            if (Objects.nonNull(requisitorioDTO.getTipoTributacaoCredor())) {
                rateio.setTipoTributacaoCredor(requisitorioDTO.getTipoTributacaoCredor());
            }

            if (!acordos.isEmpty()) {
                calculoAcordoDireto(requisitorioDTO, rateio, acordos);
            }

            return rateio;

        } catch (Exception e) {
            logger.error("[CalculoRateio] - Erro no requisitorio: {}", requisitorioDTO.getId());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void calculoAcordoDireto(RequisitorioDTO requisitorioDTO, ResultadoPagamentoRateioDTO req, List<AcordoDireto> acordos) {

        try {

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

            boolean houveAcordoCredor = Boolean.FALSE;
            boolean houveAcordoAdvogado = Boolean.FALSE;

            var percentualDesagio = acordos.getFirst().getPercentualDesagio();
            req.setPercentualDesagio(percentualDesagio);

            for (var acordo : acordos) {
                if (acordo.getTipoParte().equalsIgnoreCase("Credor Principal")) {
                    houveAcordoCredor = Boolean.TRUE;
                    req.setHouveAcordoCredor(houveAcordoCredor);
                    if (Objects.nonNull(acordo.getTipoTributacao())) {
                        if (Objects.nonNull(requisitorioDTO.getDtInicioRRA())) {
                            req.setTipoTributacaoCredor("RRA");
                        }
                    }
                } else if (acordo.getTipoParte().equalsIgnoreCase("Honorários Contratuais")) {
                    houveAcordoAdvogado = Boolean.TRUE;
                    req.setHouveAcordoAdvogado(houveAcordoAdvogado);
                    if (Objects.nonNull(acordo.getTipoTributacao())) {
                        req.setTipoTributacaoAdvogado(
                                (acordo.getTipoTributacao().equalsIgnoreCase("Pessoa Jurídica - Simples")
                                        || acordo.getTipoTributacao().equalsIgnoreCase("Pessoa Jurídica Simples Nacional")) ? "SN" : "PF"
                        );
                    }
                    if (Objects.nonNull(acordo.getPercentualHonorario())) {
                        req.setPercentualHonorario(UtilCalculo.manterValorZeroSeNulo(acordo.getPercentualHonorario()));
                    }
                }
            }

            if (houveAcordoCredor && Objects.nonNull(percentualDesagio)) {
                valorDesagioCredorPrincipalTributavelAtualizado =
                        req.getValorCredorPrincipalTributavelAtualizado().subtract(
                                req.getValorCredorPrincipalTributavelAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );

                valorDesagioCredorPrincipalNaoTributavelAtualizado =
                        req.getValorCredorPrincipalNaoTributavelAtualizado().subtract(
                                req.getValorCredorPrincipalNaoTributavelAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );
                valorDesagioCredorJurosAtualizado =
                        req.getValorCredorJurosAtualizado().subtract(
                                req.getValorCredorJurosAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );
                valorDesagioCredorMultaCustasOutrosAtualizado =
                        req.getValorCredorMultaCustasOutrosAtualizado().subtract(
                                req.getValorCredorMultaCustasOutrosAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );
                valorDesagioCredorSelicAtualizado =
                        req.getValorCredorSelicAtualizado().subtract(
                                req.getValorCredorSelicAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );
                valorDesagioCredorBrutoAtualizado =
                        req.getValorCredorBrutoAtualizado().subtract(
                                req.getValorCredorBrutoAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );
                valorDesagioCredorTotalAtualizado =
                        req.getValorCredorBrutoAtualizado().subtract(valorDesagioCredorBrutoAtualizado);

                req.setValorDesagioCredorPrincipalTributavelAtualizado(valorDesagioCredorPrincipalTributavelAtualizado);
                req.setValorDesagioCredorPrincipalNaoTributavelAtualizado(valorDesagioCredorPrincipalNaoTributavelAtualizado);
                req.setValorDesagioCredorJurosAtualizado(valorDesagioCredorJurosAtualizado);
                req.setValorDesagioCredorMultaCustasOutrosAtualizado(valorDesagioCredorMultaCustasOutrosAtualizado);
                req.setValorDesagioCredorSelicAtualizado(valorDesagioCredorSelicAtualizado);
                req.setValorDesagioCredorBrutoAtualizado(valorDesagioCredorBrutoAtualizado);
                req.setValorDesagioCredorAtualizado(valorDesagioCredorTotalAtualizado);
            }

            if (houveAcordoAdvogado && Objects.nonNull(percentualDesagio)) {
                valorDesagioAdvogadoPrincipalTributavelAtualizado =
                        req.getValorHonorarioPrincipalTributavelAtualizado().subtract(
                                req.getValorHonorarioPrincipalTributavelAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );

                valorDesagioAdvogadoPrincipalNaoTributavelAtualizado =
                        req.getValorHonorarioPrincipalNaoTributavelAtualizado().subtract(
                                req.getValorHonorarioPrincipalNaoTributavelAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );
                valorDesagioAdvogadoJurosAtualizado =
                        req.getValorHonorarioJurosAtualizado().subtract(
                                req.getValorHonorarioJurosAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );
                valorDesagioAdvogadoMultaCustasOutrosAtualizado =
                        req.getValorHonorarioMultaCustasOutrosAtualizado().subtract(
                                req.getValorHonorarioMultaCustasOutrosAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );
                valorDesagioAdvogadoSelicAtualizado =
                        req.getValorHonorarioSelicAtualizado().subtract(
                                req.getValorHonorarioSelicAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );
                valorDesagioAdvogadoBrutoAtualizado =
                        req.getValorHonorarioBrutoAtualizado().subtract(
                                req.getValorHonorarioBrutoAtualizado().multiply(
                                        percentualDesagio
                                ).divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP)
                        );
                valorDesagioAdvogadoTotalAtualizado =
                        req.getValorHonorarioBrutoAtualizado().subtract(valorDesagioAdvogadoBrutoAtualizado);

                req.setValorDesagioHonorarioPrincipalTributavelAtualizado(valorDesagioAdvogadoPrincipalTributavelAtualizado);
                req.setValorDesagioHonorarioPrincipalNaoTributavelAtualizado(valorDesagioAdvogadoPrincipalNaoTributavelAtualizado);
                req.setValorDesagioHonorarioJurosAtualizado(valorDesagioAdvogadoJurosAtualizado);
                req.setValorDesagioHonorarioMultaCustasOutrosAtualizado(valorDesagioAdvogadoMultaCustasOutrosAtualizado);
                req.setValorDesagioHonorarioSelicAtualizado(valorDesagioAdvogadoSelicAtualizado);
                req.setValorDesagioHonorarioBrutoAtualizado(valorDesagioAdvogadoBrutoAtualizado);
                req.setValorDesagioHonorarioAtualizado(valorDesagioAdvogadoTotalAtualizado);
            }

        } catch (Exception e) {
            logger.error("[CalculoRateioDesagio] - Erro no requisitorio: {}", requisitorioDTO.getId());
            throw new RuntimeException(e.getMessage());
        }
    }
}
