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

package com.tmall.wireless.vaf.virtualview.view.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;

import com.libra.Utils;
import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.ViewCache.Item;

import static com.libra.virtualview.common.ImageCommon.SCALE_TYPE_FIT_XY;

/**
 * Created by gujicheng on 16/8/16.
 */
public abstract class ImageBase extends ViewBase {
    private final static String TAG = "ImageBase_TMTEST";

    public String mSrc;
    public int mScaleType;

    public ImageBase(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mDataTag = "imgUrl";
        mScaleType = SCALE_TYPE_FIT_XY;
    }

    @Override
    public void reset() {
        super.reset();

        mData = null;
    }

    public String getSrc() {
        return mSrc;
    }

    public void setBitmap(Bitmap b) {
        setBitmap(b, true);
    }

    public abstract void setBitmap(Bitmap b, boolean refresh);

    protected BitmapDrawable getImageFromRes(String path) {
        if (!TextUtils.isEmpty(path)) {
            Context context = mContext.getContext();
            int id = context.getResources().getIdentifier(path, "drawable", context.getPackageName());
            if (id > 0) {
                return ((BitmapDrawable) ResourcesCompat.getDrawable(context.getResources(), id, null));
            }
        }

        return null;
    }

    public void loadImage(String path) {
    }

    public void setSrc(String path) {
        if (!TextUtils.equals(mSrc, path)) {
            mSrc = path;
            loadImage(path);
            refresh();
        }
    }

    @Override
    protected boolean setAttribute(int key, String stringValue) {
        boolean ret = super.setAttribute(key, stringValue);
        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_src:
                    if (Utils.isEL(stringValue)) {
                        mViewCache.put(this, StringBase.STR_ID_src, stringValue, Item.TYPE_STRING);
                    } else {
                        mSrc = stringValue;
                    }
                    break;
                default:
                    ret = false;
            }

        }
        return ret;
    }

    @Override
    protected boolean setAttribute(int key, int value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_scaleType:
                    mScaleType = value;
                    break;

                default:
                    ret = false;
            }
        }

        return ret;
    }
}
