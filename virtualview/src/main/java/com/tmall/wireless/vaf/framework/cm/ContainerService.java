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

package com.tmall.wireless.vaf.framework.cm;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewGroup.MarginLayoutParams;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.framework.ViewManager;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gujicheng on 16/9/20.
 */
public class ContainerService {
    private final static String TAG = "ContainerService_TMTEST";

    public final static int MAX_CONTAINER_MRG_COUNT = 20;

    public final static int CONTAINER_TYPE_NORMAL = 0;

    private VafContext mAppContext;
    private ViewManager mVM;
    protected ComContainerTypeMap mComContainerTypeMap;

    private List<ContainerMrg> mContainerMrg = new ArrayList<>(MAX_CONTAINER_MRG_COUNT);

    public ContainerService() {
        registerContainerMrg(new NormalManager(), CONTAINER_TYPE_NORMAL);
    }

    public void registerContainerMrg(ContainerMrg cm, int containerID) {
        if (null != cm && containerID >= 0 && containerID < MAX_CONTAINER_MRG_COUNT) {
            mContainerMrg.add(containerID, cm);
        } else {
            Log.e(TAG, "param invalidate containerID:" + containerID);
        }
    }

    public void setPageContext(VafContext context) {
        mAppContext = context;
        mVM = mAppContext.getViewManager();
        mComContainerTypeMap = mAppContext.getComContainerTypeMap();
    }

    public ComContainerTypeMap getComContainerTypeMap() {
        return mComContainerTypeMap;
    }

    public void destroy() {
        for (ContainerMrg cm : mContainerMrg) {
            if (null != cm) {
                cm.destroy();
            }
        }

        mVM = null;
        mAppContext = null;
    }

    public void recycle(IContainer container) {
        recycle(container, true);
    }

    public void recycle(IContainer container, boolean isRecycleView) {
        if (null != container) {
            if (isRecycleView) {
                ViewBase vb = container.getVirtualView();
                if (null != vb) {
                    mVM.recycle(vb);
                    if (container instanceof ViewGroup) {
                        ((ViewGroup)container).removeAllViews();
                    } else {
                    }
                } else {
                    Log.e(TAG, "recycle viewbase is null");
                }
            }

            int type = container.getType();
            if (type > -1) {
                ContainerMrg cm = mContainerMrg.get(type);
                if (null != cm) {
                    cm.recycle(container);
                } else {
                    Log.e(TAG, "recycle container type is invalidate:" + container.getType());
                }
            }
        }
    }

    public View getContainer(String viewType) {
        return getContainer(viewType, true);
    }

    public View getContainer(String viewType, int containerId) {
        return getContainer(viewType, containerId, true);
    }

    public View getContainer(String viewType, boolean createParam) {
        int type = mComContainerTypeMap.getContainerMap(viewType);
        if (type <= -1) {
            type = CONTAINER_TYPE_NORMAL;
        }
        return getContainer(viewType, type, createParam);
    }

    public View getContainer(String viewType, int containerType, boolean createParam) {
        IContainer container = null;

        ViewBase vb = mVM.getView(viewType);
        if (null == vb) {
            vb = mVM.getDefaultImage();
            vb.setViewType(viewType);
        }

        if (vb.isContainer()) {
            container = (IContainer) vb.getNativeView();
        } else {
            ContainerMrg cm = mContainerMrg.get(containerType);
            if (null != cm) {
                container = cm.getContainer(mAppContext);
            } else {
                Log.e(TAG, "getContainer type invalidate:" + containerType);
            }
        }

        if (null != container) {
            container.setVirtualView(vb);
            if (createParam) {
                Layout.Params p = vb.getComLayoutParams();
                MarginLayoutParams marginLayoutParams = new MarginLayoutParams(p.mLayoutWidth, p.mLayoutHeight);
                marginLayoutParams.leftMargin = p.mLayoutMarginLeft;
                marginLayoutParams.topMargin = p.mLayoutMarginTop;
                marginLayoutParams.rightMargin = p.mLayoutMarginRight;
                marginLayoutParams.bottomMargin = p.mLayoutMarginBottom;
                ((View)container).setLayoutParams(marginLayoutParams);
            }

            container.attachViews();
        }
        return (View)container;
    }
}
