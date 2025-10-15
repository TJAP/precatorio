package br.jus.tjap.precatorio.relatorio.service;

import br.jus.tjap.precatorio.modulos.calculadora.dto.CalculoResumoDTO;
import br.jus.tjap.precatorio.modulos.calculadora.dto.ResumoCalculoDocumentoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class ReportJsService {

    @Autowired
    RestTemplate restTemplate;

    private String URLRJS = "https://cluster.tjap.jus.br/jsreport_tucujuris/api/report";
    private String TOKENRJS = "bHVjYXMuZnJlaXRhc0B0amFwLmp1cy5icjpkMyQxJDAwN3o=";


    public byte[] getRelatorioResumosCalculo(CalculoResumoDTO dto){
        return getRelatorio("precatorio_pdpj", dto);
    }

    public byte[] getRelatorioResumosCalculo(ResumoCalculoDocumentoDTO dto){
        return getRelatorio("novo_precatorio", dto);
    }


    public byte[] getRelatorio(String nomeRelatorio, CalculoResumoDTO dto) {

        var mapper = new ObjectMapper();

        String authToken = this.TOKENRJS;
        var headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authToken);
        headers.set("Content-Type", "application/json");

        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = "{}";

        try {
            mapper.registerModule(new JavaTimeModule());
            json = mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String requestBody = "{\"template\":{\"name\":\""+nomeRelatorio+"\"},\"data\":"+json+"}";

        var requestEntity = new HttpEntity<>(requestBody, headers);
        try{
            ResponseEntity<byte[]> response = restTemplate.postForEntity(this.URLRJS, requestEntity, byte[].class);
            return response.getBody();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getRelatorio(String nomeRelatorio, ResumoCalculoDocumentoDTO dto) {

        var mapper = new ObjectMapper();

        String authToken = this.TOKENRJS;
        var headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authToken);
        headers.set("Content-Type", "application/json");

        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = "{}";

        try {
            mapper.registerModule(new JavaTimeModule());
            json = mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String requestBody = "{\"template\":{\"name\":\""+nomeRelatorio+"\"},\"data\":"+json+"}";

        var requestEntity = new HttpEntity<>(requestBody, headers);
        try{
            ResponseEntity<byte[]> response = restTemplate.postForEntity(this.URLRJS, requestEntity, byte[].class);
            return response.getBody();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}