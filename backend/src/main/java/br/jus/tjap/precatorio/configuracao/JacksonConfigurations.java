package br.jus.tjap.precatorio.configuracao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfigurations {

    @Value("${spring.jackson.time-zone}")
    private String timeZone;

    @Value("${spring.jackson.locale}")
    private String locale;

    @Value("${spring.jackson.date-format}")
    private String dateFormat;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.failOnEmptyBeans(false);
        builder.failOnUnknownProperties(false);
        builder.mixIn(Object.class, IgnoreHibernatePropertiesInJackson.class);
        builder.timeZone(timeZone);
        builder.locale(locale);
        builder.simpleDateFormat(dateFormat);
        return builder;
    }

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private abstract class IgnoreHibernatePropertiesInJackson{ }
}
