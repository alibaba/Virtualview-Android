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

package com.tmall.wireless.vaf.virtualview.Helper;

import android.text.TextUtils;
import com.libra.virtualview.common.Common;
import com.tmall.wireless.vaf.virtualview.loader.CodeReader;

/**
 * Created by longerian on 2017/9/6.
 *
 * @author longerian
 * @date 2017/09/06
 */

public class VerificationUtil {

    private static CodeReader codeReader = new CodeReader();

    public static boolean checkFormat(byte[] data) {
        if (data == null) {
            return false;
        }
        int tagLen = Common.TAG.length();
        if (data.length < tagLen) {
            return false;
        }
        String tag = new String(data, 0, tagLen);
        return TextUtils.equals(Common.TAG, tag);
    }

    public static boolean checkMajorMinorVersion(byte[] data) {
        codeReader.release();
        codeReader.setCode(data);
        codeReader.seekBy(Common.TAG.length());
        int majorVersion = codeReader.readShort();
        int minorVersion = codeReader.readShort();
        return majorVersion == Common.MAJOR_VERSION && minorVersion == Common.MINOR_VERSION;
    }

    public static boolean checkPatchVersion(byte[] data, short target) {
        codeReader.release();
        codeReader.setCode(data);
        codeReader.seekBy(Common.TAG.length() + 4);
        int version = codeReader.readShort();
        return version == target;
    }

    public static boolean checkContentLength(byte[] data) {
        if (data == null) {
            return false;
        }
        return data.length >= 58;
    }

}
