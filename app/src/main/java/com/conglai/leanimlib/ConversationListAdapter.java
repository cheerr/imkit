package com.conglai.leanimlib;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;

import java.util.List;

/**
 * Created by chenwei on 16/7/20.
 */

public class ConversationListAdapter extends BaseAdapter {

    List<AVIMConversation> convs;
    Context context;

    public ConversationListAdapter(Context context) {
        this.context = context;
    }

    public void setConvs(List<AVIMConversation> convs) {
        this.convs = convs;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return convs == null ? 0 : convs.size();
    }

    @Override
    public Object getItem(int position) {
        return convs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.conversation_item_view, parent, false);
        }
        final AVIMConversation conversation = convs.get(position);

        ((TextView) view.findViewById(R.id.name)).setText(
                "name: " + conversation.getName() + "\n"
                        + "id : " + conversation.getConversationId());
        ((TextView) view.findViewById(R.id.name)).setText(
                "name: " + conversation.getName() + "\n"
                        + "id : " + conversation.getConversationId());
        ((TextView) view.findViewById(R.id.index)).setText("" + (position + 1));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatListActivity.class);
                intent.putExtra("conversationId", conversation.getConversationId());
                context.startActivity(intent);
            }
        });
        return view;
    }

}
