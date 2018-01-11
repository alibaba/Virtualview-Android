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
import com.libra.Utils;
import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.Helper.RtlHelper;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.ViewCache.Item;
import com.tmall.wireless.vaf.virtualview.layout.RatioLayout.Params;

import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_X;
import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_Y;

/**
 * Created by gujicheng on 16/8/16.
 */
public class GridLayout extends Layout {
    final static private String TAG = "GridLayout_TMTEST";

    protected int mColCount;
    protected int mItemWidth;
    protected int mItemHeight;
    protected int mItemHorizontalMargin = 0;
    protected int mItemVerticalMargin = 0;

    public GridLayout(VafContext context, ViewCache vc) {
        super(context, vc);
        mColCount = 1;
        mItemHeight = -1;
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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

        int space = mPaddingLeft + mPaddingRight + mItemHorizontalMargin * (mColCount - 1);
        mItemWidth = (width - space) / mColCount;

        for (ViewBase child : mSubViews) {
            if (child.isGone()) {
                continue;
            }
            if (mItemHeight > 0) {
                child.measureComponent(View.MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY));
            } else {
                GridLayout.Params childParam = child.getComLayoutParams();
                child.measureComponent(View.MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY),
                    Layout.getChildMeasureSpec(heightMeasureSpec,
                        mPaddingLeft + mPaddingRight + (mBorderWidth << 1) + childParam.mLayoutMarginLeft
                            + childParam.mLayoutMarginRight, childParam.mLayoutHeight));
            }
            //measureComChild(child, View.MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.AT_MOST), heightMeasureSpec);
        }

        setComMeasuredDimension(getRealWidth(widthMode, width),
                getRealHeight(heightMode, height));
    }

    private int getRealWidth(int mode, int size) {
        int ret = size;
        if (View.MeasureSpec.AT_MOST == mode) {
            int childrenWidth = 0;
            Params p = (Params) mParams;

            childrenWidth = mPaddingLeft + mPaddingRight;
            int count = 0;
            for (ViewBase child : mSubViews) {
                childrenWidth += child.getComMeasuredWidthWithMargin();
                if (++count >= mColCount) {
                    break;
                } else {
                    childrenWidth += mItemHorizontalMargin;
                }
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

        switch (mode) {
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED: {
                int h = 0;
                if (mSubViews.size() > 0) {
                    int count = mSubViews.size();
                    int row = count / mColCount + ((count % mColCount) > 0 ? 1 : 0);
                    if (mItemHeight > 0) {
                        h = row * mItemHeight + mPaddingTop + mPaddingBottom + (row - 1) * mItemVerticalMargin;
                    } else {
                        h = row * (mSubViews.get(0).getComMeasuredHeight()) + mPaddingTop + mPaddingBottom + (row - 1) * mItemVerticalMargin;
                    }
                }

                if (View.MeasureSpec.AT_MOST == mode) {
                    ret = Math.min(size, h);
                } else {
                    ret = h;
                }
                break;
            }
        }

        return ret;
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        resolveRtlPropertiesIfNeeded();

        if (mSubViews.size() > 0) {
            Params p = (Params) mParams;
            int left = l + mPaddingLeft;
            int top = t + mPaddingTop;

            int count = mSubViews.size();
            ViewBase c = mSubViews.get(0);
            int totalWidth = this.getComMeasuredWidth();
            int itemWidth = c.getComMeasuredWidth();
            int itemHeight = c.getComMeasuredHeight();
            int rowCount = count / mColCount + ((count % mColCount) > 0 ? 1 : 0);
            int index = 0;

            //int padding = (totalWidth - itemWidth * mColCount) / (mColCount + 1);
            if (RtlHelper.isRtl()) {
                left = r - mPaddingRight - itemWidth;
            }

            for (int row = 0; row < rowCount; ++row) {
                int ll = left;
                for (int col = 0; (col < mColCount) && (index < count); ++col) {
                    ViewBase child = mSubViews.get(index++);
                    if (child.isGone()) {
                        continue;
                    }

                    child.comLayout(ll, top, ll + itemWidth, top + itemHeight);

                    if (RtlHelper.isRtl()) {
                        ll -= (itemWidth + mItemHorizontalMargin);
                    } else {
                        ll += itemWidth + mItemHorizontalMargin;
                    }
                }
                if (mItemHeight > 0) {
                    top += mItemHeight + mItemVerticalMargin;
                } else {
                    top += itemHeight + mItemVerticalMargin;
                }
            }
        }
    }

    @Override
    protected boolean setAttribute(int key, float value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_itemHeight:
                    mItemHeight = Utils.dp2px(Math.round(value));
                    break;
                case StringBase.STR_ID_itemHorizontalMargin:
                    mItemHorizontalMargin = Utils.dp2px(value);
                    break;
                case StringBase.STR_ID_itemVerticalMargin:
                    mItemVerticalMargin = Utils.dp2px(value);
                    break;
                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    @Override
    protected boolean setAttribute(int key, int value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_colCount:
                    mColCount = value;
                    break;
                case StringBase.STR_ID_itemHeight:
                    mItemHeight = Utils.dp2px(value);
                    break;
                case StringBase.STR_ID_itemHorizontalMargin:
                    mItemHorizontalMargin = Utils.dp2px(value);
                    break;
                case StringBase.STR_ID_itemVerticalMargin:
                    mItemVerticalMargin = Utils.dp2px(value);
                    break;
                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    @Override
    protected boolean setAttribute(int key, String value) {
        boolean ret = true;
        switch (key) {
            case StringBase.STR_ID_itemHorizontalMargin:
                mViewCache.put(this, StringBase.STR_ID_itemHorizontalMargin, value, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_itemVerticalMargin:
                mViewCache.put(this, StringBase.STR_ID_itemVerticalMargin, value, Item.TYPE_FLOAT);
                break;
            default:
                ret = super.setAttribute(key, value);
                break;
        }
        return ret;
    }

    @Override
    protected boolean setRPAttribute(int key, float value) {
        boolean ret = super.setRPAttribute(key, value);
        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_itemHeight:
                    mItemHeight = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_itemHorizontalMargin:
                    mItemHorizontalMargin = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_itemVerticalMargin:
                    mItemVerticalMargin = Utils.rp2px(value);
                    break;
                default:
                    ret = false;
                    break;
            }
        }
        return ret;
    }

    @Override
    protected boolean setRPAttribute(int key, int value) {
        boolean ret = super.setRPAttribute(key, value);
        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_itemHeight:
                    mItemHeight = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_itemHorizontalMargin:
                    mItemHorizontalMargin = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_itemVerticalMargin:
                    mItemVerticalMargin = Utils.rp2px(value);
                    break;
                default:
                    ret = false;
                    break;
            }
        }
        return ret;
    }

    public static class Builder implements ViewBase.IBuilder {

        @Override
        public ViewBase build(VafContext context, ViewCache vc) {
            return new GridLayout(context, vc);
        }
    }

}
