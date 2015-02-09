package com.candeo.app.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.network.UploadMediaListener;
import com.candeo.app.network.UploadMediaTask;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginActivity extends Activity implements UploadMediaListener {

    private Spinner emailSelector;
    private EditText debugEmail;
    private EditText name;
    private Button signup;
    private Button signin;
    private Button terms;
    private ImageView userProfile;
    private boolean hasImage;
    private boolean isSignup;
    private String mediaId;
    private static final int REQUEST_IMAGE_CAMERA=100;
    private static final int PICK_IMAGE_FILE=200;
    private Uri imageUri;
    private Bitmap bitmap;
    ArrayList<String> emails;

    private static final String API_REGISTER_URL = Configuration.BASE_URL +"/api/v1/users/register";
    private static final String API_LOGIN_URL = Configuration.BASE_URL +"/api/v1/users/login";

    private static final String TAG ="Candeo - Login Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailSelector = (Spinner)findViewById(R.id.candeo_login_email_selector);
        isSignup=true;
        hasImage=false;
        mediaId="";
        debugEmail= (EditText)findViewById(R.id.candeo_login_email_debug);
        debugEmail.setVisibility(View.VISIBLE);
        emails= CandeoUtil.emailAddresses(this);
        ArrayAdapter<String> emailSelectorAdapter = new ArrayAdapter<>(this, R.layout.candeo_email_spinner_item,emails.toArray(new String[emails.size()]));
        emailSelectorAdapter.setDropDownViewResource(R.layout.candeo_spinner_dropdown_item);
        emailSelector.setAdapter(emailSelectorAdapter);
        name=(EditText)findViewById(R.id.candeo_user_name);
        signup =(Button)findViewById(R.id.candeo_user_register);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = !TextUtils.isEmpty(debugEmail.getText()) ? debugEmail.getText().toString() : emailSelector.getSelectedItem().toString();
                Log.e(TAG,"And the email is "+email);
               if(isSignup)
               {
                   registerUser(name.getText().toString(), email);
               }
               else
               {
                   loginUser(email);
               }


            }
        });
        signin=(Button)findViewById(R.id.candeo_user_sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSignup)
                {
                    isSignup=false;
                    name.setVisibility(View.GONE);
                    userProfile.setVisibility(View.GONE);
                    signup.setText("SIGN IN");
                    signin.setText("SIGN IN");
                }
                else
                {
                    isSignup=true;
                    name.setVisibility(View.VISIBLE);
                    userProfile.setVisibility(View.VISIBLE);
                    signup.setText("SIGN UP");
                    signin.setText("SIGN UP");
                }
            }
        });
        userProfile=(ImageView)findViewById(R.id.candeo_user_profile_image);
        userProfile.setImageURI(Uri.parse(Preferences.getUserAvatarPath(getApplicationContext())));
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Upload Image
                initImageSelection();
            }
        });

    }

    private void initImageSelection()
    {
        System.out.println("Clicking image......");
        final CharSequence[] choices ={"Click Something", "Fetch From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setItems(choices,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(choices[which].equals("Click Something"))
                {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, UUID.randomUUID().toString()+".jpg");
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
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
    public void onSuccess(String response) {
        if(!TextUtils.isEmpty(response))
        {
            try {
                JSONObject json = new JSONObject(response);
                mediaId = json.getString("id");
                hasImage=true;
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
        if(!TextUtils.isEmpty(response))
        {
            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri=null;
        String filePath="";
        File file=null;
        ByteArrayOutputStream bos=null;

        ExifInterface exifInterface = null;
        BitmapFactory.Options options = null;
        if(RESULT_OK == resultCode)
        {
            switch (requestCode)
            {
                case REQUEST_IMAGE_CAMERA:
                    filePath = CandeoUtil.getRealPathFromUri(imageUri, getContentResolver());
                    file = new File(filePath);
                    Log.e(TAG,"Capture image path is "+filePath);
                    options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inDither = true;
                    bitmap = BitmapFactory.decodeFile(filePath,options);
                    try {
                        exifInterface = new ExifInterface(filePath);
                        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        Log.e(TAG,"orientation in image capture is "+orientation);
                        new RotateTask(orientation,file.getName(),CandeoUtil.getMimeType(imageUri, getApplicationContext())).execute(bitmap);
                    }
                    catch(IOException ioe)
                    {
                        ioe.printStackTrace();
                    }

                    break;
                case PICK_IMAGE_FILE:
                    uri = data.getData();
                    filePath = CandeoUtil.getRealPathFromUri(uri, getContentResolver());
                    file = new File(filePath);
                    Log.e(TAG,"Picked image path is "+filePath);
                    options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inDither = true;
                    bitmap=BitmapFactory.decodeFile(filePath, options);
                    try {
                        exifInterface = new ExifInterface(filePath);
                        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        Log.e(TAG,"orientation in pick image is "+orientation);
                        new RotateTask(orientation,file.getName(),CandeoUtil.getMimeType(uri, getApplicationContext())).execute(bitmap);
                    }
                    catch(IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    break;
            }

        }
    }

    private void registerUser(final String name, final String email)
    {
        if(hasImage && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(email))
        {
            System.out.println("name is "+name+" email is "+email);
            Map<String, String> params = new HashMap<>();

            params.put("name",name);
            params.put("email",email);
            params.put("media_id",mediaId);
            Log.e(TAG,"Getting Request Queue "+CandeoApplication.getInstance());
            CandeoApplication.getInstance().getAppRequestQueue().add(new RegisterUserRequest(params));
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please upload a suitable image with Full Name",Toast.LENGTH_LONG).show();
        }

    }

    private void loginUser(final String email)
    {
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("id",Preferences.getUserRowId(getApplicationContext()));
        loginParams.put("email",email);
        CandeoApplication.getInstance().getAppRequestQueue().add(new LoginUserRequest(loginParams));
    }

    class RegisterUserRequest extends JsonObjectRequest
    {
        public RegisterUserRequest(final Map<String, String> payload)
        {
            super(Method.POST,
                  API_REGISTER_URL,
                  new JSONObject(payload),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            try
                            {
                                String id = Integer.toString(response.getInt("id"));
                                System.out.println("Response is "+id);
                                Map<String, String> loginParams = new HashMap<>();
                                loginParams.put("id",id);
                                loginParams.put("email",payload.get("email"));
                                CandeoApplication.getInstance().getAppRequestQueue().add(new LoginUserRequest(loginParams));
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
                            System.out.println("Something went wrong");
                        }
                    });
        }
    }

    class LoginUserRequest extends JsonObjectRequest
    {
        public LoginUserRequest(Map<String,String> payload)
        {
            super(Method.POST,
                  API_LOGIN_URL,
                  new JSONObject(payload),
                  new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "Response is "+response);
                            CandeoUtil.appAlertDialog(LoginActivity.this, "You Must Have Received an email. Please check to continue login.");
                        }
                  },
                  new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Something went wrong");
                        }
                  });
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
        private ProgressDialog pDialog=new ProgressDialog(getApplicationContext());
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
                        userProfile.setImageBitmap(Bitmap.createScaledBitmap(bitmap,80,80,false));
                    }
                    else
                    {
                        userProfile.setImageBitmap(Bitmap.createScaledBitmap(bitmap,80,80,false));
                    }

                    userProfile.setVisibility(View.VISIBLE);
                    new UploadMediaTask(LoginActivity.this,
                                        Configuration.IMAGE,
                                        bos.toByteArray(),
                                        fileName,
                                        mimeType).execute(Configuration.MEDIA_UPLOAD_URL);

                }
            }
        }

    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation)
    {
        Matrix matrix = new Matrix();
        try
        {
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_NORMAL:
                    return bitmap;
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
                    return bitmap;

            }
            Bitmap bitmapRotated = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            return bitmapRotated;

        }
        catch (OutOfMemoryError ome)
        {
            ome.printStackTrace();
            return  bitmap;
        }

    }



}
