package com.candeo.app.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.candeo.app.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Partho on 21/12/14.
 */
public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final int DEFAULT_BUFFER_SIZE=1024*4;
    private static final int EOF=-1;
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

    public static void copyFromAssetsToData(Context context, String fileFromAssets, String outputFilename) throws IOException
    {
        File filesDirectory = context.getFilesDir();
        InputStream is=context.getAssets().open(fileFromAssets);
        final FileOutputStream fileOutputStream = new FileOutputStream(new File(filesDirectory,outputFilename));
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count;
        while (EOF!=(count=is.read(buffer)))
        {
            fileOutputStream.write(buffer,0,count);
        }
        if(is!=null)
        {
            is.close();
        }
        if(fileOutputStream!=null)
        {
            fileOutputStream.flush();
            fileOutputStream.close();
        }
    }

    public static void copyFromAssetsToAppDir(Context context, String fromAssets) throws IOException
    {
        final InputStream inputStream = context.getAssets().open(fromAssets);
        File file = new File(Configuration.CANDEO_BINROOT,"ffmpeg");
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count;
        while (EOF!=(count=inputStream.read(buffer)))
        {
            fileOutputStream.write(buffer,0,count);
        }
        if(inputStream!=null)
        {
            inputStream.close();
        }
        if(fileOutputStream!=null)
        {
            fileOutputStream.flush();
            fileOutputStream.close();
        }

    }

    public static byte[] inputStreamToByteArray(InputStream input) throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count;
        while(EOF != (count = input.read(buffer)))
        {
            output.write(buffer, 0, count);
        }
        if(input!=null)
        {
            input.close();
        }
        return output.toByteArray();
    }
}
