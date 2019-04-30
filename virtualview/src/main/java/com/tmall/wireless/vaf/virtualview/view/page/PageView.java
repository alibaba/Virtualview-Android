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

package com.tmall.wireless.vaf.virtualview.view.page;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.virtualview.core.Adapter;
import com.tmall.wireless.vaf.virtualview.core.Adapter.ViewHolder;
import com.tmall.wireless.vaf.virtualview.core.IContainer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;

/**
 * Created by gujicheng on 16/10/25.
 */
public class PageView extends ViewGroup {
    private final static String TAG = "PageView_TMTEST";

    protected final static int MAX_ITEM_COUNT = 5;
    protected final static int DEFAULT_ANIMATOR_TIME_INTERVAL = 100;
    protected final static int DEFAULT_AUTO_SWITCH_TIME_INTERVAL = 500;
    protected final static int DEFAULT_STAY_TIME = 2500;
    protected final static int MSG_AUTO_SWITCH = 1;
    protected final static int VEL_THRESHOLD = 2000;

    protected SparseArray<List<Adapter.ViewHolder>> mItemCache = new SparseArray<>();

    protected Adapter mAdapter;
    protected int mCurPos;
    protected int mDownPos;
    protected boolean mIsNext;

    protected int mStayTime = DEFAULT_STAY_TIME;
    protected int mAnimatorTimeInterval = DEFAULT_ANIMATOR_TIME_INTERVAL;
    protected int mAutoSwitchTimeInterval = DEFAULT_AUTO_SWITCH_TIME_INTERVAL;

    protected boolean mAutoSwitch = false;
    protected boolean mIsHorizontal = true;
    /**
     * for horizontal layout, children's normal layout orientation is from left to right, reverse layout is from right
     * to left
     * for vetical layout, children's normal layout orientation is from top to bottom, reverse layout is from bottom to
     * top
     */
    protected boolean mLayoutNormal = true;
    protected int mAnimationStyle = 0;
    protected boolean mCanSlide = true;
    protected long mAutoSwitchDelay = 0;

    protected Handler mAutoSwitchHandler;

    protected boolean mDataChanged = true;

    private ObjectAnimator ani;

    private int mLastX;
    private int mLastY;

    private VelocityTracker mVelocityTracker;
    private int mPointerId;
    private int mMaxVelocity;

    protected MyAnimatorListener mAniListener = new MyAnimatorListener();
    protected Listener mListener;

    protected boolean mCanSwitch = true;

    public interface Listener {
        void onPageFlip(int pos, int total);
    }

    public PageView(Context context) {
        super(context);

        mCurPos = 0;

        mAutoSwitchHandler = new AutoSwitchHandler(this);

        mMaxVelocity = ViewConfiguration.getMaximumFlingVelocity();
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    public void setListener(Listener lis) {
        mListener = lis;
    }

    public void setSlide(boolean slide) {
        mCanSlide = slide;
    }

    public void setAutoSwitchTimeInterval(int time) {
        mAutoSwitchTimeInterval = time;
    }

    public void setAnimatorTimeInterval(int time) {
        mAnimatorTimeInterval = time;
    }

    public void setAnimationStyle(int animationStyle) {
        mAnimationStyle = animationStyle;
    }

    public void setLayoutOrientation(boolean isNormal) {
        mLayoutNormal = isNormal;
    }

    public void setStayTime(int time) {
        mStayTime = time;
    }

    public void setOrientation(boolean isHorizontal) {
        mIsHorizontal = isHorizontal;
    }

    public void setAutoSwitch(boolean auto) {
        mAutoSwitch = auto;
    }

    public void setAutoSwitchDelay(long delay) {
        this.mAutoSwitchDelay = delay;
    }

    public void autoSwitch() {
        mIsNext = true;

        if (mIsHorizontal) {
            if (mLayoutNormal) {
                ani = ObjectAnimator.ofInt(this, "scrollX", 0, this.getMeasuredWidth());
            } else {
                ani = ObjectAnimator.ofInt(this, "scrollX", 0, -this.getMeasuredWidth());
            }
        } else {
            if (mLayoutNormal) {
                ani = ObjectAnimator.ofInt(this, "scrollY", 0, this.getMeasuredHeight());
            } else {
                ani = ObjectAnimator.ofInt(this, "scrollY", 0, -this.getMeasuredHeight());
            }
        }
        ani.setDuration(mAutoSwitchTimeInterval).addListener(mAniListener);
        ani.setInterpolator(getTimeInterpolater());
        ani.setStartDelay(mAutoSwitchDelay);
        ani.start();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility != View.VISIBLE) {
            mCanSwitch = false;
            mAutoSwitchHandler.removeMessages(MSG_AUTO_SWITCH);
        } else {
            mCanSwitch = true;
            if (mAutoSwitch && mAdapter.getItemCount() > 1) {
                mAutoSwitchHandler.removeMessages(MSG_AUTO_SWITCH);
                mAutoSwitchHandler.sendEmptyMessageDelayed(MSG_AUTO_SWITCH, mStayTime);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCanSwitch = false;
        mAutoSwitchHandler.removeMessages(MSG_AUTO_SWITCH);
    }

    public void refresh() {
        mCanSwitch = true;

        if (mDataChanged) {
            if (ani != null) {
                ani.cancel();
            }
            removeAll();
            mDataChanged = false;

            initData();
        }

        if (mAutoSwitch && mAdapter.getItemCount() > 1) {
            mAutoSwitchHandler.removeMessages(MSG_AUTO_SWITCH);
            mAutoSwitchHandler.sendEmptyMessageDelayed(MSG_AUTO_SWITCH, mStayTime);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refresh();
    }

    private void initData() {
        mCurPos = 0;

        int count = mAdapter.getItemCount();
        if (1 == count) {
            if (getChildCount() == 0) {
                add(mCurPos);
            } else {
                replace(mCurPos);
            }
            mCanSlide = false;
        } else if (count > 1) {
            int pre = (mCurPos - 1);
            if (pre < 0) {
                pre += count;
            }
            int next = (mCurPos + 1) % count;
            if (mLayoutNormal) {
                if (getChildCount() == 0) {
                    if (mCanSlide) {
                        //adding two children is enough to improve performance
                        add(pre);
                    }
                    add(mCurPos);
                    add(next);
                } else {
                    int index = 0;
                    if (mCanSlide) {
                        //adding two children is enough to improve performance
                        replace(pre, index++);
                    }
                    replace(mCurPos, index++);
                    replace(next, index++);
                }
            } else {
                if (getChildCount() == 0) {
                    add(next);
                    add(mCurPos);
                    if (mCanSlide) {
                        //adding two children is enough to improve performance
                        add(pre);
                    }
                } else {
                    int index = 0;
                    replace(next, index++);
                    replace(mCurPos, index++);
                    if (mCanSlide) {
                        //adding two children is enough to improve performance
                        replace(pre, index++);
                    }
                }
            }
        }

        if (count > 0 && null != mListener) {
            mListener.onPageFlip(1, count);
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

    private void replace(int pos) {
        replace(pos, -1);
    }

    protected void replace(int pos, int index) {
        View itemView = null;
        int childCount = getChildCount();
        if (childCount == 0 || index >= childCount) {
            Log.d(TAG, "childCount == 0 or index >= childCount should not happen here");
            return;
        }
        if (index == -1) {
            itemView = getChildAt(childCount - 1);
        } else {
            itemView = getChildAt(index);
        }
        ViewHolder vh = (ViewHolder)itemView.getTag();
        if (vh == null) {
            Log.d(TAG, "view holder == null, should not happen ");
            return;
        }
        mAdapter.onBindViewHolder(vh, pos);
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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mAutoSwitch && mAdapter.getItemCount() > 1) {
                    mAutoSwitchHandler.removeMessages(MSG_AUTO_SWITCH);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mAutoSwitch && mAdapter.getItemCount() > 1) {
                    mAutoSwitchHandler.removeMessages(MSG_AUTO_SWITCH);
                    mAutoSwitchHandler.sendEmptyMessageDelayed(MSG_AUTO_SWITCH, mStayTime);
                }
                break;
            default:
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = false;

        if (mCanSlide) {
            int x = (int)ev.getX();
            int y = (int)ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mIsHorizontal) {
                        mDownPos = x;
                    } else {
                        mDownPos = y;
                    }
                    mLastX = x;
                    mLastY = y;
                    mPointerId = ev.getPointerId(0);
                    break;

                case MotionEvent.ACTION_MOVE:
                    int deltaX = x - mLastX;
                    int deltaY = y - mLastY;

                    if (mIsHorizontal) {
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
            }
        }

        return ret;
    }

    private void moveV(MotionEvent event) {
        int y = (int)event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownPos = y;
                break;

            case MotionEvent.ACTION_MOVE:
                int offset = y - mDownPos;
                this.setScrollY(-offset);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                final float velocityY = mVelocityTracker.getYVelocity(mPointerId);

                // animator
                int scrollY = this.getScrollY();
                int h = this.getMeasuredHeight();
                int dis = Math.abs(scrollY);
                if (dis > h / 2 || Math.abs(velocityY) > VEL_THRESHOLD) {
                    if (scrollY < 0) {
                        mIsNext = false;
                        ani = ObjectAnimator.ofInt(this, "scrollY", scrollY, -h);
                    } else {
                        mIsNext = true;
                        ani = ObjectAnimator.ofInt(this, "scrollY", scrollY, h);
                    }
                    ani.setDuration(mAnimatorTimeInterval).addListener(mAniListener);
                    ani.setInterpolator(getTimeInterpolater());
                    ani.start();
                } else {
                    ObjectAnimator.ofInt(this, "scrollY", scrollY, 0).setDuration(mAnimatorTimeInterval).start();
                }

                releaseVelocityTracker();
                break;
        }
    }

    private void moveH(MotionEvent event) {
        int x = (int)event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownPos = x;
                break;

            case MotionEvent.ACTION_MOVE:
                int offset = x - mDownPos;
                this.setScrollX(-offset);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                final float velocityX = mVelocityTracker.getXVelocity(mPointerId);
                final float velocityY = mVelocityTracker.getYVelocity(mPointerId);

                // animator
                int scrollX = this.getScrollX();
                int w = this.getMeasuredWidth();
                int dis = Math.abs(scrollX);
                if (dis > w / 2 || Math.abs(velocityX) > VEL_THRESHOLD) {
                    if (scrollX < 0) {
                        mIsNext = false;
                        ani = ObjectAnimator.ofInt(this, "scrollX", scrollX, -w);
                    } else {
                        mIsNext = true;
                        ani = ObjectAnimator.ofInt(this, "scrollX", scrollX, w);
                    }
                    ani.setDuration(mAnimatorTimeInterval).addListener(mAniListener);
                    ani.setInterpolator(getTimeInterpolater());
                    ani.start();
                } else {
                    ObjectAnimator.ofInt(this, "scrollX", scrollX, 0).setDuration(mAnimatorTimeInterval).start();
                }

                releaseVelocityTracker();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCanSlide) {
            acquireVelocityTracker(event);

            if (mIsHorizontal) {
                moveH(event);
            } else {
                moveV(event);
            }

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    protected void removeAll() {
            int count = this.getChildCount();
            for (int i = 0; i < count; ++i) {
                removeData(i);
            }
            this.removeAllViews();
    }

    private void removeData(int index) {
        View v = this.getChildAt(index);

        Adapter.ViewHolder vh = (Adapter.ViewHolder)v.getTag();
        ((IContainer)vh.mItemView).getVirtualView().reset();
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

    private void remove(int index) {
        removeData(index);
        this.removeViewAt(index);
    }

    private void changeChildren() {
        // remove old item
        if (mAdapter == null) {
            return;
        }
        int count = mAdapter.getItemCount();
        if (count <= 0 || this.getChildCount() <= 0) {
            return;
        }

        if (mIsNext) {
            if (mLayoutNormal) {
                remove(0);
            } else {
                remove(this.getChildCount() - 1);
            }

            mCurPos = (mCurPos + 1) % count;

            int pos = (mCurPos + 1) % count;
            if (mLayoutNormal) {
                add(pos);
            } else {
                add(pos, 0);
            }
        } else {
            if (mLayoutNormal) {
                remove(this.getChildCount() - 1);
            } else {
                remove(0);
            }

            mCurPos -= 1;
            if (mCurPos < 0) {
                mCurPos += count;
            }

            int pos = (mCurPos - 1);
            if (pos < 0) {
                pos += count;
            }
            if (mLayoutNormal) {
                add(pos, 0);
            } else {
                add(pos);
            }
        }

        this.requestLayout();
        if (mIsHorizontal) {
            this.setScrollX(0);
        } else {
            this.setScrollY(0);
        }

        if (mAutoSwitch) {
            mAutoSwitchHandler.removeMessages(MSG_AUTO_SWITCH);
            if (mCanSwitch) {
                mAutoSwitchHandler.sendEmptyMessageDelayed(MSG_AUTO_SWITCH, mStayTime);
            }
        }
    }

    private void resetChildren() {
        if (mIsHorizontal) {
            this.setScrollX(0);
        } else {
            this.setScrollY(0);
        }
    }

    class MyAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            changeChildren();

            if (null != mListener) {
                mListener.onPageFlip(mCurPos + 1, mAdapter.getItemCount());
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            resetChildren();
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        this.measureChildren(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = this.getChildCount();
        int w = r - l;
        int h = b - t;
        int offset = 0;

        if (mIsHorizontal) {
            if (mLayoutNormal && mCanSlide) {
                offset = -w;
            } else if (!mLayoutNormal) {
                offset = -w;
            }
            for (int i = 0; i < count; ++i) {
                View child = this.getChildAt(i);
                child.layout(offset, 0, offset + w, h);
                offset += w;
            }
        } else {
            if (count > 1) {
                if (mLayoutNormal && mCanSlide) {
                    offset = -h;
                } else if (!mLayoutNormal) {
                    offset = -h;
                }
            }
            for (int i = 0; i < count; ++i) {
                View child = this.getChildAt(i);
                child.layout(0, offset, w, offset + h);
                offset += h;
            }
        }
    }

    private TimeInterpolator getTimeInterpolater() {
        switch (mAnimationStyle) {
            case ViewBaseCommon.ANIMATION_LINEAR:
                return new LinearInterpolator();
            case ViewBaseCommon.ANIMATION_DECELERATE:
                return new DecelerateInterpolator();
            case ViewBaseCommon.ANIMATION_ACCELERATE:
                return new AccelerateInterpolator();
            case ViewBaseCommon.ANIMATION_ACCELERATEDECELERATE:
                return new AccelerateDecelerateInterpolator();
            case ViewBaseCommon.ANIMATION_SPRING:
                return new SpringInterpolator();
            default:
                return new LinearInterpolator();
        }
    }

    static class AutoSwitchHandler extends Handler {
        private PageView mPageView;

        AutoSwitchHandler(PageView pageView) {
            mPageView = new WeakReference<>(pageView).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mPageView != null && MSG_AUTO_SWITCH == msg.what) {
                mPageView.autoSwitch();
            }
        }
    }

    static public class SpringInterpolator implements TimeInterpolator {

        private static final float FACTOR = 4f;

        @Override
        public float getInterpolation(float input) {
            return (float)(Math.pow(2, -10 * input) * Math.sin((input - FACTOR / 4) * (2 * PI) / FACTOR) + 1);
        }
    }

    static public class DecelerateInterpolator implements TimeInterpolator {

        private static final float FACTOR = 5.0f;

        @Override
        public float getInterpolation(float input) {
            float result;
            if (FACTOR == 1.0f) {
                result = (float)(1.0f - (1.0f - input) * (1.0f - input));
            } else {
                result = (float)(1.0f - Math.pow((1.0f - input), 2 * FACTOR));
            }
            return result;
        }
    }

}
