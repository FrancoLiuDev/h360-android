package com.leedian.klozr.utils.viewUtils.InfoNode.target;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.leedian.klozr.utils.viewUtils.InfoNode.view.ImagePosView;

/**
 * View Target
 *
 * @author Franco
 */
public class ViewTarget
        implements Target
{
    private Point position = new Point(0, 0);
    private ImagePosView view;

    public ViewTarget(ImagePosView view) {

        this.view = view;
    }

    @Override
    public Point getPoint() {

        int[] location = new int[2];
        view.getTargetLocationInWindow(location);
        return new Point(location[0], location[1]);
    }

    @Override
    public void setPoint(Point pos, int radius) {

        view.setTargetLocationInWindow(pos, radius);
    }

    @Override
    public Rect getRect() {

        return null;
    }

    @Override
    public RectF getRectF() {

        return null;
    }

    @Override
    public View getView() {

        return view;
    }
}
