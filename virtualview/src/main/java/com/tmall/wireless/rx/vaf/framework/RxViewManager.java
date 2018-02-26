package com.tmall.wireless.rx.vaf.framework;

import java.util.concurrent.Callable;

import android.util.Log;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by longerian on 2018/2/26.
 *
 * @author longerian
 * @date 2018/02/26
 */

public class RxViewManager {

    private TemplatePool mTemplatePool;

    private LocalTemplateLoader mLocalTemplateLoader;

    private RemoteTemplateLoader mRemoteTemplateLoader;

    public RxViewManager() {
        mTemplatePool = new TemplatePool();
    }

    public void setLocalTemplateLoader(LocalTemplateLoader localTemplateLoader) {
        mLocalTemplateLoader = localTemplateLoader;
    }

    public void setRemoteTemplateLoader(
        RemoteTemplateLoader remoteTemplateLoader) {
        mRemoteTemplateLoader = remoteTemplateLoader;
    }

    public Observable<Template> getTemplate(final String type, final String url) {
        MemoryBinaryTemplateObservable memoryBinaryTemplateObservable = new MemoryBinaryTemplateObservable(type,
            new Callable<Template>() {
                @Override
                public Template call() throws Exception {
                    return getTemplateFromMemory(type);
                }
            });
        LocalBinaryTemplateObservable localBinaryTemplateObservable = new LocalBinaryTemplateObservable(type,
            new Callable<Template>() {
                @Override
                public Template call() throws Exception {
                    return getTemplateFromLocal(type);
                }
            });
        RemoteBinaryTemplateObservable remoteBinaryTemplateObservable = new RemoteBinaryTemplateObservable(type,
            new Callable<Template>() {
                @Override
                public Template call() throws Exception {
                    return getTemplateFromNetwork(url);
                }
            });
        return Observable.concat(memoryBinaryTemplateObservable.templateChange(),
            localBinaryTemplateObservable.templateChange(),
            remoteBinaryTemplateObservable.templateChange()).filter(new Predicate<Template>() {
            @Override
            public boolean test(Template template) throws Exception {
                return template != Template.NOT_FOUND;
            }
        }).take(1)
            .doOnNext(new Consumer<Template>() {
                @Override
                public void accept(Template template) throws Exception {
                    Log.d("Longer", "in concat doOnNext " + Thread.currentThread().getId());
                    mTemplatePool.putTemplate(template.type, template);
                }
            })
            .doOnComplete(new Action() {
                @Override
                public void run() throws Exception {
                    Log.d("Longer", "in concat doOnComplete " + Thread.currentThread().getId());
                }
            });
    }

    private Template getTemplateFromMemory(String type) {
        return mTemplatePool.getTemplate(type);
    }

    private Template getTemplateFromLocal(String type) {
        if (mLocalTemplateLoader != null) {
            return mLocalTemplateLoader.binaryDataFor(type);
        } else {
            return Template.NOT_FOUND;
        }
    }

    private Template getTemplateFromNetwork(String url) {
        if (mRemoteTemplateLoader != null) {
            return mRemoteTemplateLoader.binaryDataFor(url);
        } else {
            return Template.NOT_FOUND;
        }
    }

    public interface LocalTemplateLoader {
        Template binaryDataFor(String type);
    }

    public interface RemoteTemplateLoader {
        Template binaryDataFor(String url);
    }

}
