package com.example.photogalleryapp.util;

import java.io.File;
import java.io.FileFilter;

public class WallpaperFileFilter implements FileFilter {
    private char day;

    public WallpaperFileFilter(char day) {
        this.day = day;
    }

    @Override
    public boolean accept(File pathname) {
        return pathname.getName().charAt(0) == day;
    }
}
