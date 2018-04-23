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

package com.tmall.wireless.vaf.virtualview.core;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.libra.Utils;
import com.libra.expr.common.ExprCode;
import com.libra.virtualview.common.LayoutCommon;
import com.libra.virtualview.common.StringBase;
import com.tmall.wireless.vaf.framework.VafContext;

/**
 * Created by gujicheng on 16/8/15.
 */
public abstract class Layout extends ViewBase {
    private final static String TAG = "Layout_TMTEST";

    protected List<ViewBase> mSubViews = new ArrayList<>();

    public Layout(VafContext context, ViewCache vc) {
        super(context, vc);
    }

    public Params generateParams() {
        return new Params();
    }

    final public List<ViewBase> getSubViews() {
        return mSubViews;
    }

    @Override
    public void ready() {
        super.ready();

        for (int i = 0, size = mSubViews.size(); i < size; i++) {
            ViewBase vb = mSubViews.get(i);
            vb.ready();
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        for (int i = 0, size = mSubViews.size(); i < size; i++) {
            ViewBase vb = mSubViews.get(i);
            vb.destroy();
        }

        mSubViews.clear();
    }

    @Override
    public ViewBase findViewBaseById(int id) {
        ViewBase ret = super.findViewBaseById(id);

        if (null == ret) {
            for (int i = 0, size = mSubViews.size(); i < size; i++) {
                ViewBase child = mSubViews.get(i);
                if (null != (ret = child.findViewBaseById(id))) {
                    break;
                }
            }
        }

        return ret;
    }

    @Override
    public ViewBase findViewBaseByName(String name) {
        ViewBase ret = super.findViewBaseByName(name);

        if (null == ret) {
            for (int i = 0, size = mSubViews.size(); i < size; i++) {
                ViewBase child = mSubViews.get(i);
                if (null != (ret = child.findViewBaseByName(name))) {
                    break;
                }
            }
        }

        return ret;
    }

    @Override
    public boolean handleEvent(int x, int y) {
        boolean shouldHandle = false;
        for (int i = mSubViews.size() - 1; i >= 0; i--) {
            ViewBase v = mSubViews.get(i);
            int l = v.getDrawLeft();
            int t = v.getDrawTop();
            int w = v.getComMeasuredWidth();
            int h = v.getComMeasuredHeight();
            if (x >= l && x < (l + w) && y >= t && y <= t + h) {
                shouldHandle = v.handleEvent(x, y);
                if (shouldHandle) {
                    break;
                }
            }
        }
        if (!shouldHandle) {
            shouldHandle = super.handleEvent(x, y);
        }
        return shouldHandle;
    }

    @Override
    public boolean click(int x, int y, boolean isLong) {
        boolean deal = false;

        for (int i = mSubViews.size() - 1; i >= 0; i--) {
            ViewBase v = mSubViews.get(i);
            int l = v.getDrawLeft();
            int t = v.getDrawTop();
            int w = v.getComMeasuredWidth();
            int h = v.getComMeasuredHeight();
            if (x >= l && x < (l + w) && y >= t && y <= t + h) {
                deal = v.click(x, y, isLong);
                if (deal) {
                    break;
                }
            }
        }

        if (!deal) {
            deal = super.click(x, y, isLong);
        }

        return deal;
    }

    @Override
    public ViewBase getChild(int index) {
        if (index >= 0 && index < mSubViews.size()) {
            return mSubViews.get(index);
        }

        return null;
    }

    @Override
    public void reset() {
        super.reset();

        for (int i = 0, size = mSubViews.size(); i < size; i++) {
            ViewBase v = mSubViews.get(i);
            v.reset();
        }
    }

    @Override
    public void comDraw(Canvas canvas) {
        super.comDraw(canvas);

        //FIXME let layout clip virtual children
        //canvas.save();
        //VirtualViewUtils.clipCanvas(canvas, mMeasuredWidth, mMeasuredHeight, mBorderWidth,
        //    mBorderTopLeftRadius, mBorderTopRightRadius, mBorderBottomLeftRadius, mBorderBottomRightRadius);
        // draw children
        for (int i = 0, size = mSubViews.size(); i < size; i++) {
            ViewBase v = mSubViews.get(i);
            if (v.shouldDraw()) {
                v.comDraw(canvas);
            }
        }
        //canvas.restore();
    }

    @Override
    protected void onComDraw(Canvas canvas) {
        super.onComDraw(canvas);
        drawBorder(canvas);
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();

        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
        }

    }

    @Override
    protected boolean changeVisibility() {
        boolean ret = super.changeVisibility();
        for (int i = 0, size = mSubViews.size(); i < size; i++) {
            ViewBase v = mSubViews.get(i);
            v.changeVisibility();
        }
        return ret;
    }


    public void addView(ViewBase view) {
        mSubViews.add(view);
        view.mParent = this;
        view.changeVisibility();
    }

    public boolean removeView(ViewBase view) {
        if (mSubViews.contains(view)) {
            mSubViews.remove(view);
            view.mParent = null;
            return true;
        } else {
            return false;
        }
    }

    protected void measureComChild(ViewBase child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        Params childParam = child.getComLayoutParams();
        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight + (mBorderWidth << 1) + childParam.mLayoutMarginLeft + childParam.mLayoutMarginRight, childParam.mLayoutWidth);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                mPaddingTop + mPaddingBottom + (mBorderWidth << 1) + childParam.mLayoutMarginTop + childParam.mLayoutMarginBottom, childParam.mLayoutHeight);

        child.measureComponent(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    public static int getChildMeasureSpec(int parentSpec, int padding, int childDimension) {
        int parentSpecMode = View.MeasureSpec.getMode(parentSpec);
        int parentSpecSize = View.MeasureSpec.getSize(parentSpec);

        int size = Math.max(0, parentSpecSize - padding);

        int resultSize = 0;
        int resultMode = View.MeasureSpec.UNSPECIFIED;

        switch (parentSpecMode) {
            // Parent has imposed an exact size on us
            case View.MeasureSpec.EXACTLY:
                if (childDimension >= 0) {
                    resultSize = childDimension;
                    resultMode = View.MeasureSpec.EXACTLY;
                } else if (childDimension == LayoutCommon.MATCH_PARENT) {
                    // Child wants to be our size. So be it.
                    resultSize = size;
                    resultMode = View.MeasureSpec.EXACTLY;
                } else if (childDimension == LayoutCommon.WRAP_CONTENT) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size;
                    resultMode = View.MeasureSpec.AT_MOST;
                }
                break;

            // Parent has imposed a maximum size on us
            case View.MeasureSpec.AT_MOST:
                if (childDimension >= 0) {
                    // Child wants a specific size... so be it
                    resultSize = childDimension;
                    resultMode = View.MeasureSpec.EXACTLY;
                } else if (childDimension == LayoutCommon.MATCH_PARENT) {
                    // Child wants to be our size, but our size is not fixed.
                    // Constrain child to not be bigger than us.
                    resultSize = size;
                    resultMode = View.MeasureSpec.AT_MOST;
                } else if (childDimension == LayoutCommon.WRAP_CONTENT) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size;
                    resultMode = View.MeasureSpec.AT_MOST;
                }
                break;

            // Parent asked to see how big we want to be
            case View.MeasureSpec.UNSPECIFIED:
                if (childDimension >= 0) {
                    // Child wants a specific size... let him have it
                    resultSize = childDimension;
                    resultMode = View.MeasureSpec.EXACTLY;
                } else if (childDimension == LayoutCommon.MATCH_PARENT) {
                    // Child wants to be our size... find out how big it should
                    // be
                    resultSize = size;
                    resultMode = View.MeasureSpec.UNSPECIFIED;
                } else if (childDimension == LayoutCommon.WRAP_CONTENT) {
                    // Child wants to determine its own size.... find out how
                    // big it should be
                    resultSize = size;
                    resultMode = View.MeasureSpec.UNSPECIFIED;
                }
                break;
            default:
                break;
        }

        return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

    @Override
    protected boolean setRPAttribute(int key, float value) {
        boolean ret = super.setRPAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    @Override
    protected boolean setAttribute(int key, float value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
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
                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    @Override
    protected boolean setAttribute(int key, String stringValue) {
        boolean ret = super.setAttribute(key, stringValue);
        if (!ret) {
            ret = true;
            switch (key) {
                default:
                    ret = false;
            }
        }
        return ret;
    }

    public static class Params {
        public int mLayoutWidth;
        public int mLayoutHeight;

        public int mLayoutMargin;
        public int mLayoutMarginLeft;
        protected boolean isLayoutMarginLeftSet;
        public int mLayoutMarginRight;
        protected boolean isLayoutMarginRightSet;
        public int mLayoutMarginTop;
        protected boolean isLayoutMarginTopSet;
        public int mLayoutMarginBottom;
        protected boolean isLayoutMarginBottomSet;

        public Params() {
            mLayoutWidth = 0;
            mLayoutHeight = 0;

            mLayoutMarginLeft = 0;
            mLayoutMarginRight = 0;
            mLayoutMarginTop = 0;
            mLayoutMarginBottom = 0;
        }

        public boolean setAttribute(int key, ExprCode value) {
            return false;
        }

        public boolean setRPAttribute(int key, float value) {
            boolean ret = true;

            switch (key) {
                case StringBase.STR_ID_layoutWidth:
                    mLayoutWidth = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_layoutHeight:
                    mLayoutHeight = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_layoutMargin:
                    mLayoutMargin = Utils.rp2px(value);
                    if (!isLayoutMarginLeftSet) {
                        mLayoutMarginLeft = mLayoutMargin;
                    }
                    if (!isLayoutMarginRightSet) {
                        mLayoutMarginRight = mLayoutMargin;
                    }
                    if (!isLayoutMarginTopSet) {
                        mLayoutMarginTop = mLayoutMargin;
                    }
                    if (!isLayoutMarginBottomSet) {
                        mLayoutMarginBottom = mLayoutMargin;
                    }
                    break;
                case StringBase.STR_ID_layoutMarginLeft:
                    mLayoutMarginLeft = Utils.rp2px(value);
                    isLayoutMarginLeftSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginRight:
                    mLayoutMarginRight = Utils.rp2px(value);
                    isLayoutMarginRightSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginTop:
                    mLayoutMarginTop = Utils.rp2px(value);
                    isLayoutMarginTopSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginBottom:
                    mLayoutMarginBottom = Utils.rp2px(value);
                    isLayoutMarginBottomSet = true;
                    break;

                default:
                    ret = false;
            }

            return ret;
        }

        public boolean setAttribute(int key, float value) {
            boolean ret = true;

            switch (key) {
                case StringBase.STR_ID_layoutWidth:
                    if (value > 0) {
                        mLayoutWidth = Utils.dp2px(value);
                    } else {
                        mLayoutWidth = (int) value;
                    }
                    break;
                case StringBase.STR_ID_layoutHeight:
                    if (value > 0) {
                        mLayoutHeight = Utils.dp2px(value);
                    } else {
                        mLayoutHeight = (int) value;
                    }
                    break;
                case StringBase.STR_ID_layoutMargin:
                    mLayoutMargin = Utils.dp2px(value);
                    if (!isLayoutMarginLeftSet) {
                        mLayoutMarginLeft = mLayoutMargin;
                    }
                    if (!isLayoutMarginRightSet) {
                        mLayoutMarginRight = mLayoutMargin;
                    }
                    if (!isLayoutMarginTopSet) {
                        mLayoutMarginTop = mLayoutMargin;
                    }
                    if (!isLayoutMarginBottomSet) {
                        mLayoutMarginBottom = mLayoutMargin;
                    }
                    break;
                case StringBase.STR_ID_layoutMarginLeft:
                    mLayoutMarginLeft = Utils.dp2px(value);
                    isLayoutMarginLeftSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginRight:
                    mLayoutMarginRight = Utils.dp2px(value);
                    isLayoutMarginRightSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginTop:
                    mLayoutMarginTop = Utils.dp2px(value);
                    isLayoutMarginTopSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginBottom:
                    mLayoutMarginBottom = Utils.dp2px(value);
                    isLayoutMarginBottomSet = true;
                    break;

                default:
                    ret = false;
            }

            return ret;
        }

        public boolean setStrAttribute(int key, int value) {
            //TODO
            return false;
        }

        public boolean setRPAttribute(int key, int value) {
            boolean ret = true;

            switch (key) {
                case StringBase.STR_ID_layoutWidth:
                    mLayoutWidth = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_layoutHeight:
                    mLayoutHeight = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_layoutMargin:
                    mLayoutMargin = Utils.rp2px(value);
                    if (!isLayoutMarginLeftSet) {
                        mLayoutMarginLeft = mLayoutMargin;
                    }
                    if (!isLayoutMarginRightSet) {
                        mLayoutMarginRight = mLayoutMargin;
                    }
                    if (!isLayoutMarginTopSet) {
                        mLayoutMarginTop = mLayoutMargin;
                    }
                    if (!isLayoutMarginBottomSet) {
                        mLayoutMarginBottom = mLayoutMargin;
                    }
                    break;
                case StringBase.STR_ID_layoutMarginLeft:
                    mLayoutMarginLeft = Utils.rp2px(value);
                    isLayoutMarginLeftSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginRight:
                    mLayoutMarginRight = Utils.rp2px(value);
                    isLayoutMarginRightSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginTop:
                    mLayoutMarginTop = Utils.rp2px(value);
                    isLayoutMarginTopSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginBottom:
                    mLayoutMarginBottom = Utils.rp2px(value);
                    isLayoutMarginBottomSet = true;
                    break;

                default:
                    ret = false;
            }

            return ret;
        }

        public boolean setAttribute(int key, int value) {
            boolean ret = true;

            switch (key) {
                case StringBase.STR_ID_layoutWidth:
                    if (value > 0) {
                        mLayoutWidth = Utils.dp2px(value);
                    } else {
                        mLayoutWidth = value;
                    }
                    break;
                case StringBase.STR_ID_layoutHeight:
                    if (value > 0) {
                        mLayoutHeight = Utils.dp2px(value);
                    } else {
                        mLayoutHeight = value;
                    }
                    break;
                case StringBase.STR_ID_layoutMargin:
                    mLayoutMargin = Utils.dp2px(value);
                    if (!isLayoutMarginLeftSet) {
                        mLayoutMarginLeft = mLayoutMargin;
                    }
                    if (!isLayoutMarginRightSet) {
                        mLayoutMarginRight = mLayoutMargin;
                    }
                    if (!isLayoutMarginTopSet) {
                        mLayoutMarginTop = mLayoutMargin;
                    }
                    if (!isLayoutMarginBottomSet) {
                        mLayoutMarginBottom = mLayoutMargin;
                    }
                    break;
                case StringBase.STR_ID_layoutMarginLeft:
                    mLayoutMarginLeft = Utils.dp2px(value);
                    isLayoutMarginLeftSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginRight:
                    mLayoutMarginRight = Utils.dp2px(value);
                    isLayoutMarginRightSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginTop:
                    mLayoutMarginTop = Utils.dp2px(value);
                    isLayoutMarginTopSet = true;
                    break;
                case StringBase.STR_ID_layoutMarginBottom:
                    mLayoutMarginBottom = Utils.dp2px(value);
                    isLayoutMarginBottomSet = true;
                    break;

                default:
                    ret = false;
            }

            return ret;
        }
    }
}
