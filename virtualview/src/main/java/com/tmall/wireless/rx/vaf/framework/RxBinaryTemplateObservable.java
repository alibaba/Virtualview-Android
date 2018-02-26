package com.tmall.wireless.rx.vaf.framework;

import io.reactivex.Observable;

/**
 * Created by longerian on 2018/2/26.
 *
 * @author longerian
 * @date 2018/02/26
 */

public interface RxBinaryTemplateObservable {

    Observable<Template> templateChange();

}
