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

package com.tmall.wireless.vaf.virtualview.view.scroller;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.libra.Utils;
import com.libra.expr.common.ExprCode;
import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.VHCommon;
import com.tmall.wireless.vaf.expr.engine.ExprEngine;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.NativeViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.EventManager;

import org.json.JSONObject;

import static com.libra.virtualview.common.ScrollerCommon.MODE_Linear;

/**
 * Created by gujicheng on 16/8/24.
 */
public class Scroller extends NativeViewBase {
    private final static String TAG = "Scroller_TMTEST";

    protected ScrollerImp mNative;
    protected int mOrientation;
    protected int mMode;
    protected ExprCode mAutoRefreshCode;
    protected boolean mSupportSticky;
    protected int mSpan = 0;
    protected int mAutoRefreshThreshold = 5;
    protected int mLineSpace = 0;
    protected int mFirstSpace = 0;
    protected int mLastSpace = 0;

    public Scroller(VafContext context, ViewCache viewCache) {
        super(context, viewCache);

        mSupportSticky = false;
        mMode = MODE_Linear;
        mOrientation = LinearLayoutManager.VERTICAL;
        mNative = new ScrollerImp(context, this);
        __mNative = mNative;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setAutoRefreshThreshold(int threshold) {
        mNative.setAutoRefreshThreshold(threshold);
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public void setData(Object data) {
        super.setData(data);

        if (data instanceof JSONObject) {
            JSONObject obj = (JSONObject)data;
            data = obj.optJSONArray(mDataTag);
        }
        mNative.setData(data);
    }

    @Override
    public void appendData(Object data) {
        super.appendData(data);
        if (data instanceof JSONObject) {
            data = ((JSONObject) data).opt(mDataTag);
        }

        mNative.appendData(data);
    }

    public void callAutoRefresh() {
        if (null != mAutoRefreshCode) {
            ExprEngine engine = mContext.getExprEngine();
            if (null != engine) {
                engine.getEngineContext().getDataManager().replaceData(
                    (JSONObject)getViewCache().getComponentData());
            }
            if (null != engine && engine.execute(this, mAutoRefreshCode) ) {
            } else {
                Log.e(TAG, "callAutoRefresh execute failed");
            }
        }

        mContext.getEventManager().emitEvent(EventManager.TYPE_Load, EventData.obtainData(mContext, this));
    }

    @Override
    public void destroy() {
        super.destroy();

        mNative.destroy();
        mNative = null;
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();
        if (mLineSpace != 0 || mFirstSpace != 0 || mLastSpace != 0) {
            mNative.addItemDecoration(new SpaceItemDecoration(this, mLineSpace, mFirstSpace, mLastSpace));
        }
        mNative.setModeOrientation(mMode, mOrientation);
        mNative.setSupportSticky(mSupportSticky);
        if (mSupportSticky) {
            if (mNative.getParent() == null) {
                ScrollerStickyParent ssp = new ScrollerStickyParent(mContext.getContext());
                ssp.addView(mNative, mParams.mLayoutWidth, mParams.mLayoutHeight);
                __mNative = ssp;
            }
        } else {
            __mNative = mNative;
        }

        mNative.setBackgroundColor(mBackground);
        mNative.setAutoRefreshThreshold(mAutoRefreshThreshold);

        mNative.setSpan(mSpan);
    }

    @Override
    protected boolean setAttribute(int key, ExprCode value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_onAutoRefresh:
                    mAutoRefreshCode = value;
                    break;

                default:
                    ret = false;
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
                case StringBase.STR_ID_span:
                    mSpan = Utils.dp2px(value);
                    break;

                case StringBase.STR_ID_lineSpace:
                    mLineSpace = Utils.dp2px(value);
                    break;
                case StringBase.STR_ID_firstSpace:
                    mFirstSpace = Utils.dp2px(value);
                    break;
                case StringBase.STR_ID_lastSpace:
                    mLastSpace = Utils.dp2px(value);
                    break;
                default:
                    ret = false;
            }
        }

        return ret;
    }

    @Override
    protected boolean setRPAttribute(int key, float value) {
        boolean ret = super.setRPAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_span:
                    mSpan = Utils.rp2px(value);
                    break;

                case StringBase.STR_ID_lineSpace:
                    mLineSpace = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_firstSpace:
                    mFirstSpace = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_lastSpace:
                    mLastSpace = Utils.rp2px(value);
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
                case StringBase.STR_ID_span:
                    mSpan = Utils.rp2px(value);
                    break;

                case StringBase.STR_ID_lineSpace:
                    mLineSpace = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_firstSpace:
                    mFirstSpace = Utils.rp2px(value);
                    break;
                case StringBase.STR_ID_lastSpace:
                    mLastSpace = Utils.rp2px(value);
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
                case StringBase.STR_ID_orientation:
                    if (value == VHCommon.HORIZONTAL) {
                        mOrientation = LinearLayoutManager.HORIZONTAL;
                    } else if (value == VHCommon.VERTICAL) {
                        mOrientation = LinearLayoutManager.VERTICAL;
                    }
                    break;

                case StringBase.STR_ID_mode:
                    mMode = value;
                    break;

                case StringBase.STR_ID_supportSticky:
                    mSupportSticky = (value > 0) ? true : false;
                    break;

                case StringBase.STR_ID_span:
                    mSpan = Utils.dp2px(value);
                    break;

                case StringBase.STR_ID_autoRefreshThreshold:
                    mAutoRefreshThreshold = value;
                    break;

                case StringBase.STR_ID_lineSpace:
                    mLineSpace = Utils.dp2px(value);
                    break;
                case StringBase.STR_ID_firstSpace:
                    mFirstSpace = Utils.dp2px(value);
                    break;
                case StringBase.STR_ID_lastSpace:
                    mLastSpace = Utils.dp2px(value);
                    break;
                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    static public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private Scroller mScroller;
        private int mSpace;
        private int mFirstSpace;
        private int mLastSpace;

        public SpaceItemDecoration(Scroller scroller, int space, int fist, int last) {
            mScroller = scroller;
            mSpace = space;
            mFirstSpace = fist;
            mLastSpace = last;
        }

        public void setSpace(int space, int fist, int last) {
            mSpace = space;
            mFirstSpace = fist;
            mLastSpace = last;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (mFirstSpace != 0) {
                if (0 == parent.getChildPosition(view)) {
                    if (LinearLayoutManager.HORIZONTAL == mScroller.getOrientation()) {
                        outRect.left = mFirstSpace;
                    } else {
                        outRect.top = mFirstSpace;
                    }
                }
            }

            // Space between items.
            if (mSpace != 0) {
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    if (LinearLayoutManager.HORIZONTAL == mScroller.getOrientation()) {
                        outRect.right = mSpace;
                    } else {
                        outRect.bottom = mSpace;
                    }
                }
            }

            if (mLastSpace != 0) {
                View nativeView = mScroller.getNativeView();
                ScrollerImp s;
                if (nativeView instanceof ScrollerStickyParent) {
                    s = (ScrollerImp)((ScrollerStickyParent)nativeView).getChildAt(0);
                } else {
                    s = (ScrollerImp)mScroller.getNativeView();
                }
                RecyclerView.Adapter adapter = s.getAdapter();
                if (null != adapter) {
                    int count = adapter.getItemCount();
                    if ((count - 1) == parent.getChildPosition(view)) {
                        if (LinearLayoutManager.HORIZONTAL == mScroller.getOrientation()) {
                            outRect.right = mLastSpace;
                        } else {
                            outRect.bottom = mLastSpace;
                        }
                    }
                }
            }
        }
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new Scroller(context, viewCache);
        }
    }
}
