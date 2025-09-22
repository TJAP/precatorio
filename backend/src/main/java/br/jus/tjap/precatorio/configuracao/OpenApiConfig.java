package br.jus.tjap.precatorio.configuracao;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Precatórios - TJAP")
                        .version("v1")
                        .description("Documentação da API do sistema de precatórios")
                        .contact(new Contact()
                                .name("Equipe TI")
                                .email("seges@tjap.jus.br")));
    }
}
