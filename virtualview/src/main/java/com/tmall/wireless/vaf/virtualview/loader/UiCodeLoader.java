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

package com.tmall.wireless.vaf.virtualview.loader;

import java.nio.charset.Charset;

import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.libra.virtualview.common.Common;

/**
 * Created by gujicheng on 16/11/8.
 */

public class UiCodeLoader {
    private final static String TAG = "UiCodeLoader_TMTEST";

    private ArrayMap<String, Integer>[] mUiTab = new ArrayMap[Common.MAX_TAB_SIZE];
    private ArrayMap<String, CodeReader>[] mCodeReaders = new ArrayMap[Common.MAX_TAB_SIZE];

    public void destroy() {
        mUiTab = null;
        mCodeReaders = null;
    }

    public CodeReader getCode(String type) {
        CodeReader ret = null;

        for (int i = 0, size = mCodeReaders.length; i < size; i++) {
            int tabIndex = i;
            ArrayMap<String, CodeReader> viewCodes = mCodeReaders[tabIndex];
            ArrayMap<String, Integer> codes = mUiTab[tabIndex];
            if (viewCodes != null && codes != null && viewCodes.containsKey(type) && codes.containsKey(type)) {
                if (null != codes) {
                    ret = viewCodes.get(type);
                    ret.seek(codes.get(type));
                } else {
                    Log.e(TAG, "getCode type invalide type:" + type + "  total size:");
                }
                break;
            }
        }
        return ret;
    }

    public boolean loadFromBuffer(CodeReader reader, int pageId) {
        boolean ret = true;

        int tabIndex = pageId;
        if (tabIndex < Common.MAX_TAB_SIZE) {
            ArrayMap<String, CodeReader> typeToCodeReader = mCodeReaders[tabIndex];
            if (null == typeToCodeReader) {
                typeToCodeReader = new ArrayMap<>();
                mCodeReaders[tabIndex] = typeToCodeReader;
            }

            ArrayMap<String, Integer> typeToPos = mUiTab[tabIndex];
            if (null == typeToPos) {
                typeToPos = new ArrayMap<>();
                mUiTab[tabIndex] = typeToPos;
            }

            int count = reader.readInt();
            Log.w(TAG, "load view count: " + count);
            for(int i = 0; i < count; ++i) {
                short nameSize = reader.readShort();
                String name = new String(reader.getCode(), reader.getPos(), nameSize, Charset.forName("UTF-8"));
                Log.w(TAG, "load view name " + name);
                typeToCodeReader.put(name, reader);
                reader.seekBy(nameSize);

                short uiCodeSize = reader.readShort();
                typeToPos.put(name, reader.getPos());
                if (!reader.seekBy(uiCodeSize) ) {
                    ret = false;
                    Log.e(TAG, "seekBy error:" + uiCodeSize + " i:" + i);
                    break;
                }
            }
        } else {
            ret = false;
        }

        return ret;
    }
}
