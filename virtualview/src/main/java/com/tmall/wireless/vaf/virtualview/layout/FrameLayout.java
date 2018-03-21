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

import com.libra.virtualview.common.LayoutCommon;
import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

import java.util.ArrayList;
import java.util.List;

import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_Y;

/**
 * Created by gujicheng on 16/8/22.
 */
public class FrameLayout extends Layout {
    private final static String TAG = "FrameLayout_TMTEST";
    private List<ViewBase> mMatchParentView = new ArrayList<>();

    public FrameLayout(VafContext context, ViewCache viewCache) {
        super(context, viewCache);
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

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        mMatchParentView.clear();

        for (int i = 0, size = mSubViews.size(); i < size; i++) {
            ViewBase child = mSubViews.get(i);
            if (child.isGone()) {
                continue;
            }
            Layout.Params p = child.getComLayoutParams();
            if (((View.MeasureSpec.EXACTLY != heightMode) && (LayoutCommon.MATCH_PARENT == p.mLayoutHeight)) ||
                    ((View.MeasureSpec.EXACTLY != widthMode) && (LayoutCommon.MATCH_PARENT == p.mLayoutWidth))) {
                mMatchParentView.add(child);
            }
            measureComChild(child, widthMeasureSpec, heightMeasureSpec);
        }

        setComMeasuredDimension(getRealWidth(widthMode, width),
                getRealHeight(heightMode, height));

        if (mMatchParentView.size() > 0) {
            for (int i = 0, length = mMatchParentView.size(); i < length; i++) {
                ViewBase vb = mMatchParentView.get(i);
                measureComChild(vb, View.MeasureSpec.makeMeasureSpec(mMeasuredWidth,
                        View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(mMeasuredHeight,
                        View.MeasureSpec.EXACTLY));
            }
        }
    }

    private int getRealWidth(int mode, int size) {
        int ret = size;
        if (View.MeasureSpec.AT_MOST == mode) {
            int childrenWidth = 0;
            for (int i = 0, length = mSubViews.size(); i < length; i++) {
                ViewBase child = mSubViews.get(i);
                if (child.isGone()) {
                    continue;
                }

                int w = child.getComMeasuredWidthWithMargin();
                if (w > childrenWidth) {
                    childrenWidth = w;
                }
            }

            childrenWidth += mPaddingLeft + mPaddingRight + (mBorderWidth << 1);

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

            childrenHeight += mPaddingTop + mPaddingBottom + (mBorderWidth << 1);

            ret = Math.min(size, childrenHeight);
        } else if (View.MeasureSpec.EXACTLY == mode) {
            ret = size;
        } else {
            int childrenHeight = 0;
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

            childrenHeight += mPaddingTop + mPaddingBottom + (mBorderWidth << 1);

            ret = childrenHeight;
        }

        return ret;
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0, length = mSubViews.size(); i < length; i++) {
            ViewBase child = mSubViews.get(i);
            if (child.isGone()) {
                continue;
            }

            int w = child.getComMeasuredWidth();
            int h = child.getComMeasuredHeight();

            Params childP = (Params) child.getComLayoutParams();

            int ll;
            if (0 != (childP.mLayoutGravity & ViewBaseCommon.H_CENTER)) {
                ll = (r + l - w) >> 1;
            } else if (0 != (childP.mLayoutGravity & ViewBaseCommon.RIGHT)) {
                ll = r - mPaddingRight - childP.mLayoutMarginRight - w - mBorderWidth;
            } else {
                ll = l + mPaddingLeft + childP.mLayoutMarginLeft + mBorderWidth;
            }

            int tt;
            if (0 != (childP.mLayoutGravity & ViewBaseCommon.V_CENTER)) {
                tt = (b + t - h) >> 1;
            } else if (0 != (childP.mLayoutGravity & ViewBaseCommon.BOTTOM)) {
                tt = b - h - mPaddingBottom - childP.mLayoutMarginBottom - mBorderWidth;
            } else {
                tt = t + mPaddingTop + childP.mLayoutMarginTop + mBorderWidth;
            }

            child.comLayout(ll, tt, ll + w, tt + h);
        }
    }

    public static class Params extends Layout.Params {
        public int mLayoutGravity;

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
            return new FrameLayout(context, viewCache);
        }
    }

}
