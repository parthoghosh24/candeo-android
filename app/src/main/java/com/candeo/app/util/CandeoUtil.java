package com.candeo.app.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.candeo.app.R;

import org.apache.http.util.ByteArrayBuffer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
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
        int maxLimit = 16777216; //16 MB
        if(file.length() > maxLimit)
        {
            return null;
        }

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

    public static String getCodeFromUrl(String url)
    {
        if(!TextUtils.isEmpty(url))
        {
            Pattern pattern = Pattern.compile("-?\\d+");
            Matcher matcher = pattern.matcher(url);
            if(matcher.find())
            {
                return matcher.group(0);
            }
            else
            {
                return  null;
            }
        }
        return null;
    }
    public static void appAlertDialog(Context context, String mesg)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(mesg).setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
            }
        }).create().show();
    }

    public static Uri getImageUri(Context context, Bitmap bitmap, String title)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,title,null);
        return Uri.parse(path);
    }

    public static String getRealPathFromUri(Uri uri, ContentResolver contentResolver)
    {
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(index);
    }
    public static Bitmap getBitmapFromUrl(String url)
    {
        Bitmap bitmap = null;
        try {
            URL imageUrl = new URL(url);
            bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap createScaledBitmap(Bitmap bitmap, int newHeight, int newWidth)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float)newWidth)/width;
        float scaleHeight = ((float)newHeight)/height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    private static AlertDialog mProgressDialog;

    public static void showProgress(Activity activity, String message, String icon) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View progressLayout = activity.getLayoutInflater().inflate(R.layout.loading_content, null);
        ((TextView) progressLayout.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/fa.ttf"));
        ((TextView) progressLayout.findViewById(R.id.candeo_progress_icon)).setText(icon);
        ((TextView) progressLayout.findViewById(R.id.candeo_progress_text)).setText(message);
        builder.setView(progressLayout);
        builder.setCancelable(false);
        mProgressDialog = builder.show();
    }

    public static void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

}
