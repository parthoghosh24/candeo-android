package com.candeo.app.network;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.candeo.app.Configuration;
import com.candeo.app.util.CandeoUtil;

import java.io.IOException;

/**
 * Created by partho on 2/2/15.
 */
public class UploadMediaTask extends AsyncTask<String, Void, String> {
    private UploadMediaListener uploadMediaListener=null;
    private int mediaType=-1;
    private byte[] dataArray=null;
    private String fileName = "";
    private String mimeType="";
    private Activity mContext;

    public UploadMediaTask(UploadMediaListener uploadMediaListener, int mediaType, byte[] dataArray, String fileName, String mimeType, Activity mContext)
    {
        this.uploadMediaListener=uploadMediaListener;
        this.mediaType=mediaType;
        this.dataArray=dataArray;
        this.fileName = fileName;
        this.mimeType=mimeType;
        this.mContext=mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mediaType == Configuration.AUDIO)
        {
            CandeoUtil.showProgress(mContext,"Processing! Please wait...", Configuration.FA_AUDIO);
        }
        else if(mediaType == Configuration.IMAGE)
        {
            CandeoUtil.showProgress(mContext,"Processing! Please wait...", Configuration.FA_IMAGE);
        }

    }

    @Override
    protected String doInBackground(String... params) {

        try
        {
            if(mediaType >0 && dataArray != null) //Data Array can't be empty for media files. In Showcase, media types are mandatory
            {
                String url = params[0];
                CandeoHttpClient client = new CandeoHttpClient(url);
                client.connectForMultipart();
                System.out.println("FILE is " + fileName);
                client.addFormPart("media_type",Integer.toString(mediaType));
                client.addFilePart("media",fileName,dataArray, mimeType);
                client.finishMultipart();
                return client.getResponse();
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onPostExecute(String response) {
        CandeoUtil.hideProgress();
        if(!TextUtils.isEmpty(response))
        {
            uploadMediaListener.onSuccess(response);
        }
        else
        {
            uploadMediaListener.onFailure("Something wrong with file upload. Please do not upload pirated/ripped files in case of audio.");
        }
    }
}
