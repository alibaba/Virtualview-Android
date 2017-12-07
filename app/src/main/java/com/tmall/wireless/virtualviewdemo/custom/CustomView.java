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

package com.tmall.wireless.virtualviewdemo.custom;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.libra.expr.common.StringSupport;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.VirtualViewBase;

/**
 * Created by gujicheng on 17/3/30.
 */

public class CustomView extends VirtualViewBase {
    private final static String TAG = "CustomView_TMTEST";

    private int mPropId;
    private int mProp;

    public CustomView(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        StringSupport ss = context.getStringLoader();
        mPropId = ss.getStringId("prop", false);

        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onComDraw(Canvas canvas) {
        super.onComDraw(canvas);

        Log.d(TAG, "onComDraw width:" + mMeasuredWidth + " height:" + mMeasuredHeight);
        canvas.drawRect(0, 0, mMeasuredWidth/2, mMeasuredHeight/2, mPaint);
    }

    @Override
    protected boolean setAttribute(int key, int value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            if (key == mPropId) {
                mProp = mPropId;
                Log.d(TAG, "set prop value:" + value);
            } else {
                ret = false;
            }
        }

        return ret;
    }

    public static class Builder implements IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new CustomView(context, viewCache);
        }
    }

}
