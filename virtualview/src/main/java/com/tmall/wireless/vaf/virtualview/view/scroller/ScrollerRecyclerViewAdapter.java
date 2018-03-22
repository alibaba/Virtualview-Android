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

package com.tmall.wireless.vaf.virtualview.view.scroller;

import java.util.concurrent.atomic.AtomicInteger;

import android.support.v4.util.ArrayMap;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.libra.virtualview.common.ScrollerCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.framework.cm.ContainerService;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.EventManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gujicheng on 16/8/24.
 */
public class ScrollerRecyclerViewAdapter extends RecyclerView.Adapter<ScrollerRecyclerViewAdapter.MyViewHolder> {
    private final static String TAG = "ScrRecyAdapter_TMTEST";

    private final static String WATERFALL = "waterfall";
    private final static String STICKY_TOP = "stickyTop";

    private int mAutoRefreshThreshold = 5;

    private VafContext mContext;
    private JSONArray mData;
    private ContainerService mContainerService;
    private ScrollerImp mScrollerImp;

    private AtomicInteger mTypeId = new AtomicInteger(0);
    private String mStickyTopType;
    private int mStickyTopPos = 1000000;
    private ViewGroup mStickyView;
    private int mSpan = 0;

    private ArrayMap<String, Integer> mTypeMap = new ArrayMap<>();
    private SparseArrayCompat<String> mId2Types = new SparseArrayCompat<>();

    public ScrollerRecyclerViewAdapter(VafContext context, ScrollerImp scrollerImp) {
        mContext = context;
        mScrollerImp = scrollerImp;
        mContainerService = mContext.getContainerService();
    }

    public void setSpan(int span) {
        mSpan = span;
    }

    public JSONObject getData(int index) {
        if (null != mData && index < mData.length()) {
            try {
                return mData.getJSONObject(index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void destroy() {
        mScrollerImp = null;
        mData = null;
        mContext = null;
        mContainerService = null;
    }

    public int getStickyTopPos() {
        return mStickyTopPos;
    }

    public ViewGroup getStickyView() {
        return mStickyView;
    }

    public void setAutoRefreshThreshold(int threshold) {
        mAutoRefreshThreshold = threshold;
    }

    public void setData(Object data) {
        if (null != data && data instanceof JSONArray) {
            mData = (JSONArray) data;
//            Log.d(TAG, "setData:" + mData);
        } else {
            Log.e(TAG, "setData failed:" + data);
        }

        mStickyTopPos = 1000000;
    }

    public void appendData(Object data) {
        if (data instanceof JSONArray) {
            JSONArray arr = (JSONArray) data;

            if (null == mData) {
                mData = arr;
                this.notifyDataSetChanged();
            } else {
                int startPos = mData.length();
                int len = arr.length();
                for (int i = 0; i < len; ++i) {
                    try {
                        mData.put(arr.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                this.notifyItemRangeChanged(startPos, len);
            }

        } else {
            Log.e(TAG, "appendData failed:" + data);
        }
    }

    @Override
    public ScrollerRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View container, v;
        StaggeredGridLayoutManager.LayoutParams lp = null;
        String type = mId2Types.get(viewType);
        if (ScrollerCommon.MODE_StaggeredGrid == mScrollerImp.mMode) {
            container = mContainerService.getContainer(type, false);
            Layout.Params p = ((IContainer) container).getVirtualView().getComLayoutParams();
            lp = new StaggeredGridLayoutManager.LayoutParams(p.mLayoutWidth, p.mLayoutHeight);

            container.setLayoutParams(lp);
        } else {
            container = mContainerService.getContainer(type);
        }

        if (type == mStickyTopType) {
            Layout.Params p = ((IContainer) container).getVirtualView().getComLayoutParams();
            mStickyView = new android.widget.FrameLayout(mContext.getContext());
            if (ScrollerCommon.MODE_StaggeredGrid == mScrollerImp.mMode) {
                lp = new StaggeredGridLayoutManager.LayoutParams(p.mLayoutWidth, p.mLayoutHeight);
                mStickyView.setLayoutParams(lp);
            }
            mStickyView.addView(container, p.mLayoutWidth, p.mLayoutHeight);

            v = mStickyView;
        } else {
            v = container;
        }

//        Log.d(TAG, "onCreateViewHolder id:" + mScrollerImp.mScroller.getId() + "v:" + v);

        if (null != lp && mSpan != 0) {
            int span = mSpan >> 1;
            if (mScrollerImp.mLM.canScrollVertically()) {
                lp.setMargins(span, 0, span, 0);
            } else {
                lp.setMargins(0, span, 0, span);
            }
        }

        return new MyViewHolder(v, ((IContainer) container).getVirtualView());
    }

    @Override
    public void onBindViewHolder(ScrollerRecyclerViewAdapter.MyViewHolder myViewHolder, int pos) {
        try {
            Object obj = mData.get(pos);
            myViewHolder.itemView.setTag(pos);
            if (obj instanceof JSONObject) {
                JSONObject jObj = (JSONObject) obj;

                if (ScrollerCommon.MODE_StaggeredGrid == mScrollerImp.mMode) {
                    StaggeredGridLayoutManager.LayoutParams clp1 = (StaggeredGridLayoutManager.LayoutParams) myViewHolder.itemView.getLayoutParams();
                    if (jObj.optInt(WATERFALL, -1) <= 0) {
                        clp1.setFullSpan(true);
                    } else {
                        clp1.setFullSpan(false);
                    }
                }

                if (jObj.optInt("stickyTop", -1) > 0) {
                    myViewHolder.mStickyTop = true;
                    mStickyTopPos = pos;
                } else {
                    myViewHolder.mStickyTop = false;
                }

//                Log.d(TAG, "onBindViewHolder id:" + mScrollerImp.mScroller.getId() + "  obj:" + obj);

                myViewHolder.mViewBase.setVData(obj);

                if (myViewHolder.mViewBase.supportExposure()) {
                    mContext.getEventManager().emitEvent(EventManager.TYPE_Exposure, EventData.obtainData(mContext, myViewHolder.mViewBase));
                }
                myViewHolder.mViewBase.ready();
            } else {
                Log.e(TAG, "failed");
            }

            int threshold = mAutoRefreshThreshold;
            if (mData.length() < mAutoRefreshThreshold) {
                threshold = 2;
            }
            if (pos + threshold == mData.length()) {
                // load new data
                mScrollerImp.callAutoRefresh();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "onBindViewHolder:" + e);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (null != mData) {
            try {
                JSONObject obj = mData.getJSONObject(position);
                String type = obj.optString(ViewBase.TYPE);
                if (obj.optInt(STICKY_TOP, -1) > 0) {
                    mStickyTopType = type;
                }
                if (mTypeMap.containsKey(type)) {
                    return mTypeMap.get(type).intValue();
                } else {
                    int newType = mTypeId.getAndIncrement();
                    mTypeMap.put(type, newType);
                    mId2Types.put(newType, type);
                    return newType;
                }
            } catch (JSONException e) {
                Log.e(TAG, "getItemViewType:" + e);
            }
        } else {
            Log.e(TAG, "getItemViewType data is null");
        }

        return -1;
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount id:" + mScrollerImp.mScroller.getId());
        if (null != mData) {
//            Log.d(TAG, "getItemCount:" + mData.length());
            return mData.length();
        }
        return 0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public boolean mStickyTop = false;

        public ViewBase mViewBase;

        public MyViewHolder(View itemView, ViewBase vb) {
            super(itemView);

            mViewBase = vb;
        }
    }
}
