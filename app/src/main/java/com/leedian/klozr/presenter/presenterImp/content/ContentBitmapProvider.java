package com.leedian.klozr.presenter.presenterImp.content;

import android.graphics.Bitmap;
import android.util.Size;

import com.leedian.klozr.model.dataOut.ViewModelContentBitmap;

/**
 * A  Content Bitmap Array
 *
 * @author Franco
 */
public class ContentBitmapProvider {
    private Bitmap[] bitmaps = null;
    private int Index = 0;

    /**
     * Constructor
     *
     * @param bitmaps
     */
    public ContentBitmapProvider(Bitmap[] bitmaps) {

        this.bitmaps = bitmaps;
    }

    /**
     * Get Size of Bitmap buffer
     *
     * @return int
     */
    public int getSize() {

        if (bitmaps == null) {
            return 0;
        }
        return bitmaps.length;
    }

    /**
     * get First Bitmap object
     *
     * @return ViewModelContentBitmap
     */
    public ViewModelContentBitmap getFirst() {

        Index = 0;
        if (bitmaps == null) {
            return null;
        }

        return buildBmpInfo(Index);
    }

    /**
     * Build A Bitmap Information
     *
     * @param Index the index
     * @return ViewModelContentBitmap
     */
    private ViewModelContentBitmap buildBmpInfo(int Index) {

        ViewModelContentBitmap info = new ViewModelContentBitmap();
        info.setBitmap(bitmaps[Index]);
        info.setIndex(Index);
        info.setSize(new Size(bitmaps[Index].getWidth(), bitmaps[Index].getHeight()));
        return info;
    }

    /**
     * Get Current Content Bitmap
     *
     * @return ViewModelContentBitmap
     */
    public ViewModelContentBitmap getCurrent() {

        if (bitmaps == null) {
            return null;
        }
        return buildBmpInfo(Index);
    }

    /**
     * Get Next Content Bitmap and set next as current
     *
     * @return ViewModelContentBitmap
     */
    public ViewModelContentBitmap getNext() {

        int index;

        if (bitmaps == null) {
            return null;
        }

        index = Index + 1;
        Index = index;

        if (index >= getSize()) {
            Index = 0;
        }

        return buildBmpInfo(Index);
    }

    /**
     * Get Previous Content Bitmap and set next as current
     *
     * @return ViewModelContentBitmap
     */
    public ViewModelContentBitmap getPrevious() {

        int index;

        if (bitmaps == null) {
            return null;
        }

        index = Index - 1;
        Index = index;

        if (index < 0) {
            Index = getSize() - 1;
        }

        return buildBmpInfo(Index);
    }

    /**
     * Release Bitmap provider memory
     */
    public void releaseBmpInfo() {

        if (bitmaps != null) {
            for (int i = 0; i < bitmaps.length; i++) {
                if (bitmaps[i] != null) {
                    bitmaps[i].recycle();
                    bitmaps[i] = null;
                }
            }
        }

        bitmaps = null;
    }
}
