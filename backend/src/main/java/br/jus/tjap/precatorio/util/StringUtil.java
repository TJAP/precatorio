package br.jus.tjap.precatorio.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static final int DEFAULT_BUFFER_SIZE = 8192;

    public static String formataDataDMY(LocalDate data){
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String escapeHtml(String str){
        str = str.replaceAll("\\<[^>]*>","");
        return StringEscapeUtils.unescapeHtml4(str);
    }
    public static String retornaSeCpfOuCnpj(String documento) {
        if (documento == null) {
            return "INVÁLIDO";
        }

        // remove tudo que não é número
        String numeros = documento.replaceAll("\\D", "");

        if (numeros.length() == 11) {
            return "CPF";
        } else if (numeros.length() == 14) {
            return "CNPJ";
        } else {
            return "INVÁLIDO";
        }
    }
    public static String formataNumeroProcesso(String numeroProc){
        if(Objects.isNull(numeroProc)){
            return "Sem numero";
        }
        numeroProc = removerFormatacaoNumeroDocumento(numeroProc);
        String numero = numeroProc.substring(0,7);
        String digito = numeroProc.substring(7,9);
        String ano = numeroProc.substring(9,13);
        String outro = "."+ numeroProc.substring(13,14) + "." +numeroProc.substring(14,16) + ".";
        String comarca = numeroProc.substring(16,20);
        return numero + '-' + digito + '.' + ano + outro + comarca;
    }

    public static String join(Collection s, String delimiter) {
        StringBuilder sb = new StringBuilder();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
            if (iter.hasNext()) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String pegarNomeSobrenome(String nome) {
        return nome.split(" ")[0]+" "+nome.split(" ")[nome.split(" ").length - 1];
    }

    public static List<String> split(String s, String separator) {
        return Arrays.asList(StringUtils.split(s, separator));
    }

    public static boolean regexpMatch(String str, String strPattern) {
        Pattern pattern = Pattern.compile(strPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    public static boolean nullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static String leftPad(String str, String preenchimento, int tamanho) {
        return StringUtils.leftPad(str, tamanho, preenchimento);
    }

    public static String removeAcentos(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = str.replaceAll("[^\\p{ASCII}]", "");
        return str;
    }

    public static String removerFormatacaoNumeroDocumento(String str){
        return removeAcentos(str).trim().replaceAll("[^0-9]+", "");
    }

    public static String formatInt(long num) {
        String strNum = String.valueOf(num);
        Integer intNum = Integer.valueOf(strNum);
        return formatInt(intNum);
    }

    public static String formatInt(int num) {
        return String.format(new Locale("pt", "BR"), "%,d", num);
    }

    public static String gerarNumeroTitulo(){
        String[] numeros = {"0","1","2","3","4","5","6","7","8","9"};
        String retorno = "";
        Random gerador = new Random();
        for ( int i = 0; i < 12; i++){
            int a = gerador.nextInt(numeros.length);
            retorno += numeros[a];
        }
        return retorno;
    }

    public static String gerarNumeroMatricula(){
        String[] numeros = {"0","1","2","3","4","5","6","7","8","9"};
        String retorno = "";
        Random gerador = new Random();
        for ( int i = 0; i < 6; i++){
            int a = gerador.nextInt(numeros.length);
            retorno += numeros[a];
        }
        return "90"+retorno;
    }

    private static String removeQuotesFromFontFamily(String input) {
        Pattern pattern = Pattern.compile("font-family:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(input);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, "font-family:" + matcher.group(1).replaceAll("\"", ""));
        }
        matcher.appendTail(result);

        return result.toString();
    }
    public static void main(String[] args) {
        var cpf = "123.456.789-095454AKJHGSDJkjhgjhg";


        System.out.println(removerFormatacaoNumeroDocumento(cpf));
    }

    public static boolean isStringIguaisSemAcento(String str1, String str2){
        return removeAcentos(str1).equalsIgnoreCase(removeAcentos(str2));
    }

    public static String uncapitalize(String str) {
        return StringUtils.uncapitalize(str);
    }

    public static String removeRepetidos(String stringOriginal, String separador) {
        if (StringUtil.nullOrEmpty(stringOriginal)) {
            return stringOriginal;
        }
        List<String> listaDeStrings = split(stringOriginal, separador);
        List<String> novaLista = new ArrayList<String>();
        for (String str : listaDeStrings) {
            if (!nullOrEmpty(str)) {
                novaLista.add(str.trim().toLowerCase());
            }
        }
        List<String> listaCopia2 = new ArrayList<String>(novaLista);
        for (String str2 : novaLista) {
            while (listaCopia2.indexOf(str2) != listaCopia2.lastIndexOf(str2)) {
                listaCopia2.remove(str2);
            }
        }
        String retorno = StringUtil.join(listaCopia2, separador);
        return retorno;
    }

    public static String getTextoTruncado(String texto, int tamanhoMaximoDaString) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= tamanhoMaximoDaString) {
            return texto;
        } else {
            return texto.substring(0, tamanhoMaximoDaString) + "(...)";
        }
    }

    public static String plainTextToHtml(String s) {
        StringBuilder builder = new StringBuilder();
        boolean previousWasASpace = false;
        for (char c : s.toCharArray()) {
            if (c == ' ') {
                if (previousWasASpace) {
                    builder.append("&nbsp;");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }
            switch (c) {
                case '<':
                    builder.append("&lt;");
                    break;
                case '>':
                    builder.append("&gt;");
                    break;
                case '&':
                    builder.append("&amp;");
                    break;
                case '"':
                    builder.append("&quot;");
                    break;
                case '\n':
                    builder.append("<br>");
                    break;
                // We need Tab support here, because we print StackTraces as HTML
                case '\t':
                    builder.append("&nbsp; &nbsp; &nbsp;");
                    break;
                default:
                    if (c < 128) {
                        builder.append(c);
                    } else {
                        builder.append("&#").append((int) c).append(";");
                    }
            }
        }
        return builder.toString();
    }

    public static String formatDecimalMoeda(BigDecimal v){
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        return nf.format(v);
    }

    public static String retiraEspacoAcento(String texto) {
        return StringUtil.removeAcentos(texto).replaceAll(" ", "-").replaceAll("_", "-");
    }

    public static String formatarValorMoeda(BigDecimal valor) {
        var incluirSimboloMonetario = true;
        if (valor != null) {
            String mascaraFormatacao = (incluirSimboloMonetario ? "R$ " : StringUtils.EMPTY) + "###,###,###,##0.00";
            DecimalFormatSymbols formatador = new DecimalFormatSymbols(new Locale("pt", "BR"));
            DecimalFormat valorFormatado = new DecimalFormat(mascaraFormatacao, formatador);
            return valorFormatado.format(valor);
        }
        return StringUtils.EMPTY;
    }

    public static String convertInputStreamToString(InputStream is) {

        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return new String(result.toByteArray(), StandardCharsets.UTF_8);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty() || str.equalsIgnoreCase("null");
    }

    public static boolean isExsitePalavraNaString(String str, String palavra) {
        return str.contains(palavra);
    }

    public static String transformarParaHtmlUtf8(String html) {

        if (html == null || html.trim().isEmpty()) {
            return "<html><body><h1>Conteúdo vazio</h1></body></html>";
        }

        if(!isExsitePalavraNaString(html, "<html>")){
            html = "<html><body>" + html + "</body></html>";
        }

        Document doc = Jsoup.parse(html);

        doc.outputSettings().syntax(Document.OutputSettings.Syntax.html);

        // Cria Safelist permitindo <img> com qualquer src (inclusive data:)
        Safelist safelist = Safelist.basicWithImages();
        safelist.addProtocols("img", "src", "data");  // <- Isso é o truque

        /*
        doc.outputSettings()
                .escapeMode(Entities.EscapeMode.xhtml)
                .charset("UTF-8");
        String normalizedHtml = doc.html();*/

        return Jsoup.clean(doc.html(), safelist);

        //return normalizedHtml; //replaceString(normalizedHtml);
    }

    private static String replaceString(String str){
        /*return str.replace("\"Helvetica Neue\"","")
                .replace("\"Arial\"","")
                .replace("\"Segoe UI\"","")
                .replace("\"Noto Sans\"","")
                .replace("\"Droid Sans\"","")
                .replace("\"Times New Roman\"","Times New Roman")
                .replaceAll("<!--.*?-->", "");*/
        return removeQuotesFromFontFamily(
                str.replaceAll("<!--.*?-->", "")
                        .replaceAll("href=\"(.*?)\"", "href='#'")
                        .replace(",\"serif\"", ",serif")
                        .replace(", \"serif\"", ", serif")
                        .replace(",\"sans-serif\";", ", sans-serif")
                        .replace(", \"sans-serif\";", ", sans-serif")
                        .replace("alt=\"\">", "alt=\"\"></img>")
                        .replace("<br>", "</br>")
                        .replace("& ", "§ ")
                        .replace("\"Liberation Serif_EmbeddedFont\"", "Liberation Serif_EmbeddedFont")
                        .replace("\"Liberation Serif_MSFontService\"", "Liberation Serif_MSFontService")
                        .replace("\"Segoe UI\"", "Segoe UI")
                        .replace("\"Segoe UI Web\"", "Segoe UI Web")
                        .replace("\"Noto Sans\"", "Noto Sans")
                        .replace("\"Droid Sans\"", "Droid Sans")
                        .replace("\"Helvetica Neue\"", "Helvetica Neue")
                        .replace("\"Times New Roman_EmbeddedFont\"", "Times New Roman_EmbeddedFont")
                        .replace("\"Times New Roman_MSFontService\"", "Times New Roman_MSFontService")
                        .replace("\"Helvetica Neue_EmbeddedFont\"", "Helvetica Neue_EmbeddedFont")
                        .replace("\"Helvetica Neue_MSFontService\"", "Helvetica Neue_MSFontService")
                        .replace("\"Times New Roman\"","Times New Roman")
        );
    }

    public static boolean contemMensagemAssinaturaInvalida(String mensagem) {
        if (mensagem == null) {
            return false;
        }

        var mensagemLower = mensagem.toLowerCase();

        return mensagemLower.contains("sem assinatura, não é possível juntar documento sem assinatura".toLowerCase())
                || mensagemLower.contains("Não foi possível recuperar a assinatura do documento".toLowerCase());
    }
}
