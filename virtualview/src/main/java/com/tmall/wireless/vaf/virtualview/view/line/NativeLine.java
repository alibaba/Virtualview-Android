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

package com.tmall.wireless.vaf.virtualview.view.line;

import android.graphics.Canvas;
import android.view.View;

import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.loader.StringLoader;

/**
 * Created by gujicheng on 16/11/2.
 */

public class NativeLine extends LineBase {
    private final static String TAG = "NativeLine_TMTEST";

    private NativeLineImp mNative;

    public NativeLine(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mNative = new NativeLineImp(context.getContext(), this);
    }

    @Override
    public void destroy() {
        super.destroy();

        mNative.destroy();
        mNative = null;
    }

    public View getNativeView() {
        return mNative;
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();

        mNative.setPaintParam(mLineColor, mLineWidth, mStyle);
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mNative.onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        mNative.onComLayout(changed, l, t, r, b);
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        mNative.measureComponent(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public int getComMeasuredWidth() {
        return mNative.getComMeasuredWidth();
    }

    @Override
    public int getComMeasuredHeight() {
        return mNative.getComMeasuredHeight();
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        mNative.comLayout(l, t, r, b);
    }

//    @Override
//    protected void changeVisibility() {
//        switch (mVisibility) {
//            case INVISIBLE:
//                mNative.setVisibility(View.INVISIBLE);
//                break;
//
//            case VISIBLE:
//                mNative.setVisibility(View.VISIBLE);
//                break;
//
//            case GONE:
//                mNative.setVisibility(View.GONE);
//                break;
//        }
//    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new NativeLine(context, viewCache);
        }
    }
}
