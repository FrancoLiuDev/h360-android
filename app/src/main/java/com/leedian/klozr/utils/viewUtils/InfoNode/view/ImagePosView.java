package com.leedian.klozr.utils.viewUtils.InfoNode.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Size;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.leedian.klozr.utils.viewUtils.MatrixPosition;

/**
 * ImagePosView
 *
 * @author Franco
 */
public class ImagePosView
        extends ImageView
        implements CircleTargetView,
                   View.OnTouchListener
{
    static float oldX = 0;
    static float oldY = 0;
    static float newX = 0;
    static float newY = 0;
    Point targetPos = new Point(100, 100);

    public ImagePosView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public ImagePosView(Context context) {

        super(context);
    }

    @Override
    public void getTargetLocationInWindow(@Size(2) int[] location) {

        int   imageX      = (int) this.getX();
        int   imageY      = (int) this.getY();
        int[] locationPos = {imageX, imageY};

        location[0] = locationPos[0] + targetPos.x;
        location[1] = locationPos[1] + targetPos.y;
    }

    @Override
    public void setTargetLocationInWindow(Point pos, int radius) {

        MatrixPosition matrixPosition = new MatrixPosition(this);

        int imageX = (int) this.getX();
        int imageY = (int) this.getY();

        int[] locationPos = {imageX, imageY};

        targetPos.x = pos.x - locationPos[0];
        targetPos.y = pos.y - locationPos[1];

        RectF border = matrixPosition.getMatrixRectF();

        if ((targetPos.x + radius) > (border.right)) {
            targetPos.x = (int) (border.right - radius);
        }

        if ((targetPos.x - radius) < (border.left)) {
            targetPos.x = (int) (border.left + radius);
        }

        if ((targetPos.y + radius) > (border.bottom)) {
            targetPos.y = (int) (border.bottom - radius);
        }

        if ((targetPos.y - radius) < (border.top)) {
            targetPos.y = (int) (border.top + radius);
        }
    }

    @Override
    public View getView() {

        return this;
    }

    public Bitmap getBmpImage() {

        return ((BitmapDrawable) this.getDrawable()).getBitmap();
    }

    public android.util.Size getBmpSize() {

        return new android.util.Size(getBmpImage().getWidth(), getBmpImage().getHeight());
    }

    public void setContentImage(Bitmap bitmap) {

        this.setImageBitmap(bitmap);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            oldX = event.getX();
            oldY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            newX = event.getX();
            newY = event.getY();
        }
        return false;
    }

    public int getBitmapWidth() {

        Bitmap bitmap = ((BitmapDrawable) this.getDrawable()).getBitmap();

        if (bitmap == null) return 0;

        return bitmap.getWidth();
    }

    public int getBitmapHeight() {

        Bitmap bitmap = ((BitmapDrawable) this.getDrawable()).getBitmap();

        if (bitmap == null) return 0;

        return bitmap.getHeight();
    }

    public int getViewParentWidth() {

        View parent = (View) this.getParent();
        return parent.getWidth();
    }

    public int getViewParentHeight() {

        View parent = (View) this.getParent();
        return parent.getHeight();
    }

    public RectF getViewParentSizeRectF() {

        View parent = (View) this.getParent();
        return new RectF(0, 0, parent.getWidth(), parent.getHeight());
    }

    public RectF getBitmapSizeRectF() {

        Bitmap bitmap = ((BitmapDrawable) this.getDrawable()).getBitmap();

        return new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public boolean isMatrixScaleMode() {

        return (this.getScaleType() == ImageView.ScaleType.MATRIX);
    }

    public void setScaleTypeDefault() {

        this.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    public void setScaleTypeMatrix() {

        this.setScaleType(ImageView.ScaleType.MATRIX);
    }
}
