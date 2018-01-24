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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_X;
import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_Y;

/**
 * Created by gujicheng on 16/8/16.
 */
public class NativeImage extends ImageBase {
    private final static String TAG = "NativeImage_TMTEST";

    protected NativeImageImp mNative;

    public NativeImage(VafContext context, ViewCache viewCache) {
    super(context, viewCache);
        mNative = new NativeImageImp(context.getContext());
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public void setBitmap(Bitmap b, boolean refresh) {
        mNative.setImageBitmap(b);
    }

    @Override
    public void setImageDrawable(Drawable d, boolean refresh) {
        mNative.setImageDrawable(d);
    }

    @Override
    public void setSrc(String path) {
        mSrc = path;
        this.mContext.getImageLoader().bindBitmap(mSrc, this, this.getComMeasuredWidth(), this.getComMeasuredHeight());
    }

    @Override
    public View getNativeView() {
        return mNative;
    }

    @Override
    public void reset() {
        super.reset();
        this.mContext.getImageLoader().bindBitmap(null, this, this.getComMeasuredWidth(), this.getComMeasuredHeight());
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();
        mNative.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        mNative.setScaleType(ImageBase.IMAGE_SCALE_TYPE.get(mScaleType));
        setSrc(this.mSrc);
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAutoDimDirection > 0) {
            switch (mAutoDimDirection) {
                case AUTO_DIM_DIR_X:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(widthMeasureSpec)) {
                        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(widthMeasureSpec) * mAutoDimY) / mAutoDimX), View.MeasureSpec.EXACTLY);
                    }
                    break;

                case AUTO_DIM_DIR_Y:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(heightMeasureSpec)) {
                        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(heightMeasureSpec) * mAutoDimX) / mAutoDimY), View.MeasureSpec.EXACTLY);
                    }
                    break;
                default:
                    break;
            }
        }
        mNative.measureComponent(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mAutoDimDirection > 0) {
            switch (mAutoDimDirection) {
                case ViewBaseCommon.AUTO_DIM_DIR_X:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(widthMeasureSpec)) {
                        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(widthMeasureSpec) * mAutoDimY) / mAutoDimX), View.MeasureSpec.EXACTLY);
                    }
                    break;

                case ViewBaseCommon.AUTO_DIM_DIR_Y:
                    if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(heightMeasureSpec)) {
                        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int)((View.MeasureSpec.getSize(heightMeasureSpec) * mAutoDimX) / mAutoDimY), View.MeasureSpec.EXACTLY);
                    }
                    break;
                default:
                    break;
            }
        }

        mNative.onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        mNative.onComLayout(changed, l, t, r, b);
    }

    @Override
    public int getComMeasuredWidth() {
        return mNative.getComMeasuredWidth();
    }

    @Override
    public int getComMeasuredHeight() {
        return mNative.getComMeasuredHeight();
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        mNative.comLayout(l, t, r, b);
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new NativeImage(context, viewCache);
        }
    }

}
