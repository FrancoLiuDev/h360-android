package com.leedian.klozr.utils.viewUtils.InfoNode.view;
import android.graphics.Point;
import android.support.annotation.Size;
import android.view.View;

/**
 * CircleTargetView
 *
 * @author Franco
 */
public interface CircleTargetView {
    void getTargetLocationInWindow(@Size(2) int[] location);

    void setTargetLocationInWindow(Point pos, int radius);

    View getView();
}
