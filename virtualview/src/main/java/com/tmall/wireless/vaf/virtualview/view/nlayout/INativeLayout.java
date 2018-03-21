package com.tmall.wireless.vaf.virtualview.view.nlayout;

import android.graphics.Canvas;

/**
 * Created by longerian on 2018/3/17.
 *
 * @author longerian
 * @date 2018/03/17
 */

public interface INativeLayout {

    void onLayoutMeasure(int widthMeasureSpec, int heightMeasureSpec);

    void onLayoutLayout(boolean changed, int l, int t, int r, int b);

    void layoutDraw(Canvas canvas);

}
