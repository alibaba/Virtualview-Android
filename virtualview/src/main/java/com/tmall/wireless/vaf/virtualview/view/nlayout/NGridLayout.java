package com.tmall.wireless.vaf.virtualview.view.nlayout;

import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.core.NativeViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;
import com.tmall.wireless.vaf.virtualview.core.ViewCache;

/**
 * Created by longerian on 2018/3/11.
 *
 * @author longerian
 * @date 2018/03/11
 */

public class NGridLayout extends NativeViewBase {

    private final static String TAG = "NGridLayout_TMTEST";

    public NGridLayout(VafContext context,
        ViewCache viewCache) {
        super(context, viewCache);
    }

    public static class Builder implements IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new NGridLayout(context, viewCache);
        }
    }
}
