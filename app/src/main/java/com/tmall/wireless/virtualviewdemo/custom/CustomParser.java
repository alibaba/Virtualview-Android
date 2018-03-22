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

import android.text.TextUtils;
import android.util.Log;

import com.libra.virtualview.common.Common;
import com.libra.virtualview.compiler.parser.Parser;
import com.libra.virtualview.compiler.parser.ViewBaseParser;

/**
 * Created by gujicheng on 17/3/30.
 */

public class CustomParser extends ViewBaseParser {
    private final static String TAG = "CustomParser_TMTEST";

    private int mPropId;

    public static class Builder implements IBuilder {
        @Override
        public Parser build(String name) {
            if (TextUtils.equals(name, "Custom")) {
                return new CustomParser();
            }
            return null;
        }
    }

    @Override
    public void init() {
        super.init();

        mPropId = mStringSupport.getStringId("prop", true);
    }

    @Override
    public int getId() {
        return Common.USER_VIEW_ID_START + 1;
    }

    @Override
    public int convertAttribute(int key, AttrItem value) {
        int ret = super.convertAttribute(key, value);

        if (ViewBaseParser.CONVERT_RESULT_FAILED == ret) {
            ret = ViewBaseParser.CONVERT_RESULT_OK;

            if (key == mPropId) {
                if (null != value && !TextUtils.isEmpty(value.mStrValue)) {
                    // int
                    if (value.mStrValue.equals("propValue1")) {
                        value.setIntValue(1);
                    } else if (value.mStrValue.equals("propValue2")) {
                        value.setIntValue(0);
                    } else {
                        ret = CONVERT_RESULT_ERROR;
                    }
                } else {
                    Log.e(TAG, "parseInteger value invalidate:" + value);
                    ret = CONVERT_RESULT_ERROR;
                }
            } else {
                ret = CONVERT_RESULT_FAILED;
            }
        }

        return ret;
    }
}
