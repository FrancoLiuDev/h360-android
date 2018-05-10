package com.leedian.klozr.utils.viewUtils.tools;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;

/**
 * ImageViewPositionHandler
 *
 * @author Franco
 */
public class ImageViewPositionHandler {
    private RectF     viewPortRect;
    private RectF     bmpSizeRect;
    private ImageView bmpImageView;
    private Matrix matrix      = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF mid;

    public ImageViewPositionHandler(ImageView imageView, RectF ViewPortRect, RectF BmpRect) {

        this.viewPortRect = ViewPortRect;
        this.bmpSizeRect = BmpRect;
        this.bmpImageView = imageView;
    }

    public void setScaleMid(PointF mid) {

        this.mid = mid;
    }

    public void setScaleMode(boolean setScale) {

        if (setScale) { this.bmpImageView.setScaleType(ImageView.ScaleType.MATRIX); } else {
            this.bmpImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    public void setNotifyBitmapSize(Size size) {

        //refactor
        if (bmpImageView.getScaleType() != ImageView.ScaleType.MATRIX) { return; }

        Bitmap bitmap        = ((BitmapDrawable) this.bmpImageView.getDrawable()).getBitmap();
        Matrix currentMatrix = this.bmpImageView.getImageMatrix();

        float[] values = new float[9];
        currentMatrix.getValues(values);

        float globalX = values[Matrix.MTRANS_X];
        float globalY = values[Matrix.MTRANS_Y];

        float currentWidth   = bitmap.getWidth();
        float newWidth       = size.getWidth();
        float newHeight      = size.getHeight();
        float sizeChangeRate = newWidth / currentWidth;

        if (size.getWidth() != bitmap.getWidth()) {

            float fixedScale = 0 + (1 / sizeChangeRate);

            currentMatrix.postScale(fixedScale, fixedScale, globalX, globalY);

            savedMatrix.set(currentMatrix);
            matrix.set(currentMatrix);
            applyMatrix(bmpImageView);

            this.bmpSizeRect = new RectF(this.bmpSizeRect.left, this.bmpSizeRect.top, this.bmpSizeRect.left + newWidth, this.bmpSizeRect.top + newHeight);
        }
    }

    public void initCorpCenter() {

        matrix.setRectToRect(bmpSizeRect, viewPortRect, Matrix.ScaleToFit.CENTER);
        savedMatrix.set(matrix);
    }

    public void onTriggerBmpViewPosMove(float DistX, float DistY) {

        float[] values = new float[9];
        matrix.getValues(values);

        float globalX      = values[Matrix.MTRANS_X];
        float globalY      = values[Matrix.MTRANS_Y];
        float matrixWidth  = values[Matrix.MSCALE_X] * bmpSizeRect.width();
        float matrixHeight = values[Matrix.MSCALE_Y] * bmpSizeRect.height();

        Log.d("X=", Integer.toString((int) globalX));
        Log.d("Y=", Integer.toString((int) globalY));
        Log.d("matrixWidth=", Integer.toString((int) matrixWidth));
        Log.d("matrixHeight=", Integer.toString((int) matrixHeight));

        matrix.set(savedMatrix);
        matrix.set(onPreTranslateMatrixDist(DistX, DistY));
    }

    private Matrix onPreTranslateMatrixDist(float DistX, float DistY) {

        Matrix testMatrix = new Matrix();

        testMatrix.set(matrix);
        testMatrix.postTranslate(DistX, DistY);
        testMatrix.set(checkTranslate(testMatrix));

        return testMatrix;
    }

    private Matrix onPreTranslateMatrixScale(float scale, float midX, float midY) {

        Matrix testMatrix = new Matrix();

        testMatrix.set(matrix);
        testMatrix.postScale(scale, scale, midX, midY);
        testMatrix.set(checkTranslate(testMatrix));

        return testMatrix;
    }

    private Matrix checkTranslate(Matrix cMatrix) {

        Matrix testMatrix = new Matrix();
        testMatrix.set(cMatrix);

        float[] values = new float[9];
        testMatrix.getValues(values);

        float globalX      = values[Matrix.MTRANS_X];
        float globalY      = values[Matrix.MTRANS_Y];
        float matrixWidth  = values[Matrix.MSCALE_X] * bmpSizeRect.width();
        float matrixHeight = values[Matrix.MSCALE_Y] * bmpSizeRect.height();

        Log.d("X=", Integer.toString((int) globalX));
        Log.d("Y=", Integer.toString((int) globalY));
        Log.d("matrixWidth=", Integer.toString((int) matrixWidth));
        Log.d("matrixHeight=", Integer.toString((int) matrixHeight));

        boolean isPortrait = (matrixHeight / matrixWidth) > (viewPortRect.height() / viewPortRect
                .width());

        if (isPortrait && (matrixHeight < viewPortRect.height())) {
            this.bmpImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else if ((matrixWidth < viewPortRect.width()) && (!isPortrait)) {
            this.bmpImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        if (isPortrait && (globalY > 0)) {
            testMatrix.postTranslate(0, 0 - globalY);
        } else if ((globalX > 0) && (!isPortrait)) {
            testMatrix.postTranslate(0 - globalX, 0);
        }

        if (isPortrait && ((globalY + matrixHeight) < viewPortRect.height())) {
            testMatrix.postTranslate(0, viewPortRect.height() - (globalY + matrixHeight));
        } else if (((globalX + matrixWidth) < viewPortRect.width()) && (!isPortrait)) {

            testMatrix.postTranslate(viewPortRect.width() - (globalX + matrixWidth), 0);
        }

        if (isPortrait) {

            if (matrixWidth < viewPortRect.width()) {
                testMatrix
                        .postTranslate((0 - globalX) + (viewPortRect.width() - matrixWidth) / 2, 0);
            } else {
                if (globalX > 0) {
                    testMatrix.postTranslate(0 - globalX, 0);
                }

                if ((globalX + matrixWidth) < viewPortRect.width()) {
                    testMatrix.postTranslate(viewPortRect.width() - (globalX + matrixWidth), 0);
                }
            }
        } else {

            if (matrixHeight < viewPortRect.height()) {
                testMatrix.postTranslate(0, (0 - globalY) + (viewPortRect
                                                                     .height() - matrixHeight) / 2);
            } else {
                if (globalY > 0) {
                    testMatrix.postTranslate(0, 0 - globalY);
                }

                if ((globalY + matrixHeight) < viewPortRect.height()) {
                    testMatrix.postTranslate(0, viewPortRect.height() - (globalY + matrixHeight));
                }
            }
        }

        return testMatrix;
    }

    public void onBmpViewScaleChange(float scale) {

        float[] values = new float[9];
        matrix.getValues(values);

        float globalX      = values[Matrix.MTRANS_X];
        float globalY      = values[Matrix.MTRANS_Y];
        float matrixWidth  = values[Matrix.MSCALE_X] * bmpSizeRect.width();
        float matrixHeight = values[Matrix.MSCALE_Y] * bmpSizeRect.height();

        Log.d("X=", Integer.toString((int) globalX));
        Log.d("Y=", Integer.toString((int) globalY));
        Log.d("matrixWidth=", Integer.toString((int) matrixWidth));
        Log.d("matrixHeight=", Integer.toString((int) matrixHeight));

        if (matrixWidth >= viewPortRect.width()) {
            matrix.set(savedMatrix);
            matrix.postScale(scale, scale, this.mid.x, this.mid.y);
        }

        matrix.set(savedMatrix);
        matrix.set(onPreTranslateMatrixScale(scale, this.mid.x, this.mid.y));
    }

    public void applyMatrix(ImageView view) {
        //refactor
        view.setImageMatrix(matrix);
    }

    public void onBmpViewTouchRelease() {

        float[] values = new float[9];
        matrix.getValues(values);

        float globalX      = values[Matrix.MTRANS_X];
        float globalY      = values[Matrix.MTRANS_Y];
        float matrixWidth  = values[Matrix.MSCALE_X] * bmpSizeRect.width();
        float matrixHeight = values[Matrix.MSCALE_Y] * bmpSizeRect.height();

        Log.d("X=", Integer.toString((int) globalX));
        Log.d("Y=", Integer.toString((int) globalY));
        Log.d("matrixWidth=", Integer.toString((int) matrixWidth));
        Log.d("matrixHeight=", Integer.toString((int) matrixHeight));

        savedMatrix.set(matrix);
    }
}
