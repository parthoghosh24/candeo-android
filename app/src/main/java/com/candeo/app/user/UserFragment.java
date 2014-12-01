package com.candeo.app.user;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.NetworkUtil;

public class UserFragment extends Fragment {


    View root=null;
    TextView icon;
    Button signIn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(!NetworkUtil.isNetworkAvailable(getActivity()))
        {

            root= inflater.inflate(R.layout.fragment_no_connectivity, container, false);
        }
        else
        {
            root=inflater.inflate(R.layout.fragment_user, container, false);
            icon=(TextView)root.findViewById(R.id.candeo_user_icon);
            icon.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fa.ttf"));
            icon.setText("\uf007");
            icon.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
            signIn=(Button)root.findViewById(R.id.candeo_user_sign_in);
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        return root;
    }

}
