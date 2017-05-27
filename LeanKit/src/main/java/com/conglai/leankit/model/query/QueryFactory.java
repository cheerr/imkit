package com.conglai.leankit.model.query;

import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;

/**
 * Created by chenwei on 16/7/20.
 */

public class QueryFactory {

    /**
     * 获取会话查询
     *
     * @param client
     * @return
     */
    public static AVIMConversationQuery obtainConversationQuery(AVIMClient client) {
        AVIMConversationQuery query = client.getQuery();
        query.setQueryPolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.setLimit(50);
        query.whereExists("updatedAt");
        query.whereEqualTo("c", client.getClientId());
        return query;
    }
}
