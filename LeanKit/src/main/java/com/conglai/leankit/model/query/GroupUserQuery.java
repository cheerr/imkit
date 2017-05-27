package com.conglai.leankit.model.query;

import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.conglai.common.Debug;
import com.conglai.leankit.core.IMConversationIdGetListener;
import com.conglai.leankit.core.LeanIM;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chenwei on 16/8/5.
 */

public class GroupUserQuery {

    private static final String GROUP_USER = "groupUser";


    /**
     * 每次本地group状态改变的时候调用
     *
     * @param uid
     * @param groupId
     * @param status
     */
    public static void save(final String uid, final String groupId, final int status) {
        LeanIM.getInstance().obtainConversationIdByGroupId(groupId, new IMConversationIdGetListener() {
            @Override
            public void onGet(final String conversationId) {
                if (TextUtils.isEmpty(conversationId)) return;
                AVQuery<AVObject> uidQuery = new AVQuery<>(GROUP_USER);
                uidQuery.whereEqualTo("uid", uid);

                AVQuery<AVObject> chatQuery = new AVQuery<>(GROUP_USER);
                chatQuery.whereEqualTo("conId", conversationId);

                AVQuery<AVObject> query = AVQuery.and(Arrays.asList(uidQuery, chatQuery));

                QueryWrapper.wrapperQuery(query, new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        Debug.i("ChatQuery", "list  " + list);
                        if (list == null || list.size() == 0) {
                            AVObject save = new AVObject(GROUP_USER);// 构建对象
                            save.put("uid", uid);
                            save.put("conId", conversationId);
                            save.put("status", status);
                            save.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    Debug.i("ChatQuery", "AVException " + e);
                                }
                            });
                        } else {
                            for (AVObject avObject : list) {
                                avObject.put("status", status);
                                avObject.saveInBackground();
                            }
                        }
                    }
                });
            }
        });
    }

}
