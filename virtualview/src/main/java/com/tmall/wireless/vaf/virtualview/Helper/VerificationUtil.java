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
