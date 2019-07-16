package com.tmall.wireless.virtualviewdemo.preview.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created on 2018/3/1.
 * Description:
 *
 * @author bianyue
 */
public class HttpUtil {
    private static final String DEFAULT_IP = "10.0.2.2";
    private static final String SP_NAME = "virtualviewdemo";
    private static final String SP_IP = "sp_ip";

    public static void setHostIp(Context context, String ip) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(SP_IP, ip);
        editor.apply();
    }

    public static String getHostIp(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SP_IP,
                DEFAULT_IP);
    }

    public static String getHostUrl(Context context) {
        return "http://" + HttpUtil.getHostIp(context) + ":7788/";
    }

    public static String getFirstPath(String str) {
        URI uri = null;
        try {
            uri = new URI(str);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (uri != null) {
            String path = uri.getPath();
            if (!TextUtils.isEmpty(path)) {
                String[] pathes = path.split("/");
                if (pathes != null && pathes.length > 1) {
                    return pathes[1];
                }
            }
        }
        return "";
    }
}
