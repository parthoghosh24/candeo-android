package com.candeo.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by partho on 18/6/15.
 */
public class ShoutListAdapter extends RecyclerView.Adapter<ShoutListAdapter.ShoutListHolder>{


    @Override
    public ShoutListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ShoutListHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ShoutListHolder extends RecyclerView.ViewHolder
    {
        public ShoutListHolder(View itemLayoutView)
        {
            super(itemLayoutView);
        }
    }
}
