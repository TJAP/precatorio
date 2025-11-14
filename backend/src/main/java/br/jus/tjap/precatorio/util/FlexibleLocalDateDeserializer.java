package br.jus.tjap.precatorio.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class FlexibleLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            // Tenta formato simples yyyy-MM-dd
            return LocalDate.parse(value, DATE_ONLY);
        } catch (Exception e1) {
            try {
                // Tenta formato com data e hora
                OffsetDateTime odt = OffsetDateTime.parse(value, DATE_TIME);
                return odt.toLocalDate();
            } catch (Exception e2) {
                throw new IOException("Formato de data inválido: " + value, e2);
            }
        }
    }
}
