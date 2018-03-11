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

public class NFrameLayout extends NativeViewBase {

    private final static String TAG = "NFrameLayout_TMTEST";

    private NFrameLayoutImpl mNative;

    public NFrameLayout(VafContext context,
        ViewCache viewCache) {
        super(context, viewCache);

        mNative = new NFrameLayoutImpl(context.getContext());
        //mNative.setVirtualView(this);
        __mNative = mNative;
    }

    public static class Builder implements IBuilder {
        @Override
        public ViewBase build(VafContext context, ViewCache viewCache) {
            return new NFrameLayout(context, viewCache);
        }
    }
}
