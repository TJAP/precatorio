package br.jus.tjap.precatorio.modulos.calculadora.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class RelatorioUtil {

    public static String formatarValorMoeda(BigDecimal valor) {
        var incluirSimboloMonetario = true;
        if (valor != null) {
            String mascaraFormatacao = (incluirSimboloMonetario ? "R$ " : StringUtils.EMPTY) + "###,###,###,##0.00";
            DecimalFormatSymbols formatador = new DecimalFormatSymbols(new Locale("pt", "BR"));
            DecimalFormat valorFormatado = new DecimalFormat(mascaraFormatacao, formatador);
            return valorFormatado.format(valor);
        }
        return "R$ 0,00";//StringUtils.EMPTY;
    }
}
