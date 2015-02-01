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

public class SocialFragment extends Fragment {

    private View noUserSocialContent;
    private static final String TAG="Candeo-User Social";
    private View mRoot;
    private RecyclerView socialList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRoot=inflater.inflate(R.layout.fragment_social, container, false);
        initWidgets();
        return mRoot;
    }

    private void initWidgets()
    {

        noUserSocialContent = mRoot.findViewById(R.id.candeo_user_no_social_content);
        noUserSocialContent.setBackgroundColor(getActivity().getResources().getColor(R.color.background_floating_material_light));
        ((TextView) noUserSocialContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView) noUserSocialContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_USERS);
        ((TextView) noUserSocialContent.findViewById(R.id.candeo_no_content_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));

        ((TextView) noUserSocialContent.findViewById(R.id.candeo_no_content_text)).setText("No Connections yet.");
        ((TextView) noUserSocialContent.findViewById(R.id.candeo_no_content_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));


    }


}
