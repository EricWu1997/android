package com.example.photogalleryapp;

import com.example.photogalleryapp.util.FileNameParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileNameParserTest {
    private String sample1;
    private String sample2;

    public FileNameParserTest() {
        sample1 = "/temp/picture1-122345.jpg";
        sample2 = "/temp/non_picture.txt";
    }

    @Test
    public void checkParseName_isCorrect() {
        assertEquals("picture1", FileNameParser.parseName(sample1));
    }

    @Test
    public void checkPathRename_isCorrect() {
        assertEquals("/temp/hello-122345.jpg"
                , FileNameParser.newPathWithName(sample1, "hello", '/', '-'));
    }

    @Test
    public void checkSupportedFileName_isCorrect() {
        assertTrue(FileNameParser.checkIfFileSupported(sample1));
    }

    @Test
    public void checkUnSupportedFileName_isCorrect() {
        assertFalse(FileNameParser.checkIfFileSupported(sample2));
    }

    @Test
    public void checkValidExtension_isCorrect() {
        assertTrue(FileNameParser.matchExtension(sample1, ".jpg", ".png"));
    }

    @Test
    public void checkInValidExtension_isCorrect() {
        assertFalse(FileNameParser.matchExtension(sample2, ".jpg", ".png"));
    }
}
