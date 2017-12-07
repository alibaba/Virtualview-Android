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

package com.tmall.wireless.vaf.virtualview.core;

import android.view.View;

import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.framework.cm.ContainerService;

/**
 * Created by gujicheng on 16/12/15.
 */

public abstract class Adapter {
    protected final static String TYPE = "type";

    protected VafContext mContext;
    protected boolean mDataIsChange = true;
    protected int mContainerId = ContainerService.CONTAINER_TYPE_NORMAL;
    protected ContainerService mContainerService;

    public Adapter(VafContext context) {
        mContainerService = context.getContainerService();
        mContext = context;
    }

    public abstract void setData(Object str);

    public void setContainerId(int id) {
        mContainerId = id;
    }

    public abstract int getItemCount();

    public int getType(int pos) {
        return 0;
    }

    public abstract void onBindViewHolder(ViewHolder vh, int pos);

    public abstract ViewHolder onCreateViewHolder(int viewType);

    public void notifyChange() {
        mDataIsChange = true;
    }

    public static class ViewHolder {
        public ViewHolder(View v) {
            mItemView = v;
            mItemView.setTag(this);
        }

        public View mItemView;
        public int mType;
        public int mPos;
    }

}
