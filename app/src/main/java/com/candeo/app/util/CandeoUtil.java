package com.candeo.app.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.content.ContentActivity;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.shout.ShoutActivity;
import com.candeo.app.user.UserActivity;

import org.apache.http.util.ByteArrayBuffer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Partho on 23/11/14.
 *
 * This is the Util class for Candeo which will hold all the helper methods
 */
public class CandeoUtil {

    private static int notificationId=1;

    public static Typeface loadFont(AssetManager assetsLocation, String fontFile) {
        return Typeface.createFromAsset(assetsLocation, fontFile);
    }

    public static ArrayList<String> emailAddresses(Activity activity) {
        ArrayList<String> emails = new ArrayList<String>();
        Account[] accounts = AccountManager.get(activity).getAccountsByType("com.google");
        for (Account account : accounts) {
            emails.add(account.name);
        }
        return emails;
    }

    public static byte[] fileToByteArray(File file) {
        int maxLimit = 16777216; //16 MB
        if (file.length() > maxLimit) {
            return null;
        }

        FileInputStream inputStream = null;


        byte[] bytes = new byte[(int) file.length()];

        try {
            inputStream = new FileInputStream(file);
            inputStream.read(bytes);
            inputStream.close();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return bytes;
    }

    public static String getMimeType(Uri uri, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.getType(uri);
    }

    public static String getCodeFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            return url.substring(url.lastIndexOf("/") + 1);

        }
        return null;
    }

    public static void appAlertDialog(Context context, String mesg) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(mesg).setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    public static Uri getImageUri(Context context, Bitmap bitmap, String title) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, null);
        return Uri.parse(path);
    }

    public static String getRealPathFromUri(Uri uri, ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(index);
    }

    public static Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        try {
            URL imageUrl = new URL(url);
            bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap createScaledBitmap(Bitmap bitmap, int newHeight, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    private static AlertDialog mProgressDialog;

    public static void showProgress(Activity activity, String message, String icon) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View progressLayout = activity.getLayoutInflater().inflate(R.layout.loading_content, null);
        ((TextView) progressLayout.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(activity.getAssets(), "fonts/fa.ttf"));
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

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            Log.e("CandeoUtil", "error is "+e.getLocalizedMessage());
            if(Configuration.DEBUG)Log.e("CandeoUtil", "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
        }


    }

    public static void toggleView(View view, boolean show)
    {
        view.setVisibility(show?View.VISIBLE:View.GONE);
    }

    public static String formatDateString(String oldDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String parsedDate = "";
        Date date = null;
        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            date = dateFormat.parse(oldDate);
            DateFormat dateTimeFormat = DateFormat.getDateTimeInstance();
            parsedDate = dateTimeFormat.format(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }


        return parsedDate;
    }

    public static void showNotification(Context context, HashMap<String, String> fields) {
        String title = fields.get("title");
        String body = fields.get("body");
        String url = fields.get("imageUrl");
        String bigUrl = fields.get("bigImageUrl");
        String type = fields.get("type");
        String id = fields.get("id");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(title)
                        .setTicker(body)
                        .setContentText(body);
        mBuilder.setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.logo_inverted : R.drawable.logo);
        Bitmap bitmap =null;
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        if(!TextUtils.isEmpty(url))
        {
            if(Configuration.DEBUG)Log.e("candeoutil","check image url");
            bitmap =getBitmapFromUrl(url);
            mBuilder.setLargeIcon(bitmap);
        }
        if(!TextUtils.isEmpty(bigUrl))
        {
            bitmap =getBitmapFromUrl(bigUrl);
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(body));

        }
        PendingIntent resultPendingIntent = getPendingIntent(type,id,context);
        if (resultPendingIntent != null) {
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
        }

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(++notificationId, mBuilder.build());


    }

    private static PendingIntent getPendingIntent(String type, String id, Context context)
    {
        Class mClazz= null;
        PendingIntent result = null;
        if(!TextUtils.isEmpty(type))
        {

            if("home".equalsIgnoreCase(type))
            {
                mClazz= HomeActivity.class;
            }
            if("content".equalsIgnoreCase(type))
            {
                mClazz= ContentActivity.class;
            }
            if("performance".equalsIgnoreCase(type))
            {
                mClazz= HomeActivity.class;

            }
            if("user".equalsIgnoreCase(type))
            {
                mClazz= UserActivity.class;
            }
            if("shout".equalsIgnoreCase(type))
            {
                mClazz= ShoutActivity.class;
            }

            Intent resultIntent = new Intent(context,mClazz);
            if("shout".equalsIgnoreCase(type))
            {
                resultIntent.putExtra("id",id);
            }
            if("content".equalsIgnoreCase(type))
            {
                resultIntent.putExtra("id",id);
                resultIntent.putExtra("type",Configuration.SHOWCASE);
            }
            if("performance".equalsIgnoreCase(type))
            {
                resultIntent.putExtra("performance","performance");
            }
            if("user".equalsIgnoreCase(type))
            {
                resultIntent.putExtra("id",id);
            }
            result =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT
                    );

        }

        return result;
    }
}