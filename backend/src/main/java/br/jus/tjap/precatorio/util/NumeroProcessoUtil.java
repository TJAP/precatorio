package br.jus.tjap.precatorio.util;

import br.jus.tjap.precatorio.util.enums.TribunalEnum;
import org.apache.commons.lang3.StringUtils;
import javax.swing.text.MaskFormatter;

import java.math.BigInteger;
import java.text.ParseException;

public class NumeroProcessoUtil {
    public static boolean numeroProcessoValido(String numeroProcesso){
        return NumeroProcessoUtil.numeroProcessoValido(numeroProcesso, true);
    }

    public static boolean numeroProcessoValido(String numeroProcesso, boolean validarDigitoVerificador){
        boolean numeroProcessoValido = true;

        try{
            if(StringUtils.isEmpty(numeroProcesso)) {
                numeroProcessoValido = false;
            } else if(numeroProcesso.length() != 20 && numeroProcesso.length() != 25) {
                numeroProcessoValido =  false;
            } else	if(validarDigitoVerificador) {
                //tamanho do número de processo sem máscara: atribui a máscara ao número do processo para viabilizar o cálculo do DV
                if(numeroProcesso.length() == 20) {
                    numeroProcesso = mascararNumeroProcesso(numeroProcesso);
                }

                long numeroSequencia = Integer.parseInt(numeroProcesso.substring(0, 7));
                long numeroDigitoVerificador = Integer.parseInt(numeroProcesso.substring(8, 10));
                long ano = Integer.parseInt(numeroProcesso.substring(11, 15));
                long numeroVara = Integer.parseInt(numeroProcesso.substring(16, 17) + numeroProcesso.substring(18, 20));
                long numeroOrigemProcesso = Integer.parseInt(numeroProcesso.substring(21, 25));

                if(NumeroProcessoUtil.calcDigitoVerificador(numeroSequencia, ano, numeroVara, numeroOrigemProcesso) == numeroDigitoVerificador){
                    numeroProcessoValido = true;
                }
            } else {
                numeroProcessoValido = true;
            }

        }catch (Exception e){
            numeroProcessoValido = false;
        }
        return numeroProcessoValido;
    }

    /**
     * Retorna o número do processo no formato NNNNNNN-DD.AAAA.JTR.OOOO, ou seja, com o hífen e pontos.
     * @param numeroProcesso
     * @return
     */
    public static String mascararNumeroProcesso(String numeroProcesso){
        try {
            MaskFormatter mf = new MaskFormatter("#######-##.####.#.##.####");
            mf.setValueContainsLiteralCharacters(false);
            return mf.valueToString(numeroProcesso);
        }
        catch (ParseException e) {
            //Se deu algum erro na formatação, retorna o próprio número informado,
            //ou seja, ignora o erro (muito provavelmente o número do processo
            //já veio formatado como deveria)
            return numeroProcesso;
        }
    }

    public static String getJtr(String numeroProcesso) {
        if (StringUtils.isEmpty(numeroProcesso) || (numeroProcesso.length() != 20 && numeroProcesso.length() != 25) ){
            return null;
        } else {
            if(numeroProcesso.length() == 25) {
                numeroProcesso = retiraMascaraNumeroProcesso(numeroProcesso);
            }

            return numeroProcesso.substring(13, 16);
        }
    }

    public static String retiraMascaraNumeroProcesso(String numeroProcesso){
        return PDPJStringUtils.fullTrim(numeroProcesso.replaceAll("[_\\.\\-/]", ""));
    }

    /**
     * <li><b>NNNNNNN</b> = Número sequencial do processo no ano</li> <li>
     * <b>DD</b> = Dígito de verificação</li> <li><b>AAAA</b> = Ano</li> <li>
     * <b>JTR</b> = Identificação do rgmo da justiça</li> <li><b>OOOO</b> = Origem do processo</li> Para calcular os dígitos de verificação basta
     * aplicar a seguinte fórmula:<br/>
     * DD = 98 (NNNNNNN AAAA JTR OOOO 00 mod 97)<br/>
     * O resultado da fórmula deve ser formatado em dois dígitos, incluindo o zero  esquerda se necessário. Os dígitos resultantes são os dígitos de
     * verificação.
     *
     * @param numeroSequencia = número sequencial do processo no ano
     * @param ano = ano do processo
     * @param numeroVara = identifição do rgmo da justiça
     * @param numeroOrigemProcesso = origem do processo
     * @return Digito verificador
     */
    public static int calcDigitoVerificador(long numeroSequencia, long ano, long numeroVara, long numeroOrigemProcesso){
        String numeroCalc = completaZeros(numeroSequencia, 7) + completaZeros(ano, 4) + completaZeros(numeroVara, 3)
                + completaZeros(numeroOrigemProcesso, 4) + "00";
        BigInteger nro = new BigInteger(numeroCalc);
        long digito = 98 - (nro.mod(new BigInteger("97")).longValue());
        return (int) digito;
    }

    public static String jtrPorSiglaTribunal(String sigla) {
        String jtr = "";

        if(sigla != null) {
            jtr = TribunalEnum.findBySigla(sigla).getJtr();
        }

        return jtr;
    }

    public static String nomePorJTRTribunal(String JTR) {
        String nome = "";
        if(JTR != null) {
            nome = TribunalEnum.findByJTR(JTR).getNome();
        }

        return nome;
    }

    public static String siglaPorJTRTribunal(String JTR) {
        String sigla = "";
        if(JTR != null) {
            sigla = TribunalEnum.findByJTR(JTR).getSigla();
        }

        return sigla;
    }
    private static String completaZeros(long l, int tamanho){
        StringBuilder sb = new StringBuilder();
        String lSrt = Long.toString(l);
        for (int i = 0; i < tamanho - lSrt.length(); i++){
            sb.append('0');
        }
        sb.append(lSrt);
        return sb.toString();
    }

}
