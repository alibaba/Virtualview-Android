package com.tmall.wireless.rx.vaf.framework;

import java.util.concurrent.Callable;

import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by longerian on 2018/2/26.
 *
 * @author longerian
 * @date 2018/02/26
 */

public class MemoryBinaryTemplateObservable implements RxBinaryTemplateObservable {

    private String type;

    private Callable<Template> task; //TODO  从 viewmanager 获取内存缓存

    public MemoryBinaryTemplateObservable(String type,
        Callable<Template> task) {
        this.type = type;
        this.task = task;
    }

    @Override
    public Observable<Template> templateChange() {
        return Observable.fromCallable(task)
        .doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                Log.d("Longer", "in memory: accept subscribe " + disposable + " " + Thread.currentThread().getId());
            }
        })
        .doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                Log.d("Longer", "in memory: dispose action " + Thread.currentThread().getId());
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                Log.d("Longer", "in memory: complete action " + Thread.currentThread().getId());
            }
        });
    }
}
