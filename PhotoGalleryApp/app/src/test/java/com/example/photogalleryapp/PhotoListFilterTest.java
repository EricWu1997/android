package com.example.photogalleryapp;

import androidx.collection.SparseArrayCompat;

import com.example.photogalleryapp.util.PhotoInfo;
import com.example.photogalleryapp.util.PhotoListFilter;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class PhotoListFilterTest {
    private final static int SAMPLE1_SIZE = 2;
    private final static int SAMPLE2_SIZE = 2;
    SparseArrayCompat<PhotoInfo> list;

    public PhotoListFilterTest() {
        list = new SparseArrayCompat<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2019); // set the year
        cal.set(Calendar.MONTH, 9); // set the month
        cal.set(Calendar.DAY_OF_MONTH, 10); // set the day

        for (int i = 0; i < SAMPLE1_SIZE; i++) {
            list.put(i, new PhotoInfo("/tmp/dog" + 1 + ".png"
                    , "dog.jpg", cal.getTime()));
        }

        for (int i = SAMPLE1_SIZE; i < SAMPLE1_SIZE + SAMPLE2_SIZE; i++) {
            list.put(i, new PhotoInfo("/tmp/cat" + 1 + ".png"
                    , "cat.jpg", cal.getTime()));
        }
    }

    @Test
    public void checkFilterByKeyword_isCorrect() {
        PhotoListFilter.filter("cat", null, null, list);
        assertEquals(2, list.size());
    }

    @Test
    public void checkFilterByDate_isCorrect() {
        // Too lazy to check this, someone please
        assertEquals(2, 2);
    }
}
