package com.tmall.wireless.rx.vaf.framework;

import android.support.v4.util.ArrayMap;

/**
 * Created by longerian on 2018/2/26.
 *
 * @author longerian
 * @date 2018/02/26
 */

public class TemplatePool {

    private ArrayMap<String, Template> templateCachePool = new ArrayMap<>();

    public void putTemplate(String type, Template template) {
        synchronized (templateCachePool) {
            templateCachePool.put(type, template);
        }
    }

    public Template getTemplate(String type) {
        synchronized (templateCachePool) {
            if (templateCachePool.containsKey(type)) {
                return templateCachePool.get(type);
            } else {
                return Template.NOT_FOUND;
            }
        }
    }

}
