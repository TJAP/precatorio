package br.jus.tjap.precatorio.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import java.text.ParseException;

public class PDPJStringUtils {
    public static final String CPF_EMPTYMASK = "___.___.___-__";

    public static final String CNPJ_EMPTYMASK = "__.___.___/____-__";

    public static final String TRANSLATE_PRE = "upper(TRANSLATE(";
    public static final String TRANSLATE_POS = ", '" + PDPJStringUtils.SPECIAL_CHARS + "', '" + PDPJStringUtils.ANSI_CHARS
            + "'))";
    public static final String SPECIAL_CHARS = "ÁÀÂÃÄáàâãäÉÈÊËéèêëÍÌÎÏíìîïÓÒÕÔÖóòôõöÚÙÛÜÜúùûüÇç";
    public static final String ANSI_CHARS = "AAAAAaaaaaEEEEeeeeIIIIiiiiOOOOOoooooUUUUUuuuuCc";

    /**
     * Gera um conjunto de caracteres com a função TRANSLATE aplicada sobre o
     * nome do campo indicado no argumento. Os caracteres utilizados como origem
     * são os presentes na constante {@link StringUtil.SPECIAL_CHARS} e os de
     * destino são os presentes na constante {@link StringUtil.ANSI_CHARS}.
     *
     * @param fieldName
     *            o nome do campo que será traduzido com os parênteses.
     * @return Sequência de caracteres que poderá ser inserida em comando SQL
     *         determinando que o SGDB traduza os caracteres especiais do campo
     *         string indicado.
     */
    public static final String translateSql(String fieldName) {
        return TRANSLATE_PRE + fieldName + TRANSLATE_POS;
    }

    /**
     * Retorna o texto fornecido substituindo os caracteres especiais dados
     * pelos equivalentes não especiais.
     *
     * @param txt
     * @return o texto fornecido, substituídos os caracteres especiais pelos
     *         seus equivalentes não especiais.
     */
    public static String normalize(String txt) {
        String text = txt;
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        return text.replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * Retorna o texto fornecido substituindo os caracteres constantes na
     * sequência fromChars pelos caracteres posicionados no mesmo lugar da
     * sequência toChars.
     *
     * @param from
     *            texto a ser convertido
     * @param fromChars
     *            sequência de caracteres que será traduzida pelos equivalentes
     *            da segunda sequência
     * @param toChars
     *            sequência de caracteres que substituirá os equivalentes da
     *            primeira sequência
     * @return o texto from, substituídos os caracteres iguais ao da sequência
     *         fromChars pelos equivalentes da sequência toChars
     * @throws IllegalArgumentsException
     *             se as listas de caracteres fromChars e toChars tiverem
     *             tamanho diverso.
     */
    public static String translate(String from, char[] fromChars, char[] toChars) {
        if (fromChars.length != toChars.length) {
            throw new IllegalArgumentException(
                    "As listas de caracteres de origem e de destino têm que ter a mesma quantidade de caracteres (["
                            + fromChars.length + ", " + toChars.length + "]).");
        }
        String ret = from;
        for (int i = 0; i < fromChars.length; i++) {
            ret = ret.replace(fromChars[i], toChars[i]);
        }
        return ret;
    }

    /**
     * Suprime todos os caracteres vazios à esquerda do texto de origem.
     *
     * @param source
     *            o texto do qual serão suprimidos os caracteres vazios à
     *            esquerda
     * @return o texto txt, suprimidos os caracteres vazios à esquerda
     */
    public static String trimLeft(String source) {
        return source.replaceAll("^\\s+", "");
    }

    /**
     * Suprime todos os caracteres vazios à direita do texto de origem.
     *
     * @param source
     *            o texto do qual serão suprimidos os caracteres vazios à
     *            direita
     * @return o texto source, suprimidos os caracteres vazios à direita
     */
    public static String trimRight(String source) {
        return source.replaceAll("\\s+$", "");
    }

    /**
     * Substitui todos os espaços múltiplos existentes no interior de um texto
     * por um único espaço.
     *
     * @param source
     *            o texto do qual serão suprimidos os caracteres vazios
     *            duplicados
     * @return o texto source, suprimidos os caracteres vazios duplicados
     */
    public static String trimInside(String source) {
        return source.replaceAll("\\b\\s{2,}\\b", " ");
    }

    /**
     * Substitui todos os espaços vazios à direita e à esquerda de um texto
     * dado, assim como aqueles duplicados existentes em seu interior.
     *
     * @param source
     *            o texto do qual serão suprimidos os caracteres vazios
     *            supérfluos.
     * @return o texto source, suprimidos os caracteres vazios supérfluos
     */
    public static String fullTrim(String source) {
        return trimInside(trimBorders(source));
    }

    /**
     * Substitui todos os espaços vazios à direita e à esquerda de um texto
     * dado.
     *
     * @param source
     *            o texto do qual serão suprimidos os caracteres vazios à
     *            esquerda e à direita.
     * @return o texto source, suprimidos os caracteres vazios à esquerda e à
     *         direita.
     */
    public static String trimBorders(String source) {
        return trimLeft(trimRight(source));
    }

    /**
     * Dado um nome completo, retorna as iniciais de cada palavra
     * Ex.:
     * José Silva Barroso
     * J.S.B.
     * @param source
     * @return
     */
    public static String obtemIniciais(String source) {
        String trimmed = fullTrim(source);
        String[] tokens = trimmed.split(" ");
        StringBuilder sb = new StringBuilder(tokens.length * 3);
        boolean first = true;
        for (String t : tokens) {
            if (!first) {
                sb.append(" ");
            }
            sb.append(t.toUpperCase().charAt(0));
            sb.append(".");
            first = false;
        }
        return sb.toString();
    }

    public static boolean isSet(String str) {
        return (str != null && !PDPJStringUtils.fullTrim(str).isEmpty());
    }

    public static ArrayList<String> stringCollectionToSQL(Collection<String> array) {

        ArrayList<String> buffer = new ArrayList<String>(array.size());
        for (String str : array) {
            buffer.add(normalize(str).toUpperCase());
        }

        return buffer;

    }

    public static String changeChar(String text, char c1, String c2) {
        StringBuffer aux = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == c1) {
                aux.append(c2);
            } else {
                aux.append(c);
            }
        }
        return aux.toString();
    }

    public static String changeChars(String text, String oldChars, String newChars) {
        StringBuffer aux = new StringBuffer();
        char let;
        for (int i = 0; i < text.length(); i++) {
            let = text.charAt(i);
            int pos = oldChars.indexOf(let);
            if (pos == -1) {
                aux.append(let);
            } else if (newChars.length() > pos) {
                aux.append(newChars.charAt(pos));
            }
        }
        return aux.toString();
    }

    public static String piece(String text, String delim, int p) {
        return piece(text, delim, p, p);
    }

    public static String piece(String text, String delim, int p1, int p2) {
        if ((text == null) || (text.length() == 0)) {
            return "";
        }
        if ((delim == null) || (delim.length() == 0)) {
            return text;
        }
        if (p1 < 1) {
            p1 = 1;
        }
        if (p2 < 0) {
            p2 = 0;
        }
        if ((p2 != 0) && (p2 < p1)) {
            return "";
        }
        int piece = 1;
        int ini = 0;
        int pos = 0;
        int fim = text.length();
        pos = text.indexOf(delim);
        while (((piece <= p2) || (p2 == 0)) && (pos > -1)) {
            if ((p2 > 0) && (piece == p2)) {
                fim = pos;
            }
            piece++;
            if (piece == p1) {
                ini = pos + delim.length();
            }
            pos = text.indexOf(delim, pos + delim.length());
        }
        if (piece < p1) {
            return "";
        }
        return text.substring(ini, fim);
    }

    public static String replace(String subject, String find, String replace) {
        StringBuffer buf = new StringBuffer();
        int lengthSubject = find.length();
        int posAux = 0;
        int posFind = subject.indexOf(find);

        while (posFind != -1) {
            buf.append(subject.substring(posAux, posFind));
            buf.append(replace);
            posAux = posFind + lengthSubject;
            posFind = subject.indexOf(find, posAux);
        }

        buf.append(subject.substring(posAux));
        return buf.toString();
    }

    /**
     * Elimina acentuação do texto
     *
     * @param text
     * @return o texto sem os caracteres acentuados
     */
    public static String getUsAscii(String text) {
        return normalize(text);
    }

    public static String retiraZerosEsquerda(String numero) {
        if (isEmpty(numero) || !numero.startsWith("0")) {
            return numero;
        }
        char[] charArray = numero.toCharArray();
        int posUltimoZero = 0;
        for (char c : charArray) {
            if (c != '0') {
                break;
            }
            posUltimoZero++;
        }
        return numero.substring(posUltimoZero);
    }

    public static String limparCharsNaoNumericos(String s) {
        return s.replaceAll("[^0-9]", "");
    }

    public static String replaceQuebraLinha(String texto) {
        if (isEmpty(texto)) {
            return texto;
        } else {
            String saida = texto.replace("\\015", "");
            saida = saida.replace("\\012", "");
            saida = saida.replace("\n", "");
            saida = saida.replace("\r", "");
            return saida;
        }
    }

    public static String removeNaoNumericos(String source) {
        if (source != null) {
            source = source.replaceAll("\\D", "");
        }
        return source;
    }

    /**
     * Remove os caracteres numéricos.
     *
     * @param source
     * @return
     */
    public static String removeNaoAlphaNumericos(String source) {
        return source.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static String capitalizeAllWords(String words) {
        StringBuilder out = new StringBuilder();
        if (!isEmpty(words)) {
            String[] wordList = words.trim().split(" ");
            for (int i = 0; i < wordList.length; i++) {
                if (wordList[i].length() > 1) {
                    wordList[i] = wordList[i].substring(0, 1).toUpperCase() + wordList[i].substring(1).toLowerCase();
                } else {
                    wordList[i] = wordList[i].toUpperCase();
                }
                out.append(wordList[i]);
                out.append(" ");
            }
        }
        return out.toString().trim();
    }

    public static String completaZeros(Integer numero, int tamanho){
        return completaZeros(numero != null ? numero.toString() : "0", tamanho);
    }

    public static String completaZeros(String numero, int tamanho) {
        StringBuilder sb = new StringBuilder();
        int zerosAdicionar = tamanho - numero.length();
        while (sb.length() < zerosAdicionar) {
            sb.append('0');
        }
        sb.append(numero);
        return sb.toString();
    }

    public static String formatNumericString(String string, String mask) throws java.text.ParseException {
        javax.swing.text.MaskFormatter mf = new javax.swing.text.MaskFormatter(mask);
        mf.setValueContainsLiteralCharacters(false);
        return mf.valueToString(string);
    }

    public static String formartCpf(String cpf) {
        try {
            return formatNumericString(removeNaoNumericos(cpf), "###.###.###-##");
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatCnpj(String cnpj) {
        try {
            return formatNumericString(removeNaoNumericos(cnpj), "##.###.###/####-##");
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Método responsável por formatar o CEP no estilo 99999-999
     *
     * @param cep
     *            o número que se deseja mascarar no formato do CEP
     *
     * @return <code>String</code>, o número formatado
     */
    public static String formatCep(String cep) {
        try {
            return formatNumericString(removeNaoNumericos(cep), "#####-###");
        } catch (ParseException e) {
            return null;
        }
    }

    public static String concatList(Collection<Object> list, String delimitador) {
        StringBuilder sb = new StringBuilder();
        for (Object object : list) {
            if (sb.length() > 0) {
                sb.append(delimitador);
            }
            sb.append(object);
        }
        return sb.toString();
    }

    /**
     * Monta uma String com os conteúdos separados por um delimitador e o último
     * item da lista separado pelo ultimo conectivo.
     *
     * ex: com delimitador sendo ", " e ultimoConectivo sendo " e "
     *
     * para uma lista de String com três nomes retorna: Sandra, Regina e Rodrigo
     *
     * para uma lista de String com dois nomes retorna: Sandra e Regina
     *
     * para uma lista de String com um nome retorna: Sandra
     *
     * @param list Lista com items a serem concatenados.
     *
     * @param delimitador Caractere separador dos itens usando quando a lista
     * possuir mais de dois itens
     *
     * @param ultimoConectivo Último conectivo usado quando a lista possuir mais de um item
     *
     * @return String com a representação em string da lista separados pelos
     * delimitador e ultimoConectivo
     */
    public static String concatList(List<? extends Object> list,
                                    String delimitador, String ultimoConectivo) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                // existe mais de um, adiciona delimitador ou conectivo
                if (i == (list.size()-1)) {
                    // é o último item da lista, adiciona último conectivo
                    sb.append(ultimoConectivo);
                } else {
                    // é o último item da lista, adiciona delimitador
                    sb.append(delimitador);
                }
            }
            sb.append(list.get(i));
        }

        return sb.toString();
    }

    public static String toLowerCaseFirstChar(String string) {
        if (string == null || string.length() == 0) {
            return string;
        } else {
            char[] charArray = string.toCharArray();
            charArray[0] = Character.toLowerCase(charArray[0]);
            return String.valueOf(charArray);
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    /**
     * Converte caracteres especiais de UTF-8 para ISO-8859-1.
     *
     * @param value String que será convertida.
     * @return String ISO-8859-1 ou a string original se houver erro.
     * @author Adriano Schmidt
     * @author Adriano Pamplona
     */
    public static String convertUtf8ToIso88591(String value) {
        return convertUtf8ToIso88591(value, true);
    }

    /**
     * Converte caracteres especiais de UTF-8 para ISO-8859-1.
     *
     * @param value String que será convertida.
     * @param returnDefaultIfError Booleano que retorna o valor original se houver erro na conversão.
     * @return String ISO-8859-1 ou a string original se houver erro.
     * @author Adriano Schmidt
     * @author Adriano Pamplona
     */
    public static String convertUtf8ToIso88591(String value, boolean returnDefaultIfError) {
        String convertedString = null;
        if (value != null) {
            Charset utf8charset = Charset.forName("UTF-8");
            Charset iso88591charset = Charset.forName("ISO-8859-1");
            ByteBuffer inputBuffer = ByteBuffer.wrap(value.getBytes());
            // decode UTF-8
            CharBuffer data = utf8charset.decode(inputBuffer);
            // encode ISO-8559-1
            ByteBuffer outputBuffer = iso88591charset.encode(data);
            byte[] outputData = outputBuffer.array();
            convertedString = new String(outputData);

            // se a conversao deu errado retorna o valor original
            if (returnDefaultIfError && convertedString.contains("?")) {
                convertedString = value;
            }
        }

        return convertedString;
    }

    public static boolean isEmpty(String txt){
        return txt == null || txt.isEmpty();
    }

    public static boolean isNotEmpty(String txt){
        return !isEmpty(txt);
    }

    public static boolean ehInteiro(String s) {
        for (int i = 0; i < s.length(); i++) {
            // verifica se o char não é um dígito
            if ( !Character.isDigit( s.charAt(i) ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Recupera o tamanho, com aparência de leitura humana, de uma quantidade de bytes.
     *
     * @param bytes a quantidade de bytes
     * @param si marca indicativa de que se pretende calcular o tamanho
     * utilizando 1000 como divisor, ao invés de 1024
     * @return o tamanho do arquivo em formato humano, com a unidade pertinente
     */
    public static String tamanhoBytes(long bytes, boolean si){
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Limpa os espacos a mais entre as strings
     * @param valor Conteudo
     * @return Conteudo separado por apenas um caracter de espaco
     */
    public static String limparCaracteresEntreStrings(String valor){
        return valor.replaceAll("\\s+", " ");
    }

    /**
     * Método responsável em receber uma string para que seja aplicado um split e os valores sejam
     * adicionados a uma lista de integer.
     *
     * @param string Valores separados pelo separador.
     * @param separador Separador.
     * @return List<Integer>
     */
    public static List<Integer> converterParaListaInteiro(String string, String separador) {
        List<Integer> resultado = new ArrayList<Integer>();

        String[] array = StringUtils.split(string, separador);
        if (ArrayUtils.isNotEmpty(array)) {
            for (String item : array) {
                item = StringUtils.trim(item);
                resultado.add(Integer.parseInt(item));
            }
        }
        return resultado;
    }

    /**
     * Este metodo recebe um StringBuilder e adiciona um caractere separador(parametro), caso a string nao esteja vazia.
     * @param sb
     * @param separador
     * @return sb
     */
    public static StringBuilder adicionarSeparador(StringBuilder sb,
                                                   String separador) {
        if (sb != null && !isEmpty(sb)) {
            sb.append(separador);
        }
        return sb;
    }

    /**
     * Este metodo recebe um StringBuilder e adiciona um hifen, caso a string nao esteja vazia.
     * @param sb
     * @return sb
     */
    public static StringBuilder adicionarHifen(StringBuilder sb) {
        return adicionarSeparador(sb, " - ");
    }

    /**
     * Retorna 'true' se a StringBuilder passada como parametro estiver vazia.
     * @param sb
     * @return isEmpty (boolen)
     */
    public static boolean isEmpty(StringBuilder sb) {
        return sb == null || sb.toString().trim().isEmpty();
    }

    /**
     * Verifica se e nulo ou vazio
     *
     * @param txt
     * @return
     */
    public static boolean isNullOrEmpty(String txt) {
        return isEmpty(txt);
    }

    public static String join(String[] itens, String delimiter) {
        return join(Arrays.asList(itens), delimiter);
    }

    public static String join(Collection<String> itens, String delimiter) {

        if (itens == null || itens.isEmpty()) return "";

        Iterator<String> iter = itens.iterator();

        StringBuilder builder = new StringBuilder(iter.next());

        while(iter.hasNext()) {
            builder.append(delimiter).append(iter.next());
        }

        return builder.toString();
    }

    /**
     * Método responsável por retornar uma <code>String</code> de acordo com o
     * valor booleano do primeiro parâmetro.
     *
     * @param bool
     *            parâmetro que se deseja obter a mensagem
     * @param msgTrue
     *            mensagem para o caso do primeiro parâmetro ser verdadeiro
     * @param msgFalse
     *            mensagem para o caso do primeiro parâmetro ser falso <i>ou
     *            nulo<i/>
     * @return <code>String</code>, de acordo com o valor passado no parâmetro bool.
     */
    public static String booleanToString(Boolean bool, String msgTrue, String msgFalse) {
        if (bool == null) {
            return msgFalse;
        }
        return bool ? msgTrue : msgFalse;
    }

    /**
     * Método responsável por transformar uma lista de objetos em uma string.
     *
     * @param c Lista de objetos.
     * @return Lista de objetos no formato string.
     */
    public static String listToString(Collection<?> c) {
        return c.toString().replaceAll("\\[", "").replaceAll("\\]", "");
    }

    /**
     * Método responsável por formatar o valor especificado para o padrão moeda.
     *
     * @param valor Valor a ser formatado.
     * @param incluirSimboloMonetario Informa se o sistema deve incluir o símbolo monetário no valor formatado a ser retornado.
     * @return Valor formatado para o padrão moeda.
     */
    public static String formatarValorMoeda(Double valor, boolean incluirSimboloMonetario) {
        if (valor != null) {
            String mascaraFormatacao = (incluirSimboloMonetario ? "R$ " : StringUtils.EMPTY) + "###,###,###,##0.00";
            DecimalFormatSymbols formatador = new DecimalFormatSymbols(new Locale("pt", "BR"));
            DecimalFormat valorFormatado = new DecimalFormat(mascaraFormatacao, formatador);
            return valorFormatado.format(valor);
        }
        return StringUtils.EMPTY;
    }

    /**
     * Método responsável por transformar o texto em UTF-8
     *
     * @param bytes
     *            bytes da String
     * @param encoding
     *            encoding original do texto em formato {@link String}
     * @return nova {@link String} convertida para UTF-8
     */
    public static String transformarParaUTF8(byte[] bytes, String encoding) {
        String resultado = "";
        if (bytes != null && bytes.length > 0) {
            resultado = new String(bytes);
            if (!encoding.equalsIgnoreCase(StandardCharsets.UTF_8.displayName()) && !encoding.equalsIgnoreCase(StandardCharsets.ISO_8859_1.displayName())) {
                resultado = new String(bytes, StandardCharsets.UTF_8);
            }
        }
        return resultado;
    }
    /**
     * Método que retira da tag table os atributos style, dir, height, width, class que geram problema de formatação ao ser convertido para PDF.
     *
     * @param String html HTML que deve ser tratado.
     * @return String HTML sem os atributos style, dir, height, width, class tag table.
     */
    public static String retiraTagFormatacaoInvalidaHTMLParaPDF(String html) {
        return html.replaceAll( "(?=<table)*((?:style|dir|height|width|class)\\s*=\\s*\"[^\"]*\"\\s*)(?=.*>)" , " ");
    }

    public static String appendBefore(String original, String inner, String token) {
        int index = original.lastIndexOf(token);
        String first = original.substring(0, index);
        String last = original.substring(index);
        return first + inner + last;
    }

    public static String appendAfter(String original, String inner, String token) {
        int index = original.lastIndexOf(token)+ token.length();
        String first = original.substring(0, index);
        String last = original.substring(index);
        return first + inner + last;
    }

    /**
     * Método responsável por retirar os acentos
     * @param string
     * @return
     */
    public static String substituiCaracteresAcentuados(String string) {

        String[][] caracteresAcento = {
                {"Á", "A"}, {"á", "a"},
                {"É", "E"}, {"é", "e"},
                {"Í", "I"}, {"í", "i"},
                {"Ó", "O"}, {"ó", "o"},
                {"Ú", "U"}, {"ú", "u"},
                {"À", "A"}, {"à", "a"},
                {"È", "E"}, {"è", "e"},
                {"Ì", "I"}, {"ì", "i"},
                {"Ò", "O"}, {"ò", "o"},
                {"Ù", "U"}, {"ù", "u"},
                {"Â", "A"}, {"â", "a"},
                {"Ê", "E"}, {"ê", "e"},
                {"Î", "I"}, {"î", "i"},
                {"Ô", "O"}, {"ô", "o"},
                {"Û", "U"}, {"û", "u"},
                {"Ä", "A"}, {"ä", "a"},
                {"Ë", "E"}, {"ë", "e"},
                {"Ï", "I"}, {"ï", "i"},
                {"Ö", "O"}, {"ö", "o"},
                {"Ü", "U"}, {"ü", "u"},
                {"Ã", "A"}, {"ã", "a"},
                {"Õ", "O"}, {"õ", "o"},
                {"Ç", "C"}, {"ç", "c"},
        };

        for (int i = 0; i < caracteresAcento.length; i++) {
            string = string.replaceAll(caracteresAcento[i][0], caracteresAcento[i][1]);
        }

        return string;
    }

    /**
     * Retorna a representação em String do objeto passado por parâmetro.
     *
     * @param objeto Objeto que será convertido.
     * @return String
     */
    public static String toString(Object objeto) {
        return (objeto != null ? objeto.toString() : null);
    }

    public static boolean isUUIDvalid(String uuid) {
        String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        return StringUtils.isNotEmpty(uuid) && uuid.matches(regex);
    }

}