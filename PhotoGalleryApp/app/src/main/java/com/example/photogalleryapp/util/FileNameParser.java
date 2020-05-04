package com.example.photogalleryapp.util;

public class FileNameParser {
    public static boolean matchExtension(String filename, String... extensions) {
        String file_ext = filename.substring(filename.lastIndexOf('.'));
        for (String ext : extensions) {
            if (file_ext.equals(ext))
                return true;
        }
        return false;
    }

    public static boolean checkIfFileSupported(String path) {
        return path.matches(".+\\-[0-9]+\\.jpg");
    }

    public static String parseName(String fullName) {
        if (fullName.contains("-")) {
            int i = fullName.lastIndexOf('/') + 1;
            int j = fullName.lastIndexOf('-');
            if (i != 0 && j != -1)
                return fullName.substring(i, j);
        }
        return null;
    }

    public static String newPathWithName(String oldPath, String newName, char delim1, char delim2) {
        int i = oldPath.lastIndexOf(delim1);
        int j = oldPath.lastIndexOf(delim2);
        if (j == -1)
            return null;
        return oldPath.substring(0, i + 1) + newName + oldPath.substring(j);
    }
}
