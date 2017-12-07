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

package com.tmall.wireless.vaf.framework.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by gujicheng on 16/12/23.
 */

public class ImageResLoader {
    private final static String TAG = "ImageResLoader_TMTEST";

    private String mBaseTag = "mipmap";

    private Resources mResource;
    private String mPackageName;

    public void setResBaseTag(String str) {
        if (!TextUtils.isEmpty(str)) {
            mBaseTag = str;
        }
    }

    public ImageResLoader(Context context) {
        mResource = context.getResources();
        mPackageName = context.getPackageName();
    }

    public BitmapDrawable getImage(String resName) {
        if (!TextUtils.isEmpty(resName)) {
            int id = mResource.getIdentifier(resName, mBaseTag, mPackageName);
            if (id > 0) {
                return ((BitmapDrawable) ResourcesCompat.getDrawable(mResource, id, null));
            } else {
                Log.e(TAG, "getImage failed:" + resName);
            }
        }

        return null;
    }

}
