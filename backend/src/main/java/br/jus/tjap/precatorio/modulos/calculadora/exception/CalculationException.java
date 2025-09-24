package br.jus.tjap.precatorio.modulos.calculadora.exception;

public class CalculationException extends RuntimeException {
    public CalculationException(String message, Throwable cause) { super(message, cause); }
    public CalculationException(String message) { super(message); }
}
