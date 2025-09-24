package br.jus.tjap.precatorio.configuracao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "br.jus.tjap.precatorio.modulos.calculadora.repository",
                "br.jus.tjap.precatorio.modulos.requisitorio.repository"
        })
@EntityScan(basePackages = {
        "br.jus.tjap.precatorio.modulos.calculadora.entity",
        "br.jus.tjap.precatorio.modulos.requisitorio.entity"
})
public class JPAConfig {
}
