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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageLoader {

    private static final String TAG = "ImageLoader_TMTEST";

    public static final int MESSAGE_POST_RESULT = 1;

    private static final int CPU_COUNT = Runtime.getRuntime()
            .availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT* 2 + 1;
    private static final long KEEP_ALIVE = 10L;

    private static final int QUALITY = 90;

    private static final int TAG_KEY_URI = 0;
//    private static final int TAG_KEY_URI = R.id.imageloader_uri;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final int DISK_CACHE_INDEX = 0;
    private boolean mIsDiskLruCacheCreated = false;

    private static final String PREFIX_HTTP = "http:";
    private static final String PREFIX_HTTPS = "https:";
    private static final String PREFIX_FILE = "file:";
    private static final String PREFIX_RES = "res:";

    private boolean mSetDim = true;

    private DownloadRunnableFactory mDownloadRunnableFactory = new DownloadRunnableFactory();
    private HashSet<String> mDownloadingUrls = new HashSet<>();

    public interface Listener {
        void onImageLoadSuccess(Bitmap bmp);
        void onImageLoadFailed();
    }

    public void enableSetDim(boolean enable) {
        mSetDim = enable;
    }

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), sThreadFactory);

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
//            Log.d(TAG, "handleMessage uri：" + result.uri);
            ViewBase imageView = (ViewBase)result.baseObject;
            if (null != imageView) {
                String uri = (String) imageView.getTag();
                if (uri.equals(result.uri)) {
                    if (imageView instanceof ImageBase) {
                        ((ImageBase)imageView).setBitmap(result.bitmap);
                    }
                } else {
//                Log.w(TAG, "set image bitmap,but url has changed, ignored!");
                }
            } else if (null != result.mListener) {
                result.mListener.onImageLoadSuccess(result.bitmap);
            }
        }
    };

    private Context mContext;
    private ImageResizer mImageResizer = new ImageResizer();
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
        File diskCacheDir = getDiskCacheDir(mContext, "bitmap");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1,
                        DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * build a new instance of ImageLoader
     * @param context
     * @return a new instance of ImageLoader
     */
    public static ImageLoader build(Context context) {
        return new ImageLoader(context);
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * load bitmap from memory cache or disk cache or network async, then bind imageView and bitmap.
     * NOTE THAT: should run in UI Thread
     * @param uri http url
     * @param imageView bitmap's bind object
     */
    public int bindBitmap(final String uri, final ImageBase imageView) {
        return bindBitmap(uri, imageView, 0, 0);
    }

    public int getBitmap(final String uri, final int reqWidth, final int reqHeight, Listener lis) {
        int ret = 0;
//        Log.d(TAG, "bindBitmap:" + uri + "  uuid:" + imageView.getUuid());

        Bitmap bitmap = loadBitmapFromMemCache(uri);
        if (bitmap != null) {
//            Log.d(TAG, "loadBitmapFromMemCache:" + uri);
            if (null != lis) {
                lis.onImageLoadSuccess(bitmap);
            }
            return 1;
        }

        if (!mDownloadingUrls.contains(uri)) {
            mDownloadingUrls.add(uri);
//            Runnable loadBitmapTask = new DownloadRunnable(imageView, uri, reqWidth, reqHeight);
            THREAD_POOL_EXECUTOR.execute(mDownloadRunnableFactory.get(uri, reqWidth, reqHeight, lis));

            ret = 2;
        } else {
//            Log.d(TAG, "skip url :" + uri);
        }

        return ret;
    }

    public int bindBitmap(final String uri, final ImageBase imageView,
            final int reqWidth, final int reqHeight) {
        int ret = 0;
//        Log.d(TAG, "bindBitmap:" + uri + "  uuid:" + imageView.getUuid());

        imageView.setTag(uri);
        Bitmap bitmap = loadBitmapFromMemCache(uri);
        if (bitmap != null) {
//            Log.d(TAG, "loadBitmapFromMemCache:" + uri);
            imageView.setBitmap(bitmap, false);
//            imageView.setImageBitmap(bitmap);
            return 1;
        }

        if (!mDownloadingUrls.contains(uri)) {
            mDownloadingUrls.add(uri);
//            Runnable loadBitmapTask = new DownloadRunnable(imageView, uri, reqWidth, reqHeight);
            THREAD_POOL_EXECUTOR.execute(mDownloadRunnableFactory.get(imageView, uri, reqWidth, reqHeight));

            ret = 2;
        } else {
//            Log.d(TAG, "skip url :" + uri);
        }

        return ret;
    }

    class DownloadRunnableFactory {
        private List<DownloadRunnable> mRunnables = new ArrayList<>(20);

        public DownloadRunnable get(ImageBase imageview, String uri, int reqWidth, int reqHeight) {
            DownloadRunnable r = null;
            if (mRunnables.size() > 0) {
                r = mRunnables.remove(0);
                if (null != r) {
                    r.setParam(imageview, uri, reqWidth, reqHeight);
                }
            } else {
                r = new DownloadRunnable(imageview, uri, reqWidth, reqWidth);
            }

            return r;
        }

        public DownloadRunnable get(String uri, int reqWidth, int reqHeight, Listener lis) {
            DownloadRunnable r = null;
            if (mRunnables.size() > 0) {
                r = mRunnables.remove(0);
                if (null != r) {
                    r.setParam(uri, reqWidth, reqHeight, lis);
                }
            } else {
                r = new DownloadRunnable(uri, reqWidth, reqWidth, lis);
            }

            return r;
        }


        public void put(DownloadRunnable r) {
            mRunnables.add(r);
        }
    }

    class DownloadRunnable implements Runnable {
        private String mUri;
        private ImageBase mImageView;
        private int mReqWidth;
        private int mReqHeight;
        private Listener mListener;

        public DownloadRunnable(String uri, int reqWidth, int reqHeight, Listener lis) {
            mUri = uri;
            mReqWidth = reqWidth;
            mReqHeight = reqHeight;
            mListener = lis;
        }

        public DownloadRunnable(ImageBase imageview, String uri, int reqWidth, int reqHeight) {
            mUri = uri;
            mImageView = imageview;
            mReqWidth = reqWidth;
            mReqHeight = reqHeight;
        }

        public void setParam(String uri, int reqWidth, int reqHeight, Listener lis) {
            mUri = uri;
            mListener = lis;
            mReqWidth = reqWidth;
            mReqHeight = reqHeight;
        }

        public void setParam(ImageBase imageview, String uri, int reqWidth, int reqHeight) {
            mUri = uri;
            mImageView = imageview;
            mReqWidth = reqWidth;
            mReqHeight = reqHeight;
        }

        @Override
        public void run() {
            Bitmap bitmap = loadBitmapFromMemCache(mUri);
            if (bitmap == null) {
                if (mUri.startsWith(PREFIX_HTTP) || mUri.startsWith(PREFIX_HTTPS)) {
                    bitmap = loadBitmap(mUri, mReqWidth, mReqHeight);
                } else if (mUri.startsWith(PREFIX_FILE)) {
                    bitmap = FileImageLoader.load(ImageLoader.this.mContext, mUri.substring(PREFIX_FILE.length()));
                }
            }

            if (bitmap != null) {
//                Log.d(TAG, "load bitmap successful:" + mUri);
                LoaderResult result = new LoaderResult(mImageView, mUri, bitmap, mListener);
                mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result).sendToTarget();
            } else {
                if (null != mListener) {
                    mListener.onImageLoadFailed();
                }
//                Log.e(TAG, "load bitmap failed:" + mUri);
            }

            mDownloadingUrls.remove(mUri);
            mDownloadRunnableFactory.put(this);
        }
    }

    /**
     * load bitmap from memory cache or disk cache or network.
     * @param uri http url
     * @param reqWidth the width ImageView desired
     * @param reqHeight the height ImageView desired
     * @return bitmap, maybe null.
     */
    public Bitmap loadBitmap(String uri, int reqWidth, int reqHeight) {
//        Log.d(TAG, "loadBitmap******** uri:" + uri);
        Bitmap bitmap = loadBitmapFromMemCache(uri);
        if (bitmap != null) {
//            Log.d(TAG, "loadBitmapFromMemCache,url:" + uri);
            return bitmap;
        }

        try {
            bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
            if (bitmap != null) {
//                Log.d(TAG, "loadBitmapFromDisk,url:" + uri);
                return bitmap;
            }
//            Log.d(TAG, "loadBitmapFromHttp,url:" + uri);
            bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight);
//            Log.d(TAG, "loadBitmapFromHttp end,url:" + uri);
        } catch (IOException e) {
//            e.printStackTrace();
            Log.e(TAG, "loadBitmap failed:" + uri);
        }

        if (bitmap == null && !mIsDiskLruCacheCreated) {
            Log.w(TAG, "encounter error, DiskLruCache is not created.");
            bitmap = downloadBitmapFromUrl(uri);
        }

        return bitmap;
    }

    private Bitmap loadBitmapFromMemCache(String url) {
        return getBitmapFromMemCache(hashKeyFormUrl(url));
    }

    final static private int[] CDN_PHONE = {125, 145, 180, 200, 240, 270, 300, 360, 400, 430, 480, 540, 600, 640, 670, 720};

    static int taobaoCDNPatten(int width) {
        int pos = binarySearch(CDN_PHONE, width, true);
        return CDN_PHONE[pos];

    }

    static int binarySearch(int[] srcArray, int des, boolean higher) {

        int low = 0;
        int high = srcArray.length - 1;
        while (low <= high) {
            int middle = (low + high) / 2;
            if (des == srcArray[middle]) {
                return middle;
            } else if (des < srcArray[middle]) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        if (high < 0)
            return 0;
        if (higher) {
            if (des > srcArray[high] && high + 1 <= srcArray.length - 1) {
                high = high + 1;
            }
        } else {
            if (des < srcArray[high] && high - 1 >= 0)
                high = high - 1;
        }
        return high;
    }

    private Bitmap loadBitmapFromHttp(String url, int reqWidth, int reqHeight)
            throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network from UI Thread.");
        }
        if (mDiskLruCache == null) {
            return null;
        }

        String key = hashKeyFormUrl(url);
        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            String optUrl = url;
            if (mSetDim) {
                char c1 = url.charAt(url.length() - 8);
                char c2 = url.charAt(url.length() - 12);
                char c3 = url.charAt(url.length() - 7);
                char c4 = url.charAt(url.length() - 10);
                if (!(('-' == c1 && '-' == c2) || ('-' == c3 && '-' == c4) || ('x' == c1 && '_' == c2))) {
                    int w = taobaoCDNPatten(reqWidth);
                    optUrl = String.format("%s_%dx%dq%d.jpg", url, w, w, QUALITY);
                }
            }
//            Log.d(TAG, "url len:" + url.length() + "  xx:" + url.charAt(url.length() - 8) + "  :" + url.charAt(url.length() - 1));
//            Log.d(TAG, "new url:" + optUrl);
            if (downloadUrlToStream(optUrl, outputStream)) {
                editor.commit();
            } else {
                editor.abort();
            }
            mDiskLruCache.flush();
        }
        return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
    }

    private Bitmap loadBitmapFromDiskCache(String url, int reqWidth,
            int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load bitmap from UI Thread, it's not recommended!");
        }
        if (mDiskLruCache == null) {
            return null;
        }

        Bitmap bitmap = null;
        String key = hashKeyFormUrl(url);
        DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
        if (snapShot != null) {
            FileInputStream fileInputStream = (FileInputStream)snapShot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor fileDescriptor = fileInputStream.getFD();
            bitmap = mImageResizer.decodeSampledBitmapFromFileDescriptor(fileDescriptor,
                    reqWidth, reqHeight);

            if (null != bitmap && reqWidth > 0 && reqHeight > 0) {
                bitmap = zoomImage(bitmap, reqWidth, reqHeight);
            }
//            Log.d(TAG, "loadBitmapFromDiskCache--------- reqWidth:" + reqWidth + "  reqHeight:" + reqHeight + "  width:" + bitmap.getWidth() + "  height:" + bitmap.getHeight());
            if (bitmap != null) {
                addBitmapToMemoryCache(key, bitmap);
            }
        }

        return bitmap;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    public boolean downloadUrlToStream(String urlString,
            OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "downloadBitmap failed." + e + " str:" + urlString);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if(null != out) {
                    out.close();
                }
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private Bitmap downloadBitmapFromUrl(String urlString) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),
                    IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (final IOException e) {
            Log.e(TAG, "Error in downloadBitmap: " + e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private String hashKeyFormUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public File getDiskCacheDir(Context context, String uniqueName) {
        boolean externalStorageAvailable = Environment
                .getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        return new File(cachePath + File.separator + uniqueName);
    }

    @TargetApi(VERSION_CODES.GINGERBREAD)
    private long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    private static class LoaderResult {
        public Object baseObject;
        public String uri;
        public Bitmap bitmap;
        public Listener mListener;

        public LoaderResult(Object imageView, String uri, Bitmap bitmap, Listener lis) {
            this.baseObject = imageView;
            this.uri = uri;
            this.bitmap = bitmap;
            mListener = lis;
        }
    }
}
