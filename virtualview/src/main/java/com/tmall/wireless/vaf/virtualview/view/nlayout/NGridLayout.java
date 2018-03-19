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

package com.tmall.wireless.vaf.virtualview.view.nlayout;

import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.NativeViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.layout.GridLayout;

/**
 * Created by longerian on 2018/3/11.
 *
 * @author longerian
 * @date 2018/03/11
 */

public class NGridLayout extends GridLayout implements INativeLayout {

    private final static String TAG = "NGridLayout_TMTEST";

    private NativeLayoutImpl mNative;

    public NGridLayout(VafContext context,
        ViewCache viewCache) {
        super(context, viewCache);
        mNative = new NativeLayoutImpl(context.getContext());
        mNative.setVirtualView(this);
    }

    public View getNativeView() {
        return mNative;
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public void comDraw(Canvas canvas) {
    }

    @Override
    protected void onComDraw(Canvas canvas) {
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mNative.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        mNative.onLayout(changed, l, t, r, b);
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        mNative.layout(l, t, r, b); //layout itself
    }

    @Override
    public void onLayoutMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onLayoutLayout(boolean changed, int l, int t, int r, int b) {
        super.onComLayout(changed, l, t, r, b);
    }

    @Override
    public void layoutDraw(Canvas canvas) {
        super.comDraw(canvas);
    }

    public static class Builder implements IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new NGridLayout(context, viewCache);
        }
    }
}
