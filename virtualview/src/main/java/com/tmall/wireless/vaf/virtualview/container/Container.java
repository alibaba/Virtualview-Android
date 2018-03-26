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

package com.tmall.wireless.vaf.virtualview.container;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import com.tmall.wireless.vaf.framework.cm.ContainerService;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.IView;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.view.nlayout.NativeLayoutImpl;

/**
 * Created by gujicheng on 16/8/16.
 */
public class Container extends ViewGroup implements IContainer, IView {
    private final static String TAG = "Container_TMTEST";

    protected ViewBase mView;

    public Container(Context context) {
        super(context);
    }

    @Override
    public void destroy() {
        mView.destroy();
        mView = null;
    }

    @Override
    public int getType() {
        return ContainerService.CONTAINER_TYPE_NORMAL;
    }

    @Override
    public void attachViews() {
        attachViews(mView, this);
    }

    protected void attachViews(ViewBase view, View displayViewHolder) {
        view.setDisplayViewContainer(displayViewHolder);
        if (view instanceof Layout) {
            View v = view.getNativeView();
            if (null != v) {
                LayoutParams layoutParams = new LayoutParams(view.getComLayoutParams().mLayoutWidth, view.getComLayoutParams().mLayoutHeight);
                addView(v, layoutParams);
                if (v instanceof NativeLayoutImpl) {
                    Layout layout = (Layout) view;
                    List<ViewBase> subViews = layout.getSubViews();
                    if (null != subViews) {
                        for (int i = 0, size = subViews.size(); i < size; i++) {
                            ViewBase com = subViews.get(i);
                            ((NativeLayoutImpl) v).attachViews(com, v);
                        }
                    }
                }
            } else {
                Layout layout = (Layout) view;
                List<ViewBase> subViews = layout.getSubViews();
                if (null != subViews) {
                    for (int i = 0, size = subViews.size(); i < size; i++) {
                        ViewBase com = subViews.get(i);
                        attachViews(com, displayViewHolder);
                    }
                }
            }
        } else {
            View v = view.getNativeView();
            if (null != v) {
                LayoutParams layoutParams = new LayoutParams(view.getComLayoutParams().mLayoutWidth, view.getComLayoutParams().mLayoutHeight);
                addView(v, layoutParams);
            }
        }
    }

    @Override
    public void setVirtualView(ViewBase l) {
        if (null != l) {
            mView = l;
            mView.setHoldView(this);

            if (mView.shouldDraw()) {
                setWillNotDraw(false);
            }

            new ClickHelper(this);
        }
    }

    public void detachViews() {
        this.removeAllViews();
    }

    @Override
    public ViewBase getVirtualView() {
        return mView;
    }

    @Override
    public View getHolderView() {
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null != mView && mView.shouldDraw()) {
            mView.comDraw(canvas);
        }
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        if (null != mView) {
            if (!mView.isGone()) {
                mView.measureComponent(widthMeasureSpec, heightMeasureSpec);
            }
            this.setMeasuredDimension(mView.getComMeasuredWidth(), mView.getComMeasuredHeight());
        }
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        if (null != mView && !mView.isGone()) {
            mView.comLayout(0, 0, r - l, b - t);
            this.layout(l, t, r, b);
        }
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (null != mView) {
            if (!mView.isGone()) {
                mView.onComMeasure(widthMeasureSpec, heightMeasureSpec);
            }
            setMeasuredDimension(mView.getComMeasuredWidth(), mView.getComMeasuredHeight());
        }
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        if (null != mView && !mView.isGone()) {
            mView.onComLayout(changed, l, t, r, b);
        }
    }

    @Override
    public int getComMeasuredWidth() {
        if (null != mView) {
            return mView.getComMeasuredWidth();
        } else {
            return 0;
        }
    }

    @Override
    public int getComMeasuredHeight() {
        if (null != mView) {
            return mView.getComMeasuredHeight();
        } else {
            return 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        onComLayout(changed, 0, 0, r - l, b - t);
    }

}
