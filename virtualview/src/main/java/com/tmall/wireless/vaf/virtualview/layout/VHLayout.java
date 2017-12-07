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

package com.tmall.wireless.vaf.virtualview.layout;

import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import com.libra.virtualview.common.LayoutCommon;
import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

import static com.libra.virtualview.common.ViewBaseCommon.V_CENTER;

/**
 * Created by gujicheng on 16/8/16.
 */
public class VHLayout extends Layout {
    private final static String TAG = "VHLayout_TMTEST";

    public int mOrientation;

    protected int mMeasureChildrenWidth;
    protected int mMeasureChildrenHeight;

    public VHLayout(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mOrientation = ViewBaseCommon.HORIZONTAL;
    }

    @Override
    public Params generateParams() {
        return new Params();
    }

    private int getChildrenWidth() {
        if (mMeasureChildrenWidth <= 0) {
            mMeasureChildrenWidth = 0;
            for (ViewBase child : mSubViews) {
                mMeasureChildrenWidth += child.getComMeasuredWidthWithMargin();
            }
        }

        return mMeasureChildrenWidth;
    }

    private int getChildrenHeight() {
        if (mMeasureChildrenHeight <= 0) {
            mMeasureChildrenHeight = 0;
            for (ViewBase child : mSubViews) {
                mMeasureChildrenHeight += child.getComMeasuredHeightWithMargin();
            }
        }

        return mMeasureChildrenHeight;
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        switch (mOrientation) {
            case ViewBaseCommon.HORIZONTAL: {
                int left = 0;
                if (0 != (mGravity & ViewBaseCommon.LEFT)) {
                    left = l + mPaddingLeft + mBorderWidth;
                } else if (0 != (mGravity & ViewBaseCommon.H_CENTER)) {
                    left = (r - l - getChildrenWidth()) >> 1;
                } else {
                    left = (r - getChildrenWidth() - mPaddingRight - mBorderWidth);
                }

                for (ViewBase view : mSubViews) {
                    if (view.isGone()) {
                        continue;
                    }
                    Params childP = (Params) view.getComLayoutParams();
                    int w = view.getComMeasuredWidth();
                    int h = view.getComMeasuredHeight();
                    left += childP.mLayoutMarginLeft;

                    int tt;
                    if (0 != (childP.mLayoutGravity & ViewBaseCommon.V_CENTER)) {
                        tt = (b + t - h) >> 1;
                    } else if (0 != (childP.mLayoutGravity & ViewBaseCommon.BOTTOM)) {
                        tt = b - h - mPaddingBottom - mBorderWidth - childP.mLayoutMarginBottom;
                    } else {
                        tt = t + mPaddingTop + mBorderWidth + childP.mLayoutMarginTop;
                    }
                    view.comLayout(left, tt, left + w, tt + h);

                    left += w + childP.mLayoutMarginRight;
                }
                break;
            }

            case ViewBaseCommon.VERTICAL: {
                int top;
                if (0 != (mGravity & ViewBaseCommon.TOP)) {
                    top = t + mPaddingTop + mBorderWidth;
                } else if (0 != (mGravity & V_CENTER)) {
                    top = (b + t - getChildrenHeight()) >> 1;
                } else {
                    top = (b - getChildrenHeight() - mPaddingBottom - mBorderWidth);
                }

                for (ViewBase view : mSubViews) {
                    if (view.isGone()) {
                        continue;
                    }

                    Params childP = (Params) view.getComLayoutParams();
                    int w = view.getComMeasuredWidth();
                    int h = view.getComMeasuredHeight();
                    top += childP.mLayoutMarginTop;

                    int ll;
                    if (0 != (childP.mLayoutGravity & ViewBaseCommon.H_CENTER)) {
                        ll = (r + l - w) >> 1;
                    } else if (0 != (childP.mLayoutGravity & ViewBaseCommon.RIGHT)) {
                        ll = r - mPaddingRight - mBorderWidth - childP.mLayoutMarginRight - w;
                    } else {
                        ll = l + mPaddingLeft + mBorderWidth + childP.mLayoutMarginLeft;
                    }

                    view.comLayout(ll, top, ll + w, top + h);

                    top += h + childP.mLayoutMarginBottom;
                }
                break;
            }
        }
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureChildrenWidth = 0;
        mMeasureChildrenHeight = 0;

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
            }
        }

        switch (mOrientation) {
            case ViewBaseCommon.VERTICAL:
                measureVertical(widthMeasureSpec, heightMeasureSpec);
                break;

            case ViewBaseCommon.HORIZONTAL:
                measureHorizontal(widthMeasureSpec, heightMeasureSpec);
                break;
        }
    }

    final private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        boolean hasMatchWidth = false;
        for (ViewBase child : mSubViews) {
            if(!child.isGone()) {
                Params p = (Params) child.getComLayoutParams();
                if ((View.MeasureSpec.EXACTLY != widthMode) && (LayoutCommon.MATCH_PARENT == p.mLayoutWidth)) {
                    hasMatchWidth = true;
                }
                measureComChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }

        setComMeasuredDimension(getRealWidth(widthMode, width),
                getRealHeight(heightMode, height));

        // forceUniformWidth
        if (hasMatchWidth) {
            int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getComMeasuredWidth(),
                    View.MeasureSpec.EXACTLY);

            for (ViewBase child : mSubViews) {
                if (!child.isGone()) {
                    Layout.Params p = child.getComLayoutParams();
                    if (LayoutCommon.MATCH_PARENT == p.mLayoutWidth) {
                        measureComChild(child, uniformMeasureSpec, heightMeasureSpec);
                    }
                }
            }
        }
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        boolean hasMatchHeight = false;
        int consumedWidth = 0;
        for (ViewBase child : mSubViews) {
            if (!child.isGone()) {
                Layout.Params p = child.getComLayoutParams();
                if ((View.MeasureSpec.EXACTLY != heightMode) && (LayoutCommon.MATCH_PARENT == p.mLayoutHeight)) {
                    hasMatchHeight = true;
                }
                if (widthMode != MeasureSpec.UNSPECIFIED) {
                    measureComChild(child, MeasureSpec.makeMeasureSpec(width - consumedWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
                } else {
                    measureComChild(child, widthMeasureSpec, heightMeasureSpec);
                }
                consumedWidth += child.getComMeasuredWidthWithMargin();
            }
        }

        setComMeasuredDimension(getRealWidth(widthMode, width),
                getRealHeight(heightMode, height));

        // forceUniformHeight
        if (hasMatchHeight) {
            int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getComMeasuredHeight(),
                    View.MeasureSpec.EXACTLY);

            for (ViewBase child : mSubViews) {
                if (!child.isGone()) {
                    Layout.Params p = child.getComLayoutParams();
                    if (LayoutCommon.MATCH_PARENT == p.mLayoutHeight) {
                        measureComChild(child, widthMeasureSpec, uniformMeasureSpec);
                    }
                }
            }
        }
    }

    private int getRealWidth(int mode, int size) {
        int ret = size;
        if (View.MeasureSpec.AT_MOST == mode) {
            int childrenWidth = 0;

            if (ViewBaseCommon.HORIZONTAL == mOrientation) {
                for (ViewBase child : mSubViews) {
                    if (child.isGone()) {
                        continue;
                    }
                    childrenWidth += child.getComMeasuredWidthWithMargin();
                }
                mMeasureChildrenWidth = childrenWidth;
                childrenWidth += mPaddingLeft + mPaddingRight + (mBorderWidth << 1);
            } else if (ViewBaseCommon.VERTICAL == mOrientation) {
                for (ViewBase child : mSubViews) {
                    if (child.isGone()) {
                        continue;
                    }
                    int h = child.getComMeasuredWidthWithMargin();
                    if (h > childrenWidth) {
                        childrenWidth = h;
                    }
                }

                mMeasureChildrenWidth = childrenWidth;
                childrenWidth += mPaddingLeft + mPaddingRight + (mBorderWidth << 1);
            }

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

            if (ViewBaseCommon.HORIZONTAL == mOrientation) {
                for (ViewBase child : mSubViews) {
                    if (child.isGone()) {
                        continue;
                    }
                    int h = child.getComMeasuredHeightWithMargin();
                    if (h > childrenHeight) {
                        childrenHeight = h;
                    }
                }
                mMeasureChildrenHeight = childrenHeight;
                childrenHeight += mPaddingTop + mPaddingBottom + (mBorderWidth << 1);
            } else if (ViewBaseCommon.VERTICAL == mOrientation) {
                for (ViewBase child : mSubViews) {
                    if (child.isGone()) {
                        continue;
                    }
                    childrenHeight += child.getComMeasuredHeightWithMargin();
                }
                mMeasureChildrenHeight = childrenHeight;

                childrenHeight += mPaddingTop + mPaddingBottom + (mBorderWidth << 1);
            }

            ret = Math.min(size, childrenHeight);
        } else if (View.MeasureSpec.EXACTLY == mode) {
            ret = size;
        } else {
            int childrenHeight = 0;

            if (ViewBaseCommon.HORIZONTAL == mOrientation) {
                for (ViewBase child : mSubViews) {
                    if (child.isGone()) {
                        continue;
                    }
                    int h = child.getComMeasuredHeightWithMargin();
                    if (h > childrenHeight) {
                        childrenHeight = h;
                    }
                }
                mMeasureChildrenHeight = childrenHeight;

                childrenHeight += mPaddingTop + mPaddingBottom + (mBorderWidth << 1);
            } else if (ViewBaseCommon.VERTICAL == mOrientation) {
                for (ViewBase child : mSubViews) {
                    if (child.isGone()) {
                        continue;
                    }
                    childrenHeight += child.getComMeasuredHeightWithMargin();
                }
                mMeasureChildrenHeight = childrenHeight;

                childrenHeight += mPaddingTop + mPaddingBottom + (mBorderWidth << 1);
            }

            ret = childrenHeight;
        }

        return ret;
    }

    @Override
    protected boolean setAttribute(int key, int value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_orientation:
                    mOrientation = value;
                    break;

                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    public static class Params extends Layout.Params {
        public int mLayoutGravity;

        public Params() {
        }

        @Override
        public boolean setAttribute(int key, int value) {
            boolean ret = super.setAttribute(key, value);

            if (!ret) {
                ret = true;
                switch (key) {
                    case StringBase.STR_ID_layoutGravity:
                        mLayoutGravity = value;
                        break;

                    default:
                        ret = false;
                }
            }

            return ret;
        }
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new VHLayout(context, viewCache);
        }
    }
}
