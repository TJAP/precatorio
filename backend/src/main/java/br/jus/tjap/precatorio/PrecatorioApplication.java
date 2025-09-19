package br.jus.tjap.precatorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrecatorioApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrecatorioApplication.class, args);
	}

}
