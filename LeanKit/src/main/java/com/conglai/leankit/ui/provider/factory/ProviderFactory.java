package com.conglai.leankit.ui.provider.factory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.conglai.dblib.android.Message;
import com.conglai.leankit.model.message.MessageFactory;
import com.conglai.leankit.ui.model.UIMessage;
import com.conglai.leankit.ui.provider.item.ImageItemProvider;
import com.conglai.leankit.ui.provider.item.ItemProvider;
import com.conglai.leankit.ui.provider.item.LocationItemProvider;
import com.conglai.leankit.ui.provider.item.TextItemProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by chenwei on 16/7/22.
 */

public class ProviderFactory {

    private static SparseArray<Class<? extends ItemProvider>> providers = new SparseArray<>();

    private static final String TAG = ProviderFactory.class.getSimpleName();

    public static void registerProvider(int messageType, Class<? extends ItemProvider> clazz) {
        providers.put(messageType, clazz);
    }

    static {
        registerProvider(MessageFactory.AVIMMessageType_TEXT, TextItemProvider.class);
        registerProvider(MessageFactory.AVIMMessageType_IMAGE, ImageItemProvider.class);
        registerProvider(MessageFactory.AVIMMessageType_LOCATION, LocationItemProvider.class);
    }

    public static ItemProvider getProvider(Context context, @NonNull Message message) {
        UIMessage uiMessage = new UIMessage();

        Class<? extends ItemProvider> clazz = providers.get(message.getMessageType());
        ItemProvider itemProvider = null;
        try {
            if (clazz != null) {
                Constructor<? extends ItemProvider> cst = clazz.getConstructor(
                        new Class[]{Context.class, UIMessage.class});
                if (cst != null)
                    itemProvider = cst.newInstance(new Object[]{context, uiMessage});
            }
            return itemProvider;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean containProvider(int messageType) {
        return providers.get(messageType) != null;
    }

}
