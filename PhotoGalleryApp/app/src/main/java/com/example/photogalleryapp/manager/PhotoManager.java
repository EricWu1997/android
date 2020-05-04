package com.example.photogalleryapp.manager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.collection.SparseArrayCompat;

import com.example.photogalleryapp.util.FileNameParser;
import com.example.photogalleryapp.util.PhotoInfo;
import com.example.photogalleryapp.util.PhotoListFilter;

import java.io.File;
import java.util.Date;

public class PhotoManager {
    private static PhotoManager instance;
    private SparseArrayCompat<PhotoInfo> list;
    private SparseArrayCompat<PhotoInfo> full_list;
    private int list_size;
    private int current_index;

    private PhotoManager() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        list = new SparseArrayCompat<>();
        full_list = list.clone();
        list_size = 0;
        current_index = -1;
    }

    public int readFromFolder(Context context) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "GALLERY");
        File[] fList = null;
        if (file != null)
            fList = file.listFiles();
        String temp;
        if (fList != null) {
            current_index = 0;
            for (File f : fList) {
                temp = f.getAbsolutePath();
                if (FileNameParser.matchExtension(temp, ".jpg", ".png", ".jpeg") &&
                        FileNameParser.checkIfFileSupported(f.getName())) {
                    addToList(f.getAbsolutePath(), FileNameParser.parseName(temp), new Date(f.lastModified()));
                }
            }
            return list_size;
        } else {
            return 0;
        }
    }

    public PhotoInfo nextPhoto() {
        if (current_index == -1) return null;
        current_index = (++current_index) % list_size;
        return list.valueAt(current_index);
    }

    public PhotoInfo prevPhoto() {
        if (current_index == -1) return null;
        current_index = (--current_index < 0 ? list_size - 1 : current_index);
        return list.valueAt(current_index);
    }

    public PhotoInfo currentPhoto() {
        if (current_index == -1) return null;
        return list.valueAt(current_index);
    }

    public PhotoInfo lastPhoto() {
        if (list_size == 0) return null;
        current_index = list_size - 1;
        return list.valueAt(current_index);
    }

    public void setCurrentIndexTo(int index) {
        current_index = index;
    }

    public void addToList(String filepath, String filename, Date timeStamp) {
        PhotoInfo temp = new PhotoInfo(filepath, filename, timeStamp);
        addToList(temp);
    }

    public void addToList(PhotoInfo newPhoto) {
        list.put(list_size, newPhoto);
        full_list.put(full_list.size(), newPhoto);
        current_index = list_size++;
    }

    public void deleteCurrentPhoto() {
        if (list_size != 0) {
            int key = list.keyAt(current_index);
            list.removeAt(current_index);
            full_list.remove(key);
            list_size = list.size();
            if (list_size == 0) {
                current_index = -1;
            } else {
                current_index = current_index % list_size;
            }
        }
    }

    public int getListSize() {
        return list.size();
    }

    public SparseArrayCompat<PhotoInfo> getList() {
        return list.clone();
    }

    public void applyFilter(String keyword, Date start, Date end) {
        removeFilter();
        PhotoListFilter.filter(keyword, start, end, list);
        list_size = list.size();
        if (list_size != 0)
            current_index = list_size - 1;
        else
            current_index = -1;
    }

    public void removeFilter() {
        list = full_list.clone();
        list_size = list.size();
        if (list_size != 0)
            current_index = 0;
        else
            current_index = -1;
    }

    public void updateActivePhotoInfo(String newPath, String newName) {
        PhotoInfo temp = currentPhoto();
        temp.setFilename(newName);
        temp.setFilepath(newPath);
    }

    public void clear() {
        list.clear();
        full_list.clear();
        list_size = 0;
        current_index = -1;
    }

    public synchronized static PhotoManager getInstance() {
        if (instance == null) {
            instance = new PhotoManager();
        }
        return instance;
    }
}
