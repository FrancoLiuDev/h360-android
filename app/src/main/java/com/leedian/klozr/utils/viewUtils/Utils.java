package com.leedian.klozr.utils.viewUtils;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Shader;
import android.os.Build;
import android.util.Size;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Utils
 *
 * @author Franco
 */
public class Utils {
    public static int pxToDp(int px) {

        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {

        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static Point percentToPoint(float x, float y, Size size) {

        Point pos;
        float xPos = size.getWidth() * x;
        float yPos = size.getHeight() * y;
        pos = new Point((int) xPos, (int) yPos);
        return pos;
    }

    public static int percentToRadiusLength(float percent, Size size) {

        float fullLength;

        if (size.getWidth() > size.getHeight()) {
            fullLength = size.getWidth();
        } else {
            fullLength = size.getHeight();
        }

        float length = fullLength * percent;
        return (int) length;
    }

    public static float[] pointToPercent(PointF pos, Size size) {

        float percent[] = {0, 0};
        percent[0] = pos.x / size.getWidth();
        percent[1] = pos.y / size.getHeight();
        return percent;
    }

    public static float radiusLengthToPercent(float length, Size size) {

        float fullLength;

        if (size.getWidth() > size.getHeight()) { fullLength = size.getWidth(); } else {
            fullLength = size.getHeight();
        }

        return length / fullLength;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {

        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    public static Bitmap getBmpRectangleBySource(Bitmap bitmap, int x, int y, int width, int height) {

        int[]  pixels = new int[width * height];
        Paint  eraser = new Paint();
        Bitmap rectBitmap;
        BitmapShader bitmapShader;

        eraser.setColor(0xFFFFFFFF);
        eraser.setFlags(Paint.ANTI_ALIAS_FLAG);

        Bitmap       SourceBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmapShader = new BitmapShader(SourceBitmap, Shader.TileMode.MIRROR, Shader.TileMode.REPEAT);

        eraser.setShader(bitmapShader);

        SourceBitmap.getPixels(pixels, 0, width, x, y, width, height);

        Bitmap temp = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        rectBitmap = temp.copy(Bitmap.Config.ARGB_8888, true);
        return rectBitmap;
    }

    public static Bitmap getBmpCenterCircleBySource(Bitmap bitmap, int radius) {

        Paint  eraser = new Paint();

        Canvas canvas;
        eraser.setColor(0xFFFFFFFF);
        eraser.setFlags(Paint.ANTI_ALIAS_FLAG);
        BitmapShader bitmapShader;

        Bitmap CircleBmp = Bitmap.createBitmap(bitmap.getWidth(),
                                               bitmap.getHeight(),
                                               Bitmap.Config.ARGB_8888);
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.MIRROR, Shader.TileMode.REPEAT);

        eraser.setShader(bitmapShader);
        canvas = new Canvas(CircleBmp);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, radius, eraser);
        return CircleBmp;
    }

    public static void drawOutCircle(Bitmap bitmap) {

        Paint  paint = new Paint();
        Canvas canvas;

        paint.setColor(0xFFFFFFFF);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        canvas = new Canvas(bitmap);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, (bitmap.getHeight() / 2) - 5, paint);
    }
}
