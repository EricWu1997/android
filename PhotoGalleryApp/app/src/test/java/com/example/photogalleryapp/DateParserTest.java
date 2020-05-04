package com.example.photogalleryapp;

import com.example.photogalleryapp.util.DateParser;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateParserTest {
    private Date sample;

    public DateParserTest() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2019); // set the year
        cal.set(Calendar.MONTH, 9); // set the month
        cal.set(Calendar.DAY_OF_MONTH, 10); // set the day
        sample = cal.getTime();
        cal.clear();
    }

    @Test
    public void checkDateParsing_isCorrect() {
        assertEquals("10/10/2019", DateParser.parseDate(sample));
    }
}
