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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.libra.virtualview.common.LayoutCommon;
import com.libra.virtualview.common.ViewBaseCommon;

import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_NONE;

/**
 * Created by longerian on 2018/3/11.
 *
 * @author longerian
 * @date 2018/03/11
 */

public class NFrameLayoutView extends ViewGroup {

    private static final String TAG = "NFrameLayoutView_TMTEST";

    private List<View> mMatchParentView = new ArrayList<>();

    private int mBorderTopLeftRadius = 0;
    private int mBorderTopRightRadius = 0;
    private int mBorderBottomLeftRadius = 0;
    private int mBorderBottomRightRadius = 0;
    private int mBorderWidth = 0;
    private int mBorderColor = Color.BLACK;
    protected int mAutoDimDirection = AUTO_DIM_DIR_NONE;
    protected float mAutoDimX = 1;
    protected float mAutoDimY = 1;

    public NFrameLayoutView(Context context) {
        super(context);
    }

    public NFrameLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NFrameLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAutoDimDirection > 0) {
            switch (mAutoDimDirection) {
                case ViewBaseCommon.AUTO_DIM_DIR_X:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(widthMeasureSpec)) {
                        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(widthMeasureSpec) * mAutoDimY) / mAutoDimX), View.MeasureSpec.EXACTLY);
                    }
                    break;

                case ViewBaseCommon.AUTO_DIM_DIR_Y:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(heightMeasureSpec)) {
                        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(heightMeasureSpec) * mAutoDimX) / mAutoDimY), View.MeasureSpec.EXACTLY);
                    }
                    break;
                default:
                    break;
            }
        }

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        mMatchParentView.clear();

        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            LayoutParams p = (LayoutParams)child.getLayoutParams();
            if (((View.MeasureSpec.EXACTLY != heightMode) && (LayoutCommon.MATCH_PARENT == p.height)) ||
                ((View.MeasureSpec.EXACTLY != widthMode) && (LayoutCommon.MATCH_PARENT == p.width))) {
                mMatchParentView.add(child);
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(getRealWidth(widthMode, width),
            getRealHeight(heightMode, height));

        if (mMatchParentView.size() > 0) {
            for (View vb : mMatchParentView) {
                measureChild(vb, View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
                    View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(),
                    View.MeasureSpec.EXACTLY));
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            int w = child.getMeasuredWidth();
            int h = child.getMeasuredHeight();

            LayoutParams childP = (LayoutParams) child.getLayoutParams();

            int ll;
            if (0 != (childP.mLayoutGravity & ViewBaseCommon.H_CENTER)) {
                ll = (r + l - w) >> 1;
            } else if (0 != (childP.mLayoutGravity & ViewBaseCommon.RIGHT)) {
                ll = r - getPaddingRight() - childP.rightMargin - w - mBorderWidth;
            } else {
                ll = l + getPaddingLeft() + childP.leftMargin + mBorderWidth;
            }

            int tt;
            if (0 != (childP.mLayoutGravity & ViewBaseCommon.V_CENTER)) {
                tt = (b + t - h) >> 1;
            } else if (0 != (childP.mLayoutGravity & ViewBaseCommon.BOTTOM)) {
                tt = b - h - getPaddingBottom() - childP.bottomMargin - mBorderWidth;
            } else {
                tt = t + getPaddingTop() + childP.topMargin + mBorderWidth;
            }

            child.layout(ll, tt, ll + w, tt + h);
        }
    }

    private int getRealWidth(int mode, int size) {
        int ret = size;
        if (View.MeasureSpec.AT_MOST == mode) {
            int childrenWidth = 0;
            for (int i = 0, count = getChildCount(); i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                int w = child.getMeasuredWidth(); //TODO margin
                if (w > childrenWidth) {
                    childrenWidth = w;
                }
            }

            childrenWidth += getPaddingLeft() + getPaddingRight() + (mBorderWidth << 1);

            ret = Math.min(size, childrenWidth);
        } else if (View.MeasureSpec.EXACTLY == mode) {
            ret = size;
        } else {
            Log.e(TAG, "getRealWidth error mode:" + mode);
        }

        return ret;
    }

    private int getRealHeight(int mode, int size) {
        int ret = size;
        if (View.MeasureSpec.AT_MOST == mode) {
            int childrenHeight = 0;
            for (int i = 0, count = getChildCount(); i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                int h = child.getMeasuredHeight(); //TODO margin
                if (h > childrenHeight) {
                    childrenHeight = h;
                }
            }

            childrenHeight += getPaddingTop() + getPaddingBottom() + (mBorderWidth << 1);

            ret = Math.min(size, childrenHeight);
        } else if (View.MeasureSpec.EXACTLY == mode) {
            ret = size;
        } else {
            int childrenHeight = 0;
            for (int i = 0, count = getChildCount(); i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                int h = child.getMeasuredHeight(); //TODO margin
                if (h > childrenHeight) {
                    childrenHeight = h;
                }
            }

            childrenHeight += getPaddingTop() + getPaddingBottom() + (mBorderWidth << 1);

            ret = childrenHeight;
        }

        return ret;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public int mLayoutGravity;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

    }
}
