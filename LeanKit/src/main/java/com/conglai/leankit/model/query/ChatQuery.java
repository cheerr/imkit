package com.conglai.leankit.model.query;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.conglai.common.Debug;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chenwei on 16/7/20.
 */

public class ChatQuery {

    private static final String GROUP_CHAT = "groupChat";

    /**
     * 云端查找
     *
     * @param groupId
     */
    public static void findByGroupId(String groupId, FindCallback<AVObject> callback) {
        AVQuery<AVObject> query = new AVQuery<>(GROUP_CHAT);
        query.whereEqualTo("groupId", groupId);
        query.orderByAscending("chatId");
        QueryWrapper.wrapperQuery(query, callback);
    }

    public static void findByChatId(String chatId, FindCallback<AVObject> callback) {
        AVQuery<AVObject> query = new AVQuery<>(GROUP_CHAT);
        query.whereEqualTo("chatId", chatId);
        query.orderByAscending("groupId");
        QueryWrapper.wrapperQuery(query, callback);
    }

    /**
     * 保存到云端
     *
     * @param signId
     * @param chatId
     */
    public static void save(final String signId, final String chatId) {
        AVQuery<AVObject> signQuery = new AVQuery<>(GROUP_CHAT);
        signQuery.whereEqualTo("groupId", signId);

        AVQuery<AVObject> chatQuery = new AVQuery<>(GROUP_CHAT);
        chatQuery.whereEqualTo("chatId", chatId);

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(signQuery, chatQuery));
        query.limit(1);
        QueryWrapper.wrapperQuery(query, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                Debug.i("ChatQuery", "list  " + list);
                if (list == null || list.size() == 0) {
                    AVObject save = new AVObject(GROUP_CHAT);// 构建对象
                    save.put("groupId", signId);
                    save.put("chatId", chatId);
                    save.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            Debug.i("ChatQuery", "AVException " + e);
                        }
                    });
                }
            }
        });
    }

}
