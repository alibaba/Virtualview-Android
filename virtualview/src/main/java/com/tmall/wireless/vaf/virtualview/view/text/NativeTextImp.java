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

package com.tmall.wireless.vaf.virtualview.view.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.widget.TextView;

import com.tmall.wireless.vaf.virtualview.Helper.VirtualViewUtils;
import com.tmall.wireless.vaf.virtualview.core.IView;

/**
 * Created by gujicheng on 16/8/15.
 */
public class NativeTextImp extends TextView implements IView {

    private int mBackgroundColor = Color.TRANSPARENT;
    private int mBorderTopLeftRadius = 0;
    private int mBorderTopRightRadius = 0;
    private int mBorderBottomLeftRadius = 0;
    private int mBorderBottomRightRadius = 0;
    private int mBorderWidth = 0;
    private int mBorderColor = Color.BLACK;
    private Paint mBackgroundPaint;
    private Paint mBorderPaint;

    public NativeTextImp(Context context) {
        super(context);
        this.getPaint().setAntiAlias(true);
    }

    public void setBorderTopLeftRadius(int borderTopLeftRadius) {
        mBorderTopLeftRadius = borderTopLeftRadius;
    }

    public void setBorderTopRightRadius(int borderTopRightRadius) {
        mBorderTopRightRadius = borderTopRightRadius;
    }

    public void setBorderBottomLeftRadius(int borderBottomLeftRadius) {
        mBorderBottomLeftRadius = borderBottomLeftRadius;
    }

    public void setBorderBottomRightRadius(int borderBottomRightRadius) {
        mBorderBottomRightRadius = borderBottomRightRadius;
    }

    public void setBorderWidth(int borderWidth) {
        mBorderWidth = borderWidth;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        mBackgroundColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBackgroundColor != Color.TRANSPARENT) {
            if (null == mBackgroundPaint) {
                mBackgroundPaint = new Paint();
                mBackgroundPaint.setAntiAlias(true);
            }
            mBackgroundPaint.setColor(mBackgroundColor);
            VirtualViewUtils.drawBackground(canvas, mBackgroundPaint, canvas.getWidth(), canvas.getHeight(), mBorderWidth,
                mBorderTopLeftRadius, mBorderTopRightRadius, mBorderBottomLeftRadius, mBorderBottomRightRadius);
        }
        super.onDraw(canvas);
        if (mBorderWidth > 0) {
            if (null == mBorderPaint) {
                mBorderPaint = new Paint();
                mBorderPaint.setStyle(Paint.Style.STROKE);
                mBorderPaint.setAntiAlias(true);
            }
            mBorderPaint.setStrokeWidth(mBorderWidth);
            mBorderPaint.setColor(mBorderColor);
            VirtualViewUtils.drawBorder(canvas, mBorderPaint, canvas.getWidth(), canvas.getHeight(), mBorderWidth,
                mBorderTopLeftRadius, mBorderTopRightRadius, mBorderBottomLeftRadius, mBorderBottomRightRadius);
        }
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
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        this.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        this.layout(l, t, r, b);
    }
}
