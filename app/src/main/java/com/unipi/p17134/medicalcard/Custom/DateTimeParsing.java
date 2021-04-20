package com.unipi.p17134.medicalcard.Custom;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeParsing {
    public static String currentDate() {
        Date date = Calendar.getInstance().getTime();
        return dateToDateString(date);
    }

    public static String dateToTimeString(Date date) {
        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        return formatter.format(date);
    }
    
    public static String dateToDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }
}
