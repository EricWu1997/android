package com.example.photogalleryapp.util;

import android.util.Log;

import androidx.collection.SparseArrayCompat;

import java.util.Date;

public class PhotoListFilter {
    public static SparseArrayCompat<PhotoInfo> filter(String keyword, Date start, Date end
            , SparseArrayCompat<PhotoInfo> list) {
        if (list == null || list.size() == 0) return list;
        if (!keyword.equals(""))
            for (int i = 0; i < list.size(); i++) {
                String temp = list.valueAt(i).getFilename().toLowerCase();
//                Log.d("TEST", temp + "--" + keyword);
                if (!temp.contains(keyword.toLowerCase())) {
//                    Log.d("TEST", "Remove" + temp);
                    list.removeAt(i);
                    i--;
                }
            }
        if (start != null && end != null)
            for (int i = 0; i < list.size(); i++) {
                Log.d("TEST", "CALLED");
                Date temp = list.valueAt(i).getTimeStamp();
                if (temp.compareTo(start) < 0 || temp.compareTo(end) > 0) {
                    list.removeAt(i);
                    i--;
                }
            }
        return list;
    }
}
