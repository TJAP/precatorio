package br.jus.tjap.precatorio.configuracao;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${aplicacao.api.swagger.url}")
    private String urlServer;

    @Value("${server.port}")
    private String porta;

    public String getUrlServer() {
        return urlServer;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Precatórios - TJAP")
                        .version("v1")
                        .description("Documentação da API do sistema de precatórios")
                        .contact(new Contact()
                                .name("Equipe TI")
                                .email("seges@tjap.jus.br")))
                .servers(
                        List.of(new Server().url(getUrlServer()).description("Servidor de homologação - Precatórios"))
                );
    }
}
