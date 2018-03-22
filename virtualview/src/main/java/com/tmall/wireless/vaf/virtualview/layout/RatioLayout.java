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

package com.tmall.wireless.vaf.virtualview.layout;

import android.util.Log;
import android.view.View;

import com.libra.virtualview.common.LayoutCommon;
import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_Y;

/**
 * Created by gujicheng on 16/10/10.
 */
public class RatioLayout extends Layout {
    private final static String TAG = "RatioLayout_TMTEST";

    protected int mOrientation;
    protected int mMeasureChildrenWidth;
    protected int mMeasureChildrenHeight;

    protected int mTotalRatio;
    protected int mFixDim;

    public RatioLayout(VafContext context, ViewCache vc) {
        super(context, vc);

        mOrientation = ViewBaseCommon.HORIZONTAL;
        mMeasureChildrenWidth = 0;
        mMeasureChildrenHeight = 0;
    }

    @Override
    public Params generateParams() {
        return new Params();
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAutoDimDirection > 0) {
            switch (mAutoDimDirection) {
                case ViewBaseCommon.AUTO_DIM_DIR_X:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(widthMeasureSpec)) {
                        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(widthMeasureSpec) * mAutoDimY) / mAutoDimX), View.MeasureSpec.EXACTLY);
                    }
                    break;

                case AUTO_DIM_DIR_Y:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(heightMeasureSpec)) {
                        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(heightMeasureSpec) * mAutoDimX) / mAutoDimY), View.MeasureSpec.EXACTLY);
                    }
                    break;
                default:
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
            default:
                break;
        }
    }

    protected void measureHorizontalRatioComChild(ViewBase child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        Params childParam = (Params) child.getComLayoutParams();

        int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                mPaddingTop + mPaddingBottom + (mBorderWidth << 1) + childParam.mLayoutMarginTop + childParam.mLayoutMarginBottom, childParam.mLayoutHeight);

        int childWidthMeasureSpec;
        if (childParam.mLayoutRatio > 0) {
            childWidthMeasureSpec = getRatioChildMeasureSpec(parentWidthMeasureSpec,
                    mPaddingLeft + mPaddingRight + (mBorderWidth << 1), childParam.mLayoutWidth, childParam.mLayoutRatio);
        } else {
            childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                    mPaddingLeft + mPaddingRight + (mBorderWidth << 1) + childParam.mLayoutMarginLeft + childParam.mLayoutMarginRight, childParam.mLayoutWidth);
        }

        child.measureComponent(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    protected void measureVerticalRatioComChild(ViewBase child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        Params childParam = (Params) child.getComLayoutParams();

        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight + (mBorderWidth << 1) + childParam.mLayoutMarginLeft + childParam.mLayoutMarginRight, childParam.mLayoutWidth);

        int childHeightMeasureSpec;
        if (childParam.mLayoutRatio > 0) {
            childHeightMeasureSpec = getRatioChildMeasureSpec(parentHeightMeasureSpec,
                    mPaddingTop + mPaddingBottom + (mBorderWidth << 1), childParam.mLayoutHeight, childParam.mLayoutRatio);
        } else {
            childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                    mPaddingTop + mPaddingBottom + (mBorderWidth << 1) + childParam.mLayoutMarginTop + childParam.mLayoutMarginBottom, childParam.mLayoutHeight);
        }

        child.measureComponent(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    protected int getRatioChildMeasureSpec(int parentSpec, int padding, int childDimension, float ratio) {
        int parentSpecMode = View.MeasureSpec.getMode(parentSpec);
        int parentSpecSize = View.MeasureSpec.getSize(parentSpec);

        int size = Math.max(0, parentSpecSize - padding - mFixDim);

        int resultSize = 0;
        int resultMode = View.MeasureSpec.UNSPECIFIED;

        switch (parentSpecMode) {
            // Parent has imposed an exact size on us
            case View.MeasureSpec.EXACTLY:
                if (ratio > 0) {
                    resultSize = (int)((ratio * size / mTotalRatio));
                    if (resultSize < 0) {
                        resultSize = 0;
                    }
                    resultMode = View.MeasureSpec.EXACTLY;
                } else if (childDimension >= 0) {
                    resultSize = childDimension;
                    resultMode = View.MeasureSpec.EXACTLY;
                }
                break;

            // Parent has imposed a maximum size on us
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
                break;
            default:
                break;
        }

        return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        mFixDim = 0;
        findTotalRatio();

        boolean hasMatchHeight = false;
        for (int i = 0, length = mSubViews.size(); i < length; i++) {
            ViewBase child = mSubViews.get(i);
            if (child.isGone()) {
                continue;
            }
            Params p = (Params) child.getComLayoutParams();
            if (((View.MeasureSpec.EXACTLY != heightMode) && (LayoutCommon.MATCH_PARENT == p.mLayoutHeight)) || p.mLayoutRatio > 0) {
                hasMatchHeight = true;
            }

            measureHorizontalRatioComChild(child, widthMeasureSpec, heightMeasureSpec);

            if (p.mLayoutRatio <= 0) {
                mFixDim += child.getComMeasuredWidthWithMargin();
            } else {
                mFixDim += p.mLayoutMarginLeft + p.mLayoutMarginRight;
            }
        }

        setComMeasuredDimension(getRealWidth(widthMode, width), getRealHeight(heightMode, height));

        // forceUniformHeight
        if (hasMatchHeight) {
            int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getComMeasuredWidth(),
                    View.MeasureSpec.EXACTLY);
            int uniformMeasureHeightSpec = View.MeasureSpec.makeMeasureSpec(getComMeasuredHeight(),
                    View.MeasureSpec.EXACTLY);

            for (int i = 0, length = mSubViews.size(); i < length; i++) {
                ViewBase child = mSubViews.get(i);
                if (child.isGone()) {
                    continue;
                }
                Params p = (Params) child.getComLayoutParams();
                if (LayoutCommon.MATCH_PARENT == p.mLayoutHeight || p.mLayoutRatio > 0) {
                    measureHorizontalRatioComChild(child, uniformMeasureSpec, uniformMeasureHeightSpec);
                }
            }
        }
    }

    private void findTotalRatio() {
        mTotalRatio = 0;
        for (int i = 0, length = mSubViews.size(); i < length; i++) {
            ViewBase child = mSubViews.get(i);
            if (child.isGone()) {
                continue;
            }

            Params p = (Params) child.getComLayoutParams();
            mTotalRatio += p.mLayoutRatio;
        }
    }

    final private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        mFixDim = 0;
        findTotalRatio();

        boolean hasMatchWidth = false;
        for (int i = 0, length = mSubViews.size(); i < length; i++) {
            ViewBase child = mSubViews.get(i);
            if (child.isGone()) {
                continue;
            }

            Params p = (Params) child.getComLayoutParams();
            if (((View.MeasureSpec.EXACTLY != widthMode) && (LayoutCommon.MATCH_PARENT == p.mLayoutWidth)) || p.mLayoutRatio > 0) {
                hasMatchWidth = true;
            }
            measureVerticalRatioComChild(child, widthMeasureSpec, heightMeasureSpec);

            if (p.mLayoutRatio <= 0) {
                mFixDim += child.getComMeasuredHeightWithMargin();
            } else {
                mFixDim += p.mLayoutMarginTop + p.mLayoutMarginBottom;
            }
        }

        setComMeasuredDimension(getRealWidth(widthMode, width),
                getRealHeight(heightMode, height));

        // forceUniformWidth
        if (hasMatchWidth) {
            int uniformMeasureSpec = View.MeasureSpec.makeMeasureSpec(getComMeasuredWidth(),
                    View.MeasureSpec.EXACTLY);
            int uniformMeasureHeightSpec = View.MeasureSpec.makeMeasureSpec(getComMeasuredHeight(),
                    View.MeasureSpec.EXACTLY);

            for (int i = 0, length = mSubViews.size(); i < length; i++) {
                ViewBase child = mSubViews.get(i);
                if (child.isGone()) {
                    continue;
                }

                Params p = (Params) child.getComLayoutParams();
                if (LayoutCommon.MATCH_PARENT == p.mLayoutWidth || p.mLayoutRatio > 0) {
                    measureVerticalRatioComChild(child, uniformMeasureSpec, uniformMeasureHeightSpec);
                }
            }
        }
    }

    private int getRealWidth(int mode, int size) {
        int ret = size;
        if (View.MeasureSpec.AT_MOST == mode) {
            int childrenWidth = 0;

            if (ViewBaseCommon.HORIZONTAL == mOrientation) {
                ret = size;
            } else if (ViewBaseCommon.VERTICAL == mOrientation) {
                for (int i = 0, length = mSubViews.size(); i < length; i++) {
                    ViewBase child = mSubViews.get(i);
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
                ret = Math.min(size, childrenWidth);
            }

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
                for (int i = 0, length = mSubViews.size(); i < length; i++) {
                    ViewBase child = mSubViews.get(i);
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
                ret = Math.min(size, childrenHeight);
            } else if (ViewBaseCommon.VERTICAL == mOrientation) {
                ret = size;
            }

        } else if (View.MeasureSpec.EXACTLY == mode) {
            ret = size;
        } else {
            int childrenHeight = 0;

            if (ViewBaseCommon.HORIZONTAL == mOrientation) {
                for (int i = 0, length = mSubViews.size(); i < length; i++) {
                    ViewBase child = mSubViews.get(i);
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
                ret = childrenHeight;
            } else if (ViewBaseCommon.VERTICAL == mOrientation) {
                for (int i = 0, length = mSubViews.size(); i < length; i++) {
                    ViewBase child = mSubViews.get(i);
                    if (child.isGone()) {
                        continue;
                    }
                    int h = child.getComMeasuredHeightWithMargin();
                    childrenHeight += h;
                }

                childrenHeight += mPaddingTop + mPaddingBottom + (mBorderWidth << 1);
                ret = childrenHeight;
            }
        }

        return ret;
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        switch (mOrientation) {
            case ViewBaseCommon.HORIZONTAL: {
                int left = l + mPaddingLeft + mBorderWidth;
                for (int i = 0, length = mSubViews.size(); i < length; i++) {
                    ViewBase view = mSubViews.get(i);
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
                        tt = b - h - mPaddingBottom - mBorderWidth- childP.mLayoutMarginBottom;
                    } else {
                        tt = t + mPaddingTop + mBorderWidth+ childP.mLayoutMarginTop;
                    }
                    view.comLayout(left, tt, left + w, tt + h);

                    left += w + childP.mLayoutMarginRight;
                }
                break;
            }

            case ViewBaseCommon.VERTICAL: {
                int top = t + mPaddingTop + mBorderWidth;
                for (int i = 0, length = mSubViews.size(); i < length; i++) {
                    ViewBase view = mSubViews.get(i);
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
        public float mLayoutRatio;
        public int mLayoutGravity;

        public Params() {
            mLayoutRatio = 0;
        }

        @Override
        public boolean setAttribute(int key, float value) {
            boolean ret = super.setAttribute(key, value);

            if (!ret) {
                ret = true;
                switch (key) {
                    case StringBase.STR_ID_layoutRatio:
                        mLayoutRatio = value;
                        break;

                    default:
                        ret = false;
                }
            }

            return ret;
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

                    case StringBase.STR_ID_layoutRatio:
                        mLayoutRatio = value;
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
            return new RatioLayout(context, viewCache);
        }
    }

}
