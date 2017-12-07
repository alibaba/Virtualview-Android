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

package com.tmall.wireless.virtualviewdemo.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by longerian on 17/4/9.
 *
 * @author longerian
 * @date 2017/04/09
 */

public class TMReminderTagsView extends View implements IViewInterface {

    private String[] tags;

    private int[] eachTagWidth;

    private Paint bgPaint;

    private TextPaint textPaint;

    private int tagsGap;

    private int tagsWidth;

    private int tagsHeight;

    private int hPadding;

    private Rect tagRect;

    private Paint.FontMetrics textFontMetrics;

    public TMReminderTagsView(Context context) {
        this(context, null);
    }

    public TMReminderTagsView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TMReminderTagsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (mDensity == -1) {
            initWith(context);
        }
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.rgb(255, 59, 68));
        textPaint.setTextSize(dp2px(10));

        bgPaint = new Paint();
        bgPaint.setColor(Color.rgb(250, 211, 213));

        tagsGap = dp2px(7);

        hPadding = dp2px(3);

        tagRect = new Rect();

        textFontMetrics = textPaint.getFontMetrics();
    }

    public void setTags(JSONArray tags) {
        if (tags != null) {
            int length = tags.length();
            String[] newTags = new String[length];
            for (int i = 0; i < length; i++) {
                newTags[i] = tags.optString(i);
            }
            setTags(newTags);
        }

    }

    public void setTags(com.alibaba.fastjson.JSONArray tags) {
        if (tags != null) {
            int length = tags.size();
            String[] newTags = new String[length];
            for (int i = 0; i < length; i++) {
                newTags[i] = tags.getString(i);
            }
            setTags(newTags);
        }
    }

    public void setTags(String[] tags) {
        if (tags != null && this.tags != tags) {
            this.tags = tags;
            tagsWidth = 0;
            tagsHeight = (int) ((textFontMetrics.descent - textFontMetrics.ascent) + 0.5f);
            this.eachTagWidth = new int[tags.length];
            for (int i = 0; i < tags.length; i++) {
                int tagWdith = (int) (Layout.getDesiredWidth(tags[i] != null ? tags[i] : "", textPaint) + 0.5f) + 2 * hPadding;
                eachTagWidth[i] = tagWdith;
                tagsWidth += tagWdith;
                if (i < tags.length - 1) {
                    tagsWidth += tagsGap;
                }
            }
            requestLayout();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (int i = 0; i < tags.length; i++) {
            tagRect.left = left;
            tagRect.top = top;
            tagRect.right = left + eachTagWidth[i];
            tagRect.bottom = canvas.getHeight() - getPaddingBottom();
            canvas.drawRect(tagRect, bgPaint);

            canvas.drawText(tags[i], left + hPadding,
                canvas.getHeight() - getPaddingBottom() - textFontMetrics.descent,
                textPaint);
            left += tagsGap + eachTagWidth[i];
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(tagsWidth + getPaddingRight() + getPaddingLeft(),
            tagsHeight + getPaddingTop() + getPaddingBottom());
    }

    @Override
    public void onBind(Object jsonObject) {
        if (jsonObject instanceof JSONArray) {
            setTags((JSONArray)jsonObject);
        } else if (jsonObject instanceof String) {
            try {
                setTags(new JSONArray((String) jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUnbind() {

    }

    private static float mDensity = -1f;

    public static void initWith(@NonNull final Context context) {
        final Resources resources = context.getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        mDensity = dm.density;
    }

    public static int dp2px(double dpValue) {
        float scaleRatio = mDensity;
        final float scale = scaleRatio < 0 ? 1.0f : scaleRatio;

        int finalValue;
        if (dpValue >= 0) {
            finalValue = (int) (dpValue * scale + 0.5f);
        } else {
            finalValue = -((int) (-dpValue * scale + 0.5f));
        }
        return finalValue;
    }
}
