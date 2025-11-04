package br.jus.tjap.precatorio.relatorio.service;

import br.jus.tjap.precatorio.modulos.calculadora.dto.ResumoCalculoDocumentoDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    /**
     * Gera um relatório PDF genérico baseado em um modelo JRXML e uma lista de dados (DTOs ou entidades).
     *
     * @param nomeTemplate nome do arquivo .jrxml dentro de /resources/reports (sem extensão)
     * @param dados lista de objetos (DTOs, entidades, etc.)
     * @param parametros mapa de parâmetros opcionais para o relatório
     * @param <T> tipo genérico dos dados
     * @return PDF em bytes
     * @throws JRException se houver erro de compilação ou preenchimento do relatório
     */
    public <T> byte[] gerarRelatorioPdf(String nomeTemplate, List<T> dados, Map<String, Object> parametros) throws JRException {
        if (nomeTemplate == null || nomeTemplate.isBlank()) {
            throw new IllegalArgumentException("O nome do template (.jrxml) é obrigatório.");
        }

        // Caminho padrão dos relatórios
        String caminho = String.format("/reports/%s.jrxml", nomeTemplate);
        InputStream relatorioStream = getClass().getResourceAsStream(caminho);
        if (relatorioStream == null) {
            throw new IllegalStateException("Arquivo do relatório não encontrado no classpath: " + caminho);
        }

        // Compila o relatório JRXML → JasperReport
        JasperReport jasperReport = JasperCompileManager.compileReport(relatorioStream);

        // Cria DataSource genérico
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dados);

        // Garante que o Map de parâmetros nunca seja nulo
        Map<String, Object> params = (parametros != null) ? parametros : Map.of();

        // Preenche o relatório com os dados
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        // Exporta para PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    /**
     * Gera um relatório PDF genérico baseado em um modelo JRXML e uma lista de dados (DTOs ou entidades).
     *
     * @param nomeTemplate nome do arquivo .jrxml dentro de /resources/reports (sem extensão)
     * @param dados lista de objetos (DTOs, entidades, etc.)
     * @param parametros mapa de parâmetros opcionais para o relatório
     * @param <T> tipo genérico dos dados
     * @return PDF em bytes
     * @throws JRException se houver erro de compilação ou preenchimento do relatório
     */
    public <T> byte[] gerarRelatorioPdf(String nomeTemplate, T dados, Map<String, Object> parametros) throws JRException {
        if (nomeTemplate == null || nomeTemplate.isBlank()) {
            throw new IllegalArgumentException("O nome do template (.jrxml) é obrigatório.");
        }

        // Caminho padrão dos relatórios
        String caminho = String.format("/relatorios/%s.jrxml", nomeTemplate);
        InputStream relatorioStream = getClass().getResourceAsStream(caminho);
        if (relatorioStream == null) {
            throw new IllegalStateException("Arquivo do relatório não encontrado no classpath: " + caminho);
        }

        // Compila o relatório JRXML → JasperReport
        JasperReport jasperReport = JasperCompileManager.compileReport(relatorioStream);

        // Cria DataSource genérico
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singleton(dados));

        // Garante que o Map de parâmetros nunca seja nulo
        Map<String, Object> params = (parametros != null) ? parametros : Map.of();

        // Preenche o relatório com os dados
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

        // Exporta para PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public byte[] gerarComprovantePrecatorio(ResumoCalculoDocumentoDTO dto, Map<String, Object> parametros) {
        try {
            // com dados das pessoas em lista
            //InputStream jrxmlStream = new ClassPathResource("relatorios/alvara-precatorio.jrxml").getInputStream();

            InputStream jrxmlStream = new ClassPathResource("relatorios/resumo_calculo.jrxml").getInputStream();

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

            // Usa o próprio DTO como datasource (singleton list)
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of(dto));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório: " + e.getMessage(), e);
        }
    }
}
