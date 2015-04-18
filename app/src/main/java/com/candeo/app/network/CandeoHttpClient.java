package com.candeo.app.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.candeo.app.Configuration;
import com.candeo.app.algorithms.Security;
import com.candeo.app.util.Preferences;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Partho on 7/12/14.
 * This is the HTTP Client implementation for file upload and posting for Candeo
 */
public class CandeoHttpClient {

    private String url;
    private Context mContext;
    private HttpURLConnection connection;
    private OutputStream outputStream;
    private String delimiter="--";
    private String boundary="candeo"+Long.toString(System.currentTimeMillis())+"candeo"; //Making timestamp powered boundary for request payload

    public CandeoHttpClient(String url, Context mContext)
    {
        this.url = url;
        this.mContext=mContext;
    }

    public void connectForMultipart() throws MalformedURLException, IOException
    {
        connection = (HttpURLConnection)(new URL(url)).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);//Required for POST
        connection.setConnectTimeout(50000);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type","multipart/form-data; boundary="+boundary);
        connection.setRequestProperty("email", Preferences.getUserEmail(mContext));
        String secret="";
        if(TextUtils.isEmpty(Preferences.getUserEmail(mContext)))
        {
            secret=Configuration.CANDEO_DEFAULT_SECRET;
        }
        else
        {
            secret=Preferences.getUserApiKey(mContext);
        }
        String message = Configuration.MEDIA_UPLOAD_RELATIVE_URL;
        connection.setRequestProperty("message", message);
        String hash = Security.generateHmac(secret, message);
        if(Configuration.DEBUG)Log.e("CandeoHttp", "hash->" + hash);
        connection.setRequestProperty("Authorization", "Token token=" + hash);
//        connection.setChunkedStreamingMode(1024);
        connection.connect();
        outputStream=connection.getOutputStream();
    }

    public void addFormPart(String paramKey, String paramValue) throws IOException
    {
        writeParamData(paramKey,paramValue);
    }

    public void addFilePart(String paramKey, String fileName, byte[] data, String mimeType) throws IOException
    {
        outputStream.write((delimiter+boundary+"\r\n").getBytes());
        outputStream.write(("Content-Disposition: form-data; name=\""+paramKey+"\"; filename=\""+fileName+"\"\r\n").getBytes());
        outputStream.write(("Content-Type: application/octet-stream\r\n").getBytes());
        outputStream.write(("Content-Transfer-Encoding: binary\r\n").getBytes());
        outputStream.write(("\r\n").getBytes());
        outputStream.write(data);
        outputStream.write(("\r\n").getBytes());
    }

    public void finishMultipart() throws IOException
    {
        outputStream.write((delimiter+boundary+delimiter+"\r\n").getBytes());

    }

    public String getResponse() throws IOException
    {
        InputStream iStream = connection.getInputStream();
        BufferedInputStream bInputStream = new BufferedInputStream(iStream);
        ByteArrayBuffer buffer = new ByteArrayBuffer(50);
        int read=0;
        int bufferSize=512;
        byte[] bytes = new byte[bufferSize];

        while(true)
        {
            read=bInputStream.read(bytes);
            if(read == -1)
            {
                break;
            }
            buffer.append(bytes,0,read);
        }
        iStream.close();
        bInputStream.close();
        outputStream.close();
        return new String(buffer.toByteArray(),"UTF-8");
    }

    private void writeParamData(String key, String value) throws IOException
    {
        outputStream.write((delimiter+boundary+"\r\n").getBytes());
        outputStream.write("Content-Type: text/plain\r\n".getBytes());
        outputStream.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n").getBytes());
        outputStream.write(("\r\n" + value + "\r\n").getBytes());
    }

}
