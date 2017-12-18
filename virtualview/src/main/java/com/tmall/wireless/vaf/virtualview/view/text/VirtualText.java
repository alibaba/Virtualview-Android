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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;

import com.libra.virtualview.common.TextBaseCommon;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.Helper.VirtualViewUtils;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

/**
 * Created by gujicheng on 16/8/18.
 */
public class VirtualText extends TextBase {
    private final static String TAG = "VirtualText_TMTEST";

    protected int mTextHeight = 0;
    protected int mDescent;
    protected String mDrawText = "";
    protected VirtualViewImp mImp = new VirtualViewImp();

    public VirtualText(VafContext context, ViewCache viewCache) {
        super(context, viewCache);
        mImp.setAntiAlias(true);
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();
        if (mBorderWidth > 0) {
            if (mBorderPaint == null) {
                mBorderPaint = new Paint();
                mBorderPaint.setStyle(Paint.Style.STROKE);
                mBorderPaint.setAntiAlias(true);
            }
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }

        if (0 != (mTextStyle & TextBaseCommon.BOLD)) {
            mPaint.setFakeBoldText(true);
        }
        if (0 != (mTextStyle & TextBaseCommon.STRIKE)) {
            mPaint.setStrikeThruText(true);
        }
        if (0 != (mTextStyle & TextBaseCommon.ITALIC)) {
            mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        }

        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);

        Paint.FontMetricsInt fm = mPaint.getFontMetricsInt();
        mTextHeight = fm.descent - fm.ascent;
        mDescent = fm.descent;

        mDrawText = mText;

        if (!TextUtils.isEmpty(mText)) {
            setText(mText);
        } else {
            setText("");
        }
    }

    @Override
    public void reset() {
        super.reset();

        mImp.reset();
        mDrawText = mText;
    }

    @Override
    public void setText(String text) {
        mDrawText = text;
        super.setText(text);
    }

    @Override
    public void setData(Object data) {
        super.setData(data);

        if (data instanceof String) {
            mDrawText = (String) data;
            if (mIsDrawed) {
                refresh();
            }
        } else {
            Log.e(TAG, "setData type error:" + data);
        }
    }

    public void setTextSize(int size) {
        if (mTextSize != size) {
            mTextSize = size;
            refresh();
        }
    }

    public int getTextSize() {
        return mTextSize;
    }

    @Override
    protected void onComDraw(Canvas canvas) {
        super.onComDraw(canvas);

        if (null == mContentRect) {
            makeContentRect();
        }

        if (null != mContentRect) {
            int left = mPaddingLeft;
            if (0 != (mGravity & ViewBaseCommon.RIGHT)) {
                left = mMeasuredWidth - mContentRect.width() - mPaddingLeft - mPaddingRight;
            } else if (0 != (mGravity & ViewBaseCommon.H_CENTER)) {
                left = (mMeasuredWidth - mContentRect.width()) / 2;
            }

            int top;
            if (0 != (mGravity & ViewBaseCommon.BOTTOM)) {
                top = mMeasuredHeight - mPaddingBottom;
            } else if (0 != (mGravity & ViewBaseCommon.V_CENTER)) {
                Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
                top = (mMeasuredHeight - fontMetrics.bottom - fontMetrics.top) / 2 + mDescent;
            } else {
                top = mContentRect.height() + mPaddingTop;
            }

            canvas.drawText(mDrawText, left, top - mDescent, mPaint);

            if (mBorderWidth > 0) {
                VirtualViewUtils.drawBorder(canvas, mBorderPaint, mMeasuredWidth, mMeasuredHeight, mBorderWidth,
                    mBorderTopLeftRadius, mBorderTopRightRadius, mBorderBottomLeftRadius, mBorderBottomRightRadius);
            }

        } else {
            Log.w(TAG, "skip draw text");
        }
    }

    @Override
    protected void makeContentRect() {
        float width;
        width = mPaint.measureText(mDrawText);
        if (mContentRect == null) {
            mContentRect = new Rect(0, 0, (int)width, mTextHeight);
        } else {
            mContentRect.set(0, 0, (int)width, mTextHeight);
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
            return new VirtualText(context, viewCache);
        }
    }

}
