package com.conglai.leankit.engine.upload;

import android.support.annotation.NonNull;

import com.conglai.leankit.core.LeanIM;
import com.conglai.leankit.model.message.IMFileMessage;

/**
 * Created by chenwei on 16/7/14.
 */
public class IMMessageUpload {

    private static IMUploadEngine uploadEngine;
    private static IMMessageUpload instance;

    private IMMessageUpload() {
    }

    public synchronized static IMMessageUpload getInstance() {
        if (instance == null) {
            synchronized (LeanIM.class) {
                if (instance == null) {
                    instance = new IMMessageUpload();
                }
            }
        }
        return instance;
    }

    /**
     * 设置上传引擎
     *
     * @param uploadEngine
     */
    public static void setUploadEngine(IMUploadEngine uploadEngine) {
        IMMessageUpload.uploadEngine = uploadEngine;
    }

    /**
     * @param imFileMessage
     */
    public void upload(@NonNull IMFileMessage imFileMessage) {
        if (uploadEngine != null) {
            uploadEngine.upload(imFileMessage);
        } else {
            throw new IllegalArgumentException("需要先设置UploadEngine!!!");
        }
    }
}
