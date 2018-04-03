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

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.libra.virtualview.common.ScrollerCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.IView;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

import org.json.JSONObject;

/**
 * Created by gujicheng on 16/8/24.
 */
public class ScrollerImp extends RecyclerView implements IView, IContainer {
    private final static String TAG = "ScrollerImp_TMTEST";

    protected ScrollerRecyclerViewAdapter mAdapter;
    protected LayoutManager mLM;
    protected VafContext mAppContext;
    protected Scroller mScroller;
    protected int mMode;
    protected int mOrientation;
    protected boolean mSupportSticky = false;

    protected Listener mListener;
    protected ScrollerListener mScrollerListener;

    public interface Listener {
        void onScrolled(RecyclerView recyclerView, int dx, int dy);
        void onScrollStateChanged(RecyclerView recyclerView, int newState);
    }

    public ScrollerImp(VafContext context, Scroller scroller) {
        super(context.forViewConstruction());
        mAppContext = context;
        mScroller = scroller;

        this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mAdapter = new ScrollerRecyclerViewAdapter(context, this);
        this.setAdapter(mAdapter);
        setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
//                IContainer container = (IContainer)holder.itemView;
                ViewBase view = ((ScrollerRecyclerViewAdapter.MyViewHolder) holder).mViewBase;
                if (null != view) {
//                    Log.d(TAG, "onViewRecycled id:" + view.getId());
                    view.reset();
                } else {
                    Log.e(TAG, "recycled failed:" + view);
                }
            }
        });
    }

    public void setModeOrientation(int mode, int orientation) {
        if (mMode != mode || mOrientation != orientation) {
            mMode = mode;
            mOrientation = orientation;
            switch (mode) {
                case ScrollerCommon.MODE_Linear:
                    mLM = new LinearLayoutManager(mAppContext.forViewConstruction());
                    ((LinearLayoutManager) mLM).setOrientation(orientation);
                    break;

                case ScrollerCommon.MODE_StaggeredGrid:
                    mLM = new StaggeredGridLayoutManager(2, orientation);
                    break;

                default:
                    Log.e(TAG, "mode invalidate:" + mode);
                    break;
            }
            setLayoutManager(mLM);
        }
    }

    public void setSupportSticky(boolean supportSticky) {
        if (mSupportSticky != supportSticky) {
            mSupportSticky = supportSticky;
            if (mSupportSticky) {
                mScrollerListener = new ScrollerListener();
                this.setOnScrollListener(mScrollerListener);
            } else {
                this.setOnScrollListener(null);
            }
        }
    }

    public JSONObject getData(int index) {
        if (null != mAdapter) {
            return mAdapter.getData(index);
        }

        return null;
    }

    public void setListener(Listener lis) {
        mListener = lis;
        if (null == mScrollerListener) {
            mScrollerListener = new ScrollerListener();
            this.setOnScrollListener(mScrollerListener);
        }
    }

    public void setSpan(int span) {
        mAdapter.setSpan(span);
    }

    @Override
    public void attachViews() {
    }

    @Override
    public void setVirtualView(ViewBase view) {
    }

    @Override
    public ViewBase getVirtualView() {
        return mScroller;
    }

    @Override
    public View getHolderView() {
        return null;
    }

    public void destroy() {
        mScroller = null;

        mAdapter.destroy();
        mAdapter = null;
    }

    public int getMode() {
        return mMode;
    }

    @Override
    public int getType() {
        return -1;
    }

    public void setAutoRefreshThreshold(int threshold) {
        mAdapter.setAutoRefreshThreshold(threshold);
    }

    public void callAutoRefresh() {
        mScroller.callAutoRefresh();
    }

    public void setData(Object str) {
        // parse data
        mAdapter.setData(str);
        mAdapter.notifyDataSetChanged();
    }

    public void appendData(Object data) {
        mAdapter.appendData(data);
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        this.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        this.layout(l, t, r, b);
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        this.onLayout(changed, l, t, r, b);
    }

    @Override
    public int getComMeasuredWidth() {
        return this.getMeasuredWidth();
    }

    @Override
    public int getComMeasuredHeight() {
        return this.getMeasuredHeight();
    }

    class ScrollerListener extends OnScrollListener {
        private boolean mStickied = false;
        private int mStickiedItemHeight;
        private View mStickView;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (null != mListener) {
                mListener.onScrollStateChanged(recyclerView, newState);
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (null != mListener) {
                mListener.onScrolled(recyclerView, dx, dy);
            }

            if (mSupportSticky) {
                int stickyPos = mAdapter.getStickyTopPos();
                if (!mStickied) {
                    View v = ScrollerImp.this.findChildViewUnder(0, 0);
                    int pos = (Integer) v.getTag();
//                Log.d(TAG, "pre pos:" + pos + "  stickyPos:" + stickyPos);
                    if (pos >= stickyPos) {
//                    Log.d(TAG, "do sticky");
                        mStickied = true;

                        ViewGroup vg = mAdapter.getStickyView();
                        if (vg.getChildCount() == 1) {
                            mStickView = vg.getChildAt(0);
                            vg.addView(new View(ScrollerImp.this.getContext()), vg.getMeasuredWidth(), vg.getMeasuredHeight());
                        }

                        vg.removeView(mStickView);

                        // attach to
                        addStickyWindow();

                        mStickiedItemHeight = v.getMeasuredHeight();
                    }
                } else {
                    View v = ScrollerImp.this.findChildViewUnder(0, mStickiedItemHeight);
                    int pos = (Integer) v.getTag();
//                Log.d(TAG, "post pos:" + pos + "  stickyPos:" + stickyPos);
                    if (pos <= stickyPos) {
                        mStickied = false;

                        removeStickyWindow();

                        ViewGroup vg = mAdapter.getStickyView();
                        vg.addView(mStickView, vg.getMeasuredWidth(), vg.getMeasuredHeight());
                        //
//                    Log.d(TAG, "cancel sticky");
                    }
                }
            }
        }

        private void removeStickyWindow() {
            ((ViewGroup) ScrollerImp.this.getParent()).removeView(mStickView);
        }

        private void addStickyWindow() {
            ((ViewGroup) ScrollerImp.this.getParent()).addView(mStickView);
        }
    }
}

