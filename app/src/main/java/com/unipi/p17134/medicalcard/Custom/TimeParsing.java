package com.unipi.p17134.medicalcard.Custom;

import java.text.DateFormat;
import java.util.Date;

public class TimeParsing {
    public static String dateToShort(Date date) {
        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        return formatter.format(date);
    }
}
