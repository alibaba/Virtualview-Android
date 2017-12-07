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

package com.tmall.wireless.vaf.framework;

import android.util.Log;
import android.view.View;

import com.tmall.wireless.vaf.virtualview.core.IContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujicheng on 16/10/19.
 */
public class VafPage {
    private final static String TAG = "VafPage_TMTEST";

    protected List<IContainer> mRootContainers = new ArrayList<>();
    protected VafContext mContext;

    public VafPage(VafContext context) {
        mContext = context;
    }

    public void onCreate() {
    }

    public VafContext getVafContext() {
        return mContext;
    }

    public View createRootContainer(String type) {
        View v = mContext.createContainer(type);
        if (null != v) {
            mRootContainers.add((IContainer) v);
        }
        return v;
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        for (IContainer container : mRootContainers) {
            container.destroy();
        }
        mRootContainers.clear();
        mRootContainers = null;

        mContext.getDataLoader().cancelAll();

//        mContext.onDestroy();
    }

}
