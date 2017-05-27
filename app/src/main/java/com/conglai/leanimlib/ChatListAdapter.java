package com.conglai.leanimlib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.conglai.dblib.android.Message;

import java.util.List;

/**
 * Created by chenwei on 16/7/20.
 */

public class ChatListAdapter extends BaseAdapter {

    private List<Message> list;


    Context context;

    public ChatListAdapter(Context context) {
        this.context = context;
    }


    public void setList(List<Message> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
        Message message = list.get(position);
        ((TextView) view.findViewById(R.id.index)).setText("" + (position + 1));

        ((TextView) view.findViewById(R.id.name)).setText(
                "from: " + message.getFrom() + "\n" +
                        "content: " + message.getContent());

        return view;
    }
}
