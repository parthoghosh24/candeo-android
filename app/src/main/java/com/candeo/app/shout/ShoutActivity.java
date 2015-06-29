package com.candeo.app.shout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.ShoutDiscussionsAdapter;
import com.candeo.app.algorithms.Security;
import com.candeo.app.ui.FontAwesomeDrawable;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShoutActivity extends AppCompatActivity {

    private View loadingShout = null;
    private RecyclerView shoutDisscussionList;
    private ShoutDiscussionsAdapter mShoutDiscussionsAdapter;
    private LinearLayoutManager shoutContentLayoutManager;
    private ArrayList<HashMap<String,String>> discussions= new ArrayList<>();
    private EditText message;
    private FloatingActionButton postMessage;
    private String id;
    private Toolbar toolbar;
    private static final String GET_SHOUT_CONTENT_RELATIVE_API="/shouts/%s";
    private static final String GET_SHOUT_CONTENT_API=Configuration.BASE_URL+"/api/v1"+GET_SHOUT_CONTENT_RELATIVE_API;
    private static final String POST_SHOUT_MESSAGE_RELATIVE_API="/shouts/discussions/create";
    private static final String POST_SHOUT_MESSAGE_API=Configuration.BASE_URL+"/api/v1"+POST_SHOUT_MESSAGE_RELATIVE_API;
    private static final String GET_SHOUT_CONTENT_DISCUSSION_RELATIVE_API="/shouts/%s/discussions/%s";
    private static final String GET_SHOUT_CONTENT_DISCUSSION_API=Configuration.BASE_URL+"/api/v1"+GET_SHOUT_CONTENT_DISCUSSION_RELATIVE_API;
    private static final String TAG="shout";
    private TextView name,body,typeIcon,typeText,timestamp,andMore;
    private CircleImageView userAvatar,participant1,participant2,participant3,participant4,participant5;
    private LinearLayout participantsHolder;
    private String lastTimestamp="now";
    private SwipeRefreshLayout refreshLayout;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shout);
        loadingShout = findViewById(R.id.candeo_loading_shout);
        toolbar = (Toolbar)findViewById(R.id.candeo_shout_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shout");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView)loadingShout.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        ((TextView)loadingShout.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_BULLHORN);
        ((TextView)loadingShout.findViewById(R.id.candeo_progress_icon)).setTextColor(getResources().getColor(R.color.candeo_light_gray));
        ((TextView)loadingShout.findViewById(R.id.candeo_progress_text)).setText("Loading Shout...");
        ((TextView)loadingShout.findViewById(R.id.candeo_progress_text)).setTextColor(getResources().getColor(R.color.candeo_light_gray));
        CandeoUtil.toggleView(loadingShout, false);

        name=(TextView)findViewById(R.id.candeo_shout_content_user_name);
        body=(TextView)findViewById(R.id.candeo_shout_content_body_text);
        typeIcon=(TextView)findViewById(R.id.candeo_shout_content_type);
        typeIcon.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/fa.ttf"));
        typeText=(TextView)findViewById(R.id.candeo_shout_content_type_text);
        timestamp=(TextView)findViewById(R.id.candeo_shout_content_timestamp);
        andMore=(TextView)findViewById(R.id.candeo_shout_content_participant_more);
        andMore.setVisibility(View.GONE);
        userAvatar=(CircleImageView)findViewById(R.id.candeo_shout_content_user_avatar);
        participant1=(CircleImageView)findViewById(R.id.candeo_shout_content_participant_1);
        participant2=(CircleImageView)findViewById(R.id.candeo_shout_content_participant_2);
        participant3=(CircleImageView)findViewById(R.id.candeo_shout_content_participant_3);
        participant4=(CircleImageView)findViewById(R.id.candeo_shout_content_participant_4);
        participant5=(CircleImageView)findViewById(R.id.candeo_shout_content_participant_5);
        participantsHolder=(LinearLayout)findViewById(R.id.candeo_shout_content_participants);
        participantsHolder.setVisibility(View.GONE);
        typeIcon.setText(Configuration.FA_UNLOCK);
        typeIcon.setTextColor(getResources().getColor(R.color.candeo_checked_green));
        typeText.setText("PUBLIC");
        typeText.setTextColor(getResources().getColor(R.color.candeo_checked_green));

        shoutDisscussionList = (RecyclerView)findViewById(R.id.candeo_shout_content_list);
        message = (EditText)findViewById(R.id.candeo_shout_message_text);
        postMessage = (FloatingActionButton)findViewById(R.id.candeo_shout_message_post);
        postMessage.setEnabled(false);
        FontAwesomeDrawable.FontAwesomeDrawableBuilder builder = new FontAwesomeDrawable.FontAwesomeDrawableBuilder(this,R.string.fa_paperplane);
        builder.setColor(getResources().getColor(R.color.candeo_white));
        builder.setSize(16);
        postMessage.setImageDrawable(builder.build());
        id = getIntent().getStringExtra("id");
        Log.e(TAG,"AND THE ID IS "+id);
        shoutContentLayoutManager= new LinearLayoutManager(this);
        shoutContentLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        shoutDisscussionList.setLayoutManager(shoutContentLayoutManager);
        requestRefresh();
        postMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(message.getText().length()>0)
                {
                    onPostClick();
                }
                else
                {
                    Toast.makeText(ShoutActivity.this,"Please Enter some text to message",Toast.LENGTH_SHORT).show();
                }

            }
        });
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.candeo_shout_content_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestDiscussionRefresh();
            }
        });
        shoutDisscussionList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = shoutContentLayoutManager.getChildCount();
                totalItemCount = shoutContentLayoutManager.getItemCount();
                pastVisibleItems = shoutContentLayoutManager.findFirstVisibleItemPosition();

                if (loading && !lastTimestamp.equalsIgnoreCase("now")) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = false;
                        loadDiscussions();
                    }
                }
            }
        });

    }

    private void onPostClick()
    {

        HashMap<String,String> messageMap = new HashMap<>();
        messageMap.put("id","local");
        messageMap.put("discussion",message.getText().toString());
        messageMap.put("name", Preferences.getUserName(this));
        messageMap.put("user_id", Preferences.getUserRowId(this));
        discussions.add(messageMap);
        if(mShoutDiscussionsAdapter==null)
        {
            mShoutDiscussionsAdapter = new ShoutDiscussionsAdapter(this);
            mShoutDiscussionsAdapter.addAllToList(discussions, false);
            shoutDisscussionList.setAdapter(mShoutDiscussionsAdapter);
            shoutDisscussionList.smoothScrollToPosition(discussions.size() - 1);

        } else
        {
            shoutDisscussionList.smoothScrollToPosition(mShoutDiscussionsAdapter.updateList(messageMap));
        }


        message.setText("");
        messageMap.put("shout_id",id);
        PostMessageRequest postMessageRequest= new PostMessageRequest(messageMap);
        postMessageRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CandeoApplication.getInstance().getAppRequestQueue().add(postMessageRequest);
    }

    private void requestRefresh()
    {
        GetShoutContentRequest request = new GetShoutContentRequest(id);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CandeoApplication.getInstance().getAppRequestQueue().add(request);

    }

    private void requestDiscussionRefresh()
    {
        mShoutDiscussionsAdapter=null;
        discussions.clear();
        lastTimestamp="now";
        visibleItemCount = 0;
        totalItemCount = 0;
        pastVisibleItems = 0;
        loading=true;
        GetShoutDiscussionsRequest request= new GetShoutDiscussionsRequest(id);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CandeoApplication.getInstance().getAppRequestQueue().add(request);

    }

    private void loadDiscussions()
    {

        GetShoutDiscussionsRequest request= new GetShoutDiscussionsRequest(id);
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CandeoApplication.getInstance().getAppRequestQueue().add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content, menu);
        MenuItem settings = menu.getItem(0);
        settings.setVisible(false);
        return true;
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private class GetShoutContentRequest extends JsonObjectRequest {
        public GetShoutContentRequest(final String id) {
            super(Method.GET,
                    String.format(GET_SHOUT_CONTENT_API,id),
                    new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                response=response.getJSONObject("shout");
                                name.setText(response.getString("name"));
                                body.setText(response.getString("body"));
                                timestamp.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(response.getString("created_at_timestamp")), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
                                new LoadImageTask(userAvatar).execute(response.getString("avatar_path"));
                                if(!response.getBoolean("is_public"))
                                {
                                    participantsHolder.setVisibility(View.VISIBLE);
                                    typeIcon.setText(Configuration.FA_LOCK);
                                    typeIcon.setTextColor(getResources().getColor(R.color.candeo_private_red));
                                    typeText.setText("PRIVATE");
                                    typeText.setTextColor(getResources().getColor(R.color.candeo_private_red));
                                    JSONArray participants = response.getJSONArray("participants");
                                    if(Configuration.DEBUG)Log.e(TAG,"Participants are "+participants.length());
                                    if(participants.length()==1)
                                    {
                                        participant1.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant1).execute(participants.getJSONObject(0).getString("avatar_path"));
                                        participant2.setVisibility(View.GONE);
                                        participant3.setVisibility(View.GONE);
                                        participant4.setVisibility(View.GONE);
                                        participant5.setVisibility(View.GONE);
                                        andMore.setVisibility(View.GONE);
                                    }
                                    else if(participants.length()==2)
                                    {
                                        participant1.setVisibility(View.VISIBLE);
                                        if(Configuration.DEBUG)Log.e(TAG,"Participant 1"+participants.getJSONObject(0).getString("avatar_path"));
                                        new LoadImageTask(participant1).execute(participants.getJSONObject(0).getString("avatar_path"));
                                        participant2.setVisibility(View.VISIBLE);
                                        if(Configuration.DEBUG)Log.e(TAG,"Participant 2"+participants.getJSONObject(1).getString("avatar_path"));
                                        new LoadImageTask(participant2).execute(participants.getJSONObject(1).getString("avatar_path"));
                                        participant3.setVisibility(View.GONE);
                                        participant4.setVisibility(View.GONE);
                                        participant5.setVisibility(View.GONE);
                                        andMore.setVisibility(View.GONE);

                                    }
                                    else if(participants.length()==3)
                                    {
                                        participant1.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant1).execute(participants.getJSONObject(0).getString("avatar_path"));
                                        participant2.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant2).execute(participants.getJSONObject(1).getString("avatar_path"));
                                        participant3.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant3).execute(participants.getJSONObject(2).getString("avatar_path"));
                                        participant4.setVisibility(View.GONE);
                                        participant5.setVisibility(View.GONE);
                                        andMore.setVisibility(View.GONE);

                                    }
                                    else if(participants.length()==4)
                                    {
                                        participant1.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant1).execute(participants.getJSONObject(0).getString("avatar_path"));
                                        participant2.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant2).execute(participants.getJSONObject(1).getString("avatar_path"));
                                        participant3.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant3).execute(participants.getJSONObject(2).getString("avatar_path"));
                                        participant4.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant4).execute(participants.getJSONObject(3).getString("avatar_path"));
                                        participant5.setVisibility(View.GONE);
                                        andMore.setVisibility(View.GONE);

                                    }
                                    else if(participants.length()==5)
                                    {
                                        participant1.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant1).execute(participants.getJSONObject(0).getString("avatar_path"));
                                        participant2.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant2).execute(participants.getJSONObject(1).getString("avatar_path"));
                                        participant3.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant3).execute(participants.getJSONObject(2).getString("avatar_path"));
                                        participant4.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant4).execute(participants.getJSONObject(3).getString("avatar_path"));
                                        participant5.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant5).execute(participants.getJSONObject(4).getString("avatar_path"));
                                        andMore.setVisibility(View.GONE);
                                    }
                                    else if(participants.length()>5)
                                    {
                                        participant1.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant1).execute(participants.getJSONObject(0).getString("avatar_path"));
                                        participant2.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant2).execute(participants.getJSONObject(1).getString("avatar_path"));
                                        participant3.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant3).execute(participants.getJSONObject(2).getString("avatar_path"));
                                        participant4.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant4).execute(participants.getJSONObject(3).getString("avatar_path"));
                                        participant5.setVisibility(View.VISIBLE);
                                        new LoadImageTask(participant5).execute(participants.getJSONObject(4).getString("avatar_path"));
                                        andMore.setVisibility(View.VISIBLE);
                                        andMore.setText("+"+(participants.length()-5));

                                    }

                                }
                                else
                                {
                                    participantsHolder.setVisibility(View.GONE);
                                    typeIcon.setText(Configuration.FA_UNLOCK);
                                    typeIcon.setTextColor(getResources().getColor(R.color.candeo_checked_green));
                                    typeText.setText("PUBLIC");
                                    typeText.setTextColor(getResources().getColor(R.color.candeo_checked_green));

                                }

                                lastTimestamp="now";
                                GetShoutDiscussionsRequest request= new GetShoutDiscussionsRequest(id);
                                request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                CandeoApplication.getInstance().getAppRequestQueue().add(request);
                            }
                            catch (JSONException jse)
                            {
                                if(Configuration.DEBUG)Log.e(TAG,"json ex is "+jse.getLocalizedMessage());
                            }
                            catch (Exception e)
                            {
                                if(Configuration.DEBUG)Log.e(TAG,"ex is "+e.getLocalizedMessage());
                            }

                            }
                        }

                        ,
                                new Response.ErrorListener()

                        {
                            @Override
                            public void onErrorResponse (VolleyError error){
                            if (Configuration.DEBUG) Log.e(TAG, "Error occured");
                            if (Configuration.DEBUG)
                                Log.e(TAG, "localized error while fetching is shout " + error.getLocalizedMessage());
                            NetworkResponse response = error.networkResponse;
                            if (response != null) {
                                if (Configuration.DEBUG)
                                    Log.e(TAG, "Actual error while fetching leaderboard is " + new String(response.data));
                            }

                        }

                        }

                        );
                    }

            @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            String secret="";
            if (Preferences.isUserLoggedIn(getApplicationContext()) && !TextUtils.isEmpty(Preferences.getUserEmail(getApplicationContext()))) {
                params.put("email", Preferences.getUserEmail(getApplicationContext()));
                secret=Preferences.getUserApiKey(getApplicationContext());

            }
            String message = String.format(GET_SHOUT_CONTENT_RELATIVE_API,id);
                params.put("message", message);
            if(Configuration.DEBUG)Log.e(TAG,"secret->"+secret);
            String hash = Security.generateHmac(secret, message);
            if(Configuration.DEBUG)Log.e(TAG,"hash->"+hash);
            params.put("Authorization", "Token token=" + hash);
            return params;
        }
    }

    private class GetShoutDiscussionsRequest extends JsonObjectRequest
    {
        private String id;
        public GetShoutDiscussionsRequest(String id)
        {
            super(Method.GET,
                    String.format(GET_SHOUT_CONTENT_DISCUSSION_API,id,lastTimestamp),
                    new JSONObject(),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {
                                try {

                                    JSONArray array = response.getJSONArray("discussions");
                                    if(Configuration.DEBUG)Log.e(TAG,"ARRAY IS "+array.toString());
                                    discussions.clear();
                                    if(array.length()>0)
                                    {
                                        for(int index=0; index<array.length();++index)
                                        {
                                            JSONObject object= array.getJSONObject(index);
                                            HashMap<String,String> discussion = new HashMap<>();
                                            discussion.put("id",object.getString("id"));
                                            discussion.put("discussion",object.getJSONObject("discussion").getString("text"));
                                            discussion.put("name",object.getString("name"));
                                            discussion.put("user_id",object.getString("user_id"));

                                            discussions.add(discussion);
                                            if(index == array.length()-1)
                                            {
                                                lastTimestamp=object.getString("created_at");
                                            }
                                            if(Configuration.DEBUG)Log.e(TAG,"last shout discussions timestamp "+lastTimestamp);
                                        }

                                        if(Configuration.DEBUG)Log.e(TAG,"Discussions list is "+discussions.size());


                                        if(mShoutDiscussionsAdapter == null)
                                        {
                                            mShoutDiscussionsAdapter= new ShoutDiscussionsAdapter(getApplicationContext());
                                            mShoutDiscussionsAdapter.addAllToList(discussions, false);
                                            shoutDisscussionList.setAdapter(mShoutDiscussionsAdapter);
                                        }
                                        else
                                        {
                                            mShoutDiscussionsAdapter.addAllToList(discussions, true);
                                        }

                                        Log.e(TAG, "Adapter length " + mShoutDiscussionsAdapter.getItemCount());


                                    }
                                    refreshLayout.setRefreshing(false);


                                }
                                catch (JSONException jse)
                                {
                                    jse.printStackTrace();
                                    refreshLayout.setRefreshing(false);
                                }
                                postMessage.setEnabled(true);

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(Configuration.DEBUG)Log.e(TAG, "error is " + error.getLocalizedMessage());
                            postMessage.setEnabled(true);
                        }
                    });

            this.id=id;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            if (Preferences.isUserLoggedIn(getApplicationContext()) && !TextUtils.isEmpty(Preferences.getUserEmail(getApplicationContext()))) {
                String secret="";
                params.put("email", Preferences.getUserEmail(getApplicationContext()));
                secret=Preferences.getUserApiKey(getApplicationContext());
                String message = String.format(GET_SHOUT_CONTENT_DISCUSSION_RELATIVE_API,id,lastTimestamp);
                params.put("message", message);
                if(Configuration.DEBUG)Log.e(TAG,"secret->"+secret);
                String hash = Security.generateHmac(secret, message);
                if(Configuration.DEBUG)Log.e(TAG,"hash->"+hash);
                params.put("Authorization", "Token token=" + hash);

            }

            return params;
        }
    }

    class PostMessageRequest extends JsonObjectRequest
    {
        private Map<String, String> payload;
        public PostMessageRequest(final Map<String,String> payload)
        {
            super(Method.POST,
                    POST_SHOUT_MESSAGE_API,
                    new JSONObject(payload),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(Configuration.DEBUG)Log.e(TAG,"success");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(Configuration.DEBUG)Log.e(TAG,"failed "+error.getLocalizedMessage());
                        }
                    });
            this.payload=payload;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            if (Preferences.isUserLoggedIn(getApplicationContext()) && !TextUtils.isEmpty(Preferences.getUserEmail(getApplicationContext()))) {
                String secret="";
                params.put("email", Preferences.getUserEmail(getApplicationContext()));
                secret=Preferences.getUserApiKey(getApplicationContext());
                String message = POST_SHOUT_MESSAGE_RELATIVE_API+"|"+new JSONObject(payload).toString();
                params.put("message", message);
                if(Configuration.DEBUG)Log.e(TAG,"secret->"+secret);
                String hash = Security.generateHmac(secret, message);
                if(Configuration.DEBUG)Log.e(TAG,"hash->"+hash);
                params.put("Authorization", "Token token=" + hash);
            }
            return params;
        }
    }

    private class LoadImageTask extends AsyncTask<String, String, Bitmap> {

        private ImageView image;

        public LoadImageTask(ImageView image)
        {
            this.image=image;
        }
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
                if(getApplicationContext()!=null)
                {
                    Animation in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                    image.startAnimation(in);
                }
                image.setImageBitmap(bitmap);
            }
        }
    }
}
