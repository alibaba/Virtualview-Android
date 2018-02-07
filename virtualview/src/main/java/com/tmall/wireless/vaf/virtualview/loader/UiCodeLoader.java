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

    private ArrayMap<String, Integer> mTypeToPos = new ArrayMap<>();
    private ArrayMap<String, CodeReader> mTypeToCodeReader = new ArrayMap<>();

    public void clear() {
        mTypeToPos.clear();
        mTypeToCodeReader.clear();
    }

    public void destroy() {
    }

    public CodeReader getCode(String type) {
        CodeReader ret = null;

        if (mTypeToCodeReader.containsKey(type) && mTypeToPos.containsKey(type)) {
            ret = mTypeToCodeReader.get(type);
            ret.seek(mTypeToPos.get(type));
        } else {
            Log.e(TAG, "getCode type invalide type:" + type + mTypeToCodeReader.containsKey(type) + " " + mTypeToPos
                .containsKey(type));
        }
        return ret;
    }

    public boolean loadFromBuffer(CodeReader reader, int pageId, int patchVersion) {
        boolean ret = true;

        int count = reader.readInt();
        //count should be 1
        Log.w(TAG, "load view count: " + count);
        short nameSize = reader.readShort();
        String name = new String(reader.getCode(), reader.getPos(), nameSize, Charset.forName("UTF-8"));
        CodeReader oldCodeReader = mTypeToCodeReader.get(name);
        if (oldCodeReader != null) {
            int oldPatchVersion = oldCodeReader.getPatchVersion();
            if (patchVersion <= oldPatchVersion) {
                //avoid loading code repeat
                Log.w(TAG, "load view name " + name + " should not override from " + patchVersion + " to "
                    + patchVersion);
                ret = false;
                return ret;
            }
        }
        ret = loadFromBufferInternally(reader, nameSize, name);
        return ret;
    }

    public boolean forceLoadFromBuffer(CodeReader reader, int pageId, int patchVersion) {
        boolean ret = true;

        int count = reader.readInt();
        //count should be 1
        Log.w(TAG, "load view count: " + count);
        short nameSize = reader.readShort();
        String name = new String(reader.getCode(), reader.getPos(), nameSize, Charset.forName("UTF-8"));
        ret = loadFromBufferInternally(reader, nameSize, name);
        return ret;
    }

    private boolean loadFromBufferInternally(CodeReader reader, short nameSize, String name) {
        boolean ret = true;
        Log.w(TAG, "load view name " + name);
        mTypeToCodeReader.put(name, reader);
        reader.seekBy(nameSize);

        short uiCodeSize = reader.readShort();
        mTypeToPos.put(name, reader.getPos());
        if (!reader.seekBy(uiCodeSize) ) {
            ret = false;
            Log.e(TAG, "seekBy error:" + uiCodeSize);
        }

        return ret;
    }

}
