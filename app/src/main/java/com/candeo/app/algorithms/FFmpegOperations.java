package com.candeo.app.algorithms;

import android.content.Context;
import android.util.Log;

import com.candeo.app.Configuration;
import com.candeo.app.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by partho on 27/3/15.
 */
public class FFmpegOperations {


    public static void init(Context context)
    {
        try {
//            FileUtil.copyFromAssetsToData(context,"ffmpeg/ffmpeg","ffmpeg");
            FileUtil.copyFromAssetsToAppDir(context,"ffmpeg/ffmpeg");
        }
        catch (IOException ie)
        {

        }

    }
    public static String getFffmpegVersion(Context context)
    {
//        String file=context.getFilesDir().getAbsolutePath()+ File.separator+"ffmpeg";
//        String file= context.getApplicationInfo().nativeLibraryDir+File.separator+"ffmpeg";
        String file= Configuration.CANDEO_BINROOT+File.separator+"ffmpeg";
        File ffmpegFile = new File(file);
//        String command = file+" -version";
        String command = "ffmpeg -version";
        if(Configuration.DEBUG)Log.e("ffmpgop","file exists "+ffmpegFile.exists());
        if(Configuration.DEBUG)Log.e("ffmpgop","command is "+command);
        String output="";
        try
        {
            if(Configuration.DEBUG)Log.e("ffmpgop","file "+file);
            Process process = Runtime.getRuntime().exec(command);
            if(process.waitFor()==0)
            {
                output = new String(FileUtil.inputStreamToByteArray(process.getInputStream()));
            }
        }
        catch (Exception e)
        {
            if(Configuration.DEBUG)Log.e("ffmpgop","error "+e.getLocalizedMessage());
            e.printStackTrace();
        }
        return output;
    }

}
