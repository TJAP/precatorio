package br.jus.tjap.precatorio.util;

import java.time.LocalDate;

public class DataUtil {

    public static boolean estaEntreADataAtual(LocalDate ini, LocalDate fim) {
        LocalDate hoje = LocalDate.now();
        return (hoje.isAfter(ini) && hoje.isBefore(fim));
    }
}
