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

package com.tmall.wireless.vaf.virtualview.view.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.text.TextUtils;
import com.libra.virtualview.common.ImageCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

/**
 * Created by gujicheng on 16/8/19.
 */
public class VirtualImage extends ImageBase {
    private final static String TAG = "VirtualImage_TMTEST";

    protected Bitmap mBitmap;
    protected Matrix mMatrix;

    private VirtualViewImp mImp = new VirtualViewImp();

    public VirtualImage(VafContext context, ViewCache viewCache) {
        super(context, viewCache);
        mMatrix = new Matrix();
        mImp.setViewBase(this);
    }

    @Override
    public void reset() {
        super.reset();

        mImp.reset();
        mBitmap = null;
    }

    @Override
    protected void makeContentRect() {
        if (null != mBitmap) {
            if (mContentRect == null) {
                mContentRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            } else {
                mContentRect.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            }
        } else {
            if (this.mMeasuredWidth > 0 && this.mMeasuredHeight > 0) {
                if (!TextUtils.isEmpty(mSrc)) {
                    loadImage(mSrc);
                }
            }
        }
    }

    @Override
    public void setBitmap(Bitmap b, boolean refresh) {
        mBitmap = b;
        mContentRect = null;
        if (refresh) {
            refresh();
        }
    }

    @Override
    public void loadImage(String path) {
        if (this.mMeasuredWidth > 0 && this.mMeasuredHeight > 0) {
            mContext.getImageLoader().bindBitmap(path, this, this.mMeasuredWidth, this.mMeasuredHeight);
        }
    }

    @Override
    public void setSrc(String path) {
        if (!TextUtils.equals(mSrc, path)) {
            mSrc = path;
            loadImage(path);
        }
    }


    @Override
    protected void onComDraw(Canvas canvas) {
        super.onComDraw(canvas);

        if (null == mContentRect) {
            makeContentRect();
        }

        if (null != mContentRect) {
            switch (mScaleType) {
                case ImageCommon.SCALE_TYPE_MATRIX:
                    canvas.drawBitmap(mBitmap, 0, 0, mPaint);
                    break;

                case ImageCommon.SCALE_TYPE_FIT_XY:
                    mMatrix.setScale(((float) mMeasuredWidth) / mContentRect.width(), ((float) mMeasuredHeight) / mContentRect.height());
                    canvas.drawBitmap(mBitmap, mMatrix, mPaint);
                    break;

                case ImageCommon.SCALE_TYPE_FIT_START:
                    mMatrix.setScale(((float) mMeasuredWidth) / mContentRect.width(), ((float) mMeasuredHeight) / mContentRect.height());
                    canvas.drawBitmap(mBitmap, mMatrix, mPaint);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();
        mPaint.setFilterBitmap(true);
        loadImage(mSrc);
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mImp.onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        mImp.measureComponent(widthMeasureSpec, heightMeasureSpec);
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new VirtualImage(context, viewCache);
        }
    }
}
