package com.conglai.leankit.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenwei on 16/7/21.
 */

public class Utils {

    public static String createSignId(String userUid1, String userUid2) {
        if (userUid1.compareTo(userUid2) < 0) {
            return userUid1 + "_" + userUid2;
        } else {
            return userUid2 + "_" + userUid1;
        }
    }


    public static String parseDate(long time, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * 对话框的时间显示规则
     *
     * @param time
     * @return
     */
    public static String getConversationTimeStr(Long time) {
        time = Utils.parseLong(time);
        String today = parseDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm");
        String yesterday = parseDate(System.currentTimeMillis() - 24 * 3600 * 1000, "yyyy-MM-dd HH:mm");
        String timeDay = parseDate(time, "yyyy-MM-dd HH:mm");

        if (today.substring(0, 10).equals(timeDay.substring(0, 10))) {
            return timeDay.substring(11);
        }

        if (yesterday.substring(0, 10).equals(timeDay.substring(0, 10))) {
            return "昨天 " + timeDay.substring(11);
        }

        if (today.substring(0, 4).equals(timeDay.substring(0, 4))) {
            return parseDate(time, "MM月dd日 HH:mm");
        } else {
            return parseDate(time, "yyyy年MM月dd日 HH:mm");
        }
    }

    public static String getStaticMapUrl(double lat, double lon) {
        String locationStr = new DecimalFormat("#.000000").format(lon) + "," + new DecimalFormat("#.000000").format(lat);

        String builder = "http://restapi.amap.com/v3/staticmap?" +
                "key=" + "08ba356d6467f70e0659c103e64fe3ef" +
                "&location=" + locationStr +
                "&size=" + 450 + "*" + 240 +
                "&markers=" + "-1,http://cache.amap.com/lbs/static/cuntom_marker1.png,0:" + locationStr +
                "&zoom=" + "17";
        return builder;
    }

    public static long parseLong(Long value) {
        return value == null ? 0 : value;
    }

    /**
     * 设置margins
     *
     * @param view
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public static void setViewMargins(View view, int left, int top, int right, int bottom) {
        if (view == null || view.getLayoutParams() == null) return;
        if (view.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = ((RelativeLayout.LayoutParams) view.getLayoutParams());
            layoutParams.setMargins(left, top, right, bottom);
            view.requestLayout();
        } else if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams) view.getLayoutParams());
            layoutParams.setMargins(left, top, right, bottom);
            view.requestLayout();
        } else if (view.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams layoutParams = ((FrameLayout.LayoutParams) view.getLayoutParams());
            layoutParams.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }


    public static int dp2px(Context context, float dp) {
        return (int) (Math.abs(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.getResources().getDisplayMetrics())) * dp);
    }

    public static int sp2px(Context context, float sp) {
        return (int) (Math.abs(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1f, context.getResources().getDisplayMetrics())) * sp);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
