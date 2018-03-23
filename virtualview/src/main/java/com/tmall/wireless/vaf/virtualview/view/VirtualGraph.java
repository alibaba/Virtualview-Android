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

package com.tmall.wireless.vaf.virtualview.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.libra.Utils;
import com.libra.virtualview.common.StringBase;
import com.libra.virtualview.common.ViewBaseCommon;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;
import com.tmall.wireless.vaf.virtualview.core.VirtualViewBase;

import static com.libra.virtualview.common.ViewBaseCommon.H_CENTER;
import static com.libra.virtualview.common.VirtualGraphCommon.PAINT_STYLE_Fill;
import static com.libra.virtualview.common.VirtualGraphCommon.PAINT_STYLE_Stroke;
import static com.libra.virtualview.common.VirtualGraphCommon.TYPE_Circle;
import static com.libra.virtualview.common.VirtualGraphCommon.TYPE_Oval;
import static com.libra.virtualview.common.VirtualGraphCommon.TYPE_Rect;

/**
 * Created by gujicheng on 16/11/10.
 */

public class VirtualGraph extends VirtualViewBase {
    private final static String TAG = "VirtualGraph_TMTEST";

    protected RectF mOvalRect;
    protected int mColor;
    protected int mDiameterX;
    protected int mDiameterY;
    protected int mType = TYPE_Circle;

    public VirtualGraph(VafContext context, ViewCache viewCache) {
        super(context, viewCache);
        mImp.setAntiAlias(true);
    }

    public void setType(int type) {
        setType(type, 1);
    }

    public void setType(int type, int isRefresh) {
        mType = type;
        if (isRefresh > 0) {
            refresh();
        }
    }

    public void setDiameterY(int y) {
        setDiameterY(y, 1);
    }

    public void setDiameterY(int y, int isRefresh) {
        mDiameterY = y;
        if (isRefresh > 0) {
            refresh();
        }
    }

    public void setDiameterX(int x) {
        setDiameterX(x, 1);
    }

    public void setDiameterX(int x, int isRefresh) {
        mDiameterX = x;
        if (TYPE_Circle == mDiameterX) {
            mDiameterY = mDiameterX;
        }
        if (isRefresh > 0) {
            refresh();
        }
    }

    public void setColor(int color) {
        setColor(color, 1);
    }

    public void setColor(int color, int isRefresh) {
        mColor = color;
        mPaint.setColor(mColor);
        if (isRefresh > 0) {
            refresh();
        }
    }

    @Override
    protected void makeContentRect() {
        if (mContentRect == null) {
            mContentRect = new Rect(0, 0, mDiameterX, mDiameterY);
        } else {
            mContentRect.set(0, 0, mDiameterX, mDiameterY);
        }
    }

    @Override
    protected void onComDraw(Canvas canvas) {
        super.onComDraw(canvas);

        int l = mPaddingLeft;
        int dX = mDiameterX;
        int dY = mDiameterY;
        if (mDiameterX > 0) {
            if (0 != (mGravity & ViewBaseCommon.RIGHT)) {
                l = mMeasuredWidth - mPaddingRight - mDiameterX;
            } else if (0 != (mGravity & H_CENTER)) {
                l = (mMeasuredWidth - mDiameterX) >> 1;
            }
        } else {
            dX = mMeasuredWidth - mPaddingLeft - mPaddingRight;
        }

        int t = mPaddingTop;
        if (mDiameterY > 0) {
            if (0 != (mGravity & ViewBaseCommon.BOTTOM)) {
                t = mMeasuredHeight - mPaddingBottom - mDiameterY;
            } else if (0 != (mGravity & ViewBaseCommon.V_CENTER)) {
                t = (mMeasuredHeight - mDiameterY) >> 1;
            }
        } else {
            dY = mMeasuredHeight - mPaddingTop - mPaddingBottom;
        }

        switch (mType) {
            case TYPE_Circle:
                int radius = dX >> 1;
                canvas.drawCircle(l + radius, t + radius, radius, mPaint);
                break;

            case TYPE_Rect:
                canvas.drawRect(l, t, l + dX, t + dY, mPaint);
                break;

            case TYPE_Oval:
                if (mOvalRect == null) {
                    mOvalRect = new RectF();
                }
                mOvalRect.set(l, t, l + dX, t + dY);
                canvas.drawOval(mOvalRect, mPaint);
                break;
            default:
                break;
        }
    }

    @Override
    public void onParseValueFinished() {
        super.onParseValueFinished();

        if (TYPE_Circle == mType) {
            mDiameterY = mDiameterX;
        }

        mPaint.setColor(mColor);
    }

    @Override
    protected boolean setAttribute(int key, float value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_paintWidth:
                    mPaint.setStrokeWidth(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_diameterX:
                    mDiameterX = Utils.dp2px(value);
                    break;

                case StringBase.STR_ID_diameterY:
                    mDiameterY = Utils.dp2px(value);
                    break;

                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    @Override
    protected boolean setAttribute(int key, int value) {
        boolean ret = super.setAttribute(key, value);

        if (!ret) {
            ret = true;
            switch (key) {
                case StringBase.STR_ID_paintWidth:
                    mPaint.setStrokeWidth(Utils.dp2px(value));
                    break;

                case StringBase.STR_ID_paintStyle:
                    switch (value) {
                        case PAINT_STYLE_Stroke:
                            mPaint.setStyle(Paint.Style.STROKE);
                            break;

                        case PAINT_STYLE_Fill:
                            mPaint.setStyle(Paint.Style.FILL);
                            break;
                        default:
                            break;
                    }
                    break;

                case StringBase.STR_ID_color:
                    mColor = value;
                    break;

                case StringBase.STR_ID_diameterX:
                    mDiameterX = Utils.dp2px(value);
                    break;

                case StringBase.STR_ID_diameterY:
                    mDiameterY = Utils.dp2px(value);
                    break;

                case StringBase.STR_ID_type:
                    mType = value;
                    break;

                default:
                    ret = false;
                    break;
            }
        }

        return ret;
    }

    public static class Builder implements ViewBase.IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new VirtualGraph(context, viewCache);
        }
    }

}
