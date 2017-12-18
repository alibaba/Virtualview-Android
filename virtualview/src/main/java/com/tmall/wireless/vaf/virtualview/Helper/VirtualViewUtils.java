package com.tmall.wireless.vaf.virtualview.Helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by longerian on 2017/12/18.
 *
 * @author longerian
 * @date 2017/12/18
 */

public class VirtualViewUtils {

    private static RectF oval = new RectF();

    public static void drawBorder(Canvas canvas, Paint borderPaint, int width, int height, int borderWidth,
        int borderTopLeftRadius, int borderTopRightRadius, int borderBottomLeftRadius, int borderBottomRightRadius) {
        float halfBorderWidth = (borderWidth / 2.0f);
        //draw left border
        canvas.drawLine(halfBorderWidth, borderTopLeftRadius > 0 ? borderTopLeftRadius : 0, halfBorderWidth,
            borderBottomLeftRadius > 0 ? height - borderBottomLeftRadius : height, borderPaint);

        //draw top border
        canvas.drawLine(borderTopLeftRadius > 0 ? borderTopLeftRadius : 0, halfBorderWidth,
            borderTopRightRadius > 0 ? width - borderTopRightRadius : width, halfBorderWidth, borderPaint);

        //draw right border
        canvas.drawLine(width - halfBorderWidth, borderTopRightRadius > 0 ? borderTopRightRadius : 0, width - halfBorderWidth,
            borderBottomRightRadius > 0 ? height - borderBottomRightRadius : height, borderPaint);

        //draw bottom border
        canvas.drawLine(borderBottomLeftRadius > 0 ? borderBottomLeftRadius : 0, height - halfBorderWidth,
            borderBottomRightRadius > 0 ? width - borderBottomRightRadius : width, height - halfBorderWidth, borderPaint);

        //draw top left corner
        if (borderTopLeftRadius > 0) {
            oval.set(0, 0, 2 * borderTopLeftRadius, 2 * borderTopLeftRadius);
            oval.offset(halfBorderWidth, halfBorderWidth);
            canvas.drawArc(oval, 180, 90, false, borderPaint);
        }

        //draw top right corner
        if (borderTopRightRadius > 0) {
            oval.set(width - 2 * borderTopRightRadius, 0, width, 2 * borderTopRightRadius);
            oval.offset(-halfBorderWidth, halfBorderWidth);
            canvas.drawArc(oval, 270, 90, false, borderPaint);
        }

        //draw bottom right corner
        if (borderBottomRightRadius > 0) {
            oval.set(width - 2 * borderBottomRightRadius, height - 2 * borderBottomRightRadius, width, height);
            oval.offset(-halfBorderWidth, -halfBorderWidth);
            canvas.drawArc(oval, 0, 90, false, borderPaint);
        }

        //draw bottom left corner
        if (borderBottomLeftRadius > 0) {
            oval.set(0, height - 2 * borderBottomLeftRadius, 2 * borderBottomLeftRadius, height);
            oval.offset(halfBorderWidth, -halfBorderWidth);
            canvas.drawArc(oval, 90, 90, false, borderPaint);
        }
    }

}
