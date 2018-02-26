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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.util.Log;
import com.tmall.wireless.rx.vaf.framework.RxViewManager;
import com.tmall.wireless.rx.vaf.framework.RxViewManager.LocalTemplateLoader;
import com.tmall.wireless.rx.vaf.framework.RxViewManager.RemoteTemplateLoader;
import com.tmall.wireless.rx.vaf.framework.Template;
import com.tmall.wireless.virtualviewdemo.utils.Utils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.tmall.wireless.virtualviewdemo.utils.Utils.getBytesByInputStream;

/**
 * Created by longerian on 2018/2/26.
 *
 * @author longerian
 * @date 2018/02/26
 */
@RunWith(AndroidJUnit4.class)
public class TemplateObservableTest extends AndroidTestCase {

    @Test
    @LargeTest
    public void testTwoLevelCache() {
        Log.d("Longer", "------");
        RxViewManager rxViewManager = new RxViewManager();
        rxViewManager.setLocalTemplateLoader(new LocalTemplateLoader() {
            @Override
            public Template binaryDataFor(String type) {
                String path = "/sdcard/virtualview.out";
                return new Template(type, 1, Utils.readBin(path));
            }
        });
        Observable<Template> observable = rxViewManager.getTemplate("test", null);
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Template>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("Longer", "on subscribe " + Thread.currentThread().getId());
            }

            @Override
            public void onNext(Template template) {
                Log.d("Longer", "on next " + template.type + " " + Thread.currentThread().getId());
                Assert.assertEquals("test", template.type);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("Longer", "on error " + Thread.currentThread().getId());

            }

            @Override
            public void onComplete() {
                Log.d("Longer", "on complete " + Thread.currentThread().getId());

            }
        });
    }

    @Test
    @LargeTest
    public void testThreeLevelCache() {
        Log.d("Longer", "------");
        RxViewManager rxViewManager = new RxViewManager();
        rxViewManager.setLocalTemplateLoader(new LocalTemplateLoader() {
            @Override
            public Template binaryDataFor(String type) {
                return Template.NOT_FOUND;
            }
        });
        rxViewManager.setRemoteTemplateLoader(new RemoteTemplateLoader() {
            @Override
            public Template binaryDataFor(String url) {
                URL url1 = null;
                HttpURLConnection conn = null;
                try {
                    url1 = new URL(url);
                    conn = (HttpURLConnection) url1.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    InputStream is = conn.getInputStream();
                    byte[] responseBody = getBytesByInputStream(is);
                    return new Template("remote", 1, responseBody);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Template.NOT_FOUND;
            }
        });
        Observable<Template> observable = rxViewManager.getTemplate("test", "http://yosemite.cn-hangzhou.oss.aliyun-inc.com/VVTemplateManagePRE/0b470132737f4bd492acd9abb4a53c67");
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Template>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("Longer", "1 on subscribe " + Thread.currentThread().getId());
            }

            @Override
            public void onNext(Template template) {
                Log.d("Longer", "1 on next " + template.type + " " + Thread.currentThread().getId());
                Assert.assertEquals("remote", template.type);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("Longer", "1 on error " + Thread.currentThread().getId());

            }

            @Override
            public void onComplete() {
                Log.d("Longer", "1 on complete " + Thread.currentThread().getId());

            }
        });
    }

}
