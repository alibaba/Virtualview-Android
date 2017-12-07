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

package com.tmall.wireless.vaf.virtualview.view.vh;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.libra.virtualview.common.VHCommon;

/**
 * Created by gujicheng on 16/11/10.
 */

public class VHView extends ViewGroup {
    private final static String TAG = "VHView_TMTEST";

    protected int mOrientation = VHCommon.HORIZONTAL;

    protected int mItemMargin = 0;
    protected int mItemWidth = 0;
    protected int mItemHeight = 0;

    protected int mItemCount = 0;

    public VHView(Context context) {
        super(context);
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setItemMargin(int margin) {
        mItemMargin = margin;
    }

    public void setItemWidth(int w) {
        mItemWidth = w;
    }

    public void setItemHeight(int h) {
        mItemHeight = h;
    }

    private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        if (0 == mItemWidth) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            mItemWidth = width - this.getPaddingLeft() - this.getPaddingRight();
        }

        if (0 == mItemHeight) {
            int space = this.getPaddingTop() + this.getPaddingBottom() + mItemMargin * (mItemCount - 1);
            if (mItemCount > 1) {
                mItemHeight = (height - space) / mItemCount;
            } else {
                mItemHeight = height - space;
            }
        } else if (mItemCount > 0) {
            height = this.getPaddingTop() + this.getPaddingBottom() + (mItemMargin + mItemHeight) * (mItemCount - 1) + mItemHeight;
        }

        int widthSpec = View.MeasureSpec.makeMeasureSpec(mItemWidth, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(mItemHeight, View.MeasureSpec.EXACTLY);
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            child.measure(widthSpec, heightSpec);
        }

        setMeasuredDimension(mItemWidth + this.getPaddingLeft() + this.getPaddingRight(), height);
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if (0 == mItemHeight) {
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            mItemHeight = height - this.getPaddingTop() - this.getPaddingBottom();
        }

        if (0 == mItemWidth) {
            int space = this.getPaddingLeft() + this.getPaddingRight() + mItemMargin * (mItemCount - 1);
            if (mItemCount > 1) {
                mItemWidth = (width - space) / mItemCount;
            } else {
                mItemWidth = width - space;
            }
        } else if (mItemCount > 0) {
            width = this.getPaddingLeft() + this.getPaddingRight() + (mItemMargin + mItemWidth) * (mItemCount - 1) + mItemWidth;
        }

        int widthSpec = View.MeasureSpec.makeMeasureSpec(mItemWidth, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(mItemHeight, View.MeasureSpec.EXACTLY);
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            child.measure(widthSpec, heightSpec);
        }

        setMeasuredDimension(width, mItemHeight + this.getPaddingTop() + this.getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mItemCount = this.getChildCount();

        switch (mOrientation) {
            case VHCommon.VERTICAL:
                measureVertical(widthMeasureSpec, heightMeasureSpec);
                break;

            case VHCommon.HORIZONTAL:
                measureHorizontal(widthMeasureSpec, heightMeasureSpec);
                break;

            default:
                Log.e(TAG, "onMeasure invalidate orientation:" + mOrientation);
                break;
        }
    }

    private void layoutVertical(boolean changed, int l, int t, int r, int b) {
        int left = this.getPaddingLeft();
        int right = left + mItemWidth;
        int top = this.getPaddingTop();
        for (int i = 0; i < mItemCount; ++i) {
            getChildAt(i).layout(left, top, right, top + mItemHeight);
            top += mItemHeight + mItemMargin;
        }
    }

    private void layoutHorizontal(boolean changed, int l, int t, int r, int b) {
        int left = this.getPaddingLeft();
        int top = this.getPaddingTop();
        int bottom = top + mItemHeight;
        for (int i = 0; i < mItemCount; ++i) {
            getChildAt(i).layout(left, top, left + mItemWidth, bottom);
            left += mItemWidth + mItemMargin;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        switch (mOrientation) {
            case VHCommon.VERTICAL:
                layoutVertical(changed, l, t, r, b);
                break;

            case VHCommon.HORIZONTAL:
                layoutHorizontal(changed, l, t, r, b);
                break;

            default:
                Log.e(TAG, "onLayout invalidate orientation:" + mOrientation);
                break;
        }
    }
}
