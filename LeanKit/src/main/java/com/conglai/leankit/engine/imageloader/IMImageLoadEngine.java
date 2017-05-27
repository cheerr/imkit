package com.conglai.leankit.engine.imageloader;

import android.widget.ImageView;

/**
 * Created by chenwei on 16/7/22.
 */

public interface IMImageLoadEngine {

    public void displayImage(ImageView imageView, ImgOpt opt,IMImageLoaderCallback callback);

}
