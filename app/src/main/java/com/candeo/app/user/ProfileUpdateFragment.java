package com.candeo.app.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.algorithms.Security;
import com.candeo.app.network.UploadMediaListener;
import com.candeo.app.network.UploadMediaTask;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ProfileUpdateFragment extends DialogFragment implements UploadMediaListener {

    private ContextThemeWrapper contextThemeWrapper;
    private View dialog;
    private ImageView userAvatar;
    private EditText userName;
    private EditText bio;
    private Bitmap bitmap;
    private UploadMediaListener uploadMediaListener;
    private UserProfileUpdateListener userProfileUpdateListener;
    private static final String TAG="Candeo-profupdt";
    private String name;
    private String avatarUrl;
    private String userBio;
    private Uri imageUri;
    private String mediaId;
    private static final String USER_PROFILE_UPDATE_RELATIVE_API="/users/update_profile";
    private static final String USER_PROFILE_UPDATE_API=Configuration.BASE_URL+"/api/v1"+USER_PROFILE_UPDATE_RELATIVE_API;
    private static final int REQUEST_IMAGE_CAMERA=100;
    private static final int PICK_IMAGE_FILE=200;
    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        uploadMediaListener=this;
        mContext=getActivity();
        // Inflate the layout for this fragment
        return super.onCreateView(inflater,container,savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        contextThemeWrapper = new ContextThemeWrapper(getActivity(),R.style.Theme_AppCompat_Light_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        AlertDialog.Builder builder = new AlertDialog.Builder(contextThemeWrapper);
        builder.setTitle("Update Profile");
        name=getArguments().getString("name");
        avatarUrl=getArguments().getString("avatarUrl");
        userBio=getArguments().getString("bio");
        dialog = inflater.inflate(R.layout.fragment_profile_update, null);
        userAvatar=(ImageView)dialog.findViewById(R.id.candeo_user_profile_image);
        userAvatar.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.default_avatar));
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initImageSelection();
            }
        });
        new LoadImageTask().execute(avatarUrl);
        userName =(EditText)dialog.findViewById(R.id.candeo_user_name);
        userName.setText(name);
        bio=(EditText)dialog.findViewById(R.id.candeo_user_bio);
        bio.setText(userBio);
        builder.setView(dialog);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, String> payloadMap = new HashMap<>();
                payloadMap.put("id",Preferences.getUserRowId(getActivity()));
                payloadMap.put("name",userName.getText().toString());
                payloadMap.put("bio",bio.getText().toString());
                payloadMap.put("media_id",mediaId);
                CandeoApplication.getInstance().getAppRequestQueue().add(new UpdateUserProfileRequest(payloadMap));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog responseDialog = builder.create();
        responseDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return responseDialog;
    }

    @Override
    public void onSuccess(String response) {
        if(!TextUtils.isEmpty(response))
        {
            try {
                JSONObject json = new JSONObject(response);
                mediaId = json.getString("id");
                Log.e(TAG,"Media id is "+mediaId);
            }
            catch (JSONException jsonex)
            {
                jsonex.printStackTrace();
            }

        }
    }

    @Override
    public void onFailure(String response) {

    }

    public void setUpdateProfileListener(UserProfileUpdateListener userProfileUpdateListener)
    {
        this.userProfileUpdateListener=userProfileUpdateListener;
    }
    private void initImageSelection()
    {
        System.out.println("Clicking image......");
        final CharSequence[] choices ={"Click Something", "Fetch From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(choices,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(choices[which].equals("Click Something"))
                {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, UUID.randomUUID().toString()+".jpg");
                    imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
                }
                else if(choices[which].equals("Fetch From Gallery"))
                {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            PICK_IMAGE_FILE);

                }
                else if(choices[which].equals("Cancel"))
                {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri=null;
        String filePath="";
        File file=null;
        ByteArrayOutputStream bos=null;

        ExifInterface exifInterface = null;
        BitmapFactory.Options options = null;
        if(Activity.RESULT_OK == resultCode)
        {
            switch (requestCode)
            {
                case REQUEST_IMAGE_CAMERA:
                    filePath = CandeoUtil.getRealPathFromUri(imageUri, getActivity().getContentResolver());
                    file = new File(filePath);
                    Log.e(TAG,"Capture image path is "+filePath);
                    bitmap = BitmapFactory.decodeFile(filePath);
                    try {
                        exifInterface = new ExifInterface(filePath);
                        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        Log.e(TAG,"orientation in image capture is "+orientation);
                        new RotateTask(orientation,file.getName(),CandeoUtil.getMimeType(imageUri, getActivity())).execute(bitmap);
                    }
                    catch(IOException ioe)
                    {
                        ioe.printStackTrace();
                    }

                    break;
                case PICK_IMAGE_FILE:
                    uri = data.getData();
                    filePath = CandeoUtil.getRealPathFromUri(uri, getActivity().getContentResolver());
                    file = new File(filePath);
                    Log.e(TAG,"Picked image path is "+filePath);
                    bitmap = BitmapFactory.decodeFile(filePath);
                    try {
                        exifInterface = new ExifInterface(filePath);
                        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        Log.e(TAG,"orientation in pick image is "+orientation);
                        new RotateTask(orientation,file.getName(),CandeoUtil.getMimeType(uri, getActivity())).execute(bitmap);
                    }
                    catch(IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    break;
            }

        }
    }


    class RotateTask extends AsyncTask<Bitmap, Void, Bitmap>
    {
        private int orientation = ExifInterface.ORIENTATION_UNDEFINED;
        private String fileName="";
        private String mimeType="";
        public RotateTask(int orientation, String fileName, String mimeType)
        {
            this.orientation = orientation;
            this.fileName=fileName;
            this.mimeType=mimeType;
        }
        private ProgressDialog pDialog=new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                pDialog.setMessage("Preparing Image...");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap bitmap =params[0];
            bitmap = rotateBitmap(bitmap, orientation);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(pDialog!=null)
            {
                pDialog.dismiss();
                if(result!=null)
                {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap=result;
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                    if(bitmap.getWidth() > bitmap.getHeight())
                    {
                        userAvatar.setImageBitmap(Bitmap.createScaledBitmap(bitmap,80,80,false));
                    }
                    else
                    {
                        userAvatar.setImageBitmap(Bitmap.createScaledBitmap(bitmap,80,80,false));
                    }

                    userAvatar.setVisibility(View.VISIBLE);
                    new UploadMediaTask(uploadMediaListener,
                            Configuration.IMAGE,
                            bos.toByteArray(),
                            fileName,
                            mimeType,
                            getActivity()).execute(Configuration.MEDIA_UPLOAD_URL);

                }
            }
        }

    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation)
    {
        Matrix matrix = new Matrix();
        Log.e(TAG, "WIDTH IS " + bitmap.getWidth());
        Log.e(TAG,"HEIGHT IS "+bitmap.getHeight());
        try
        {
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_NORMAL:
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1,1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1,1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1,1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1,1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    break;

            }
            Bitmap bitmapRotated;
            if(bitmap.getWidth()>=bitmap.getHeight())
            {
                bitmapRotated = Bitmap.createBitmap(bitmap,bitmap.getWidth()/2-bitmap.getHeight()/2,0,bitmap.getHeight(),bitmap.getHeight(),matrix,false);
            }
            else
            {
                bitmapRotated = Bitmap.createBitmap(bitmap,0,bitmap.getHeight()/2-bitmap.getWidth()/2,bitmap.getWidth(),bitmap.getWidth(),matrix,false);
            }
            return CandeoUtil.createScaledBitmap(bitmapRotated, 400, 400);

        }
        catch (OutOfMemoryError ome)
        {
            ome.printStackTrace();
            return  bitmap;
        }

    }

    private class LoadImageTask extends AsyncTask<String, String, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL imageUrl= new URL(params[0]);
                bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null)
            {
                userAvatar.setImageBitmap(bitmap);
            }
        }
    }

    class UpdateUserProfileRequest extends JsonObjectRequest
    {
        private Map<String, String> payload;

        public UpdateUserProfileRequest(final Map<String, String> payload)
        {
            super(Method.POST,
                    USER_PROFILE_UPDATE_API,
                    new JSONObject(payload),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            try
                            {
                                if(response!=null)
                                {
                                    if(!TextUtils.isEmpty(response.getString("response")))
                                    {
                                        String url = Configuration.BASE_URL+response.getString("response");
                                        HashMap<String, String> params = new HashMap<>();
                                        params.put("name",userName.getText().toString());
                                        params.put("bio",bio.getText().toString());
                                        params.put("avatarUrl",url);
                                        userProfileUpdateListener.onProfileUpdate(params);
                                    }

                                }
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(mContext, "Sorry! This email has already been taken", Toast.LENGTH_LONG).show();
                            NetworkResponse response = error.networkResponse;
                            if(response!=null)
                            {
                                Log.e(TAG,"error is "+new String(response.data));
                            }
                        }
                    });
            this.payload=payload;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            Log.e(TAG,"getActivity is "+getActivity());
            if (Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(Preferences.getUserEmail(getActivity()))) {
                String secret="";
                params.put("email", Preferences.getUserEmail(getActivity()));
                secret=Preferences.getUserApiKey(getActivity());
                String message = USER_PROFILE_UPDATE_RELATIVE_API+"|"+new JSONObject(payload).toString();
                params.put("message", message);
                Log.e(TAG,"secret->"+secret);
                String hash = Security.generateHmac(secret, message);
                Log.e(TAG,"hash->"+hash);
                params.put("Authorization", "Token token=" + hash);

            }
            return params;
        }
    }
}
