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
import android.view.View;
import android.view.ViewGroup;

import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_NONE;
import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_X;
import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_Y;

/**
 * Created by gujicheng on 16/10/27.
 */
public class GridView extends ViewGroup {
    private final static String TAG = "GridView_TMTEST";

    protected final static int DEFAULT_COLUMN_COUNT = 2;
    protected final static int DEFAULT_ITEM_HEIGHT = 0;

    protected int mColumnCount = DEFAULT_COLUMN_COUNT;
    protected int mRowCount;
    protected int mItemHeight = DEFAULT_ITEM_HEIGHT;
    protected int mItemWidth;
    protected int mCalItemHeight;

    protected int mItemHorizontalMargin = 0;
    protected int mItemVerticalMargin = 0;

    protected int mAutoDimDirection = AUTO_DIM_DIR_NONE;
    protected float mAutoDimX = 1;
    protected float mAutoDimY = 1;

    protected boolean isRtl;

    public GridView(Context context) {
        super(context);
    }

    public void setAutoDimDirection(int direction) {
        mAutoDimDirection = direction;
    }

    public void setAutoDimX(float dimX) {
        mAutoDimX = dimX;
    }

    public void setAutoDimY(float dimY) {
        mAutoDimY = dimY;
    }

    public void setColumnCount(int count) {
        mColumnCount = count;
    }

    public void setItemHorizontalMargin(int margin) {
        mItemHorizontalMargin = margin;
    }

    public void setItemVerticalMargin(int margin) {
        mItemVerticalMargin = margin;
    }

    public void setItemHeight(int h) {
        mItemHeight = h;
    }

    public void setRtl(boolean rtl) {
        isRtl = rtl;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        int space = this.getPaddingLeft() + this.getPaddingRight() + mItemHorizontalMargin * (mColumnCount - 1);

        int childCount = this.getChildCount();

//        Log.d(TAG, "width:" + width+ "  widthMode:" + widthMode + "  height:" + height + "  heightMode:" + heightMode);
        mItemWidth = (width - space) / mColumnCount;
        int index = 0;
        if (mItemHeight <= 0) {
            if (childCount > 0) {
                View child = this.getChildAt(index++);
                child.measure(View.MeasureSpec.makeMeasureSpec(mItemWidth, View.MeasureSpec.EXACTLY), heightMeasureSpec);
                mCalItemHeight = child.getMeasuredHeight();
            } else {
                mCalItemHeight = height - this.getPaddingTop() - this.getPaddingBottom();
            }
        } else {
            mCalItemHeight = mItemHeight;
        }

//        Log.d(TAG, "mCalItemHeight:" + mCalItemHeight + "mItemWidth:" + mItemWidth);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(mItemWidth, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(mCalItemHeight, View.MeasureSpec.EXACTLY);
        for (int i = index; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            child.measure(widthSpec, heightSpec);
        }

        mRowCount = (childCount / mColumnCount) + ((childCount % mColumnCount) > 0 ? 1 : 0);
        int h = mRowCount * mCalItemHeight + (mRowCount - 1) * mItemVerticalMargin + this.getPaddingTop() + this.getPaddingBottom();

        int realHeight;
        if (MeasureSpec.UNSPECIFIED == heightMode) {
            realHeight = h;
        } else {
            realHeight = Math.min(height, h);
        }

        setMeasuredDimension(width, realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = this.getChildCount();

        int index = 0;
        int top = this.getPaddingTop();

        int ll = this.getPaddingLeft();
        if (isRtl) {
            ll = r - mItemWidth - this.getPaddingRight();
        }

        for (int row = 0; row < mRowCount; ++row) {
            int left = ll;
            for (int col = 0; col < mColumnCount; ++col) {
                if (index < count) {
                    View child = this.getChildAt(index);
                    child.layout(left, top, left + mItemWidth, top + mCalItemHeight);
                } else {
                    break;
                }
                ++index;
                if (isRtl) {
                    left -= (mItemWidth + mItemHorizontalMargin);
                } else {
                    left += mItemWidth + mItemHorizontalMargin;
                }
            }
            top += mCalItemHeight + mItemVerticalMargin;
        }
    }
}
