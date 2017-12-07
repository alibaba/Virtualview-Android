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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.tmall.wireless.vaf.framework.cm.ContainerService;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.IView;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

/**
 * Created by gujicheng on 16/8/24.
 */
public class SurfaceContainer extends SurfaceView implements SurfaceHolder.Callback, IContainer, IView {
    private final static String TAG = "SurfaceContainer";

    protected ViewBase mView;

    private final static int MSG_DRAW = 1;
    private static HandlerThread mThread;
    private Handler mMsgHandler;
    private SurfaceHolder mHolder;

    private boolean mIsCreate = false;
    private boolean mIsLayout = false;

    public SurfaceContainer(Context context) {
        super(context);

        new ClickHelper(this);

        mHolder = this.getHolder();
        mHolder.addCallback(this);

        // for transparent background
        this.setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        if (null == mThread) {
            mThread = new HandlerThread("my_ui_thread");
            mThread.start();
        }

        mMsgHandler = new Handler(mThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_DRAW: {
                        Canvas c = mHolder.lockCanvas();
                        if (null != c) {
                            // draw
                            mView.comDraw(c);

                            mHolder.unlockCanvasAndPost(c);
                        }
                        break;
                    }
                }
            }
        };
    }

    public void mydraw() {
        mMsgHandler.sendEmptyMessage(MSG_DRAW);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        onComLayout(changed, 0, 0, right - left, bottom - top);

        mIsLayout = true;
        if (mIsCreate && mIsLayout) {
            mydraw();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mView.comDraw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mIsCreate = true;

        if (mIsCreate && mIsLayout) {
            mydraw();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mIsCreate = false;

    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        this.layout(l, t, r, b);
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mView.onComMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(mView.getComMeasuredWidth(), mView.getComMeasuredHeight());
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        mView.onComLayout(changed, l, t, r, b);
    }

    @Override
    public int getComMeasuredWidth() {
        return mView.getComMeasuredWidth();
    }

    @Override
    public int getComMeasuredHeight() {
        return mView.getComMeasuredHeight();
    }

    @Override
    public void attachViews() {
    }

    @Override
    public void setVirtualView(ViewBase view) {
        mView = view;

        Layout.Params p = mView.getComLayoutParams();
        this.setLayoutParams(new ViewGroup.LayoutParams(p.mLayoutWidth, p.mLayoutHeight));
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
    public void destroy() {

    }

    @Override
    public int getType() {
        return ContainerService.CONTAINER_TYPE_SURFACE;
    }
}
