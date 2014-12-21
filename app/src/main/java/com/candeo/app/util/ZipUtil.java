package com.candeo.app.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Partho on 21/12/14.
 */
public class ZipUtil {

    private static final String TAG="ZipUtil";
    private static final int BUFFERVAL=1024;
    FileUtil mFileUtil;
    File unzippedFolder;

    public String unzipBook(String zipFile, String outputFolder)
    {
        String outputPath="";
        byte[] buffer = new byte[BUFFERVAL];

        try
        {
            mFileUtil = new FileUtil();
            unzippedFolder = mFileUtil.createFolder(outputFolder);
            outputPath=unzippedFolder.getAbsolutePath();
            Log.v(TAG, "Output Folder path: " + outputPath);
            ZipInputStream zipInput = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zEntry = zipInput.getNextEntry();

            while(zEntry!= null)
            {
                String fileName = zEntry.getName();
                Log.v(TAG, "Entry file "+fileName);
                File newFile = new File(outputFolder+File.separator+fileName);
                Log.v(TAG, "New File/Folder: "+newFile.getAbsolutePath());

                if(zEntry.isDirectory())
                {
                    (new File(newFile.getAbsolutePath())).mkdirs();
                }
                else
                {
                    (new File(newFile.getParent())).mkdirs();
                    FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                    int length=0;
                    while((length=zipInput.read(buffer))>0)
                    {
                        fileOutputStream.write(buffer,0,length);
                    }
                }


                zEntry=zipInput.getNextEntry();
            }
            zipInput.close();

        }
        catch(IOException ie)
        {
            ie.printStackTrace();
        }
        return outputPath;

    }
}
