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

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.libra.virtualview.common.BizCommon;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.framework.ViewManager;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader.IImageLoaderAdapter;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader.Listener;
import com.tmall.wireless.vaf.virtualview.event.EventManager;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;
import com.tmall.wireless.virtualviewdemo.bytes.CLICKSCRIPT;
import com.tmall.wireless.virtualviewdemo.bytes.FRAMELAYOUT;
import com.tmall.wireless.virtualviewdemo.bytes.GRID;
import com.tmall.wireless.virtualviewdemo.bytes.GRIDITEM;
import com.tmall.wireless.virtualviewdemo.bytes.GRIDLAYOUT;
import com.tmall.wireless.virtualviewdemo.bytes.NFRAMELAYOUT;
import com.tmall.wireless.virtualviewdemo.bytes.NGRIDLAYOUT;
import com.tmall.wireless.virtualviewdemo.bytes.NIMAGE;
import com.tmall.wireless.virtualviewdemo.bytes.NLINE;
import com.tmall.wireless.virtualviewdemo.bytes.NRATIOLAYOUT;
import com.tmall.wireless.virtualviewdemo.bytes.NTEXT;
import com.tmall.wireless.virtualviewdemo.bytes.NVH2LAYOUT;
import com.tmall.wireless.virtualviewdemo.bytes.NVHLAYOUT;
import com.tmall.wireless.virtualviewdemo.bytes.PAGE;
import com.tmall.wireless.virtualviewdemo.bytes.PAGEITEM;
import com.tmall.wireless.virtualviewdemo.bytes.PAGESCROLLSCRIPT;
import com.tmall.wireless.virtualviewdemo.bytes.PICASSO;
import com.tmall.wireless.virtualviewdemo.bytes.PROGRESS;
import com.tmall.wireless.virtualviewdemo.bytes.RATIOLAYOUT;
import com.tmall.wireless.virtualviewdemo.bytes.SCROLLERH;
import com.tmall.wireless.virtualviewdemo.bytes.SCROLLERVL;
import com.tmall.wireless.virtualviewdemo.bytes.SCROLLERVS;
import com.tmall.wireless.virtualviewdemo.bytes.SLIDER;
import com.tmall.wireless.virtualviewdemo.bytes.SLIDERITEM;
import com.tmall.wireless.virtualviewdemo.bytes.TMALLCOMPONENT1;
import com.tmall.wireless.virtualviewdemo.bytes.TMALLCOMPONENT2;
import com.tmall.wireless.virtualviewdemo.bytes.TMALLCOMPONENT3;
import com.tmall.wireless.virtualviewdemo.bytes.TMALLCOMPONENT4;
import com.tmall.wireless.virtualviewdemo.bytes.TMALLCOMPONENT5;
import com.tmall.wireless.virtualviewdemo.bytes.TMALLCOMPONENT6;
import com.tmall.wireless.virtualviewdemo.bytes.TMALLCOMPONENT7;
import com.tmall.wireless.virtualviewdemo.bytes.TMALLCOMPONENT8;
import com.tmall.wireless.virtualviewdemo.bytes.TOTALCONTAINER;
import com.tmall.wireless.virtualviewdemo.bytes.VGRAPH;
import com.tmall.wireless.virtualviewdemo.bytes.VH;
import com.tmall.wireless.virtualviewdemo.bytes.VH2LAYOUT;
import com.tmall.wireless.virtualviewdemo.bytes.VHLAYOUT;
import com.tmall.wireless.virtualviewdemo.bytes.VIMAGE;
import com.tmall.wireless.virtualviewdemo.bytes.VLINE;
import com.tmall.wireless.virtualviewdemo.bytes.VTEXT;
import com.tmall.wireless.virtualviewdemo.custom.ClickProcessorImpl;
import com.tmall.wireless.virtualviewdemo.custom.ExposureProcessorImpl;
import com.tmall.wireless.virtualviewdemo.custom.PicassoImage;
import com.tmall.wireless.virtualviewdemo.custom.TMReminderTagsView;
import com.tmall.wireless.virtualviewdemo.custom.TotalContainer;

/**
 * Created by longerian on 2018/1/6.
 *
 * @author longerian
 * @date 2018/01/06
 */

public class VirtualViewApplication extends Application {

    private VafContext sVafContext;

    private ViewManager sViewManager;

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
            Log.d("VirtualViewApplication", "onBitmapLoaded " + from);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            if (mListener != null) {
                mListener.onImageLoadFailed();
            }
            Log.d("VirtualViewApplication", "onBitmapFailed ");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("VirtualViewApplication", "onPrepareLoad ");
        }
    }

    public VafContext getVafContext() {
        return sVafContext;
    }

    public ViewManager getViewManager() {
        return sViewManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (sVafContext == null) {
            Picasso.setSingletonInstance(new Picasso.Builder(this).loggingEnabled(true).build());
            sVafContext = new VafContext(this.getApplicationContext());
            sVafContext.setImageLoaderAdapter(new IImageLoaderAdapter() {

                private List<ImageTarget> cache = new ArrayList<ImageTarget>();

                @Override
                public void bindImage(String uri, final ImageBase imageBase, int reqWidth, int reqHeight) {
                    RequestCreator requestCreator = Picasso.with(VirtualViewApplication.this).load(uri);
                    Log.d("VirtualViewApplication", "bindImage request width height " + reqHeight + " " + reqWidth);
                    if (reqHeight > 0 || reqWidth > 0) {
                        requestCreator.resize(reqWidth, reqHeight);
                    }
                    ImageTarget imageTarget = new ImageTarget(imageBase);
                    cache.add(imageTarget);
                    requestCreator.into(imageTarget);
                }

                @Override
                public void getBitmap(String uri, int reqWidth, int reqHeight, final Listener lis) {
                    RequestCreator requestCreator = Picasso.with(VirtualViewApplication.this).load(uri);
                    Log.d("VirtualViewApplication", "getBitmap request width height " + reqHeight + " " + reqWidth);
                    if (reqHeight > 0 || reqWidth > 0) {
                        requestCreator.resize(reqWidth, reqHeight);
                    }
                    ImageTarget imageTarget = new ImageTarget(lis);
                    cache.add(imageTarget);
                    requestCreator.into(imageTarget);
                }
            });
            sViewManager = sVafContext.getViewManager();
            sViewManager.init(this.getApplicationContext());
            sViewManager.loadBinBufferSync(NTEXT.BIN);
            sViewManager.loadBinBufferSync(VTEXT.BIN);
            sViewManager.loadBinBufferSync(NIMAGE.BIN);
            sViewManager.loadBinBufferSync(VIMAGE.BIN);
            sViewManager.loadBinBufferSync(VLINE.BIN);
            sViewManager.loadBinBufferSync(NLINE.BIN);
            sViewManager.loadBinBufferSync(PROGRESS.BIN);
            sViewManager.loadBinBufferSync(VGRAPH.BIN);
            sViewManager.loadBinBufferSync(PAGE.BIN);
            sViewManager.loadBinBufferSync(PAGEITEM.BIN);
            sViewManager.loadBinBufferSync(PAGESCROLLSCRIPT.BIN);
            sViewManager.loadBinBufferSync(SLIDER.BIN);
            sViewManager.loadBinBufferSync(SLIDERITEM.BIN);
            sViewManager.loadBinBufferSync(FRAMELAYOUT.BIN);
            sViewManager.loadBinBufferSync(RATIOLAYOUT.BIN);
            sViewManager.loadBinBufferSync(GRIDLAYOUT.BIN);
            sViewManager.loadBinBufferSync(GRID.BIN);
            sViewManager.loadBinBufferSync(GRIDITEM.BIN);
            sViewManager.loadBinBufferSync(VHLAYOUT.BIN);
            sViewManager.loadBinBufferSync(VH2LAYOUT.BIN);
            sViewManager.loadBinBufferSync(VH.BIN);
            sViewManager.loadBinBufferSync(SCROLLERVL.BIN);
            sViewManager.loadBinBufferSync(SCROLLERVS.BIN);
            sViewManager.loadBinBufferSync(SCROLLERH.BIN);
            sViewManager.loadBinBufferSync(TOTALCONTAINER.BIN);
            sViewManager.loadBinBufferSync(NFRAMELAYOUT.BIN);
            sViewManager.loadBinBufferSync(NGRIDLAYOUT.BIN);
            sViewManager.loadBinBufferSync(NRATIOLAYOUT.BIN);
            sViewManager.loadBinBufferSync(NVHLAYOUT.BIN);
            sViewManager.loadBinBufferSync(NVH2LAYOUT.BIN);
            sViewManager.loadBinBufferSync(CLICKSCRIPT.BIN);
            sViewManager.loadBinBufferSync(TMALLCOMPONENT1.BIN);
            sViewManager.loadBinBufferSync(TMALLCOMPONENT2.BIN);
            sViewManager.loadBinBufferSync(TMALLCOMPONENT3.BIN);
            sViewManager.loadBinBufferSync(TMALLCOMPONENT4.BIN);
            sViewManager.loadBinBufferSync(TMALLCOMPONENT5.BIN);
            sViewManager.loadBinBufferSync(TMALLCOMPONENT6.BIN);
            sViewManager.loadBinBufferSync(TMALLCOMPONENT7.BIN);
            sViewManager.loadBinBufferSync(TMALLCOMPONENT8.BIN);
            sViewManager.loadBinBufferSync(PICASSO.BIN);
            sViewManager.getViewFactory().registerBuilder(BizCommon.TM_TOTAL_CONTAINER,new TotalContainer.Builder());
            sViewManager.getViewFactory().registerBuilder(1014,new PicassoImage.Builder());
            sVafContext.getCompactNativeManager().register("TMTags", TMReminderTagsView.class);
            sVafContext.getEventManager().register(EventManager.TYPE_Click, new ClickProcessorImpl());
            sVafContext.getEventManager().register(EventManager.TYPE_Exposure, new ExposureProcessorImpl());
        }

    }
}
