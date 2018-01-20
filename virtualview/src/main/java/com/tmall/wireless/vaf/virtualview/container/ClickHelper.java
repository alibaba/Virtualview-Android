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

package com.tmall.wireless.vaf.virtualview.container;

import android.os.Handler;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

/**
 * Created by gujicheng on 16/9/7.
 */
public class ClickHelper {
    private final static String TAG = "ClickHelper_TMTEST";

    protected final static int LONG_PRESS_THRESHOLD = 500;

    protected boolean mClickFinished = true;

    protected boolean mLongClickPressed = false;

    protected int mLastX;

    protected int mLastY;

    protected int mStartX;

    protected int mStartY;

    protected IContainer mContainer;

    protected LongRunnable mRunnable;

    public ClickHelper(IContainer c) {
        mContainer = c;
        mRunnable = new LongRunnable();
        final View holderView = c.getHolderView();
        final ViewBase vb = c.getVirtualView();
        //if (vb.isClickable()) {
        //    holderView.setOnClickListener(new OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            final ViewBase vView = mContainer.getVirtualView();
        //            if (null != vView) {
        //                vView.click(0, 0, false);
        //            }
        //        }
        //    });
        //}
        //if (vb.isLongClickable()) {
        //    holderView.setOnLongClickListener(new OnLongClickListener() {
        //        @Override
        //        public boolean onLongClick(View v) {
        //            final ViewBase vView = mContainer.getVirtualView();
        //            if (vView != null) {
        //                vView.click(0, 0, true);
        //            }
        //            return true;
        //        }
        //    });
        //}
        //if (vb.isTouchable()) {
        //    holderView.setOnTouchListener(new OnTouchListener() {
        //        @Override
        //        public boolean onTouch(View v, MotionEvent event) {
        //            final ViewBase vView = mContainer.getVirtualView();
        //            if (vView != null) {
        //                return vView.onTouch(v, event);
        //            }
        //            return false;
        //        }
        //    });
        //}

        //if (vb.isClickable() || vb.isLongClickable() || vb.isTouchable()) {
        holderView.setOnTouchListener(new View.OnTouchListener() {



            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean ret = false;
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mClickFinished = false;
                        mLongClickPressed = false;
                        mStartX = (int)motionEvent.getX();
                        mStartY = (int)motionEvent.getY();
                        mLastX = mStartX;
                        mLastY = mStartY;
                        if (vb.handleEvent(mStartX, mStartY)) {
                            ret = true;
                            Handler h = holderView.getHandler();
                            h.removeCallbacks(mRunnable);
                            mRunnable.setView(mContainer.getVirtualView());
                            mRunnable.setHolderView(holderView);
                            h.postDelayed(mRunnable, LONG_PRESS_THRESHOLD);
                            vb.onTouch(view, motionEvent);
                        } else {
                            ret = false;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        final ViewBase vView = mContainer.getVirtualView();
                        if (null != vView && !mLongClickPressed) {
                            ret = vView.click(mStartX, mStartY, false);
                            if (ret) {
                                holderView.playSoundEffect(SoundEffectConstants.CLICK);

                            }
                        }
                        vb.onTouch(view, motionEvent);
                        mClickFinished = true;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int currentX = (int)motionEvent.getX();
                        int currentY = (int)motionEvent.getY();
                        if (Math.sqrt(Math.pow(currentX - mLastX, 2) + Math.pow(currentY - mLastY, 2))
                            > VafContext.SLOP) {
                            holderView.removeCallbacks(mRunnable);
                        }
                        mLastX = currentX;
                        mLastY = currentY;
                        vb.onTouch(view, motionEvent);
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        vb.onTouch(view, motionEvent);
                        mClickFinished = true;
                        break;
                    default:
                        break;
                }

                return ret;
            }
        });
        //}
    }

    class LongRunnable implements Runnable {
        protected ViewBase mView;

        protected View mHolderView;

        public void setHolderView(View holderView) {
            mHolderView = holderView;
        }

        public void setView(ViewBase v) {
            mView = v;

        }

        @Override
        public void run() {
            if (!mClickFinished && null != mView) {
                boolean ret = mView.click(mStartX, mStartY, true);
                if (ret && mHolderView != null) {
                    mLongClickPressed = true;
                    mHolderView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                }
            }
        }
    }
}
