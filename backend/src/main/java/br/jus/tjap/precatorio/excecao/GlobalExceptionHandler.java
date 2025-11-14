package br.jus.tjap.precatorio.excecao;

import br.jus.tjap.precatorio.util.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Captura erros de acesso negado (403) */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        Response<Void> response = new Response<>(
                HttpStatus.FORBIDDEN.value(),
                Collections.singletonList("Acesso negado: não autorizado"),
                null,
                "error"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /** Captura qualquer exceção genérica não tratada */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGenericException(Exception ex) {
        Response<Void> response = new Response<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Collections.singletonList("Erro interno no servidor"),
                null,
                "error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
