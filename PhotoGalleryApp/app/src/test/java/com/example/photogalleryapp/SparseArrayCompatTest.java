package com.example.photogalleryapp;

import androidx.collection.SparseArrayCompat;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SparseArrayCompatTest {
    private SparseArrayCompat<String> list;

    public SparseArrayCompatTest() {
        list = new SparseArrayCompat<>();
        list.put(1, "Dog");
        list.put(2, "Cat");
        list.put(3, "Cow");
/// ->
        list.put(4, "Pig");
/// ->
        list.put(5, "Apple");
        list.put(6, "Kiwi");
    }

    @Test
    public void removeItem_isCorrect() {
        int currentIndex = 3;
        int key = list.keyAt(currentIndex);
        list.removeAt(currentIndex);
        assertEquals("Apple", list.valueAt(currentIndex));
    }

}
