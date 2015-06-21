package com.candeo.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by partho on 18/6/15.
 */
public class ShoutNetworkAdapter extends RecyclerView.Adapter<ShoutNetworkAdapter.ShoutNetworkHolder>{


    @Override
    public ShoutNetworkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ShoutNetworkHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ShoutNetworkHolder extends RecyclerView.ViewHolder
    {
        public ShoutNetworkHolder(View itemLayoutView)
        {
            super(itemLayoutView);
        }
    }
}
