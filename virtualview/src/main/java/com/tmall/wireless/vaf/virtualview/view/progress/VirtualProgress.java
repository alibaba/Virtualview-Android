/*
 * MIT License
 *
 * Copyright (c) 2018 Alibaba Group
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

package com.tmall.wireless.vaf.virtualview.view.progress;

import android.graphics.Canvas;
import android.graphics.Color;

import com.libra.Utils;
import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.VirtualViewBase;

/**
 * Created by gujicheng on 16/12/19.
 */

public class VirtualProgress extends VirtualViewBase {
    private final static String TAG = "Progress_TMTEST";

    public final static int TYPE_Normal = 1;

    private int mType = TYPE_Normal;
    private int mInitPos = 0;
    private int mProgressColor = Color.BLUE;

    private int mCur = 0;
    private int mTotal = 0;

    public VirtualProgress(VafContext context, ViewCache viewCache) {
        super(context, viewCache);
    }

    public void setProgress(int cur, int total) {
        if (mCur != cur) {
            mCur = cur;
            mTotal = total;

            refresh();
        }
    }

    @Override
    public void reset() {
        super.reset();

        mInitPos = 0;
        mCur = 0;
        mTotal = 0;
    }

    @Override
    protected void onComDraw(Canvas canvas) {
        super.onComDraw(canvas);

        int dis = mInitPos;
        if (mCur > 0) {
            dis += ((mMeasuredWidth - mInitPos - mPaddingLeft - mPaddingRight) * mCur) / mTotal;
        }

        if (dis > 0) {
            canvas.drawRect(mPaddingLeft, mPaddingTop, dis + mPaddingLeft, mMeasuredHeight - mPaddingBottom, mPaint);
        }
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();

        switch (mType) {
            case TYPE_Normal:
                break;
        }
    }

    @Override
    protected boolean setAttribute(int key, float value) {
        boolean ret = super.setAttribute(key, value);
        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_initValue:
                    mInitPos = Utils.dp2px(value);
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
                case StringBase.STR_ID_type:
                    mType = value;
                    break;

                case StringBase.STR_ID_initValue:
                    mInitPos = Utils.dp2px(value);
                    break;

                case StringBase.STR_ID_color:
                    mProgressColor = value;
                    mPaint.setColor(mProgressColor);
                    break;

                default:
                    ret = false;
            }
        }

        return ret;
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new VirtualProgress(context, viewCache);
        }
    }

}
