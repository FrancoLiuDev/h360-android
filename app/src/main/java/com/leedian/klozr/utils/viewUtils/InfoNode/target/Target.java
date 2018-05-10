package com.leedian.klozr.utils.viewUtils.InfoNode.target;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

/**
 * Target
 *
 * @author Franco
 */
public interface Target {
    /**
     * Returns center point of target.
     * We can get x and y coordinates using
     * point object
     *
     * @return Point
     */
    Point getPoint();

    void setPoint(Point pos, int radius);

    /**
     * Returns Rectangle points of target view
     *
     * @return Rect
     */
    Rect getRect();

    RectF getRectF();

    /**
     * return target view
     *
     * @return
     */
    View getView();
}
