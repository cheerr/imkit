package com.conglai.leankit.util;

import android.text.TextUtils;

/**
 * Created by chenwei on 16/7/13.
 */

public class TextUtil {


    public static String absText(String string) {
        return string == null ? "" : string;
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return TextUtils.isEmpty(charSequence) || "null".equals(charSequence);
    }

    public static boolean isPiTuKey(String text) {
        if (isEmpty(text)) return false;
        if (text.startsWith("bg_template"))
            return true;
        if (text.contains("."))
            text = text.substring(0, text.lastIndexOf("."));
        if (text.length() != 32) return false;
        for (int i = 0; i < text.length(); i++) {
            if (!(text.charAt(i) <= '9' && text.charAt(i) >= '0') && !(text.charAt(i) <= 'z' && text.charAt(i) >= 'a')) {
                return false;
            }
        }
        return true;
    }
}
