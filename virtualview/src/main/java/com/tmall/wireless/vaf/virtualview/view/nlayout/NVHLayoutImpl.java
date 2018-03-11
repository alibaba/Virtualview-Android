package com.tmall.wireless.vaf.virtualview.view.nlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.tmall.wireless.vaf.virtualview.core.IContainer;
import com.tmall.wireless.vaf.virtualview.core.IView;
import com.tmall.wireless.vaf.virtualview.core.ViewBase;

/**
 * Created by longerian on 2018/3/11.
 *
 * @author longerian
 * @date 2018/03/11
 */

public class NVHLayoutImpl extends NVHLayoutView implements IView, IContainer {
    public NVHLayoutImpl(Context context) {
        super(context);
    }

    public NVHLayoutImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NVHLayoutImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {

    }

    @Override
    public void comLayout(int l, int t, int r, int b) {

    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public int getComMeasuredWidth() {
        return 0;
    }

    @Override
    public int getComMeasuredHeight() {
        return 0;
    }

    @Override
    public void attachViews() {

    }

    @Override
    public void setVirtualView(ViewBase view) {

    }

    @Override
    public ViewBase getVirtualView() {
        return null;
    }

    @Override
    public View getHolderView() {
        return null;
    }

    @Override
    public void destroy() {

    }

    @Override
    public int getType() {
        return 0;
    }
}
