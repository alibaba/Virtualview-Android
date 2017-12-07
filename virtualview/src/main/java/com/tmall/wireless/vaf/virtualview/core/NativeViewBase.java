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

package com.tmall.wireless.vaf.virtualview.core;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.view.View;

import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;

import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_X;
import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_Y;

/**
 * Created by gujicheng on 17/3/30.
 */

public class NativeViewBase extends ViewBase {
    protected View __mNative;

    public NativeViewBase(VafContext context, ViewCache viewCache) {
        super(context, viewCache);
    }

    @Override
    public void reset() {
        super.reset();
        if (VERSION.SDK_INT >= 16) {
            ((View)__mNative).setBackground(null);
        } else {
            ((View)__mNative).setBackgroundDrawable(null);
        }
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();
        //if (!Float.isNaN(mAlpha)) {
        //    if (mAlpha > 1.0f) {
        //        mAlpha = 1.0f;
        //    } else if (mAlpha < 0.0f) {
        //        mAlpha = 0.0f;
        //    }
        //    ((View)__mNative).setAlpha(mAlpha);
        //}

    }

    protected void setBackgroundColor(int color) {
        ((View)__mNative).setBackgroundColor(color);
    }

    @Override
    protected void setBackgroundImage(Bitmap bmp) {
        if (VERSION.SDK_INT >= 16) {
            ((View)__mNative).setBackground(new BitmapDrawable(mContext.getContext().getResources(), bmp));
        } else {
            ((View)__mNative).setBackgroundDrawable(new BitmapDrawable(mContext.getContext().getResources(), bmp));
        }
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAutoDimDirection > 0) {
            switch (mAutoDimDirection) {
                case AUTO_DIM_DIR_X:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(widthMeasureSpec)) {
                        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(widthMeasureSpec) * mAutoDimY) / mAutoDimX), View.MeasureSpec.EXACTLY);
                    }
                    break;

                case AUTO_DIM_DIR_Y:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(heightMeasureSpec)) {
                        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(heightMeasureSpec) * mAutoDimX) / mAutoDimY), View.MeasureSpec.EXACTLY);
                    }
                    break;
            }
        }
        if (__mNative instanceof IView) {
            ((IView) __mNative).onComMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        if (__mNative instanceof IView) {
            ((IView) __mNative).onComLayout(changed, l, t, r, b);
        }
    }

    @Override
    public int getComMeasuredWidth() {
        if (__mNative instanceof IView) {
            return ((IView) __mNative).getComMeasuredWidth();
        } else {
           return __mNative.getMeasuredWidth();
        }
    }

    @Override
    public int getComMeasuredHeight() {
        if (__mNative instanceof IView) {
            return ((IView) __mNative).getComMeasuredHeight();
        } else {
            return __mNative.getMeasuredHeight();
        }
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAutoDimDirection > 0) {
            switch (mAutoDimDirection) {
                case ViewBaseCommon.AUTO_DIM_DIR_X:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(widthMeasureSpec)) {
                        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(widthMeasureSpec) * mAutoDimY) / mAutoDimX), View.MeasureSpec.EXACTLY);
                    }
                    break;

                case AUTO_DIM_DIR_Y:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(heightMeasureSpec)) {
                        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(heightMeasureSpec) * mAutoDimX) / mAutoDimY), View.MeasureSpec.EXACTLY);
                    }
                    break;
            }
        }
        if (__mNative instanceof IView) {
            ((IView) __mNative).measureComponent(widthMeasureSpec, heightMeasureSpec);
        } else {
            __mNative.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        if (__mNative instanceof IView) {
            ((IView) __mNative).comLayout(l, t, r, b);
        } else {
            __mNative.layout(l, t, r, b);
        }
    }

    @Override
    public View getNativeView() {
        return (View) __mNative;
    }
}
