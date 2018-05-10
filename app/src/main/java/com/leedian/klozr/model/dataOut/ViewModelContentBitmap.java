package com.leedian.klozr.model.dataOut;
import android.graphics.Bitmap;
import android.util.Size;

/**
 * Model for a bitmap for display
 *
 * @author Franco
 */
public class ViewModelContentBitmap {
    private int    index;
    private Bitmap bitmap;
    private Size   size;

    /**
     * get Bitmap
     *
     * @return  Bitmap
     **/
    public Bitmap getBitmap() {

        return bitmap;
    }

    /**
     * get Bitmap
     *
     * @return  Bitmap
     **/
    public void setBitmap(Bitmap bitmap) {

        this.bitmap = bitmap;
    }


    /**
     * get index
     *
     * @return  int
     **/
    public int getIndex() {

        return index;
    }

    /**
     * set index
     *
     * @param  index the index
     **/
    public void setIndex(int index) {

        this.index = index;
    }

    /**
     * get bitmap size
     *
     * @return   Size
     **/
    public Size getSize() {

        return size;
    }


    /**
     * set bitmap size
     *
     * @param  size the size
     **/
    public void setSize(Size size) {

        this.size = size;
    }
}

