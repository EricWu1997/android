package com.example.photogalleryapp;

import com.example.photogalleryapp.util.IntToDay;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntToDayTest {
    public IntToDayTest() {
    }

    @Test
    public void checkIntToDay_isCorrect() {
        assertEquals("Mon", IntToDay.convert(0));
    }

    @Test
    public void checkInvalidIntToDay_isCorrect() {
        assertEquals(null, IntToDay.convert(8));
    }
}
