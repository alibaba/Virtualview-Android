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

package com.tmall.wireless.vaf.virtualview.view.slider;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.virtualview.core.Adapter;
import com.tmall.wireless.vaf.virtualview.core.IContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujicheng on 16/12/15.
 */

public class SliderView extends ViewGroup {
    private final static String TAG = "SliderView_TMTEST";

    protected final static int MAX_ITEM_COUNT = 5;

    private int mNewThreshold;
    private int mDeleteThreshold;

    protected SparseArray<List<Adapter.ViewHolder>> mItemCache = new SparseArray<>();

    private int mWidth;
    private int mItemWidth;
    private int mOrientation = ViewBaseCommon.HORIZONTAL;

    protected boolean mDataChanged = true;
    protected Adapter mAdapter;

    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;

    private int mPrePos;
    private int mLTPos;
    private int mRBPos;
    private int mScrollPos;

    private int mLTDataIndex;
    private int mRBDataIndex;

    private int mDataCount;
    private int mTotalLen;

    private int mLastX;
    private int mLastY;
    private int mPointerId;
    protected int mDownPos;

    private ObjectAnimator mAutoScrollAni;
    private int mScrollDis;
    private int mPreAutoScrollPos;

    protected Listener mListener;

    // space between Slider items
    private int mSpan;
    public void setSpan(int span) {
        mSpan = span;
    }

    public interface Listener {
        void onScroll(int pos, int total);
    }

    public SliderView(Context context) {
        super(context);
        mMaxVelocity = ViewConfiguration.getMaximumFlingVelocity();
    }

    public void setListener(Listener lis) {
        mListener = lis;
    }

    public void setOrientation(int o) {
        mOrientation = o;
    }

    public void setItemWidth(int w) {
        mItemWidth = w;
        mNewThreshold = mItemWidth >> 1;
        mDeleteThreshold = mItemWidth << 1;
    }

    public void refresh() {
        if (mDataChanged) {
            removeAll();
            mDataChanged = false;
            mDataCount = mAdapter.getItemCount();
            mTotalLen = mDataCount * mItemWidth + (mDataCount - 1) * mSpan - mWidth;

            initData();
        }
    }

    protected void add(int pos) {
        add(pos, -1);
    }

    protected void add(int pos, int index) {
        int type = mAdapter.getType(pos);
        List<Adapter.ViewHolder> items = mItemCache.get(type);
        Adapter.ViewHolder vh;
        if (null != items && items.size() > 0) {
            vh = items.remove(0);
            vh.mPos = pos;
        } else {
            vh = mAdapter.onCreateViewHolder(type);
            vh.mType = type;
            vh.mPos = pos;
        }
        mAdapter.onBindViewHolder(vh, pos);
        if (index < 0) {
            this.addView(vh.mItemView);
        } else {
            this.addView(vh.mItemView, index);
        }
    }

    private void initData() {
        if (null != mAdapter) {
            int count = mAdapter.getItemCount();
            if (count > 0) {
                int totalWidth = 0;
                mLTPos = 0;
                mLTDataIndex = 0;
                mScrollPos = 0;
                int maxWidth = mWidth + mItemWidth + mSpan;
                mRBDataIndex = count - 1;
                for (int i = 0; i < count; ++i) {
                    add(i);
                    totalWidth += mItemWidth;
                    if (i < count - 1) {
                        totalWidth += mSpan;
                    }
                    if (totalWidth >= maxWidth) {
                        mRBDataIndex = i;
                        break;
                    }
                }
                mRBPos = totalWidth - mWidth;
            }
        }
    }

    protected void removeAll() {
        int count = this.getChildCount();
        for (int i = 0; i < count; ++i) {
            removeData(i);
        }
        this.removeAllViews();
    }

    private void remove(int index) {
        removeData(index);
        this.removeViewAt(index);
    }

    private void removeData(int index) {
        View v = this.getChildAt(index);
        Adapter.ViewHolder vh = (Adapter.ViewHolder) v.getTag();
        ((IContainer) vh.mItemView).getVirtualView().reset();
        List<Adapter.ViewHolder> items = mItemCache.get(vh.mType);
        if (null == items) {
            items = new ArrayList<>();
            mItemCache.put(vh.mType, items);
        }
        if (items.size() >= MAX_ITEM_COUNT) {
            // recycle
            items.remove(0);
        }
        items.add(vh);
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);

        moveH(event);

        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = false;

        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (ViewBaseCommon.HORIZONTAL == mOrientation) {
                    mDownPos = x;
                } else {
                    mDownPos = y;
                }
                mLastX = x;
                mLastY = y;
                mPointerId = ev.getPointerId(0);

                mPrePos = x;
                if (null != mAutoScrollAni) {
                    mAutoScrollAni.cancel();
                }

                break;

            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;

                if (ViewBaseCommon.HORIZONTAL == mOrientation) {
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                        ret = true;
                    }
                } else {
                    if (Math.abs(deltaY) > Math.abs(deltaX)) {
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                        ret = true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }

        return ret;
    }

    private void scrollDeal(int moveDis) {
        if (moveDis < 0) {
            // left
            if (mRBPos + moveDis < 0) {
                moveDis = -mRBPos;
            }
        } else if (moveDis > 0) {
            // right
            if (mLTPos - moveDis < 0) {
                moveDis = mLTPos;
            }
        } else {
            return;
        }

        if (moveDis != 0) {
            mScrollPos += -moveDis;

            mPrePos += moveDis;

            scrollBy(-moveDis, 0);

            mLTPos -= moveDis;
            mRBPos += moveDis;

            if (null != mListener) {
                mListener.onScroll(mScrollPos, mTotalLen);
            }
        }

        if (mLTPos >= mDeleteThreshold) {
            // remove from head
            if (mLTDataIndex < this.getChildCount() - 1) {
                remove(0);

                mLTDataIndex++;
                mLTPos -= (mItemWidth + mSpan);
                scrollBy(-mItemWidth - mSpan, 0);
            }
        } else if (mLTPos <= mNewThreshold) {
            // add to head
            if (mLTDataIndex > 0) {

                add(--mLTDataIndex, 0);
                scrollBy(mItemWidth + mSpan, 0);

                mLTPos += mItemWidth + mSpan;
            }
        }

        if (mRBPos >= mDeleteThreshold) {
            // remove from tail
            if (mRBDataIndex > 0) {
                remove(this.getChildCount() - 1);

                mRBDataIndex--;
                mRBPos -= (mItemWidth + mSpan);
            }
        } else if (mRBPos <= mNewThreshold) {
            // add to tail
            if (mRBDataIndex < mDataCount - 1) {
                add(++mRBDataIndex);

                mRBPos += mItemWidth + mSpan;
            }
        }
    }

    public void setAutoScrollX(int delta) {
        int d = delta - mPreAutoScrollPos;
        scrollDeal(d);

        if (mScrollDis < 0) {
            if (mRBPos == 0) {
                mAutoScrollAni.cancel();
            }
        } else {
            if (mLTPos == 0) {
                mAutoScrollAni.cancel();
            }
        }
        mPreAutoScrollPos = delta;
    }

    private void moveH(MotionEvent event) {
        int x = (int) event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrePos = x;
                if (null != mAutoScrollAni) {
                    mAutoScrollAni.cancel();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                mScrollDis = x - mPrePos;
                scrollDeal(mScrollDis);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1, mMaxVelocity);
                final float velocityX = mVelocityTracker.getXVelocity(mPointerId);
                final float velocityY = mVelocityTracker.getYVelocity(mPointerId);

                int total = (int) velocityX * mScrollDis;

                if (mScrollDis > 0) {
                    total = -total;
                }
                mPreAutoScrollPos = total;
                mAutoScrollAni = ObjectAnimator.ofInt(this, "autoScrollX", total, 0);
                mAutoScrollAni.setInterpolator(new DecelerateInterpolator());
                mAutoScrollAni.setDuration(300).start();
                releaseVelocityTracker();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        refresh();

        this.measureChildren(View.MeasureSpec.makeMeasureSpec(mItemWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));

        setMeasuredDimension(mWidth, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = this.getChildCount();
        int w = r - l;
        int paddingTop = this.getPaddingTop();
        int h = b - t - paddingTop - this.getPaddingBottom();
        int offset = this.getPaddingLeft();

        for (int i = 0; i < count; ++i) {
            View child = this.getChildAt(i);
            child.layout(offset, paddingTop, offset + mItemWidth, h);
            offset += (mItemWidth + mSpan);
        }
    }

}
