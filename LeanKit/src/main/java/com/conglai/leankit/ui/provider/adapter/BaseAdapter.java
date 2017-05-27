package com.conglai.leankit.ui.provider.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    private Context mContext;
    private List<T> mList;
    private List<String> keyList;

    public BaseAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList<>();
        this.keyList = new ArrayList<>();
    }

    protected <T extends View> T findViewById(View view, int id) {
        return (T) view.findViewById(id);
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getList() {
        return mList;
    }

    public int findPosition(T message) {
        int index = this.getCount();
        int position = -1;

        while (index-- > 0) {
            if (String.valueOf(message).equals(String.valueOf(getItem(index)))) {
                position = index;
                break;
            }
        }

        return position;
    }

    public int findPosition(long id) {
        int index = this.getCount();
        int position = -1;

        while (index-- > 0) {
            if (this.getItemId(index) == id) {
                position = index;
                break;
            }
        }

        return position;
    }

    public int findPosition(String key) {
        return keyList != null ? keyList.indexOf(key) : -1;
    }


    public void setCollection(Collection<T> collection) {
        this.mList.clear();
        addCollection(collection);
    }

    public void addCollection(Collection<T> collection) {
        if (collection != null) {
            this.mList.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addCollection(int pos, Collection<T> collection) {
        if (collection != null) {
            this.mList.addAll(pos, collection);
            notifyDataSetChanged();
        }
    }

    public void addCollection(T... collection) {
        if (collection != null && collection.length > 0)
            addCollection(Arrays.asList(collection));
    }

    public void add(T t) {
        this.mList.add(t);
    }

    public void add(int position, T t) {
        this.mList.add(position, t);
    }

    public void set(int position, T t) {
        if (position < mList.size() && t != null) {
            this.mList.set(position, t);
        } else {
            this.mList.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        this.keyList.clear();
        for (T t : mList) {
            keyList.add(getItemKey(t));
        }
        super.notifyDataSetChanged();
    }

    public void remove(int position) {
        if (position >= 0 && position < mList.size())
            this.mList.remove(position);
    }

    public void removeAll() {
        this.mList.clear();
    }

    public void clear() {
        this.mList.clear();
    }

    public int getCount() {
        return this.mList == null ? 0 : this.mList.size();
    }

    public T getItem(int position) {
        return this.mList == null ? null : (position >= this.mList.size() ? null : this.mList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public final View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = this.newView(this.mContext, position, parent);
        }

        this.bindView(view, position, this.getItem(position));
        return view;
    }

    protected abstract String getItemKey(T message);

    protected abstract View newView(Context var1, int var2, ViewGroup var3);

    protected abstract void bindView(View var1, int var2, T var3);
}