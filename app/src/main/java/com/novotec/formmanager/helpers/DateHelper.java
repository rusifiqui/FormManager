package com.novotec.formmanager.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jvilam on 01/04/2016.
 * Clase de ayuda para fechas.
 * @author jvilam
 * @version 1
 * @since 01/04/2016
 */
public class DateHelper {
    public static String APP_CREATION_DATE = "01/04/2016";

    public static String getSystemDateAsString(){
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar newDate = Calendar.getInstance();
        return dateFormatter.format(newDate.getTime());
    }

    public static Date getDate(String d){
        int day;
        int month;
        int year;
        day = Integer.valueOf(d.substring(0, d.indexOf("/")));
        month = Integer.valueOf(d.substring(d.indexOf("/")+1, d.lastIndexOf("/"))) +1;
        year = Integer.valueOf(d.substring(d.lastIndexOf("/")+1, d.length()));
        Calendar cal = Calendar.getInstance();
        cal.set(year, month,day);
        return cal.getTime();
    }
}
