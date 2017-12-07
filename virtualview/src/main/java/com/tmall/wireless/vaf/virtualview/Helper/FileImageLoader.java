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

package com.tmall.wireless.vaf.virtualview.Helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gujicheng on 16/8/1.
 */
public class FileImageLoader {
    private static final String TAG = "FileImageLoader_TMTEST";

    public static Bitmap load(Context context, String path) {
        Bitmap ret = null;
        Log.d(TAG, "load path:" + path);

        InputStream is = getAssertsFile(context, path);
        if (null != is) {
            // from asset
            ret = BitmapFactory.decodeStream(is);
        } else {
            // from
            ret = decodeFile(path);
        }

        return ret;
    }

    private static InputStream getAssertsFile(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        try {
            return assetManager.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Bitmap decodeFile(String path) {
        Bitmap ret = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            if (options.inSampleSize > 1) {
                Log.d(TAG, "decodeFile scale outWidth:"
                        + options.outWidth + "  outHeight:" + options.outHeight
                        + "  inSampleSize:" + options.inSampleSize);
            }

            options.inJustDecodeBounds = false;
            ret = BitmapFactory.decodeFile(path, options);
            if (options.inSampleSize > 1) {
                Log.d(TAG, "decodeFile scale after outWidth :"
                        + options.outWidth + "  outHeight:" + options.outHeight
                        + "  inSampleSize:" + options.inSampleSize);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Log.e(TAG, "decodeFile out of memory :" + path);
        }

        return ret;
    }

}
