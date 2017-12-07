/*
 * MIT License
 *
 * Copyright (c) 2017 Alibaba Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tmall.wireless.vaf.virtualview.Helper;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gujicheng on 16/8/11.
 */
public class ImageHelper {
    private final static String TAG = "ImageHelper";
    private Map<String, BitmapDrawable> mCache = new HashMap<>();
//    private ImageLoader mImageLoader;
    private Context mContext;

    private static ImageHelper sThis = null;

    public interface Listener {
        void onImageComplete(String url, BitmapDrawable image);
        void onImageFailed(String url);
    }

    public static ImageHelper getInstance() {
        if (null == sThis) {
            sThis = new ImageHelper();
        }
        return sThis;
    }

    private ImageHelper() {
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void getImage(String url, final Listener lis) {
        BitmapDrawable ret = null;
        if (!TextUtils.isEmpty(url)) {
            ret = mCache.get(url);
            if (null != ret) {
                if (null != lis) {
                    lis.onImageComplete(url, ret);
                }
            }
        }
    }
}
