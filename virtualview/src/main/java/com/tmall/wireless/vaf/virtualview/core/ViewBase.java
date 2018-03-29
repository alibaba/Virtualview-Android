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

package com.tmall.wireless.vaf.virtualview.core;

import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Trace;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import com.libra.Utils;
import com.libra.expr.common.ExprCode;
import com.libra.virtualview.common.Common;
import com.libra.virtualview.common.IDataLoaderCommon;
import com.libra.virtualview.common.LayoutCommon;
import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.expr.engine.ExprEngine;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader;
import com.tmall.wireless.vaf.virtualview.Helper.RtlHelper;
import com.tmall.wireless.vaf.virtualview.Helper.VirtualViewUtils;
import com.tmall.wireless.vaf.virtualview.core.ViewCache.Item;
import com.tmall.wireless.vaf.virtualview.event.EventData;
import com.tmall.wireless.vaf.virtualview.event.EventManager;
import com.tmall.wireless.vaf.virtualview.loader.StringLoader;
import com.tmall.wireless.vaf.virtualview.view.nlayout.INativeLayoutImpl;
import org.json.JSONException;
import org.json.JSONObject;

import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_NONE;
import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_X;
import static com.libra.virtualview.common.ViewBaseCommon.AUTO_DIM_DIR_Y;
import static com.libra.virtualview.common.ViewBaseCommon.FLAG_CLICKABLE;
import static com.libra.virtualview.common.ViewBaseCommon.FLAG_DYNAMIC;
import static com.libra.virtualview.common.ViewBaseCommon.FLAG_EVENT;
import static com.libra.virtualview.common.ViewBaseCommon.FLAG_EXPOSURE;
import static com.libra.virtualview.common.ViewBaseCommon.FLAG_LONG_CLICKABLE;
import static com.libra.virtualview.common.ViewBaseCommon.FLAG_TOUCHABLE;
import static com.tmall.wireless.vaf.virtualview.core.ViewCache.Item.FLAG_INVALIDATE;

/**
 * Created by gujicheng on 16/8/15.
 */
public abstract class ViewBase implements IView {
    private final static String TAG = "ViewBase_TMTEST";

    final public static String TYPE = "type";

    protected ViewCache mViewCache;
    protected String mViewType;
    protected int mVersion;
    protected boolean mIsDrawed;

    protected View mDisplayViewContainer;

    protected int mDrawLeft;
    protected int mDrawTop;
    protected Paint mPaint;

    protected int mBackground;
    protected String mBackgroundImagePath;
    protected Bitmap mBackgroundImage = null;
    protected Matrix mMatrixBG = null;

    protected int mBorderWidth = 0;
    protected int mBorderColor = Color.BLACK;
    protected int mBorderRadius = 0;
    protected int mBorderTopLeftRadius = 0;
    protected int mBorderTopRightRadius = 0;
    protected int mBorderBottomLeftRadius = 0;
    protected int mBorderBottomRightRadius = 0;
    protected float mAlpha = Float.NaN;

    protected int mId;

    protected int mVisibility = ViewBaseCommon.VISIBLE;

    protected String mDataUrl;
    protected String mDataParam;
    protected int mDataMode = IDataLoaderCommon.MODE_SET;
    protected Object mJSONData;
    protected String mData;
    protected String mDataTag;
    protected String mAction;
    protected String mActionParam;

    protected int mAutoDimDirection = AUTO_DIM_DIR_NONE;
    protected float mAutoDimX = 1;
    protected float mAutoDimY = 1;

    private int mPadding;
    private boolean isPaddingLeftSet;
    protected int mPaddingLeft;
    private boolean isPaddingRightSet;
    protected int mPaddingRight;
    private boolean isPaddingTopSet;
    protected int mPaddingTop;
    private boolean isPaddingBottomSet;
    protected int mPaddingBottom;

    protected int mGravity;

    protected int mMeasuredWidth;
    protected int mMeasuredHeight;

    protected int mFlag;

    protected int mMinWidth;
    protected int mMinHeight;
    protected int mUuid;

    protected String mClass;
    protected IBean mBean;
    protected VafContext mContext;

    // parent
    protected Layout mParent;

    protected Rect mContentRect;

    // layout params
    protected Layout.Params mParams;

    protected String mName;

    protected Object mTag;

    /**
     * Map used to store views' tags.
     */
    private SimpleArrayMap<String, Object> mKeyedTags;

    protected ExprCode mClickCode;
    protected ExprCode mBeforeLoadDataCode;
    protected ExprCode mAfterLoadDataCode;
    protected ExprCode mSetDataCode;

    static class UserVarItem {
        public UserVarItem(int type, Object value) {
            mType = type;
            mValue = value;
        }

        int mType;
        Object mValue;
    }

    protected SparseArray<UserVarItem> mUserVarItems;

    public ViewBase(VafContext context, ViewCache viewCache) {
        mContext = context;
        mViewCache = viewCache;


        mBackground = Color.TRANSPARENT;

        mGravity = ViewBaseCommon.LEFT | ViewBaseCommon.TOP;

        mFlag = 0;

        mPaddingLeft = 0;
        mPaddingTop = 0;
        mPaddingRight = 0;
        mPaddingBottom = 0;

        mMinWidth = 0;
        mMinHeight = 0;

        mId = -1;
        mName = "";

        mViewType = "";
        mVersion = 0;
        mUuid = 0;
    }

    public void setDisplayViewContainer(View displayViewContainer) {
        mDisplayViewContainer = displayViewContainer;
    }

    public View getDisplayViewContainer() {
        return mDisplayViewContainer;
    }

    public String getAction() {
        return mAction;
    }

    public String getActionParam() {
        return mActionParam;
    }

    public Object getJSONData() {
        return mJSONData;
    }

    public void setBorderWidth(int width) {
        mBorderWidth = width;
        this.refresh();
    }

    public void setBorderColor(int color) {
        mBorderColor = color;
        this.refresh();
    }

    public ViewCache getViewCache() {
        return mViewCache;
    }

    public void setBackground(int color) {
        mBackground = color;
        refresh();
    }

    protected void setBackgroundColor(int color) {
        mBackground = color;
        View view = getNativeView();
        if (null != view && !(view instanceof INativeLayoutImpl)) {
            view.setBackgroundColor(color);
        }
    }

    public boolean isContainer() {
        return false;
    }

    public void addUserVar(int type, int nameId, int initValue) {
        if (null == mUserVarItems) {
            mUserVarItems = new SparseArray<>();
        }

        Object value = null;
        switch (type) {
            case Common.TYPE_INT:
                value = initValue;
                break;

            case Common.TYPE_FLOAT:
                value = Float.intBitsToFloat(initValue);
                break;

            case Common.TYPE_STRING:
                value = mContext.getStringLoader().getString(initValue);
                break;
            default:
                break;
        }

        mUserVarItems.put(nameId, new UserVarItem(type, value));
    }

    public Object getUserVar(int nameId) {
        Object ret = null;

        if (null != mUserVarItems) {
            UserVarItem item = mUserVarItems.get(nameId);
            if (null != item) {
                ret = item.mValue;
            }
        }

        return ret;
    }

    public boolean setUserVar(int nameId, Object value) {
        boolean ret = false;

        if (null != mUserVarItems) {
            UserVarItem item = mUserVarItems.get(nameId);
            if (null != item) {
                switch (item.mType) {
                    case Common.TYPE_INT:
                        if (value instanceof Integer) {
                            item.mValue = value;
                            ret = true;
                        } else {
                            Log.e(TAG, "setUserVar set int failed");
                        }
                        break;

                    case Common.TYPE_FLOAT:
                        if (value instanceof Float) {
                            item.mValue = value;
                            ret = true;
                        } else {
                            Log.e(TAG, "setUserVar set float failed");
                        }
                        break;

                    case Common.TYPE_STRING:
                        if (value instanceof String) {
                            item.mValue = value;
                            ret = true;
                        } else {
                            Log.e(TAG, "setUserVar set string failed");
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return ret;
    }

    public int getBackground() {
        return mBackground;
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public int getBorderRadius() {
        return mBorderRadius;
    }

    public int getBorderTopLeftRadius() {
        return mBorderTopLeftRadius;
    }

    public int getBorderTopRightRadius() {
        return mBorderTopRightRadius;
    }

    public int getBorderBottomLeftRadius() {
        return mBorderBottomLeftRadius;
    }

    public int getBorderBottomRightRadius() {
        return mBorderBottomRightRadius;
    }

    public int getAlign() {
        return mGravity;
    }

    public int getUuid() {
        return mUuid;
    }

    public ViewBase getParent() {
        if (null == this.mParent) {
            return ((IContainer) mViewCache.getHolderView().getParent()).getVirtualView();
        } else {
            return this.mParent;
        }
    }

    public boolean isRoot() {
        return mParent == null;
    }

    public int decideFinalVisibility() {
        if (mParent == null) {
            return mVisibility;
        } else {
            int parentVisibility = mParent.decideFinalVisibility();
            if (parentVisibility == ViewBaseCommon.VISIBLE) {
                return mVisibility;
            } else if (parentVisibility == ViewBaseCommon.INVISIBLE) {
                return ViewBaseCommon.INVISIBLE;
            } else {
                return ViewBaseCommon.GONE;
            }
        }
    }

    public String getViewType() {
        return mViewType;
    }

    public void setViewType(String type) {
        mViewType = type;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int version) {
        this.mVersion = version;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }

    public Object getTag(String key) {
        if (mKeyedTags != null) {
            return mKeyedTags.get(key);
        }
        return null;
    }

    private void setTag(String key, Object tag) {
        if (mKeyedTags == null) {
            mKeyedTags = new SimpleArrayMap<>();
        }

        mKeyedTags.put(key, tag);
    }

    public IBean getBean() {
        return mBean;
    }

    public void destroy() {
        mContext = null;
        mBean = null;
        mUserVarItems = null;
    }

    final public boolean supportDynamic() {
        return (0 != (mFlag & FLAG_DYNAMIC));
    }

    final public boolean supportExposure() {
        return (0 != (mFlag & FLAG_EXPOSURE)) && isVisible();
    }

    final public boolean isClickable() {
        return (0 != (mFlag & FLAG_CLICKABLE));
    }

    final public boolean isLongClickable() {
        return (0 != (mFlag & FLAG_LONG_CLICKABLE));
    }

    final public boolean isTouchable() {
        return (0 != (mFlag & FLAG_TOUCHABLE));
    }

    public void setVisibility(int visibility) {
        if (mVisibility != visibility) {
            mVisibility = visibility;
            if (!changeVisibility()) {
                refresh();
            }
        }
    }

    protected boolean changeVisibility() {
        int finalVisibility = decideFinalVisibility();
        boolean ret = false;
        View nativeView = this.getNativeView();
        if (null != nativeView) {
            switch (finalVisibility) {
                case ViewBaseCommon.INVISIBLE:
                    nativeView.setVisibility(View.INVISIBLE);
                    break;

                case ViewBaseCommon.VISIBLE:
                    nativeView.setVisibility(View.VISIBLE);
                    break;

                case ViewBaseCommon.GONE:
                    nativeView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            ret = true;
        } else if (isContainer()) {
            switch(finalVisibility) {
                case ViewBaseCommon.INVISIBLE:
                    mViewCache.getHolderView().setVisibility(View.INVISIBLE);
                    break;
                case ViewBaseCommon.VISIBLE:
                    mViewCache.getHolderView().setVisibility(View.VISIBLE);
                    break;
                case ViewBaseCommon.GONE:
                    mViewCache.getHolderView().setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            ret = true;
        } else {
//            refresh();
        }

        return ret;
    }

    public boolean isGone() {
        return (mVisibility == ViewBaseCommon.GONE);
    }

    public int getVisibility() {
        return mVisibility;
    }

    public int getHeight() {
        return mMeasuredHeight;
    }

    public int getWidth() {
        return mMeasuredWidth;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public String getDataUrl() {
        return mDataUrl;
    }

    public String getDataParam() {
        return mDataParam;
    }

    public int getDataMode() {
        return mDataMode;
    }

    @Deprecated
    public void loadData() {

    }

    public boolean handleEvent(int x, int y) {
        return handleRoute(mId);
    }

    protected boolean handleRoute(int id) {
        boolean ret = onCheckHandle(id);
        if (!ret && null != mParent) {
            ret = mParent.handleRoute(id);
        }
        return ret;
    }

    protected boolean onCheckHandle(int id) {
        return isClickable() || isLongClickable() || isTouchable();
    }

    protected boolean clickRoute(int id, boolean isLong) {
        boolean ret;
        if (isLong) {
            ret = onLongClick(id);
        } else {
            ret = onClick(id);
        }
        if (!ret && null != mParent) {
            ret = mParent.clickRoute(id, isLong);
        }
        return ret;
    }

    // return top view id
    public boolean click(int x, int y, boolean isLong) {
        return clickRoute(mId, isLong);
    }

    protected boolean onLongClick(int id) {
        if (null != mBean) {
            mBean.click(id, true);
        }

        if (isLongClickable()) {
            boolean ret = mContext.getEventManager().emitEvent(EventManager.TYPE_LongCLick, EventData.obtainData(mContext, this));
            return ret;
        }

        return false;
    }

    protected boolean onClick(int id) {
        boolean ret = false;
        if (null != mBean) {
            mBean.click(id, false);
        }
        if (null != mClickCode) {
            ExprEngine engine = mContext.getExprEngine();
            if (null != engine) {
                engine.getEngineContext().getDataManager().replaceData(
                    (JSONObject)getViewCache().getComponentData());
            }
            if (null != engine && engine.execute(this, mClickCode)) {
            } else {
                Log.e(TAG, "onClick execute failed");
            }
        }

        if (isClickable()) {
           ret = mContext.getEventManager().emitEvent(EventManager.TYPE_Click, EventData.obtainData(mContext, this));
        }

        return ret;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (isTouchable()) {
            boolean ret = mContext.getEventManager().emitEvent(EventManager.TYPE_Touch, EventData.obtainData(mContext, this, v, event));
            return ret;
        }
        return false;
    }

    final public int getDrawLeft() {
        return mDrawLeft;
    }

    final public int getDrawTop() {
        return mDrawTop;
    }

    final public void setHoldView(View v) {
        mViewCache.setHoldView(v);
        if (softwareRender()) {
            v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    final public boolean isVisible() {
        return (mVisibility == ViewBaseCommon.VISIBLE);
    }

    final public int getComBaseline() {
        return 0;
    }

    public ViewBase getChild(int index) {
        return null;
    }

    public void reset() {
        mContentRect = null;
        mIsDrawed = false;
    }

    public void refresh() {
        refresh(mDrawLeft, mDrawTop, mDrawLeft + mMeasuredWidth, mDrawTop + mMeasuredHeight);
    }

    public void refresh(int l, int t, int r, int b) {
        if (mDisplayViewContainer != null) {
            mDisplayViewContainer.invalidate(l, t, r, b);
        }
    }

    public ViewBase findViewBaseById(int id) {
        if (mId == id) {
            return this;
        }
        return null;
    }

    public ViewBase findViewBaseByName(String name) {
        if (TextUtils.equals(mName, name)) {
            return this;
        }
        return null;
    }

    public boolean softwareRender() {
        return (0 != (mFlag & ViewBaseCommon.FLAG_SOFTWARE));
    }

    public boolean shouldDraw() {
        return (mVisibility == ViewBaseCommon.VISIBLE);
    }

    @Deprecated
    final public boolean canHandleEvent() {
        return (0 != (mFlag & FLAG_EVENT));
    }

    public View getNativeView() {
        return null;
    }

    final public int getComPaddingLeft() {
        return mPaddingLeft;
    }

    final public int getComPaddingTop() {
        return mPaddingTop;
    }

    final public int getComPaddingRight() {
        return mPaddingRight;
    }

    final public int getComPaddingBottom() {
        return mPaddingBottom;
    }

    final public void setComLayoutParams(Layout.Params params) {
        mParams = params;
    }

    public Layout.Params getComLayoutParams() {
        return mParams;
    }

    final protected void setComMeasuredDimension(int measuredWidth, int measuredHeight) {
        mMeasuredWidth = measuredWidth;
        mMeasuredHeight = measuredHeight;
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        mDrawLeft = l;
        mDrawTop = t;

        onComLayout(true, l, t, r, b);
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
        onComMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    final public int getComMeasuredWidthWithMargin() {
        return getComMeasuredWidth() + mParams.mLayoutMarginLeft + mParams.mLayoutMarginRight;
    }

    final public int getComMeasuredHeightWithMargin() {
        return getComMeasuredHeight() + mParams.mLayoutMarginTop + mParams.mLayoutMarginBottom;
    }

    public void onBeforeLoadData() {
        if (null != mBeforeLoadDataCode) {
            ExprEngine engine = mContext.getExprEngine();
            if (null != engine && engine.execute(this, mBeforeLoadDataCode)) {
            } else {
                Log.e(TAG, "mBeforeLoadDataCode execute failed");
            }
        }
    }

    public void onAfterLoadData(boolean successful) {
        if (null != mAfterLoadDataCode) {
            ExprEngine engine = mContext.getExprEngine();
            if (null != engine && engine.execute(this, mAfterLoadDataCode)) {
            } else {
                Log.e(TAG, "BeforeLoadDataCode execute failed");
            }
        }
    }

    final public void setVData(Object data) {
        setVData(data, false);
    }

    final public void setVData(Object data, boolean isAppend) {
        if (VERSION.SDK_INT >= 18) {
            Trace.beginSection("ViewBase.setVData");
        }
        mViewCache.setComponentData(data);
        if (data instanceof JSONObject) {
            boolean invalidate = false;
            if (((JSONObject)data).optBoolean(FLAG_INVALIDATE)) {
                invalidate = true;
            }
            List<ViewBase> cacheView = mViewCache.getCacheView();
            if (cacheView != null) {
                for (int i = 0, size = cacheView.size(); i < size; i++) {
                    ViewBase viewBase = cacheView.get(i);
                    List<Item> items = mViewCache.getCacheItem(viewBase);
                    if (null != items) {
                        for(int j = 0, length = items.size(); j < length; j++) {
                            Item item = items.get(j);
                            if (invalidate) {
                                item.invalidate(data.hashCode());
                            }
                            item.bind(data, isAppend);
                        }
                        viewBase.onParseValueFinished();
                        if (!viewBase.isRoot() && viewBase.supportExposure()) {
                            mContext.getEventManager().emitEvent(EventManager.TYPE_Exposure,
                                EventData
                                    .obtainData(mContext, viewBase));
                        }

                    }
                }
            }
            ((JSONObject)data).remove(FLAG_INVALIDATE);
        }
        if (VERSION.SDK_INT >= 18) {
            Trace.endSection();
        }
    }


    protected void setBackgroundImage(Bitmap bmp) {
        mBackgroundImage = bmp;
        refresh();
    }

    public void setBackgroundImage(String path) {
        mBackgroundImagePath = path;
        mBackgroundImage = null;
        if (null == mMatrixBG) {
            mMatrixBG = new Matrix();
        }
        mContext.getImageLoader().getBitmap(path, mMeasuredWidth, mMeasuredHeight, new ImageLoader.Listener() {
            @Override
            public void onImageLoadSuccess(Bitmap bmp) {
                setBackgroundImage(bmp);
            }

            @Override
            public void onImageLoadSuccess(Drawable drawable) {

            }

            @Override
            public void onImageLoadFailed() {
            }
        });
    }

    public void ready() {
        // for load data
        loadData();
    }

    public void setData(Object data) {
        mJSONData = data;

        if (null != mBean) {
            mBean.setData(data);
        }

        if (null != mSetDataCode) {
            ExprEngine engine = mContext.getExprEngine();
            if (null != engine && engine.execute(this, mSetDataCode)) {
            } else {
                Log.e(TAG, "setData execute failed");
            }
        }
    }

    public void appendData(Object data) {
        if (null != mBean) {
            mBean.appendData(data);
        }
    }

    public void comDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mDrawLeft, mDrawTop);
        onComDraw(canvas);
        canvas.restore();
        mIsDrawed = true;
    }

    protected void onComDraw(Canvas canvas) {
        if (getNativeView() == null) {
            if (mBackground != Color.TRANSPARENT) {
                VirtualViewUtils.drawBackground(canvas, mBackground, mMeasuredWidth, mMeasuredHeight, mBorderWidth,
                    mBorderTopLeftRadius, mBorderTopRightRadius, mBorderBottomLeftRadius, mBorderBottomRightRadius);
            } else if (null != mBackgroundImage) {
                //TODO clip canvas if border radius set
                mMatrixBG.setScale(((float) mMeasuredWidth) / mBackgroundImage.getWidth(), ((float) mMeasuredHeight) / mBackgroundImage.getHeight());
                canvas.drawBitmap(mBackgroundImage, mMatrixBG, null);
            }
        }
    }

    public void drawBorder(Canvas canvas) {
        VirtualViewUtils.drawBorder(canvas, mBorderColor, mMeasuredWidth, mMeasuredHeight, mBorderWidth,
            mBorderTopLeftRadius, mBorderTopRightRadius, mBorderBottomLeftRadius, mBorderBottomRightRadius);
    }

    public void onParseValueFinished() {
        resolveRtlPropertiesIfNeeded();

        if (getNativeView() != null) {
            getNativeView().setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        }
        if (!TextUtils.isEmpty(mClass)) {
            parseBean();
        }
    }

    private void parseBean() {
        try {
            Class<? extends IBean> clazz = mContext.getBeanManager().getBeanFor(mClass);
            if (clazz != null) {
                if (mBean == null) {
                    Object obj = clazz.newInstance();
                    //Object obj = Class.forName(mClass, true, this.getClass().getClassLoader()).newInstance();
                    if (obj instanceof IBean) {
                        mBean = (IBean) obj;
                        mBean.init(mContext.getContext(), this);
                    } else {
                        Log.e(TAG, mClass + " is not bean");
                    }
                }
            }
        } catch (InstantiationException e) {
            Log.e(TAG, "error:" + e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "error:" + e);
            e.printStackTrace();
        }
    }

    @Override
    public int getComMeasuredWidth() {
        return mMeasuredWidth;
    }

    @Override
    public int getComMeasuredHeight() {
        return mMeasuredHeight;
    }

    final public boolean setValue(int key, ExprCode value) {
        boolean ret = this.setAttribute(key, value);

        // set layout param value
        if (!ret && null != mParams) {
            ret = mParams.setAttribute(key, value);
        }

        return ret;
    }

    public String getDataTag() {
        return mDataTag;
    }

    final public boolean setRPValue(int key, float value) {
        boolean ret = this.setRPAttribute(key, value);

        // set layout param value
        if (!ret && null != mParams) {
            ret = mParams.setRPAttribute(key, value);
        }

        return ret;
    }

    final public boolean setValue(int key, float value) {
        boolean ret = this.setAttribute(key, value);

        // set layout param value
        if (!ret && null != mParams) {
            ret = mParams.setAttribute(key, value);
        }

        return ret;
    }

    final public boolean setStrValue(int key, int value) {
        boolean ret = this.setStrAttribute(key, value);

        if (!ret && null != mParams) {
            ret = mParams.setStrAttribute(key, value);
        }

        return ret;
    }

    final public boolean setRPValue(int key, int value) {
        // set self value
        boolean ret = this.setRPAttribute(key, value);

        // set layout param value
        if (!ret && null != mParams) {
            ret = mParams.setRPAttribute(key, value);
        }

        return ret;
    }

    final public boolean setValue(int key, int value) {
        // set self value
        boolean ret = this.setAttribute(key, value);

        // set layout param value
        if (!ret && null != mParams) {
            ret = mParams.setAttribute(key, value);
        }

        return ret;
    }

    protected boolean setAttribute(int key, ExprCode value) {
        boolean ret = true;

        switch (key) {
            case StringBase.STR_ID_onClick:
                mClickCode = value;
                break;

            case StringBase.STR_ID_onBeforeDataLoad:
                mBeforeLoadDataCode = value;
                break;

            case StringBase.STR_ID_onAfterDataLoad:
                mAfterLoadDataCode = value;
                break;

            case StringBase.STR_ID_onSetData:
                mSetDataCode = value;
                break;

            default:
                ret = false;
        }

        return ret;
    }

    protected boolean setRPAttribute(int key, float value) {
        boolean ret = true;
        switch (key) {
            case StringBase.STR_ID_padding:
                mPadding = Utils.rp2px(value);
                if (!isPaddingLeftSet) {
                    mPaddingLeft = mPadding;
                }
                if (!isPaddingRightSet) {
                    mPaddingRight = mPadding;
                }
                if (!isPaddingTopSet) {
                    mPaddingTop = mPadding;
                }
                if (!isPaddingBottomSet) {
                    mPaddingBottom = mPadding;
                }
                break;
            case StringBase.STR_ID_paddingLeft:
                mPaddingLeft = Utils.rp2px(value);
                isPaddingLeftSet = true;
                break;

            case StringBase.STR_ID_paddingTop:
                mPaddingTop = Utils.rp2px(value);
                isPaddingTopSet = true;
                break;

            case StringBase.STR_ID_paddingRight:
                mPaddingRight = Utils.rp2px(value);
                isPaddingRightSet = true;
                break;

            case StringBase.STR_ID_paddingBottom:
                mPaddingBottom = Utils.rp2px(value);
                isPaddingBottomSet = true;
                break;

            case StringBase.STR_ID_minWidth:
                mMinWidth = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_minHeight:
                mMinHeight = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_layoutWidth:
                if (value > -1) {
                    this.mParams.mLayoutWidth = Utils.rp2px(value);
                } else {
                    this.mParams.mLayoutWidth = (int)value;
                }
                break;
            case StringBase.STR_ID_layoutMargin:
                this.mParams.mLayoutMargin = Utils.rp2px(value);
                if (!this.mParams.isLayoutMarginLeftSet) {
                    this.mParams.mLayoutMarginLeft = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginRightSet) {
                    this.mParams.mLayoutMarginRight = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginTopSet) {
                    this.mParams.mLayoutMarginTop = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginBottomSet) {
                    this.mParams.mLayoutMarginBottom = this.mParams.mLayoutMargin;
                }
                break;
            case StringBase.STR_ID_layoutMarginLeft:
                this.mParams.mLayoutMarginLeft = Utils.rp2px(value);
                this.mParams.isLayoutMarginLeftSet = true;
                break;
            case StringBase.STR_ID_layoutMarginTop:
                this.mParams.mLayoutMarginTop = Utils.rp2px(value);
                this.mParams.isLayoutMarginTopSet = true;
                break;
            case StringBase.STR_ID_layoutMarginRight:
                this.mParams.mLayoutMarginRight = Utils.rp2px(value);
                this.mParams.isLayoutMarginRightSet = true;
                break;
            case StringBase.STR_ID_layoutMarginBottom:
                this.mParams.mLayoutMarginBottom = Utils.rp2px(value);
                this.mParams.isLayoutMarginBottomSet = true;
                break;

            case StringBase.STR_ID_layoutHeight:
                if (value > -1) {
                    this.mParams.mLayoutHeight = Utils.rp2px(value);
                } else {
                    this.mParams.mLayoutHeight = (int)value;
                }
                break;
            case StringBase.STR_ID_borderWidth:
                mBorderWidth = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_borderRadius:
                mBorderRadius = Utils.rp2px(value);
                if (mBorderTopLeftRadius <= 0) {
                    mBorderTopLeftRadius = mBorderRadius;
                }
                if (mBorderTopRightRadius <= 0) {
                    mBorderTopRightRadius = mBorderRadius;
                }
                if (mBorderBottomLeftRadius <= 0) {
                    mBorderBottomLeftRadius = mBorderRadius;
                }
                if (mBorderBottomRightRadius <= 0) {
                    mBorderBottomRightRadius = mBorderRadius;
                }
                break;
            case StringBase.STR_ID_borderTopLeftRadius:
                mBorderTopLeftRadius = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_borderTopRightRadius:
                mBorderTopRightRadius = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_borderBottomLeftRadius:
                mBorderBottomLeftRadius = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_borderBottomRightRadius:
                mBorderBottomRightRadius = Utils.rp2px(value);
                break;
            default:
                ret = false;
                break;
        }

        return ret;
    }

    protected boolean setAttribute(int key, float value) {
        boolean ret = true;
        switch (key) {
            case StringBase.STR_ID_alpha:
                mAlpha = value;
                break;
            case StringBase.STR_ID_padding:
                mPadding = Utils.dp2px(value);
                if (!isPaddingLeftSet) {
                    mPaddingLeft = mPadding;
                }
                if (!isPaddingRightSet) {
                    mPaddingRight = mPadding;
                }
                if (!isPaddingTopSet) {
                    mPaddingTop = mPadding;
                }
                if (!isPaddingBottomSet) {
                    mPaddingBottom = mPadding;
                }
                break;
            case StringBase.STR_ID_paddingLeft:
                mPaddingLeft = Utils.dp2px(value);
                isPaddingLeftSet = true;
                break;

            case StringBase.STR_ID_paddingTop:
                mPaddingTop = Utils.dp2px(value);
                isPaddingTopSet = true;
                break;

            case StringBase.STR_ID_paddingRight:
                mPaddingRight = Utils.dp2px(value);
                isPaddingRightSet = true;
                break;

            case StringBase.STR_ID_paddingBottom:
                mPaddingBottom = Utils.dp2px(value);
                isPaddingBottomSet = true;
                break;

            case StringBase.STR_ID_minWidth:
                mMinWidth = Utils.dp2px(value);
                break;
            case StringBase.STR_ID_minHeight:
                mMinHeight = Utils.dp2px(value);
                break;

            case StringBase.STR_ID_autoDimX:
                mAutoDimX = value;
                break;

            case StringBase.STR_ID_autoDimY:
                mAutoDimY = value;
                break;
            case StringBase.STR_ID_layoutWidth:
                if (value > -1) {
                    this.mParams.mLayoutWidth = Utils.dp2px(value);
                } else {
                    this.mParams.mLayoutWidth = (int)value;
                }
                break;
            case StringBase.STR_ID_layoutMargin:
                this.mParams.mLayoutMargin = Utils.dp2px(value);
                if (!this.mParams.isLayoutMarginLeftSet) {
                    this.mParams.mLayoutMarginLeft = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginRightSet) {
                    this.mParams.mLayoutMarginRight = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginTopSet) {
                    this.mParams.mLayoutMarginTop = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginBottomSet) {
                    this.mParams.mLayoutMarginBottom = this.mParams.mLayoutMargin;
                }
                break;
            case StringBase.STR_ID_layoutMarginLeft:
                this.mParams.mLayoutMarginLeft = Utils.dp2px(value);
                this.mParams.isLayoutMarginLeftSet = true;
                break;
            case StringBase.STR_ID_layoutMarginTop:
                this.mParams.mLayoutMarginTop = Utils.dp2px(value);
                this.mParams.isLayoutMarginTopSet = true;
                break;
            case StringBase.STR_ID_layoutMarginRight:
                this.mParams.mLayoutMarginRight = Utils.dp2px(value);
                this.mParams.isLayoutMarginRightSet = true;
                break;
            case StringBase.STR_ID_layoutMarginBottom:
                this.mParams.mLayoutMarginBottom = Utils.dp2px(value);
                this.mParams.isLayoutMarginBottomSet = true;
                break;

            case StringBase.STR_ID_layoutHeight:
                if (value > -1) {
                    this.mParams.mLayoutHeight = Utils.dp2px(value);
                } else {
                    this.mParams.mLayoutHeight = (int)value;
                }
                break;
            case StringBase.STR_ID_borderWidth:
                mBorderWidth = Utils.dp2px(value);
                break;
            case StringBase.STR_ID_borderRadius:
                mBorderRadius = Utils.dp2px(value);
                if (mBorderTopLeftRadius <= 0) {
                    mBorderTopLeftRadius = mBorderRadius;
                }
                if (mBorderTopRightRadius <= 0) {
                    mBorderTopRightRadius = mBorderRadius;
                }
                if (mBorderBottomLeftRadius <= 0) {
                    mBorderBottomLeftRadius = mBorderRadius;
                }
                if (mBorderBottomRightRadius <= 0) {
                    mBorderBottomRightRadius = mBorderRadius;
                }
                break;
            case StringBase.STR_ID_borderTopLeftRadius:
                mBorderTopLeftRadius = Utils.dp2px(value);
                break;
            case StringBase.STR_ID_borderTopRightRadius:
                mBorderTopRightRadius = Utils.dp2px(value);
                break;
            case StringBase.STR_ID_borderBottomLeftRadius:
                mBorderBottomLeftRadius = Utils.dp2px(value);
                break;
            case StringBase.STR_ID_borderBottomRightRadius:
                mBorderBottomRightRadius = Utils.dp2px(value);
                break;
            default:
                ret = false;
                break;
        }

        return ret;
    }

    protected boolean setAttribute(int key, String stringValue) {
        boolean ret = true;
        switch (key) {
            case StringBase.STR_ID_alpha:
                mViewCache.put(this, StringBase.STR_ID_alpha, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_layoutWidth:
                mViewCache.put(this, StringBase.STR_ID_layoutWidth, stringValue, Item.TYPE_FLOAT);
                this.mParams.mLayoutWidth = LayoutCommon.WRAP_CONTENT;
                break;
            case StringBase.STR_ID_layoutMargin:
                mViewCache.put(this, StringBase.STR_ID_layoutMargin, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_layoutMarginLeft:
                mViewCache.put(this, StringBase.STR_ID_layoutMarginLeft, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_layoutMarginTop:
                mViewCache.put(this, StringBase.STR_ID_layoutMarginTop, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_layoutMarginRight:
                mViewCache.put(this, StringBase.STR_ID_layoutMarginRight, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_layoutMarginBottom:
                mViewCache.put(this, StringBase.STR_ID_layoutMarginBottom, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_layoutHeight:
                mViewCache.put(this, StringBase.STR_ID_layoutHeight, stringValue, Item.TYPE_FLOAT);
                this.mParams.mLayoutHeight = LayoutCommon.WRAP_CONTENT;
                break;
            case StringBase.STR_ID_padding:
                mViewCache.put(this, StringBase.STR_ID_padding, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_paddingLeft:
                mViewCache.put(this, StringBase.STR_ID_paddingLeft, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_paddingTop:
                mViewCache.put(this, StringBase.STR_ID_paddingTop, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_paddingRight:
                mViewCache.put(this, StringBase.STR_ID_paddingRight, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_paddingBottom:
                mViewCache.put(this, StringBase.STR_ID_paddingBottom, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_data:
                if (Utils.isEL(stringValue)) {
                    mViewCache.put(this, StringBase.STR_ID_data, stringValue, Item.TYPE_STRING);
                } else {
                    mData = stringValue;
                }
                break;

            case StringBase.STR_ID_visibility:
                mViewCache.put(this, StringBase.STR_ID_visibility, stringValue, Item.TYPE_VISIBILITY);
                break;

            case StringBase.STR_ID_dataTag:
                if (Utils.isEL(stringValue)) {
                    mViewCache.put(this, StringBase.STR_ID_dataTag, stringValue, Item.TYPE_OBJECT);
                } else {
                    mDataTag = stringValue;
                }
                break;

            case StringBase.STR_ID_action:
                if (Utils.isEL(stringValue)) {
                    mViewCache.put(this, StringBase.STR_ID_action, stringValue, Item.TYPE_STRING);
                } else {
                    mAction = stringValue;
                }
                break;
            case StringBase.STR_ID_actionParam:
                if (Utils.isEL(stringValue)) {
                    mViewCache.put(this, StringBase.STR_ID_actionParam, stringValue, Item.TYPE_STRING);
                } else {
                    mActionParam = stringValue;
                }
                break;
            case StringBase.STR_ID_class:
                if (Utils.isEL(stringValue)) {
                    mViewCache.put(this, StringBase.STR_ID_class, stringValue, Item.TYPE_STRING);
                } else {
                    mClass = stringValue;
                }
                break;
            case StringBase.STR_ID_name:
                if (Utils.isEL(stringValue)) {
                    mViewCache.put(this, StringBase.STR_ID_name, stringValue, Item.TYPE_STRING);
                } else {
                    mName = stringValue;
                }
                break;
            case StringBase.STR_ID_dataUrl:
                if (Utils.isEL(stringValue)) {
                    mViewCache.put(this, StringBase.STR_ID_dataUrl, stringValue, Item.TYPE_STRING);
                } else {
                    mDataUrl = stringValue;
                }
                break;

            case StringBase.STR_ID_dataParam:
                if (Utils.isEL(stringValue)) {
                    mViewCache.put(this, StringBase.STR_ID_dataParam, stringValue, Item.TYPE_STRING);
                } else {
                    mDataParam = stringValue;
                }
                break;

            case StringBase.STR_ID_background:
                mViewCache.put(this, StringBase.STR_ID_background, stringValue, Item.TYPE_COLOR);
                break;

            case StringBase.STR_ID_gravity:
                mViewCache.put(this, StringBase.STR_ID_gravity, stringValue, Item.TYPE_GRAVITY);
                break;

            case StringBase.STR_ID_backgroundImage:
                if (Utils.isEL(stringValue)) {
                    mViewCache.put(this, StringBase.STR_ID_backgroundImage, stringValue, Item.TYPE_STRING);
                } else {
                    setBackgroundImage(stringValue);
                }
                break;

            case StringBase.STR_ID_autoDimDirection:
                mViewCache.put(this, StringBase.STR_ID_autoDimDirection, stringValue, Item.TYPE_INT);
                break;

            case StringBase.STR_ID_autoDimX:
                mViewCache.put(this, StringBase.STR_ID_autoDimX, stringValue, Item.TYPE_FLOAT);
                break;

            case StringBase.STR_ID_autoDimY:
                mViewCache.put(this, StringBase.STR_ID_autoDimY, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_borderWidth:
                mViewCache.put(this, StringBase.STR_ID_borderWidth, stringValue, Item.TYPE_FLOAT);
                break;

            case StringBase.STR_ID_borderColor:
                mViewCache.put(this, StringBase.STR_ID_borderColor, stringValue, Item.TYPE_COLOR);
                break;
            case StringBase.STR_ID_borderRadius:
                mViewCache.put(this, StringBase.STR_ID_borderRadius, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_borderTopLeftRadius:
                mViewCache.put(this, StringBase.STR_ID_borderTopLeftRadius, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_borderTopRightRadius:
                mViewCache.put(this, StringBase.STR_ID_borderTopRightRadius, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_borderBottomLeftRadius:
                mViewCache.put(this, StringBase.STR_ID_borderBottomLeftRadius, stringValue, Item.TYPE_FLOAT);
                break;
            case StringBase.STR_ID_borderBottomRightRadius:
                mViewCache.put(this, StringBase.STR_ID_borderBottomRightRadius, stringValue, Item.TYPE_FLOAT);
                break;

            case StringBase.STR_ID_tag:
                if (Utils.isEL(stringValue)) {
                    mViewCache.put(this, StringBase.STR_ID_tag, stringValue, Item.TYPE_STRING);
                } else {
                    if (!TextUtils.isEmpty(stringValue)) {
                        try {
                            // if has more data, use Keyed Tag.
                            JSONObject jsonObject = new JSONObject(stringValue);
                            Iterator<String> sIterator = jsonObject.keys();
                            while (sIterator.hasNext()) {
                                // tag key
                                String tagKey = sIterator.next();
                                setTag(tagKey, jsonObject.getString(tagKey));
                            }
                        } catch (JSONException e) {
                            // just a String value, can't convert to a JSONObject, use Tag only
                            mTag = stringValue;
                        }
                    }
                }
                break;

            default:
                ret = false;
        }

        return ret;
    }

    protected boolean setStrAttribute(int key, int value) {
        StringLoader sm = mContext.getStringLoader();
        String stringValue = sm.getString(value);
        boolean ret = setAttribute(key, stringValue);
        return ret;
    }

    protected boolean setAttribute(int key, Object value) {
        boolean ret = false;
        return ret;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    protected boolean setRPAttribute(int key, int value) {
        boolean ret = true;

        switch (key) {
            case StringBase.STR_ID_padding:
                mPadding = Utils.rp2px(value);
                if (!isPaddingLeftSet) {
                    mPaddingLeft = mPadding;
                }
                if (!isPaddingRightSet) {
                    mPaddingRight = mPadding;
                }
                if (!isPaddingTopSet) {
                    mPaddingTop = mPadding;
                }
                if (!isPaddingBottomSet) {
                    mPaddingBottom = mPadding;
                }
                break;
            case StringBase.STR_ID_paddingLeft:
                mPaddingLeft = Utils.rp2px(value);
                isPaddingLeftSet = true;
                break;
            case StringBase.STR_ID_paddingRight:
                mPaddingRight = Utils.rp2px(value);
                isPaddingRightSet = true;
                break;
            case StringBase.STR_ID_paddingTop:
                mPaddingTop = Utils.rp2px(value);
                isPaddingTopSet = true;
                break;
            case StringBase.STR_ID_paddingBottom:
                mPaddingBottom = Utils.rp2px(value);
                isPaddingBottomSet = true;
                break;

            case StringBase.STR_ID_minWidth:
                mMinWidth = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_minHeight:
                mMinHeight = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_layoutWidth:
                if (value > -1) {
                    this.mParams.mLayoutWidth = Utils.rp2px(value);
                } else {
                    this.mParams.mLayoutWidth = value;
                }
                break;
            case StringBase.STR_ID_layoutMargin:
                this.mParams.mLayoutMargin = Utils.rp2px(value);
                if (!this.mParams.isLayoutMarginLeftSet) {
                    this.mParams.mLayoutMarginLeft = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginRightSet) {
                    this.mParams.mLayoutMarginRight = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginTopSet) {
                    this.mParams.mLayoutMarginTop = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginBottomSet) {
                    this.mParams.mLayoutMarginBottom = this.mParams.mLayoutMargin;
                }
                break;
            case StringBase.STR_ID_layoutMarginLeft:
                this.mParams.mLayoutMarginLeft = Utils.rp2px(value);
                this.mParams.isLayoutMarginLeftSet = true;
                break;
            case StringBase.STR_ID_layoutMarginTop:
                this.mParams.mLayoutMarginTop = Utils.rp2px(value);
                this.mParams.isLayoutMarginTopSet = true;
                break;
            case StringBase.STR_ID_layoutMarginRight:
                this.mParams.mLayoutMarginRight = Utils.rp2px(value);
                this.mParams.isLayoutMarginRightSet = true;
                break;
            case StringBase.STR_ID_layoutMarginBottom:
                this.mParams.mLayoutMarginBottom = Utils.rp2px(value);
                this.mParams.isLayoutMarginBottomSet = true;
                break;

            case StringBase.STR_ID_layoutHeight:
                if (value > -1) {
                    this.mParams.mLayoutHeight = Utils.rp2px(value);
                } else {
                    this.mParams.mLayoutHeight = value;
                }
                break;
            case StringBase.STR_ID_borderWidth:
                mBorderWidth = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_borderRadius:
                mBorderRadius = Utils.rp2px(value);
                if (mBorderTopLeftRadius <= 0) {
                    mBorderTopLeftRadius = mBorderRadius;
                }
                if (mBorderTopRightRadius <= 0) {
                    mBorderTopRightRadius = mBorderRadius;
                }
                if (mBorderBottomLeftRadius <= 0) {
                    mBorderBottomLeftRadius = mBorderRadius;
                }
                if (mBorderBottomRightRadius <= 0) {
                    mBorderBottomRightRadius = mBorderRadius;
                }
                break;
            case StringBase.STR_ID_borderTopLeftRadius:
                mBorderTopLeftRadius = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_borderTopRightRadius:
                mBorderTopRightRadius = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_borderBottomLeftRadius:
                mBorderBottomLeftRadius = Utils.rp2px(value);
                break;
            case StringBase.STR_ID_borderBottomRightRadius:
                mBorderBottomRightRadius = Utils.rp2px(value);
                break;
            default:
                ret = false;
        }

        return ret;
    }

    protected boolean setAttribute(int key, int value) {
        boolean ret = true;

        switch (key) {
            case StringBase.STR_ID_padding:
                mPadding = Utils.dp2px(value);
                if (!isPaddingLeftSet) {
                    mPaddingLeft = mPadding;
                }
                if (!isPaddingRightSet) {
                    mPaddingRight = mPadding;
                }
                if (!isPaddingTopSet) {
                    mPaddingTop = mPadding;
                }
                if (!isPaddingBottomSet) {
                    mPaddingBottom = mPadding;
                }
                break;
            case StringBase.STR_ID_paddingLeft:
                mPaddingLeft = Utils.dp2px(value);
                isPaddingLeftSet = true;
                break;
            case StringBase.STR_ID_paddingRight:
                mPaddingRight = Utils.dp2px(value);
                isPaddingRightSet = true;
                break;
            case StringBase.STR_ID_paddingTop:
                mPaddingTop = Utils.dp2px(value);
                isPaddingTopSet = true;
                break;
            case StringBase.STR_ID_paddingBottom:
                mPaddingBottom = Utils.dp2px(value);
                isPaddingBottomSet = true;
                break;

            case StringBase.STR_ID_id:
                mId = value;
                break;

            case StringBase.STR_ID_background:
                setBackgroundColor(value);
                break;

            case StringBase.STR_ID_gravity:
                mGravity = value;
                break;

            case StringBase.STR_ID_flag:
                mFlag = value;
                break;

            case StringBase.STR_ID_minWidth:
                mMinWidth = Utils.dp2px(value);
                break;
            case StringBase.STR_ID_minHeight:
                mMinHeight = Utils.dp2px(value);
                break;

            case StringBase.STR_ID_uuid:
                mUuid = value;
                break;

            case StringBase.STR_ID_autoDimDirection:
                mAutoDimDirection = value;
                break;

            case StringBase.STR_ID_autoDimX:
                mAutoDimX = value;
                break;

            case StringBase.STR_ID_autoDimY:
                mAutoDimY = value;
                break;

            case StringBase.STR_ID_visibility:
                mVisibility = value;
                changeVisibility();
                break;

            case StringBase.STR_ID_dataMode:
                mDataMode = value;
                break;
            case StringBase.STR_ID_layoutWidth:
                if (value > -1) {
                    this.mParams.mLayoutWidth = Utils.dp2px(value);
                } else {
                    this.mParams.mLayoutWidth = value;
                }
                break;
            case StringBase.STR_ID_layoutMargin:
                this.mParams.mLayoutMargin = Utils.dp2px(value);
                if (!this.mParams.isLayoutMarginLeftSet) {
                    this.mParams.mLayoutMarginLeft = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginRightSet) {
                    this.mParams.mLayoutMarginRight = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginTopSet) {
                    this.mParams.mLayoutMarginTop = this.mParams.mLayoutMargin;
                }
                if (!this.mParams.isLayoutMarginBottomSet) {
                    this.mParams.mLayoutMarginBottom = this.mParams.mLayoutMargin;
                }
                break;
            case StringBase.STR_ID_layoutMarginLeft:
                this.mParams.mLayoutMarginLeft = Utils.dp2px(value);
                this.mParams.isLayoutMarginLeftSet = true;
                break;
            case StringBase.STR_ID_layoutMarginTop:
                this.mParams.mLayoutMarginTop = Utils.dp2px(value);
                this.mParams.isLayoutMarginTopSet = true;
                break;
            case StringBase.STR_ID_layoutMarginRight:
                this.mParams.mLayoutMarginRight = Utils.dp2px(value);
                this.mParams.isLayoutMarginRightSet = true;
                break;
            case StringBase.STR_ID_layoutMarginBottom:
                this.mParams.mLayoutMarginBottom = Utils.dp2px(value);
                this.mParams.isLayoutMarginBottomSet = true;
                break;

            case StringBase.STR_ID_layoutHeight:
                if (value > -1) {
                    this.mParams.mLayoutHeight = Utils.dp2px(value);
                } else {
                    this.mParams.mLayoutHeight = value;
                }
                break;
            case StringBase.STR_ID_borderColor:
                mBorderColor = value;
                break;
            case StringBase.STR_ID_borderWidth:
                mBorderWidth = Utils.dp2px(value);
                break;
            case StringBase.STR_ID_borderRadius:
                mBorderRadius = Utils.dp2px(value);
                if (mBorderTopLeftRadius <= 0) {
                    mBorderTopLeftRadius = mBorderRadius;
                }
                if (mBorderTopRightRadius <= 0) {
                    mBorderTopRightRadius = mBorderRadius;
                }
                if (mBorderBottomLeftRadius <= 0) {
                    mBorderBottomLeftRadius = mBorderRadius;
                }
                if (mBorderBottomRightRadius <= 0) {
                    mBorderBottomRightRadius = mBorderRadius;
                }
                break;
            case StringBase.STR_ID_borderTopLeftRadius:
                mBorderTopLeftRadius = Utils.dp2px(value);
                break;
            case StringBase.STR_ID_borderTopRightRadius:
                mBorderTopRightRadius = Utils.dp2px(value);
                break;
            case StringBase.STR_ID_borderBottomLeftRadius:
                mBorderBottomLeftRadius = Utils.dp2px(value);
                break;
            case StringBase.STR_ID_borderBottomRightRadius:
                mBorderBottomRightRadius = Utils.dp2px(value);
                break;
            default:
                ret = false;
        }

        return ret;
    }

    protected void makeContentRect() {
    }

    public interface IBuilder {
        ViewBase build(VafContext context, ViewCache vc);
    }

    protected class VirtualViewImp implements IView {

        protected ViewBase mViewBase;
        protected int mPreWidthMeasureSpec = 0;
        protected int mPreHeightMeasureSpec = 0;
        protected boolean mContentChanged;

        public VirtualViewImp() {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            reset();
        }

        public void setViewBase(ViewBase viewBase) {
            mViewBase = viewBase;
        }

        public void setAntiAlias(boolean aa) {
            mPaint.setAntiAlias(aa);
        }

        public void reset() {
            mPreWidthMeasureSpec = 0;
            mPreHeightMeasureSpec = 0;
            mContentChanged = false;
            mBackgroundImage = null;
            mBackgroundImagePath = null;
        }

        @Override
        public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
            if ((widthMeasureSpec != mPreWidthMeasureSpec) || (heightMeasureSpec != mPreHeightMeasureSpec) || mContentChanged) {
                onComMeasure(widthMeasureSpec, heightMeasureSpec);

                mPreWidthMeasureSpec = widthMeasureSpec;
                mPreHeightMeasureSpec = heightMeasureSpec;
                mContentChanged = false;
            }
        }

        @Override
        public void comLayout(int l, int t, int r, int b) {
        }

        @Override
        public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

            if (null == mContentRect) {
                makeContentRect();
            }

            int mAutoDimDirection = mViewBase.mAutoDimDirection;
            float autoX = mViewBase.mAutoDimX;
            float autoY = mViewBase.mAutoDimY;
            if (mAutoDimDirection > 0) {
                switch (mAutoDimDirection) {
                    case ViewBaseCommon.AUTO_DIM_DIR_X:
                        if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(widthMeasureSpec)) {
                            mMeasuredWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                            mMeasuredHeight = (int)((mMeasuredWidth * autoY) / autoX);
                        }
                        return;

                    case AUTO_DIM_DIR_Y:
                        if (View.MeasureSpec.EXACTLY == View.MeasureSpec.getMode(heightMeasureSpec)) {
                            mMeasuredHeight = View.MeasureSpec.getSize(heightMeasureSpec);
                            mMeasuredWidth = (int)((mMeasuredHeight * autoX) / autoY);
                        }
                        return;
                    default:
                        break;
                }
            }

            if (LayoutCommon.WRAP_CONTENT == mParams.mLayoutWidth) {
                if (null != mContentRect) {
                    mMeasuredWidth = mContentRect.width() + mPaddingLeft + mPaddingRight;
                } else {
                    mMeasuredWidth = mMinWidth;
                }
            } else if (LayoutCommon.MATCH_PARENT == mParams.mLayoutWidth) {
                if (View.MeasureSpec.EXACTLY == widthMode) {
                    mMeasuredWidth = width;
                } else {
                    mMeasuredWidth = 0;
                }
            } else {
                if (View.MeasureSpec.EXACTLY == widthMode) {
                    mMeasuredWidth = width;
                } else {
                    mMeasuredWidth = mParams.mLayoutWidth;
                }
            }

            if (LayoutCommon.WRAP_CONTENT == mParams.mLayoutHeight) {
                if (null != mContentRect) {
                    mMeasuredHeight = mContentRect.height() + mPaddingTop + mPaddingBottom;
                } else {
                    mMeasuredHeight = mMinHeight;
                }
            } else if (LayoutCommon.MATCH_PARENT == mParams.mLayoutHeight) {
                if (View.MeasureSpec.EXACTLY == heightMode) {
                    mMeasuredHeight = height;
                } else {
                    mMeasuredHeight = 0;
                }
            } else {
                if (View.MeasureSpec.EXACTLY == heightMode) {
                    mMeasuredHeight = height;
                } else {
                    mMeasuredHeight = mParams.mLayoutHeight;
                }
            }
        }

        @Override
        public void onComLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public int getComMeasuredWidth() {
            return 0;
        }

        @Override
        public int getComMeasuredHeight() {
            return 0;
        }
    }

    //----- RTL support begin --- //
    // use this attr to control RTL or not.
    private boolean disableRtl;

    /**
     * Use Rtl or not.
     * @return true if in locale use Rtl direction && this layout do not disable Rtl.
     */
    public boolean isRtl() {
        return RtlHelper.isRtl() && !disableRtl;
    }

    /**
     * resolve rtl properties. such as Padding etc.
     * Depends on CSS Box Model: https://www.w3.org/TR/CSS2/box.html
     * Do not convert Margin cause Margin out of the view.
     */
    public void resolveRtlPropertiesIfNeeded() {
        if (isRtl()) {
            // padding
            int tempPadding = mPaddingLeft;
            mPaddingLeft = mPaddingRight;
            mPaddingRight = tempPadding;
        }
    }
    //----- RTL support end --- //
}
