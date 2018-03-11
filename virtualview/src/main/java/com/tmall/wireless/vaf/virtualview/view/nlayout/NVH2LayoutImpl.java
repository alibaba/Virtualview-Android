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

public class NVH2LayoutImpl extends NVH2LayoutView implements IView, IContainer {
    public NVH2LayoutImpl(Context context) {
        super(context);
    }

    public NVH2LayoutImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NVH2LayoutImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void measureComponent(int widthMeasureSpec, int heightMeasureSpec) {
        this.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void comLayout(int l, int t, int r, int b) {
        this.layout(l, t, r, b);
    }

    @Override
    public void onComMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onComLayout(boolean changed, int l, int t, int r, int b) {
        this.onLayout(changed, l, t, r, b);
    }

    @Override
    public int getComMeasuredWidth() {
        return this.getMeasuredWidth();
    }

    @Override
    public int getComMeasuredHeight() {
        return this.getMeasuredHeight();
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
        return this;
    }

    @Override
    public void destroy() {

    }

    @Override
    public int getType() {
        return -1;
    }
}
