package com.conglai.leankit.engine.imageloader;

import android.widget.ImageView;

import com.conglai.leankit.core.LeanIM;

/**
 * Created by chenwei on 16/7/22.
 */

public class IMImageLoader {

    private static IMImageLoadEngine imImageLoadEngine;
    private static IMImageLoader instance;

    private IMImageLoader() {
    }

    public synchronized static IMImageLoader getInstance() {
        if (instance == null) {
            synchronized (LeanIM.class) {
                if (instance == null) {
                    instance = new IMImageLoader();
                }
            }
        }
        return instance;
    }

    public static void setImageLoadEngine(IMImageLoadEngine imImageLoadEngine) {
        IMImageLoader.imImageLoadEngine = imImageLoadEngine;
    }

    public void displayImage(ImageView imageView, ImgOpt opt, IMImageLoaderCallback callback) {
        if (imImageLoadEngine != null) {
            imImageLoadEngine.displayImage(imageView, opt, callback);
        } else {
            throw new IllegalArgumentException("需要先设置IMImageLoader!!!");
        }
    }

    public void displayImage(ImageView imageView, ImgOpt opt) {
        displayImage(imageView, opt, null);
    }
}
