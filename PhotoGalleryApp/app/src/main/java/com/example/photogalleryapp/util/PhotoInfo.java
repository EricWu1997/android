package com.example.photogalleryapp.util;

import java.util.Date;

public class PhotoInfo {
    private String filepath;
    private String filename;
    private Date timeStamp;

    public PhotoInfo(String filepath, String filename, Date timeStamp) {
        this.filepath = filepath;
        this.filename = filename;
        this.timeStamp = timeStamp;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getFilename() {
        return filename;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setFilepath(String path) {
        this.filepath = path;
    }

    public void setFilename(String name) {
        this.filename = name;
    }
}
