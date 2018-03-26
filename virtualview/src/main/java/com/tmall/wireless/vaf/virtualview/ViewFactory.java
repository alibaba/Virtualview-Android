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

package com.tmall.wireless.vaf.virtualview;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;

import com.libra.virtualview.common.Common;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.container.Container;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.ViewCache.Item;
import com.tmall.wireless.vaf.virtualview.layout.FlexLayout;
import com.tmall.wireless.vaf.virtualview.layout.FrameLayout;
import com.tmall.wireless.vaf.virtualview.layout.GridLayout;
import com.tmall.wireless.vaf.virtualview.layout.RatioLayout;
import com.tmall.wireless.vaf.virtualview.layout.VH2Layout;
import com.tmall.wireless.vaf.virtualview.layout.VHLayout;
import com.tmall.wireless.vaf.virtualview.loader.BinaryLoader;
import com.tmall.wireless.vaf.virtualview.loader.CodeReader;
import com.tmall.wireless.vaf.virtualview.loader.ExprCodeLoader;
import com.tmall.wireless.vaf.virtualview.loader.UiCodeLoader;
import com.tmall.wireless.vaf.virtualview.view.VirtualContainer;
import com.tmall.wireless.vaf.virtualview.view.VirtualGraph;
import com.tmall.wireless.vaf.virtualview.view.VirtualTime;
import com.tmall.wireless.vaf.virtualview.view.grid.Grid;
import com.tmall.wireless.vaf.virtualview.view.image.NativeImage;
import com.tmall.wireless.vaf.virtualview.view.image.VirtualImage;
import com.tmall.wireless.vaf.virtualview.view.line.NativeLine;
import com.tmall.wireless.vaf.virtualview.view.line.VirtualLine;
import com.tmall.wireless.vaf.virtualview.view.nlayout.NFrameLayout;
import com.tmall.wireless.vaf.virtualview.view.nlayout.NGridLayout;
import com.tmall.wireless.vaf.virtualview.view.nlayout.NRatioLayout;
import com.tmall.wireless.vaf.virtualview.view.nlayout.NVH2Layout;
import com.tmall.wireless.vaf.virtualview.view.nlayout.NVHLayout;
import com.tmall.wireless.vaf.virtualview.view.page.Page;
import com.tmall.wireless.vaf.virtualview.view.progress.VirtualProgress;
import com.tmall.wireless.vaf.virtualview.view.scroller.Scroller;
import com.tmall.wireless.vaf.virtualview.view.slider.Slider;
import com.tmall.wireless.vaf.virtualview.view.text.NativeText;
import com.tmall.wireless.vaf.virtualview.view.text.VirtualText;
import com.tmall.wireless.vaf.virtualview.view.vh.VH;

import java.util.List;
import java.util.Stack;

/**
 * Created by gujicheng on 16/8/16.
 */
public class ViewFactory {
    private final static String TAG = "ViewFac_TMTEST";

    private static final int STATE_continue = 0;
    private static final int STATE_successful = 1;
    private static final int STATE_failed = 2;

    private static UiCodeLoader mUiCodeLoader = new UiCodeLoader();
    private static ExprCodeLoader mExprCodeLoader = new ExprCodeLoader();
    private static BinaryLoader mLoader = new BinaryLoader();
    private Stack<ViewBase> mComArr = new Stack<>();

    static {
        mLoader.setUiCodeManager(mUiCodeLoader);
        mLoader.setExprCodeManager(mExprCodeLoader);
    }
    private SparseArray<ViewBase.IBuilder> mBuilders = new SparseArray<>();

    private VafContext mAppContext;

    public ViewFactory() {

        mBuilders.put(Common.VIEW_ID_FrameLayout, new FrameLayout.Builder());
        mBuilders.put(Common.VIEW_ID_GridLayout, new GridLayout.Builder());
        mBuilders.put(Common.VIEW_ID_VHLayout, new VHLayout.Builder());
        mBuilders.put(Common.VIEW_ID_FlexLayout, new FlexLayout.Builder());
        mBuilders.put(Common.VIEW_ID_RatioLayout, new RatioLayout.Builder());
        mBuilders.put(Common.VIEW_ID_VH2Layout, new VH2Layout.Builder());
        mBuilders.put(Common.VIEW_ID_NativeText, new NativeText.Builder());
        mBuilders.put(Common.VIEW_ID_VirtualText, new VirtualText.Builder());
        mBuilders.put(Common.VIEW_ID_NativeImage, new NativeImage.Builder());
        mBuilders.put(Common.VIEW_ID_VirtualImage, new VirtualImage.Builder());
        mBuilders.put(Common.VIEW_ID_VirtualLine, new VirtualLine.Builder());
        mBuilders.put(Common.VIEW_ID_Scroller, new Scroller.Builder());
        mBuilders.put(Common.VIEW_ID_Page, new Page.Builder());
        mBuilders.put(Common.VIEW_ID_Grid, new Grid.Builder());
        mBuilders.put(Common.VIEW_ID_NativeLine, new NativeLine.Builder());
        mBuilders.put(Common.VIEW_ID_VirtualGraph, new VirtualGraph.Builder());
        mBuilders.put(Common.VIEW_ID_VH, new VH.Builder());
        mBuilders.put(Common.VIEW_ID_VirtualTime, new VirtualTime.Builder());
        mBuilders.put(Common.VIEW_ID_Slider, new Slider.Builder());
        mBuilders.put(Common.VIEW_ID_VirtualProgress, new VirtualProgress.Builder());
        mBuilders.put(Common.VIEW_ID_VirtualContainer, new VirtualContainer.Builder());
        mBuilders.put(Common.VIEW_ID_NFrameLayout, new NFrameLayout.Builder());
        mBuilders.put(Common.VIEW_ID_NGridLayout, new NGridLayout.Builder());
        mBuilders.put(Common.VIEW_ID_NRatioLayout, new NRatioLayout.Builder());
        mBuilders.put(Common.VIEW_ID_NVH2Layout, new NVH2Layout.Builder());
        mBuilders.put(Common.VIEW_ID_NVHLayout, new NVHLayout.Builder());
    }

    public void destroy() {
        mAppContext = null;

        //mUiCodeLoader.destroy();
        //mUiCodeLoader = null;
        //
        //mExprCodeLoader.reset();
        //mExprCodeLoader = null;
        //
        //mLoader.destroy();
        //mLoader = null;

        mComArr.clear();
        mBuilders.clear();
    }

    public void setPageContext(VafContext context) {
        mAppContext = context;

        mLoader.setPageContext(context);
    }

    public boolean init(Context context) {
        final Resources resources = context.getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        com.libra.Utils.init(dm.density, dm.widthPixels);
        return true;
    }

    public int loadBinFile(String resPath) {
        return mLoader.loadFromFile(resPath);
    }

    public int loadBinBuffer(byte[] buf) {
        return mLoader.loadFromBuffer(buf);
    }

    public int loadBinBuffer(byte[] buf, boolean override) {
        return mLoader.loadFromBuffer(buf, override);
    }

    public boolean registerBuilder(int id, ViewBase.IBuilder builder) {
        boolean ret = false;

        if (null != builder) {
            if (null == mBuilders.get(id)) {
                mBuilders.put(id, builder);
                ret = true;
            } else {
                Log.e(TAG, "register builder failed, already exist id:" + id);
            }
        } else {
            Log.e(TAG, "register builder failed, builder is null");
        }

        return ret;
    }

    public boolean overrideBuilder(int id, ViewBase.IBuilder builder) {
        boolean ret = false;

        if (null != builder) {
            mBuilders.put(id, builder);
            ret = true;
        } else {
            Log.e(TAG, "register builder failed, builder is null");
        }

        return ret;
    }

    public IContainer newViewWithContainer(String type) {
        IContainer ret = null;

        ViewBase view = newView(type);
        if (null != view) {
            ret = new Container(mAppContext.getContext());
            ret.setVirtualView(view);

            ret.attachViews();
        } else {
            Log.e(TAG, "new view failed type:" + type);
        }

        return ret;
    }

    public int getViewVersion(String type) {
        CodeReader cr = mUiCodeLoader.getCode(type);
        if (cr != null) {
            return cr.getPatchVersion();
        }
        return 0;
    }

    public ViewBase newView(String type) {
        return newView(type, null);
    }

    public ViewBase newView(String type, SparseArray<ViewBase> uuidContainers) {
        ViewBase ret = null;

        if (null != mLoader) {
            CodeReader cr = mUiCodeLoader.getCode(type);
            if (null != cr) {
                mComArr.clear();
                ViewBase curView = null;

                int tag = cr.readByte();
                int state = STATE_continue;
                ViewCache viewCache = new ViewCache();
                while (true) {
                    switch (tag) {
                        case Common.CODE_START_TAG:
                            short comID = cr.readShort();
                            ViewBase view = createView(mAppContext, comID, viewCache);
                            if (null != view) {
                                Layout.Params p;
                                if (null != curView) {
                                    p = ((Layout)curView).generateParams();
                                    mComArr.push(curView);
                                } else {
                                    p = new Layout.Params();
                                }
                                view.setComLayoutParams(p);
                                curView = view;

                                // int
                                byte attrCount = cr.readByte();
                                while (attrCount > 0) {
                                    int key = cr.readInt();
                                    int value = cr.readInt();
                                    view.setValue(key, value);
                                    --attrCount;
                                }

                                // int RP
                                attrCount = cr.readByte();
                                while (attrCount > 0) {
                                    int key = cr.readInt();
                                    int value = cr.readInt();
                                    view.setRPValue(key, value);
                                    --attrCount;
                                }

                                // float
                                attrCount = cr.readByte();
                                while (attrCount > 0) {
                                    int key = cr.readInt();
                                    float value = Float.intBitsToFloat(cr.readInt());
                                    view.setValue(key, value);
                                    --attrCount;
                                }

                                // float RP
                                attrCount = cr.readByte();
                                while (attrCount > 0) {
                                    int key = cr.readInt();
                                    float value = Float.intBitsToFloat(cr.readInt());
                                    view.setRPValue(key, value);
                                    --attrCount;
                                }

                                // string code
                                attrCount = cr.readByte();
                                while (attrCount > 0) {
                                    int key = cr.readInt();
                                    int value = cr.readInt();
                                    view.setStrValue(key, value);
                                    --attrCount;
                                }

                                // expr code
                                attrCount = cr.readByte();
                                while (attrCount > 0) {
                                    int key = cr.readInt();
                                    int value = cr.readInt();
                                    view.setValue(key, mExprCodeLoader.get(value));
                                    --attrCount;
                                }

                                // user var
                                attrCount = cr.readByte();
                                while (attrCount > 0) {
                                    int varType = cr.readByte();
                                    int nameId = cr.readInt();
                                    int value = cr.readInt();
                                    view.addUserVar(varType, nameId, value);
                                    --attrCount;
                                }

                                int uuid = view.getUuid();
                                if (uuid > 0 && null != uuidContainers) {
                                    uuidContainers.put(uuid, view);
                                }
                                List<Item> pendingItems = viewCache.getCacheItem(view);
                                if (pendingItems == null || pendingItems.isEmpty()) {
                                    view.onParseValueFinished();
                                }
                            } else {
                                state = STATE_failed;
                                Log.e(TAG, "can not find view id:" + comID);
                            }
                            break;

                        case Common.CODE_END_TAG:
                            if (mComArr.size() > 0) {
                                ViewBase c = mComArr.pop();
                                if (c instanceof Layout) {
                                    ((Layout) c).addView(curView);
                                } else {
                                    state = STATE_failed;
                                    Log.e(TAG, "com can not contain subcomponent");
                                }
                                curView = c;
                            } else {
                                // can break;
                                state = STATE_successful;
                            }
                            break;

                        default:
                            Log.e(TAG, "invalidate tag type:" + tag);
                            state = STATE_failed;
                            break;
                    }

                    if (STATE_continue != state) {
                        break;
                    } else {
                        tag = cr.readByte();
                    }
                }

                if (STATE_successful == state) {
                    ret = curView;
                    cr.seek(Common.TAG.length() + 4);
                    int version = cr.readShort();
                    ret.setVersion(version );
                }
            } else {
                Log.e(TAG, "can not find component type:" + type);
            }
        } else {
            Log.e(TAG, "loader is null");
        }

        return ret;
    }

    private ViewBase createView(VafContext context, int comID, ViewCache viewCache) {
        ViewBase.IBuilder builder = mBuilders.get(comID);
        if (null != builder) {
            return builder.build(context, viewCache);
        }
        return null;
    }
}
