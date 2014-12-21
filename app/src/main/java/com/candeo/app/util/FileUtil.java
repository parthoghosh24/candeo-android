package com.candeo.app.util;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Partho on 21/12/14.
 */
public class FileUtil {
    private static final String TAG = "FileUtil";
    public File ebookFiles[];
    File fileSystemRoot;

    public File getFileSystemRoot() {
        File root = Environment.getExternalStorageDirectory();
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            root = Environment.getExternalStorageDirectory();
        }
        return root;
    }

    public File createFolder(String path) {
        File outputFolder = new File(path);
        outputFolder.mkdir();
        return outputFolder;
    }

    public void deleteDir(File dir)
    {
        if(dir.isDirectory())
        {
            for(File file:dir.listFiles())
            {
                deleteDir(file);
            }
        }
        dir.delete();
    }
}
