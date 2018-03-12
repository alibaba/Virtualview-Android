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

package com.tmall.wireless.vaf.virtualview.view.nlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.IView;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

/**
 * Created by longerian on 2018/3/11.
 *
 * @author longerian
 * @date 2018/03/11
 */

public class NGridLayoutImpl extends NGridLayoutView implements IView, IContainer {

    protected ViewBase mVirtualView;


    public NGridLayoutImpl(Context context) {
        super(context);
    }

    public NGridLayoutImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NGridLayoutImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    @Override
    public void attachViews() {

    }

    @Override
    public void setVirtualView(ViewBase view) {

    }

    @Override
    public ViewBase getVirtualView() {
        return mVirtualView;
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
        return -1;
    }
}
