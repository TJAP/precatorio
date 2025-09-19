package br.jus.tjap.precatorio.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.joda.time.*;
import java.time.Period;

public class DateUtil {

    static final long ONE_HOUR = 60 * 60 * 1000L;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    static final Locale BRAZIL = new Locale("pt","BR");

    public static String getDataHojeFormatada(){
        return DateFormat.getDateInstance(DateFormat.LONG,BRAZIL).format(new Date());
    }

    public static long daysBetween(Date ini, Date fim) {
        return ((fim.getTime() - ini.getTime() + ONE_HOUR) / (ONE_HOUR * 24));
    }

    public static boolean estaEntreADataAtual(Date ini, Date fim) {
        Date hoje = new Date();
        return (hoje.compareTo(ini) > 0 && hoje.compareTo(fim) < 0);
    }

    public static Date newDate(String data) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(data);
        } catch (ParseException ex) {
            throw new RuntimeException("Erro ao criar a data.", ex);
        }
    }

    public static Date addDiasAData(Date d, Integer i) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, +(i - 1));
        return c.getTime();
    }

    public static String addDiasDataFormatada(Date d, Integer i) {
        return dataFormatada(addDiasAData(d, i), "dd/MM/yyyy");
    }

    public static Date dataFormatada(String dataHorayyyyMMdd_HHmmss) {
        return dataFormatadaStringToDate(dataHorayyyyMMdd_HHmmss);
    }

    public static String dataHoraFormatada(Date d) {
        return dataFormatada(d, "dd/MM/yyyy HH:mm");
    }

    public static String dataFormatada(Date d) {
        return dataFormatada(d, "dd/MM/yyyy");
    }

    public static String dataFormatadaAnoComDoisDigitos(Date d) {
        return dataFormatada(d, "dd/MM/yy");
    }

    public static String dataFormatada(Date d, String formato) {
        return new SimpleDateFormat(formato).format(d);
    }

    public static String localDataFormatada(java.time.LocalDate d, String formato) {
        //return "";
        if(d == null){return "";}
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(formato);
        return d.format(formatters);
    }

    public static Date dataFormatadaStringToDate(String dataString) {
        Date data = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
        try {
            data = (Date) format.parse(dataString);
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    public static String mesAnoSemSeparador(Date d) {
        return dataFormatada(d, "MMyyyy");
    }

    public static String getTextoStatusFerias(Date ini, Date fim) {
        Date hoje = new Date();
        String txt = "Nem uma.";
        if (hoje.compareTo(fim) > 0) {
            txt = "Férias gozadas!";
        } else if (hoje.compareTo(ini) > 0 && hoje.compareTo(fim) < 0) {
            txt = "Gozando férias...";
        } else if (hoje.compareTo(ini) < 0) {
            txt = "Aguardando férias...";
        }
        return txt;
    }

    public static void main(String[] args) {
        System.out.println("Status: " + getMes(new Date()));
    }

    public static int getDiasUteisPeriodo(Date initialDate, Date finalDate, Integer totalFeriado) {
        int totalDiasUteis = 0;
        int totalDias = getTotalDiasPeriodo(initialDate, finalDate) - 1;
        Calendar calendar = new GregorianCalendar();
        //Setando o calendar com a Data Inicial
        calendar.setTime(initialDate);
        for (int i = 0; i <= totalDias; i++) {
            if (!(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) && !(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
                totalDiasUteis++;
            }
            calendar.add(Calendar.DATE, 1);
        }
        return totalDiasUteis - totalFeriado;
    }

    public static int getTotalDiasPeriodo(Date initialDate, Date finalDate) {
        if (initialDate == null || finalDate == null) {
            return 0;
        }
        int days = (int) ((finalDate.getTime() - initialDate.getTime()) / (24 * 60 * 60 * 1000));
        return (days > 0 ? days + 1 : 0);
    }

    public static String getConverteMinutoToHoraString(int minutos) {
        int hora = minutos / 60;
        int minuto = minutos % 60;
        return String.format("%d:%02d", hora, Math.abs(minuto));
    }

    public static Date getConverteMinutoToHora(int minutos) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d = sdf.parse(getConverteMinutoToHoraString(minutos));
        } catch (ParseException ex) {
            Logger.getLogger(DateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return d;
    }

    public static Integer getConverteHorasToMinutos(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        Integer h = cal.get(Calendar.HOUR_OF_DAY);
        Integer m = cal.get(Calendar.MINUTE);
        return (h * 60 + m);
    }

    public static String getDiaSemana(Date data) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(data);
        String[] diaSemana = new String[]{"Domingo", "Segunda",
                "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};
        return diaSemana[calendar.get(GregorianCalendar.DAY_OF_WEEK) - 1];
    }

    public static Integer getAno(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.YEAR);
    }

    public static Integer getMes(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal.get(Calendar.MONTH)+1;
    }

    public static Integer getAnoMenosUm() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return (cal.get(Calendar.YEAR)-1);
    }

    public static Integer getAnoAtual() {
        return getAno(new Date());
    }

    public static Integer getMesAtual() {
        return getMes(new Date());
    }



    public static String getAnoAtualString() {
        return getAno(new Date()).toString();
    }

    /**
     * Pega uma string no formato "HH:MM" e retorno um array de inteiros com a
     * hora na posição 0 e os minutos na posição 1.
     *
     * @param h String de hora no formato "HH:MM"
     * @return array de inteiros com a hora e os minutos
     */
    public static int[] getHoraMinuto(String h) {
        int retorno[] = {Integer.parseInt(StringUtil.split(h, ":").get(0)), Integer.parseInt(StringUtil.split(h, ":").get(1))};
        return retorno;

    }

    public static long getTotalMesesPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        Period periodo = Period.between(dataInicial, dataFinal);
        return periodo.toTotalMonths();
    }

    public static String formatarData(LocalDate data) {
        if (data == null) {
            return null;
        }
        return data.format(FORMATTER);
    }

    public static LocalDate parseStringParaLocalDate(String data) {
        if (data == null || data.isBlank()) {
            return null; // ou lançar uma exceção, se preferir
        }

        try {
            return LocalDate.parse(data, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida: " + data, e);
        }
    }

}
