package com.conglai.leankit.model.query;

import com.avos.avoscloud.AVException;

import java.util.List;

/**
 * Created by chenwei on 16/9/27.
 */

public interface CallBack<T> {
    public void done(List<T> list, AVException e);
}
