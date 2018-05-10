package com.leedian.klozr.utils.viewUtils.InfoNode.shape;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.leedian.klozr.utils.viewUtils.InfoNode.target.Target;

/**
 * Circle shape
 *
 * @author Franco
 */
public class Circle {
    private Target target;
    private int    radius;
    private Point  circlePoint;

    public Circle(Target target, int radius) {

        this.target = target;
        this.radius = radius;
        circlePoint = getFocusPoint();
    }

    public void draw(Canvas canvas, Paint paint) {

        circlePoint = getFocusPoint();
        canvas.drawCircle(circlePoint.x, circlePoint.y, radius, paint);
    }

    public void drawOutLine(Canvas canvas, Paint paint) {

        circlePoint = getFocusPoint();
        canvas.drawCircle(circlePoint.x, circlePoint.y, radius - 5, paint);
    }

    public void drawCenter(Canvas canvas, Paint paint, int centerRadius) {

        circlePoint = getFocusPoint();
        canvas.drawCircle(circlePoint.x, circlePoint.y, centerRadius, paint);
    }

    private Point getFocusPoint() {

        return target.getPoint();
    }

    public void reCalculateAll() {

        circlePoint = getFocusPoint();
    }

    public int getRadius() {

        return radius;
    }

    public void setRadius(int radius) {

        this.radius = radius;
    }

    public Point getPoint() {

        return circlePoint;
    }

    public RectF getRectF() {

        return new RectF(getFocusPoint().x - this.radius, getFocusPoint().y - this.radius, getFocusPoint().x + this.radius, getFocusPoint().y + this.radius);
    }
}
