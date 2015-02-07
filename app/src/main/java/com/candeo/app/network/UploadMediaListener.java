package com.candeo.app.network;

/**
 * Created by partho on 2/2/15.
 */
public interface UploadMediaListener {
    public void onSuccess(String response);
    public void onFailure(String response);
}
