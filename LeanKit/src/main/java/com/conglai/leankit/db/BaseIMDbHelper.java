package com.conglai.leankit.db;

import android.content.Context;

import com.conglai.dblib.android.BaseBean;
import com.conglai.dblib.helper.BaseCacheDbHelper;
import com.conglai.dblib.util.Utils;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by chenwei on 16/7/26.
 */
public abstract class BaseIMDbHelper<K extends AbstractDao, T extends BaseBean> extends BaseCacheDbHelper<T> {

    protected BaseIMDbHelper(Context c) {
        super(c);
    }

    public abstract K getDao();

    protected boolean compare(T t, T old) {
        if (t == null || old == null) return false;
        if (t.hashCode() == old.hashCode()) return false;
        return Utils.toStringDo(t).equals(Utils.toStringDo(old));
    }
}
