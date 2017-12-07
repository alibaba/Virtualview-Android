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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.View;

import com.libra.virtualview.common.LayoutCommon;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.virtualview.core.IView;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

import static com.libra.virtualview.common.LineBaseCommon.STYLE_DASH;
import static com.libra.virtualview.common.LineBaseCommon.STYLE_SOLID;
import static com.libra.virtualview.common.ViewBaseCommon.H_CENTER;

/**
 * Created by gujicheng on 16/11/2.
 */

public class NativeLineImp extends View implements IView {
    private final static String TAG = "NLineImp_TMTEST";

    protected Paint mPaint;

    protected LineBase mBase;

    public NativeLineImp(Context context, LineBase base) {
        super(context);
        mPaint = new Paint();
        mBase = base;
    }

    public void destroy() {
        mBase = null;
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setPaintParam(int color, int paintWidth, int style) {
        mPaint.setStrokeWidth(paintWidth);
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);

        switch (style) {
            case STYLE_DASH:
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setPathEffect(new DashPathEffect(mBase.mDashEffect, 1));

                this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                break;

            case STYLE_SOLID:
                mPaint.setStyle(Paint.Style.FILL);
                break;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int selfWidthParam = mBase.getComLayoutParams().mLayoutWidth;
        int selfHeightParam = mBase.getComLayoutParams().mLayoutHeight;
        int measuredWidth = 0;
        int measuredHeight = 0;
        if (widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            if (selfWidthParam == LayoutCommon.MATCH_PARENT) {
                measuredWidth = widthSize;
            } else if (selfWidthParam == LayoutCommon.WRAP_CONTENT) {
                measuredWidth = (int)Math.min(widthSize, mPaint.getStrokeWidth());
            } else {
                measuredWidth = selfWidthParam;
            }
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            if (selfWidthParam == LayoutCommon.MATCH_PARENT) {
                measuredWidth = widthSize;
            } else if (selfWidthParam == LayoutCommon.WRAP_CONTENT) {
                measuredWidth = (int)mPaint.getStrokeWidth();
            } else {
                measuredWidth = selfWidthParam;
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            if (selfHeightParam == LayoutCommon.MATCH_PARENT) {
                measuredHeight = heightSize;
            } else if (selfHeightParam == LayoutCommon.WRAP_CONTENT) {
                measuredHeight = (int)Math.min(heightSize, mPaint.getStrokeWidth());
            } else {
                measuredHeight = selfHeightParam;
            }
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            if (selfHeightParam == LayoutCommon.MATCH_PARENT) {
                measuredHeight = heightSize;
            } else if (selfHeightParam == LayoutCommon.WRAP_CONTENT) {
                measuredHeight = (int)mPaint.getStrokeWidth();
            } else {
                measuredHeight = selfHeightParam;
            }
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = this.getMeasuredWidth();
        int height = this.getComMeasuredHeight();

        Paint backgroundPaint = mBase.getBackgroundPaint();
        if (null != backgroundPaint) {
            canvas.drawRect(0, 0, width, height, backgroundPaint);
        }

        int strokeWidth = (int) mPaint.getStrokeWidth();
        int align = mBase.getAlign();
        if (mBase.horizontal()) {
            int top;
            if (0 != (align & ViewBaseCommon.V_CENTER)) {
                top = height >> 1;
            } else if (0 != (align & ViewBaseCommon.BOTTOM)) {
                top = height - (strokeWidth >> 1);
            } else {
                top = strokeWidth >> 1;
            }
            canvas.drawLine(this.mBase.getComPaddingLeft(), top, width - this.mBase.getComPaddingRight(), top, mPaint);
        } else {
            int left;
            if (0 != (align & H_CENTER)) {
                left = width >> 1;
            } else if (0 != (align & ViewBaseCommon.RIGHT)) {
                left = width - (strokeWidth >> 1);
            } else {
                left = strokeWidth >> 1;
            }
            canvas.drawLine(left, this.mBase.getComPaddingTop(), left, height - this.mBase.getComPaddingBottom(), mPaint);
        }
    }
}
