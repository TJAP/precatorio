package br.jus.tjap.precatorio.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

/**
 * Classe utilitária para padronizar retornos dos controllers.
 * Usa o modelo Response<M> para uniformizar responses.
 */
public class ResponseFactory {

    private ResponseFactory() {
        // Construtor privado para não instanciar
    }

    /** Sucesso simples (HTTP 200) */
    public static <T> ResponseEntity<Response<T>> ok(T result) {
        return build(HttpStatus.OK.value(), "success", Collections.emptyList(), result);
    }

    /** Sucesso criado (HTTP 201) */
    public static <T> ResponseEntity<Response<T>> created(T result) {
        return build(HttpStatus.CREATED.value(), "success", Collections.emptyList(), result);
    }

    /** Retorno sem conteúdo (HTTP 204) */
    public static <T> ResponseEntity<Response<T>> noContent() {
        return build(HttpStatus.NO_CONTENT.value(), "success", Collections.emptyList(), null);
    }

    /** Erro com lista de mensagens */
    public static <T> ResponseEntity<Response<T>> error(List<String> messages, HttpStatus status) {
        return build(status.value(), "error", messages, null);
    }

    /** Erro com única mensagem */
    public static <T> ResponseEntity<Response<T>> error(String message, HttpStatus status) {
        return build(status.value(), "error", Collections.singletonList(message), null);
    }

    /** Retorno paginado (HTTP 200) */
    public static <T> ResponseEntity<Response<PageResponse<T>>> paginated(Page<T> page) {
        PageResponse<T> pageResponse = new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
        return ok(pageResponse);
    }

    /** Método genérico para montar o response */
    private static <T> ResponseEntity<Response<T>> build(Integer code, String status, List<String> messages, T result) {
        Response<T> response = new Response<>(code, messages, result, status);
        return ResponseEntity.status(code).body(response);
    }
}