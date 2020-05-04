package com.example.photogalleryapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateParser {
    private static String myFormat = "MM/dd/yyyy"; //In which you need put here
    private static SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);

    public static String parseDate(Date date) {
        return sdf.format(date.getTime());
    }
}
