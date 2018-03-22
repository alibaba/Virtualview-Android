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

package com.tmall.wireless.virtualviewdemo.page;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.IContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujicheng on 16/9/13.
 */
public class Page extends VafContext {
    private final static String TAG = "Page_TMTEST";

    protected List<IContainer> mRootContainers = new ArrayList<>();

    protected ViewGroup mRootView;

    public Page(Context context) {
        super(context);
    }

    public boolean onCreate(ViewGroup root, String binPath) {
        Log.d(TAG, "onCreate");
        boolean ret = false;

        mRootView = root;
        if (!TextUtils.isEmpty(binPath)) {
            ret = mViewManager.init(mContext);
        }

        // register pageManager

        return ret;
    }

    public void finish() {
    }

    public View createRootContainer(String type) {
        View v = createContainer(type);
        if (null != v) {
            mRootContainers.add((IContainer) v);
        }
        return v;
    }

    public void onResume() {
    }

    public void onPause() {
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        for (IContainer container : mRootContainers) {
            container.destroy();
        }
        mRootContainers.clear();
        mRootContainers = null;
    }
}
