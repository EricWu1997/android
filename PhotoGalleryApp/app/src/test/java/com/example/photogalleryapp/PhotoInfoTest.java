package com.example.photogalleryapp;

import com.example.photogalleryapp.util.DateParser;
import com.example.photogalleryapp.util.PhotoInfo;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class PhotoInfoTest {

    private PhotoInfo photoInfo;

    public PhotoInfoTest() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2019); // set the year
        cal.set(Calendar.MONTH, 9); // set the month
        cal.set(Calendar.DAY_OF_MONTH, 10); // set the day
        photoInfo = new PhotoInfo("/tmp/dog.jpg", "dog.jpg", cal.getTime());
        cal.clear();
    }

    @Test
    public void checkFilepath_isCorrect() {
        assertEquals("/tmp/dog.jpg", photoInfo.getFilepath());
    }

    @Test
    public void checkFilename_isCorrect() {
        assertEquals("dog.jpg", photoInfo.getFilename());
    }

    @Test
    public void checkTimeStamp_isCorrect() {
        assertEquals("10/10/2019", DateParser.parseDate(photoInfo.getTimeStamp()));
    }
}
