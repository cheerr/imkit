package com.conglai.leanimlib;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.conglai.common.Debug;
import com.conglai.leankit.core.LeanIM;
import com.conglai.leankit.engine.EngineMgr;
import com.conglai.leankit.engine.imageloader.IMImageLoadEngine;
import com.conglai.leankit.engine.imageloader.IMImageLoaderCallback;
import com.conglai.leankit.engine.imageloader.ImgOpt;
import com.conglai.leankit.model.user.IMUser;
import com.conglai.leankit.model.user.IMUserProvider;
import com.conglai.leankit.receiver.CustomClientEventHandler;
import com.conglai.leankit.receiver.CustomMessageHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

/**
 * Created by chenwei on 16/7/14.
 */

public class LeanIMDemoApplication extends Application {


    private static String TAG = "LeanIMDemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
        initLeanIM();
    }


    private void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .resetViewBeforeLoading(false)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .defaultDisplayImageOptions(options)
                .memoryCache(new LRULimitedMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
    }

    private void initLeanIM() {
        Debug.i(TAG, "initLeanIM");
        //leanCloud的注册
        LeanIM.registerApp(this, "14IUSqSbIskzUmzbKogO7Hql-gzGzoHsz", "hNjSprxMrYP2Fm3FaCpaEzlp");
        //LeanIM先注册自身,并且登录
        LeanIM.getInstance().initAndLogin(this, "123456789");

        LeanIM.setImUserProvider(new IMUserProvider() {
            @Override
            public IMUser getIMUser(String userId) {
                return new IMUser(userId);
            }
        });
        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler() {

            @Override
            public void onReceivedMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
                Debug.i(TAG, "onReceivedMessage:" + message.getContent());
            }
        });

        AVIMClient.setClientEventHandler(new CustomClientEventHandler() {

            @Override
            public void onConnectionResume(AVIMClient avimClient) {
                super.onConnectionResume(avimClient);
                Debug.i(TAG, "onConnectionResume:");
            }

            @Override
            public void onConnectionPaused(AVIMClient avimClient) {
                Debug.i(TAG, "onConnectionPaused:");
            }

            @Override
            public void loginAtOther(AVIMClient avimClient) {
                Debug.i(TAG, "loginAtOther");

            }

            @Override
            public void loseClient(AVIMClient avimClient, int code) {
                Debug.i(TAG, "loseClient:" + code);
            }
        });

        EngineMgr.setImageLoadEngine(new IMImageLoadEngine() {

            @Override
            public void displayImage(ImageView imageView, ImgOpt opt, final IMImageLoaderCallback callback) {
                if (!TextUtils.isEmpty(opt.getPath()) && new File(opt.getPath()).exists()) {
                    display("file://" + opt.getPath(), imageView, callback);
                } else if (!TextUtils.isEmpty(opt.getUrl())) {
                    display(opt.getUrl(), imageView, callback);
                } else if (!TextUtils.isEmpty(opt.getKey())) {
                    display("http://static.withme.cn/" + opt.getKey(), imageView, callback);
                } else if (opt.getDefaultImg() != 0) {
                    imageView.setImageResource(opt.getDefaultImg());
                } else {
                    imageView.setImageDrawable(null);
                }
            }
        });
    }

    private void display(String url, ImageView imageView, final IMImageLoaderCallback callback) {

        ImageLoader.getInstance().displayImage(url, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (callback != null) {
                    callback.onFailure(0, failReason.toString());
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (callback != null) {
                    if (loadedImage != null) {
                        callback.onSuccess(loadedImage);
                    } else {
                        callback.onFailure(0, "error");
                    }
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (callback != null) {
                    Bitmap bitmap = view.getDrawingCache();
                    if (bitmap != null) {
                        callback.onSuccess(bitmap);
                    } else {
                        callback.onFailure(0, "error");
                    }
                }
            }
        });
    }
}
