package com.tmall.wireless.vaf.virtualview.Helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;

/**
 * Created by longerian on 2017/12/18.
 *
 * @author longerian
 * @date 2017/12/18
 */

public class VirtualViewUtils {

    private static boolean enableBorderRadius = true;

    public static void setEnableBorderRadius(boolean enableBorderRadius) {
        VirtualViewUtils.enableBorderRadius = enableBorderRadius;
    }

    private static RectF oval = new RectF();

    private static Path sPath = new Path();

    private static Paint sBorderPaint;

    private static Paint sBackgroundPaint;

    public static void drawBorder(Canvas canvas, int borderColor, int width, int height, int borderWidth,
        int borderTopLeftRadius, int borderTopRightRadius, int borderBottomLeftRadius, int borderBottomRightRadius) {
        if (canvas == null || borderWidth <= 0 || borderColor == Color.TRANSPARENT) {
            return;
        }
        if (sBorderPaint == null) {
            sBorderPaint = new Paint();
            sBorderPaint.setAntiAlias(true);
            sBorderPaint.setStyle(Paint.Style.STROKE);
        }
        sBorderPaint.setColor(borderColor);
        sBorderPaint.setStrokeWidth(borderWidth);
        if (!enableBorderRadius) {
            borderTopLeftRadius = 0;
            borderTopRightRadius = 0;
            borderBottomLeftRadius = 0;
            borderBottomRightRadius = 0;
        }
        float halfBorderWidth = (borderWidth / 2.0f);
        //draw left border
        canvas.drawLine(halfBorderWidth, borderTopLeftRadius > 0 ? borderTopLeftRadius + halfBorderWidth : 0, halfBorderWidth,
            borderBottomLeftRadius > 0 ? height - borderBottomLeftRadius - halfBorderWidth : height, sBorderPaint);

        //draw top border
        canvas.drawLine(borderTopLeftRadius > 0 ? halfBorderWidth + borderTopLeftRadius : 0, halfBorderWidth,
            borderTopRightRadius > 0 ? width - borderTopRightRadius - halfBorderWidth : width, halfBorderWidth, sBorderPaint);

        //draw right border
        canvas.drawLine(width - halfBorderWidth, borderTopRightRadius > 0 ? halfBorderWidth + borderTopRightRadius : 0, width - halfBorderWidth,
            borderBottomRightRadius > 0 ? height - borderBottomRightRadius - halfBorderWidth : height, sBorderPaint);

        //draw bottom border
        canvas.drawLine(borderBottomLeftRadius > 0 ? halfBorderWidth + borderBottomLeftRadius : 0, height - halfBorderWidth,
            borderBottomRightRadius > 0 ? width - borderBottomRightRadius - halfBorderWidth : width, height - halfBorderWidth, sBorderPaint);

        //draw top left corner
        if (borderTopLeftRadius > 0) {
            oval.set(0, 0, 2 * borderTopLeftRadius, 2 * borderTopLeftRadius);
            oval.offset(halfBorderWidth, halfBorderWidth);
            canvas.drawArc(oval, 179, 91, false, sBorderPaint);
        }

        //draw top right corner
        if (borderTopRightRadius > 0) {
            oval.set(width - 2 * borderTopRightRadius, 0, width, 2 * borderTopRightRadius);
            oval.offset(-halfBorderWidth, halfBorderWidth);
            canvas.drawArc(oval, 269, 91, false, sBorderPaint);
        }

        //draw bottom right corner
        if (borderBottomRightRadius > 0) {
            oval.set(width - 2 * borderBottomRightRadius, height - 2 * borderBottomRightRadius, width, height);
            oval.offset(-halfBorderWidth, -halfBorderWidth);
            canvas.drawArc(oval, -1, 91, false, sBorderPaint);
        }

        //draw bottom left corner
        if (borderBottomLeftRadius > 0) {
            oval.set(0, height - 2 * borderBottomLeftRadius, 2 * borderBottomLeftRadius, height);
            oval.offset(halfBorderWidth, -halfBorderWidth);
            canvas.drawArc(oval, 89, 91, false, sBorderPaint);
        }
    }

    public static void drawBackground(Canvas canvas, int backgruondColor, int width, int height, int borderWidth,
        int borderTopLeftRadius, int borderTopRightRadius, int borderBottomLeftRadius, int borderBottomRightRadius) {
        if (canvas == null) {
            return;
        }
        if (null == sBackgroundPaint) {
            sBackgroundPaint = new Paint();
            sBackgroundPaint.setAntiAlias(true);
        }
        sBackgroundPaint.setColor(backgruondColor);
        if (!enableBorderRadius) {
            borderTopLeftRadius = 0;
            borderTopRightRadius = 0;
            borderBottomLeftRadius = 0;
            borderBottomRightRadius = 0;
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
        canvas.drawPath(sPath, sBackgroundPaint);
    }

    public static void clipCanvas(Canvas canvas, int width, int height, int borderWidth,
        int borderTopLeftRadius, int borderTopRightRadius, int borderBottomLeftRadius, int borderBottomRightRadius) {
        clipCanvas(null, canvas, width, height, borderWidth, borderTopLeftRadius, borderTopRightRadius, borderBottomLeftRadius, borderBottomRightRadius);
    }

    public static void clipCanvas(View view, Canvas canvas, int width, int height, int borderWidth,
        int borderTopLeftRadius, int borderTopRightRadius, int borderBottomLeftRadius, int borderBottomRightRadius) {
        if (canvas == null) {
            return;
        }

        if (!enableBorderRadius) {
            borderTopLeftRadius = 0;
            borderTopRightRadius = 0;
            borderBottomLeftRadius = 0;
            borderBottomRightRadius = 0;
        }

        if (!isRounded(borderTopLeftRadius, borderTopRightRadius, borderBottomLeftRadius, borderBottomRightRadius)) {
            return;
        }
        sPath.reset();
        //start point
        sPath.moveTo((borderTopLeftRadius > 0 ? borderTopLeftRadius : 0), 0);
        //line top edge
        sPath.lineTo(width - (borderTopRightRadius > 0 ? borderTopRightRadius : 0), 0);
        //arc to right edge
        if (borderTopRightRadius > 0) {
            oval.set(width - 2 * borderTopRightRadius, 0, width, 2 * borderTopRightRadius);
            sPath.arcTo(oval, 270, 90);
        }
        //line right edge
        sPath.lineTo(width, height - (borderBottomRightRadius > 0 ? borderBottomRightRadius : 0));
        //arc to bottom edge
        if (borderBottomRightRadius > 0) {
            oval.set(width - 2 * borderBottomRightRadius, height - 2 * borderBottomRightRadius, width, height);
            sPath.arcTo(oval, 0, 90);
        }
        //line bottom edge
        sPath.lineTo((borderBottomLeftRadius > 0 ? borderBottomLeftRadius : 0), height);
        //arc to left edge
        if (borderBottomLeftRadius > 0) {
            oval.set(0, height - 2 * borderBottomLeftRadius, 2 * borderBottomLeftRadius, height);
            sPath.arcTo(oval, 90, 90);
        }
        //line left edge
        sPath.lineTo(0, (borderTopLeftRadius > 0 ? borderTopLeftRadius : 0));
        //arc to top edge
        if (borderTopLeftRadius > 0) {
            oval.set(0, 0, 2 * borderTopLeftRadius, 2 * borderTopLeftRadius);
            sPath.arcTo(oval, 180, 90);
        }
        if (canvas.isHardwareAccelerated() && (Build.VERSION.SDK_INT < 18) && view != null) {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        canvas.clipPath(sPath);
    }

    private static boolean isRounded(int borderTopLeftRadius, int borderTopRightRadius, int borderBottomLeftRadius,
        int borderBottomRightRadius) {
        return borderTopLeftRadius > 0 || borderTopRightRadius > 0 || borderBottomLeftRadius > 0
            || borderBottomRightRadius > 0;
    }

}
