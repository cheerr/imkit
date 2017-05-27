package com.conglai.leankit.engine.imageloader;

import android.graphics.Bitmap;

/**
 * Created by chenwei on 16/7/22.
 */

public interface IMImageLoaderCallback {

    public void onSuccess(Bitmap bitmap);

    public void onFailure(int code, String errorMsg);
}
