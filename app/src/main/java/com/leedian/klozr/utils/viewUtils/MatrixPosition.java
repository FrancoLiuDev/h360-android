package com.leedian.klozr.utils.viewUtils;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * MatrixPosition
 *
 * @author Franco
 */
public class MatrixPosition {
    private Matrix    matrix;
    private ImageView imageView;
    private float MatrixPosX   = 0;
    private float MatrixPosY   = 0;
    private float MatrixWidth  = 0;
    private float MatrixHeight = 0;
    private float MatrixScaleX = 0;
    private float MatrixScaleY = 0;
    private float BitmapWidth  = 0;
    private float BitmapHeight = 0;

    public MatrixPosition(ImageView view) {

        this.imageView = view;
        this.matrix = view.getImageMatrix();
        initPosition();
    }

    public MatrixPosition(ImageView view, Matrix matrix) {

        this.imageView = view;
        this.matrix = matrix;
        initPosition();
    }

    public Matrix getMatrix() {

        return matrix;
    }

    public ImageView getImageView() {

        return imageView;
    }

    public float getMatrixPosX() {

        return MatrixPosX;
    }

    public float getMatrixPosY() {

        return MatrixPosY;
    }

    public float getMatrixWidth() {

        return MatrixWidth;
    }

    public float getMatrixHeight() {

        return MatrixHeight;
    }

    public float getMatrixScaleX() {

        return MatrixScaleX;
    }

    public float getMatrixScaleY() {

        return MatrixScaleY;
    }

    public float getBitmapWidth() {

        return BitmapWidth;
    }

    public float getBitmapHeight() {

        return BitmapHeight;
    }

    private void initPosition() {

        float[] values = new float[9];
        Bitmap  bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        this.matrix.getValues(values);
        BitmapWidth = bitmap.getWidth();
        BitmapHeight = bitmap.getHeight();
        MatrixPosX = values[Matrix.MTRANS_X];
        MatrixPosY = values[Matrix.MTRANS_Y];
        MatrixScaleX = values[Matrix.MSCALE_X];
        MatrixScaleY = values[Matrix.MSCALE_Y];
        MatrixWidth = BitmapWidth * MatrixScaleX;
        MatrixHeight = BitmapHeight * MatrixScaleY;
    }

    public PointF getMatrixPosition(float x, float y) {

        return new PointF(MatrixPosX + (x * MatrixScaleX), MatrixPosY + (y * MatrixScaleY));
    }

    public PointF getBmpPositionFormMatrix(float x1, float y1) {

        PointF pos = new PointF(0, 0);
        pos.x = (x1 - MatrixPosX) / MatrixScaleX;
        pos.y = (y1 - MatrixPosY) / MatrixScaleY;
        return pos;
    }

    public RectF getMatrixRectF() {

        return new RectF(MatrixPosX, MatrixPosY, MatrixPosX + (BitmapWidth * MatrixScaleX), MatrixPosY + (BitmapHeight * MatrixScaleY));
    }

    public RectF getMatrixDisplayRectF() {

        float rectLeft   = (MatrixPosX >= 0) ?
                (MatrixPosX) :
                (0);
        float rectTop    = (MatrixPosY >= 0) ?
                (MatrixPosY) :
                (0);
        float rectRight  = ((MatrixPosX + (BitmapWidth * MatrixScaleX)) <= imageView.getWidth()) ?
                (MatrixPosX + (BitmapWidth * MatrixScaleX)) :
                (imageView.getWidth());
        float rectBottom = ((MatrixPosY + (BitmapHeight * MatrixScaleY)) <= imageView.getHeight()) ?
                (MatrixPosY + (BitmapHeight * MatrixScaleY)) :
                (imageView.getHeight());
        return new RectF(rectLeft, rectTop, rectRight, rectBottom);
    }

    public int getImageViewWidth() {

        return imageView.getWidth();
    }

    public int getImageViewHeight() {

        return imageView.getHeight();
    }
}
