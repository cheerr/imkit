package com.conglai.leanimlib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.conglai.leankit.core.LeanIM;
import com.conglai.leankit.model.query.QueryFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by chenwei on 16/7/20.
 */

public class ConversationListFragment extends Fragment {

    ListView mListView;
    ConversationListAdapter mListAdapter;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view;
        if ((view = getView()) == null) {
            view = inflater.inflate(R.layout.conversation_list_view, container, false);
        }
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (ListView) getView().findViewById(android.R.id.list);
        mListAdapter = new ConversationListAdapter(getActivity());
        mListView.setAdapter(mListAdapter);

        LeanIM.getInstance().obtainClient(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    AVIMConversationQuery query = QueryFactory.obtainConversationQuery(client);
                    query.orderByDescending("updatedAt");
                    query.findInBackground(new AVIMConversationQueryCallback() {
                        @Override
                        public void done(List<AVIMConversation> convs, AVIMException e) {
                            if (e == null) {
                                if (convs != null && convs.size() > 1) {
                                    Collections.sort(convs, new Comparator<AVIMConversation>() {
                                        @Override
                                        public int compare(AVIMConversation a, AVIMConversation b) {
                                            return b.getUpdatedAt().compareTo(a.getUpdatedAt());
                                        }
                                    });
                                }
                                mListAdapter.setConvs(convs);
                            }
                        }
                    });
                }
            }
        });
    }

}
