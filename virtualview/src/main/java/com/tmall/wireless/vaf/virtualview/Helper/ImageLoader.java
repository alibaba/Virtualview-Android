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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;

public class ImageLoader {

    private static final String TAG = "ImageLoader_TMTEST";

    public interface IImageLoaderAdapter {

        void bindImage(final String uri, final ImageBase imageBase,
            final int reqWidth, final int reqHeight);

        void getBitmap(final String uri,
            final int reqWidth, final int reqHeight, Listener lis);

    }

    public interface Listener {
        void onImageLoadSuccess(Bitmap bmp);
        void onImageLoadSuccess(Drawable drawable);
        void onImageLoadFailed();
    }

    private Context mContext;

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
    }

    public void setImageLoaderAdapter(
        IImageLoaderAdapter imageLoaderAdapter) {
        mImageLoaderAdapter = imageLoaderAdapter;
    }

    private IImageLoaderAdapter mImageLoaderAdapter;

    /**
     * build a new instance of ImageLoader
     * @param context
     * @return a new instance of ImageLoader
     */
    public static ImageLoader build(Context context) {
        return new ImageLoader(context);
    }

    public void getBitmap(final String uri, final int reqWidth, final int reqHeight, Listener listener) {
        if (mImageLoaderAdapter != null) {
            mImageLoaderAdapter.getBitmap(uri, reqWidth, reqHeight, listener);
        }
    }

    public void bindBitmap(final String uri, final ImageBase imageView,
            final int reqWidth, final int reqHeight) {
        if (mImageLoaderAdapter != null) {
            mImageLoaderAdapter.bindImage(uri, imageView, reqWidth, reqHeight);
        }
    }
}
