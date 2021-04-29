package com.unipi.p17134.medicalcard.Custom;

import com.unipi.p17134.medicalcard.API.BaseDAO;

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

    public static String dateToDayString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(date);
    }

    public static String dateToMonthString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
        return dateFormat.format(date);
    }

    public static String dateToAPIDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(BaseDAO.APPOINTMENT_TIME_FORMAT);
        return dateFormat.format(date);
    }
}
