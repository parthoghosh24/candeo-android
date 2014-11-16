package com.candeo.app.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.candeo.app.ContentActivity;
import com.candeo.app.R;
import com.candeo.app.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView feedView;
    FeedAdapter feedAdapter;
    ArrayList<HashMap<String, String>> feeds;
    private String domain="http://192.168.0.104:3000";
    private String feedsURL = domain+"/api/v1/contents";

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View homeView= inflater.inflate(R.layout.fragment_home, container, false);
        feedView = (ListView)homeView.findViewById(R.id.feed_list);
        feeds = new ArrayList<HashMap<String, String>>();
        new LoadFeeds().execute(feedsURL);
        feedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> feed = feeds.get(position);
                Intent contentIntent = new Intent(getActivity(),ContentActivity.class);
                System.out.println("PUSHIN ID IS :"+feed.get("id"));
                contentIntent.putExtra("contentId",feed.get("id"));
                startActivity(contentIntent);
            }
        });
        return homeView;
    }

    private class LoadFeeds extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching Inspiritions...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            return JSONParser.parseGET(urls[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            pDialog.dismiss();
            try {
                JSONArray array = jsonObject.getJSONArray("contents");
                for(int index=0; index< array.length(); ++index)
                {
                    JSONObject content = array.getJSONObject(index);
                    HashMap<String, String> feedMap = new HashMap<String, String>();
                    feedMap.put("id", content.optString("id"));
                    feedMap.put("desc", content.optString("desc"));
                    feedMap.put("username", content.optString("username"));
                    feedMap.put("timestamp", content.optString("time"));
                    feeds.add(feedMap);
                    feedAdapter= new FeedAdapter(getActivity());
                    feedView.setAdapter(feedAdapter);
                }
                System.out.println("Feeds Size in post exec: "+feeds.size());
            }catch (JSONException je)
            {
                je.printStackTrace();
            }



        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class FeedAdapter extends BaseAdapter
    {

        Activity activity;

        FeedAdapter(Activity activity)
        {
            this.activity=activity;
        }

        @Override
        public int getCount() {
            System.out.println("Feeds Size: "+feeds.size());
            return feeds.size();
        }

        class ViewHolder
        {
            TextView description;
            TextView username;
            TextView time;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            HashMap<String, String> feed = feeds.get(position);
            if(convertView == null)
            {
                convertView= LayoutInflater.from(activity).inflate(R.layout.feed_item, parent, false);
                holder = new ViewHolder();
                holder.description = (TextView)convertView.findViewById(R.id.content_description);
                holder.username = (TextView)convertView.findViewById(R.id.username);
                holder.time = (TextView)convertView.findViewById(R.id.timestamp);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.description.setText(feed.get("desc"));
            holder.username.setText(feed.get("username"));
            holder.time.setText(feed.get("timestamp"));
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return feeds.get(position);
        }
    }

}
