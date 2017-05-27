package com.conglai.leankit.engine;

import com.conglai.leankit.engine.imageloader.IMImageLoadEngine;
import com.conglai.leankit.engine.imageloader.IMImageLoader;
import com.conglai.leankit.engine.upload.IMMessageUpload;
import com.conglai.leankit.engine.upload.IMUploadEngine;

/**
 * Created by chenwei on 16/7/25.
 */

public class EngineMgr {

    public static void setImageLoadEngine(IMImageLoadEngine imageLoadEngine) {
        IMImageLoader.setImageLoadEngine(imageLoadEngine);
    }

    public static void setUploadEngine(IMUploadEngine imUploadEngine) {
        IMMessageUpload.setUploadEngine(imUploadEngine);
    }
}

