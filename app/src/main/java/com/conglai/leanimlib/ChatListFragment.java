package com.conglai.leanimlib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.conglai.dblib.android.Message;
import com.conglai.leankit.db.MessageDbHelper;
import com.conglai.leankit.ui.IMConversationAdapter;

import java.util.List;

/**
 * Created by chenwei on 16/7/20.
 */

public class ChatListFragment extends Fragment {

    ListView mListView;
    IMConversationAdapter mListAdapter;

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
        mListAdapter = new IMConversationAdapter(getActivity()) {
            @Override
            public void onObserverView(IMConversationAdapter.ViewHolder holder, int position, Message message) {

            }
        };
        mListView.setAdapter(mListAdapter);

        String conversationId = getArguments().getString("conversationId");
        Log.i("ChatListFragment", conversationId);
        List<Message> messages = MessageDbHelper.getInstance(getActivity()).queryShowMessageByConversationId(conversationId);
        mListAdapter.addCollection(messages);
    }


}
