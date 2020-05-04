package com.example.photogalleryapp;

import com.example.photogalleryapp.manager.PhotoManager;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class PhotoManagerTest {
    private PhotoManager manager = PhotoManager.getInstance();

    public PhotoManagerTest() {
        manager.clear();
        manager.addToList("/tmp/dog.jpg", "dog.jpg", new Date());
        manager.addToList("/tmp/cat.jpg", "cat.jpg", new Date());
        manager.addToList("/tmp/pig.jpg", "pig.jpg", new Date());
        manager.addToList("/tmp/cow.jpg", "cow.jpg", new Date());
    }

    @Test
    public void returnLastAddedPhoto_isCorrect() {
        assertEquals("/tmp/cow.jpg", manager.currentPhoto().getFilepath());
    }

    @Test
    public void returnNextPhoto_isCorrect() {
        manager.nextPhoto(); // cow --> dog
        manager.nextPhoto(); // dog --> cat
        manager.nextPhoto(); // cat --> pig
        assertEquals("/tmp/cow.jpg", manager.nextPhoto().getFilepath());
    }

    @Test
    public void returnPrevPhoto_isCorrect() {
        manager.nextPhoto(); // cow --> dog
        manager.nextPhoto(); // dog --> cat
        manager.nextPhoto(); // cat --> pig
        assertEquals("/tmp/cat.jpg", manager.prevPhoto().getFilepath());
    }

    @Test
    public void applyFilter_isCorrect() {
        manager.applyFilter("cat", null, null);
        assertEquals(1,  manager.getList().size());
    }

    @Test
    public void singleTonCreation_isCorrect() {
        PhotoManager manager2 = PhotoManager.getInstance();
        assertEquals(manager.hashCode(), manager2.hashCode());
    }
}
