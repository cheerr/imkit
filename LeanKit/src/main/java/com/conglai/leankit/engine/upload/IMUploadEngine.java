package com.conglai.leankit.engine.upload;

/**
 * Created by chenwei on 16/7/14.
 */

import android.support.annotation.NonNull;

import com.conglai.leankit.model.message.IMFileMessage;

/**
 * 上传引擎
 * <p>
 * 控制上传上传方式
 * <p>
 * 重写upload函数实现不同的上传逻辑
 */
public interface IMUploadEngine {

    public void upload(@NonNull IMFileMessage imFileMessage);
}
