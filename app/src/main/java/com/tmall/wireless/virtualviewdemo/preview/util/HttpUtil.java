package com.tmall.wireless.virtualviewdemo.preview.util;

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

    public static String getHostIp() {
        return "10.0.2.2";
    }

    public static String getHostUrl() {
        return "http://" + HttpUtil.getHostIp() + ":7788/";
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
