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

package com.tmall.wireless.virtualviewdemo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.framework.ViewManager;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader.IImageLoaderAdapter;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader.Listener;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.Layout;
import com.tmall.wireless.vaf.virtualview.event.EventManager;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;
import com.tmall.wireless.virtualviewdemo.custom.ClickProcessorImpl;
import com.tmall.wireless.virtualviewdemo.custom.ExposureProcessorImpl;
import org.json.JSONObject;

/**
 * Created by longerian on 2018/3/2.
 *
 * @author longerian
 * @date 2018/03/02
 */

public class PreviewActivity extends Activity {

    private LinearLayout mLinearLayout;

    private VafContext mVafContext;

    private ViewManager mViewManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_component);
        mLinearLayout = (LinearLayout)findViewById(R.id.container);
        initForPreview();
        handlePreviewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handlePreviewIntent(intent);
    }

    private void handlePreviewIntent(Intent intent) {
        String name = intent.getStringExtra("name");
        String path = intent.getStringExtra("path");
        byte[] data = getByte(path);
        if (data == null) {
            Toast.makeText(this, "读取模板数据失败", Toast.LENGTH_LONG).show();
            return;
        }
        mViewManager.loadBinBufferSync(data, true);
        mLinearLayout.removeAllViews();
        View container = ((VirtualViewApplication)getApplication()).getVafContext().getContainerService().getContainer(
            name, true);
        IContainer iContainer = (IContainer)container;
        Layout.Params p = iContainer.getVirtualView().getComLayoutParams();
        LinearLayout.LayoutParams marginLayoutParams = new LinearLayout.LayoutParams(p.mLayoutWidth, p.mLayoutHeight);
        marginLayoutParams.leftMargin = p.mLayoutMarginLeft;
        marginLayoutParams.topMargin = p.mLayoutMarginTop;
        marginLayoutParams.rightMargin = p.mLayoutMarginRight;
        marginLayoutParams.bottomMargin = p.mLayoutMarginBottom;
        mLinearLayout.addView(container, marginLayoutParams);
        iContainer.getVirtualView().setVData(new JSONObject());
    }

    private void initForPreview() {
        if (mVafContext == null) {
            mVafContext = new VafContext(this);
            mVafContext.setImageLoaderAdapter(new IImageLoaderAdapter() {

                private List<ImageTarget> cache = new ArrayList<ImageTarget>();

                @Override
                public void bindImage(String uri, final ImageBase imageBase, int reqWidth, int reqHeight) {
                    RequestCreator requestCreator = Picasso.with(PreviewActivity.this).load(uri);
                    Log.d("PreviewActivity", "bindImage request width height " + reqHeight + " " + reqWidth);
                    if (reqHeight > 0 || reqWidth > 0) {
                        requestCreator.resize(reqWidth, reqHeight);
                    }
                    ImageTarget imageTarget = new ImageTarget(imageBase);
                    cache.add(imageTarget);
                    requestCreator.into(imageTarget);
                }

                @Override
                public void getBitmap(String uri, int reqWidth, int reqHeight, final Listener lis) {
                    RequestCreator requestCreator = Picasso.with(PreviewActivity.this).load(uri);
                    Log.d("PreviewActivity", "getBitmap request width height " + reqHeight + " " + reqWidth);
                    if (reqHeight > 0 || reqWidth > 0) {
                        requestCreator.resize(reqWidth, reqHeight);
                    }
                    ImageTarget imageTarget = new ImageTarget(lis);
                    cache.add(imageTarget);
                    requestCreator.into(imageTarget);
                }
            });
            mViewManager = mVafContext.getViewManager();
            mViewManager.init(this.getApplicationContext());
            mVafContext.getEventManager().register(EventManager.TYPE_Click, new ClickProcessorImpl());
            mVafContext.getEventManager().register(EventManager.TYPE_Exposure, new ExposureProcessorImpl());
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ImageTarget implements Target {

        ImageBase mImageBase;

        Listener mListener;

        public ImageTarget(ImageBase imageBase) {
            mImageBase = imageBase;
        }

        public ImageTarget(Listener listener) {
            mListener = listener;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
            mImageBase.setBitmap(bitmap, true);
            if (mListener != null) {
                mListener.onImageLoadSuccess(bitmap);
            }
            Log.d("PreviewActivity", "onBitmapLoaded " + from);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            if (mListener != null) {
                mListener.onImageLoadFailed();
            }
            Log.d("PreviewActivity", "onBitmapFailed ");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("PreviewActivity", "onPrepareLoad ");
        }
    }

    private byte[] getByte(String path) {
        try {
            InputStream inputStream = new FileInputStream(path);
            int length = inputStream.available();
            byte[] buf = new byte[length];
            inputStream.read(buf);
            inputStream.close();
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
