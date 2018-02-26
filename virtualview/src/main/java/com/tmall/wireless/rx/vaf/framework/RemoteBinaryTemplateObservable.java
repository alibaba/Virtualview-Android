package com.tmall.wireless.rx.vaf.framework;

import java.util.concurrent.Callable;

import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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

    private int localVersion;

    private Callable<Template> task;

    public RemoteBinaryTemplateObservable(String type, int localVersion,
        Callable<Template> task) {
        this.type = type;
        this.localVersion = localVersion;
        this.task = task;
    }

    @Override
    public Observable<Template> templateChange() {
        return Observable.create(new ObservableOnSubscribe<Template>() {
            @Override
            public void subscribe(ObservableEmitter<Template> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    emitter.onNext(task.call());
                    //TODO try on error
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io()).filter(new Predicate<Template>() {
            @Override
            public boolean test(Template template) throws Exception {
                return template.version > localVersion && template.type.equals(type);
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
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
