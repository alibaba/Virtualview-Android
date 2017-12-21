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

package com.tmall.wireless.vaf.virtualview.view.grid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.tmall.wireless.vaf.virtualview.container.ClickHelper;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.IView;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

/**
 * Created by gujicheng on 16/10/27.
 */
public class GridImp extends GridView implements IView, IContainer {
    private final static String TAG = "GridImp_TMTEST";

    protected ViewBase mVirtualView;
    protected Paint mBackgroundPaint;
    protected int mBGColor = Color.TRANSPARENT;

    public GridImp(Context context) {
        super(context);
    }

    @Override
    public void setBackgroundColor(int color) {
        mBGColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBGColor != Color.TRANSPARENT) {
            int borderWidth = mVirtualView.getBorderWidth();
            if (null == mBackgroundPaint) {
                mBackgroundPaint = new Paint();
                mBackgroundPaint.setColor(mBGColor);
            }
            canvas.drawRect(borderWidth, borderWidth, mVirtualView.getComMeasuredWidth() - borderWidth, mVirtualView.getComMeasuredHeight() - borderWidth, mBackgroundPaint);
        }
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        this.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        this.layout(l, t, r, b);
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        this.onLayout(changed, l, t, r, b);
    }

    @Override
    public int getComMeasuredWidth() {
        return this.getMeasuredWidth();
    }

    @Override
    public int getComMeasuredHeight() {
        return this.getMeasuredHeight();
    }

    @Override
    public void attachViews() {
    }

    @Override
    public void setVirtualView(ViewBase view) {
        if (null != view) {
            mVirtualView = view;
            mVirtualView.setHoldView(this);

            if (mVirtualView.shouldDraw()) {
                setWillNotDraw(false);
            }

            new ClickHelper(this);
        }

    }

    @Override
    public ViewBase getVirtualView() {
        return mVirtualView;
    }

    @Override
    public View getHolderView() {
        return this;
    }

    @Override
    public void destroy() {
    }

    @Override
    public int getType() {
        return -1;
    }
}
