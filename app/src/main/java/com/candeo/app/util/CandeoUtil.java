package com.candeo.app.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Partho on 23/11/14.
 *
 * This is the Util class for Candeo which will hold all the helper methods
 */
public class CandeoUtil {

    public static Typeface loadFont(AssetManager assetsLocation, String fontFile)
    {
        return Typeface.createFromAsset( assetsLocation,fontFile);
    }

   public static ArrayList<String> emailAddresses(Activity activity)
   {
       ArrayList<String> emails=new ArrayList<String>();
       Account[] accounts = AccountManager.get(activity).getAccountsByType("com.google");
       for(Account account: accounts)
       {
           emails.add(account.name);
       }
       return emails;
   }

    public static byte[] fileToByteArray(File file)
    {
        FileInputStream inputStream =null;

        byte[] bytes= new byte[(int)file.length()];

        try
        {
            inputStream = new FileInputStream(file);
            inputStream.read(bytes);
            inputStream.close();
        }
        catch (FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch (IOException ie)
        {
            ie.printStackTrace();
        }
        return bytes;
    }

    public static String getMimeType(Uri uri, Context context)
    {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.getType(uri);
    }
}
