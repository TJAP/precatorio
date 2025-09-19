package br.jus.tjap.precatorio.configuracao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "br.jus.tjap.precatorio"
        })
@EntityScan(basePackages = {
        "br.jus.pdpj.precatorio"
})
public class JPAConfig {
}
