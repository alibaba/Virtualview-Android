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

package com.tmall.wireless.virtualviewdemo.custom;

import android.view.View;
import com.libra.Utils;
import com.libra.expr.common.StringSupport;
import com.squareup.picasso.Picasso;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.ViewCache.Item;

/**
 * Created by longerian on 2018/3/17.
 *
 * @author longerian
 * @date 2018/03/17
 */

public class PicassoImage extends ViewBase {

    protected PicassoImageView mPicassoImageView;

    private int urlId;

    private int degreeId;

    private String url;

    private float degrees;

    public PicassoImage(VafContext context,
        ViewCache viewCache) {
        super(context, viewCache);
        mPicassoImageView = new PicassoImageView(context.getContext());
        StringSupport mStringSupport = context.getStringLoader();
        urlId = mStringSupport.getStringId("url", false);
        degreeId = mStringSupport.getStringId("degree", false);
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mPicassoImageView.onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        mPicassoImageView.onComLayout(changed, l, t, r, b);
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        super.comLayout(l, t, r, b);
        mPicassoImageView.comLayout(l, t, r, b);
    }

    @Override
    public View getNativeView() {
        return mPicassoImageView;
    }

    @Override
    public int getComMeasuredWidth() {
        return mPicassoImageView.getComMeasuredWidth();
    }

    @Override
    public int getComMeasuredHeight() {
        return mPicassoImageView.getComMeasuredHeight();
    }

    @Override
    protected boolean setAttribute(int key, float value) {
        boolean ret = true;
        if (key == degreeId) {
            degrees = value;
        } else {
            ret = super.setAttribute(key, value);
        }
        return ret;
    }

    @Override
    protected boolean setAttribute(int key, String stringValue) {
        boolean ret = true;
        if (key == degreeId) {
            if (Utils.isEL(stringValue)) {
                mViewCache.put(this, degreeId, stringValue, Item.TYPE_FLOAT);
            }
        } else if (key == urlId) {
            if (Utils.isEL(stringValue)) {
                mViewCache.put(this, urlId, stringValue, Item.TYPE_STRING);
            } else {
                url = stringValue;
            }
        } else {
            ret = super.setAttribute(key, stringValue);
        }
        return ret;
    }

    @Override
    public void reset() {
        super.reset();
        Picasso.with(mContext.getContext()).cancelRequest(mPicassoImageView);
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();
        Picasso.with(mContext.getContext()).load(url).rotate(degrees).into(mPicassoImageView);
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new PicassoImage(context, viewCache);
        }
    }
}
