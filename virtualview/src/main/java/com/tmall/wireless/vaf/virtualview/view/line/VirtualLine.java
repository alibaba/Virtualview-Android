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

package com.tmall.wireless.vaf.virtualview.view.line;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import com.libra.virtualview.common.LineBaseCommon;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

import static com.libra.virtualview.common.LineBaseCommon.STYLE_DASH;
import static com.libra.virtualview.common.LineBaseCommon.STYLE_SOLID;

/**
 * Created by gujicheng on 16/8/19.
 */
public class VirtualLine extends LineBase {
    private final static String TAG = "VirtualLine_TMTEST";

    protected VirtualViewImp mImp = new VirtualViewImp();

    protected Path mPath;

    public VirtualLine(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mImp.setAntiAlias(true);
        mImp.setViewBase(this);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();

        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setColor(mLineColor);

        switch (mStyle) {
            case LineBaseCommon.STYLE_DASH:
                if (mPath == null) {
                    mPath = new Path();
                }
                mPath.reset();
                mPaint.setStyle(Paint.Style.STROKE);
                PathEffect effects = new DashPathEffect(mDashEffect, 1);
                mPaint.setPathEffect(effects);
                break;

            case LineBaseCommon.STYLE_SOLID:
                mPaint.setStyle(Paint.Style.FILL);
                break;
            default:
                break;
        }
    }

    public void setColor(int color) {
        mLineColor = color;
        mPaint.setColor(mLineColor);
        refresh();
    }

    @Override
    protected void onComDraw(Canvas canvas) {
        super.onComDraw(canvas);

        int strokeWidth = (int) mPaint.getStrokeWidth();
        if (mIsHorizontal) {
            int top;
            if (0 != (mGravity & ViewBaseCommon.V_CENTER)) {
                top = mMeasuredHeight >> 1;
            } else if (0 != (mGravity & ViewBaseCommon.BOTTOM)) {
                top = mMeasuredHeight - (strokeWidth >> 1);
            } else {
                top = strokeWidth >> 1;
            }
            if (mStyle == STYLE_SOLID) {
                canvas.drawLine(mPaddingLeft, top, mMeasuredWidth - mPaddingRight, top, mPaint);
            } else if (mStyle == STYLE_DASH) {
                mPath.moveTo(mPaddingLeft, top);
                mPath.lineTo(mMeasuredWidth - mPaddingRight, top);
                canvas.drawPath(mPath, mPaint);
            }
        } else {
            int left;
            if (0 != (mGravity & ViewBaseCommon.H_CENTER)) {
                left = mMeasuredWidth >> 1;
            } else if (0 != (mGravity & ViewBaseCommon.RIGHT)) {
                left = mMeasuredWidth - (strokeWidth >> 1);
            } else {
                left = strokeWidth >> 1;
            }
            if (mStyle == STYLE_SOLID) {
                canvas.drawLine(left, mPaddingTop, left, mMeasuredHeight - mPaddingBottom, mPaint);
            } else if (mStyle == STYLE_DASH) {
                mPath.moveTo(left, mPaddingTop);
                mPath.lineTo(left, mMeasuredHeight - mPaddingBottom);
                canvas.drawPath(mPath, mPaint);
            }
        }
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mImp.onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        mImp.measureComponent(widthMeasureSpec, heightMeasureSpec);
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new VirtualLine(context, viewCache);
        }
    }

}
