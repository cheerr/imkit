package com.conglai.leankit.model.query;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.List;

/**
 * Created by chenwei on 16/11/5.
 */

public class QueryWrapper {

    private boolean isAccepted = false;

    private QueryWrapper() {
    }

    private void findInBackground(AVQuery<AVObject> query, final FindCallback<AVObject> callback) {
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                doneCallback(list, e, callback);
            }
        });
    }

    private synchronized void doneCallback(List<AVObject> list, AVException e, FindCallback<AVObject> callback) {
        if (!isAccepted && callback != null) {
            isAccepted = true;
            callback.done(list, e);

            if (e != null) {
                AVAnalytics.onEvent(AVOSCloud.applicationContext, "QueryWrapperError", "" + e.getMessage());
            }
        }
    }

    public synchronized static void wrapperQuery(AVQuery<AVObject> query, FindCallback<AVObject> callback) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.findInBackground(query, callback);
    }
}
