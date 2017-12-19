package com.tmall.wireless.vaf.virtualview.Helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by longerian on 2017/12/18.
 *
 * @author longerian
 * @date 2017/12/18
 */

public class VirtualViewUtils {

    private static RectF oval = new RectF();

    private static Path sPath = new Path();

    public static void drawBorder(Canvas canvas, Paint borderPaint, int width, int height, int borderWidth,
        int borderTopLeftRadius, int borderTopRightRadius, int borderBottomLeftRadius, int borderBottomRightRadius) {
        if (canvas == null || borderPaint == null) {
            return;
        }
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

    public static void drawBackground(Canvas canvas, Paint backgroundPaint, int width, int height, int borderWidth,
        int borderTopLeftRadius, int borderTopRightRadius, int borderBottomLeftRadius, int borderBottomRightRadius) {
        if (canvas == null || backgroundPaint == null) {
            return;
        }
        float halfBorderWidth = (borderWidth / 2.0f);

        sPath.reset();
        //start point
        sPath.moveTo(borderWidth + (borderTopLeftRadius > 0 ? borderTopLeftRadius : 0), borderWidth);
        //line top edge
        sPath.lineTo(width - borderWidth - (borderTopRightRadius > 0 ? borderTopRightRadius : 0), borderWidth);
        //arc to right edge
        if (borderTopRightRadius > 0) {
            oval.set(width - 2 * borderTopRightRadius, 0, width, 2 * borderTopRightRadius);
            oval.offset(-halfBorderWidth, halfBorderWidth);
            sPath.arcTo(oval, 270, 90);
        }
        //line right edge
        sPath.lineTo(width - borderWidth, height - borderWidth - (borderBottomRightRadius > 0 ? borderBottomRightRadius : 0));
        //arc to bottom edge
        if (borderBottomRightRadius > 0) {
            oval.set(width - 2 * borderBottomRightRadius, height - 2 * borderBottomRightRadius, width, height);
            oval.offset(-halfBorderWidth, -halfBorderWidth);
            sPath.arcTo(oval, 0, 90);
        }
        //line bottom edge
        sPath.lineTo(borderWidth + (borderBottomLeftRadius > 0 ? borderBottomLeftRadius : 0), height - borderWidth);
        //arc to left edge
        if (borderBottomLeftRadius > 0) {
            oval.set(0, height - 2 * borderBottomLeftRadius, 2 * borderBottomLeftRadius, height);
            oval.offset(halfBorderWidth, -halfBorderWidth);
            sPath.arcTo(oval, 90, 90);
        }
        //line left edge
        sPath.lineTo(borderWidth, borderWidth + (borderTopLeftRadius > 0 ? borderTopLeftRadius : 0));
        //arc to top edge
        if (borderTopLeftRadius > 0) {
            oval.set(0, 0, 2 * borderTopLeftRadius, 2 * borderTopLeftRadius);
            oval.offset(halfBorderWidth, halfBorderWidth);
            sPath.arcTo(oval, 180, 90);
        }
        canvas.drawPath(sPath, backgroundPaint);
    }

}
