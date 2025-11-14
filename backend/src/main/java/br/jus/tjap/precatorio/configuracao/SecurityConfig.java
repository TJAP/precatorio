package br.jus.tjap.precatorio.configuracao;

import br.jus.tjap.precatorio.seguranca.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint entryPoint;

    public SecurityConfig(CustomAuthenticationEntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // normalmente desabilitado em APIs
                .cors(cors -> {}) // habilita o CORS usando o bean corsConfigurationSource()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/docs/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(entryPoint)
                );

        return http.build();
    }
}
