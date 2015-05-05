package com.candeo.app.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.candeo.app.Configuration;
import com.candeo.app.algorithms.Security;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by partho on 25/10/14.
 */
public class JSONParser {

    /*
    * Parses GET request
    * */

    public static JSONObject parseGET(String url, Context mContext, String relativeUrl)
    {
        InputStream inputStream=null;
        JSONObject json = null;
        /*
         API URL is getting read and stored to inputStream
         */
        System.out.println("URL is: "+url);
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.setHttpRequestRetryHandler(new  DefaultHttpRequestRetryHandler(3,true));
            HttpGet get = new HttpGet(url);
            String secret="";
            if(TextUtils.isEmpty(Preferences.getUserEmail(mContext)))
            {
                secret= Configuration.CANDEO_DEFAULT_SECRET;
            }
            else
            {
                secret=Preferences.getUserApiKey(mContext);
            }
            String message = relativeUrl;
            String hash = Security.generateHmac(secret, message);
            get.setHeader("message",message);
            if(Configuration.DEBUG)Log.e("JsonParser","Message is "+message);
            get.setHeader("email",Preferences.getUserEmail(mContext));
            get.setHeader("Authorization", "Token token=" + hash);
            if(Configuration.DEBUG)Log.e("JsonParser","Hash is "+hash);
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            System.out.println("InputStream is: "+inputStream);
        }catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();

        }catch (ClientProtocolException cpe)
        {
            cpe.printStackTrace();

        }catch (IOException ie)
        {
            ie.printStackTrace();

        }

        /*
            Inputstream is parsed to fetch string and convert in jsonObject to return
         */
        StringBuilder result=new StringBuilder();
        if(inputStream!=null)
        {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String line="";
                while ((line=reader.readLine())!= null)
                {
                    result.append(line+"\n");
                }
                inputStream.close();
                reader.close();
                json = new JSONObject(result.toString());

            }catch (UnsupportedEncodingException ue)
            {
                ue.printStackTrace();
            }catch (IOException ie)
            {
                ie.printStackTrace();
            }catch (JSONException je)
            {
                je.printStackTrace();
            }
        }

        System.out.println("Incoming JSON: "+result.toString());
        return json;
    }

    /*
     * Parses POST request
     * */
    public static JSONObject parsePOST(String url)
    {
        InputStream inputStream=null;
        JSONObject json = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(new HttpPost(url));
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
        }catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();

        }catch (ClientProtocolException cpe)
        {
            cpe.printStackTrace();

        }catch (IOException ie)
        {
            ie.printStackTrace();

        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuffer result=new StringBuffer();
            String line="";
            while ((line=reader.readLine())!= null)
            {
                result.append(line+"\n");
            }
            inputStream.close();
            reader.close();
            json = new JSONObject(result.toString());

        }catch (UnsupportedEncodingException ue)
        {
            ue.printStackTrace();
        }catch (IOException ie)
        {
            ie.printStackTrace();
        }catch (JSONException je)
        {
            je.printStackTrace();
        }

        return json;
    }
}
