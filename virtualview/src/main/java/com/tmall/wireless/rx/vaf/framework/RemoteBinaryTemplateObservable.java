package com.tmall.wireless.rx.vaf.framework;

import java.util.concurrent.Callable;

import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by longerian on 2018/2/26.
 *
 * @author longerian
 * @date 2018/02/26
 */

public class RemoteBinaryTemplateObservable implements RxBinaryTemplateObservable {

    private String type;

    private Callable<Template> task;

    public RemoteBinaryTemplateObservable(String type, Callable<Template> task) {
        this.type = type;
        this.task = task;
    }

    @Override
    public Observable<Template> templateChange() {
        //TODO retry when error
        return Observable.fromCallable(task).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                Log.d("Longer", "in remote: accept subscribe " + disposable + " " + Thread.currentThread().getId());
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                Log.d("Longer", "in remote: dispose action" + Thread.currentThread().getId());
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                Log.d("Longer", "in remote: complete action" + Thread.currentThread().getId());
            }
        });
    }
}
