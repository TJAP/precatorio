package br.jus.tjap.precatorio.execao;

import br.jus.tjap.precatorio.modelo.Response;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Optional;

@ControllerAdvice
public class BaseExceptionHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    //==== Handlers gerais ======//

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<String>> defaultErrorHandler(HttpServletRequest req, Exception ex){
        return responseErro(ex, ex instanceof EntityNotFoundException ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<String>> businessExceptionHandler(final BusinessException ex, final WebRequest request){
        return responseErro(ex, HttpStatus.BAD_REQUEST);
    }

    //==== Handlers de exceções de validação e erro do banco de dados ======//

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<String>> onConstraintValidationException(ConstraintViolationException e){
        log.error("[" + e.getClass().getName() + "]", e);
        StringBuilder msg = new StringBuilder();
        for (@SuppressWarnings("rawtypes") ConstraintViolation violation : e.getConstraintViolations()) {
            msg.append(violation.getPropertyPath().toString() + " - " + violation.getMessage() + "; ");
        }
        return responseErro(msg.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<String>> onMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("[" + e.getClass().getName() + "]", e);
        StringBuilder msg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            msg.append(fieldError.getField() + ": " + fieldError.getDefaultMessage() + "; ");
        }
        return responseErro(msg.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<String>> onDataIntegrityViolationException(DataIntegrityViolationException e){
        log.error("[" + e.getClass().getName() + "]", e);
        String mensagem = getMessage(e);
        if(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException){
            org.hibernate.exception.ConstraintViolationException cause = (org.hibernate.exception.ConstraintViolationException)e.getCause();
            if(cause!=null && cause.getCause() != null && cause.getCause().getMessage().contains("duplicate key")) {
                mensagem = "Registro duplicado. Chave: "+ cause.getConstraintName();
            }
        }
        return responseErro(mensagem, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<String>> invalidFormatException(final HttpMessageNotReadableException e) {
        if(e.getCause() instanceof InvalidFormatException) {
            final String message = Optional.ofNullable(e.getCause().getMessage()).orElse(e.getClass().getSimpleName());
            String[] formats = message.split("format");
            String[] campos = message.split("\\[\"");
            StringBuilder sb = new StringBuilder().append("Formato do Campo Inválido. ")
                    .append("Campo: ").append(campos[1].replace("\"])", ""))
                    .append(" - Formato: ").append(formats[1]);
            return responseErro(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        return responseErro(e, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    //==== Métodos utilitários ======//

    private String getMessage(Exception erro) {
        String message = (!(erro instanceof BusinessException) ? "Ocorreu um erro inesperado: " : "") + erro.getMessage();
        if (erro.getCause()!=null) {
            message = message + "; Causa: " + erro.getMessage();
        }
        return message;
    }

    private ResponseEntity<Response<String>> responseErro(Exception erro, HttpStatus status) {
        log.error("[" + erro.getClass().getName() + "]", erro);
        return responseErro(getMessage(erro), status);
    }

    private ResponseEntity<Response<String>> responseErro(String mensagem, HttpStatus status) {
        Response<String> response = new Response<String>(status.value(), Arrays.asList(mensagem), null, "error");
        return new ResponseEntity<Response<String>>(response, new HttpHeaders(), status);
    }
}
