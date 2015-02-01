package com.candeo.app.user;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class CreatedFragment extends Fragment {


    private View noUserCreatedContent;
    private static final String TAG="Candeo-User Creation";
    private View mRoot;
    private RecyclerView createdList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRoot=inflater.inflate(R.layout.fragment_created, container, false);
        initWidgets();
        return mRoot;
    }

    private void initWidgets()
    {

        noUserCreatedContent = mRoot.findViewById(R.id.candeo_user_no_created_content);
        noUserCreatedContent.setBackgroundColor(getActivity().getResources().getColor(R.color.background_floating_material_light));
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/fa.ttf"));
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_MAGIC);
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));

        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_text)).setText("No content created yet.");
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));


    }

}
