package com.leedian.klozr.utils.viewUtils.tools;
import android.graphics.PointF;
import android.os.SystemClock;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

/**
 * ImageViewTouchHandler
 *
 * @author Franco
 */
public class ImageViewTouchHandler
        implements View.OnTouchListener
{
    private static final int NONE = 0;

    private static final int DRAG = 1;

    private static final int ZOOM = 2;

    private static final int SCROLL = 3;

    private boolean isReady = false;

    private boolean isScalable = true;

    private float oldX = 0;

    private float oldY = 0;

    private String direction = "";

    private int mode = NONE;

    private PointF start = new PointF();

    private PointF mid = new PointF();

    private float oldDist = 1f;

    private VelocityHelp velocityHelp = new VelocityHelp();

    private TouchTabHelp touchTabHelp = new TouchTabHelp();

    private OnViewEventListener eventListener = null;

    public void setIsReady(boolean isReady) {

        this.isReady = isReady;
    }

    public void setScalable(boolean scalable) {

        isScalable = scalable;
    }

    public void dismiss() {

    }

    public void setOnViewEventListener(OnViewEventListener listener) {

        this.eventListener = listener;
    }

    private int getEventIndex(MotionEvent event) {

        return (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }

    private void fireOnScaleStartEvent(PointF mid) {

        if (isScalable) {
            this.eventListener.onBmpViewStartScale(mid);
        }
    }

    private void fireOnScaleStartEvent(float scale, float midX, float midY) {

        if (isScalable) {
            this.eventListener.onBmpViewZoom(scale, midX, midY);
        }
    }

    private boolean isMultiTouchDistanceIncrease(float newDist) {

        return (newDist - oldDist) > 100;
    }

    private float getNewMultiTouchScale(float NewDist) {

        return NewDist / oldDist;
    }

    private void saveOriginalMultiTouchDistance(float dis) {

        this.oldDist = dis;
    }

    private int getTouchMotionMode() {

        return this.mode;
    }

    private void setTouchMotionMode(int mode) {

        this.mode = mode;
    }

    private void saveStartPosition(float x, float y) {

        start.set(x, y);
    }

    private void saveScrollOldPosition(float x, float y) {

        oldX = x;
        oldY = y;
    }

    private boolean isMultiTouchPoint(MotionEvent event) {

        int count = event.getPointerCount();
        return count > 1;
    }

    private boolean isSecondPointDown(MotionEvent event) {

        int index = getEventIndex(event);
        return index == 1;
    }

    @Override public boolean onTouch(View v, MotionEvent event) {

        int index     = event.getActionIndex();
        int pointerId = event.getPointerId(index);

        if (!this.isReady) {
            return true;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                this.eventListener.onBmpViewStartTouch(event.getX(), event.getY());
                this.eventListener.onBmpViewStartScroll(mid);

                saveStartPosition(event.getX(), event.getY());
                saveScrollOldPosition(event.getX(), event.getY());

                velocityHelp.reset(event);
                setTouchMotionMode(SCROLL);

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (isSecondPointDown(event)) {

                    this.eventListener
                            .onBmpViewStartPointTouch(event.getX(getEventIndex(event)), event
                                    .getY(getEventIndex(event)), spacing(event));

                    saveOriginalMultiTouchDistance(spacing(event));
                    midPoint(mid, event);

                    if (oldDist > 500) {
                        fireOnScaleStartEvent(mid);
                        setTouchMotionMode(ZOOM);
                    } else {
                        setTouchMotionMode(DRAG);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                if (velocityHelp.hasVelocityMove()) {
                    this.eventListener.onVelocityEvent(velocityHelp.getVelocityMove());
                }

                if (touchTabHelp.getIsDoubleTagRelease(pointerId)) {
                    this.eventListener.onDoubleTab();
                }

                this.eventListener.onBmpViewTouchRelease();
                setTouchMotionMode(NONE);

                break;
            case MotionEvent.ACTION_MOVE:

                if (!velocityHelp.isVelocityMoveAvailable()) {
                    return true;
                }

                velocityHelp.setVelocityMove(event, pointerId);

                if (getTouchMotionMode() == DRAG) {
                    this.eventListener
                            .onBmpViewDrag(event.getX() - start.x, event.getY() - start.y);

                    if (isMultiTouchPoint(event) && isMultiTouchDistanceIncrease(spacing(event))) {
                        fireOnScaleStartEvent(mid);
                        setTouchMotionMode(ZOOM);
                    }
                } else if (getTouchMotionMode() == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        fireOnScaleStartEvent(getNewMultiTouchScale(newDist), mid.x, mid.y);
                    }
                } else if (mode == SCROLL) {
                    handleScrollEvent(event);
                }

                break;
        }
        return true;
    }

    private void handleScrollEvent(MotionEvent event) {

        String NewDirection;
        int    move_step = 3000 / (12 * (4 + 20));
        float  newX      = event.getX();
        float  newY      = event.getY();
        float  dx        = newX - oldX;

        float distance = (float) Math
                .sqrt((newX - oldX) * (newX - oldX) + (newY - oldY) * (newY - oldY));
        if (dx > 0) {
            NewDirection = "right";
        } else {
            NewDirection = "left";
        }
        if (!direction.equals(NewDirection)) {
            direction = NewDirection;
            oldX = newX;
            oldY = newY;
            return;
        }
        if (distance >= move_step) {
            oldX = newX;
            oldY = newY;
            if (direction.equals("right")) {
                this.eventListener.onBmpViewScrollRight();
            } else {
                this.eventListener.onBmpViewScrollLeft();
            }
        }
    }

    private float spacing(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {

        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public interface OnViewEventListener {
        void onBmpViewStartTouch(float posX, float posY);

        void onBmpViewStartPointTouch(float posX, float posY, float distance);

        void onBmpViewStartScale(PointF mid);

        void onBmpViewStartScroll(PointF mid);

        void onBmpViewDrag(float DisX, float DisY);

        void onBmpViewZoom(float scale, float midX, float midY);

        void onBmpViewScrollRight();

        void onBmpViewScrollLeft();

        void onBmpViewTouchRelease();

        void onVelocityEvent(float Velocity);

        void onDoubleTab();
    }

    private class TouchTabHelp {
        int DOUBLE_PRESS_INTERVAL = 300;

        long lastRelease = SystemClock.uptimeMillis();

        int lastId = 99;

        boolean getIsDoubleTagRelease(int pointId) {

            long    newRelease = SystemClock.uptimeMillis();
            boolean bDoubleTag = false;

            if (lastId != pointId) {
                reset(newRelease, pointId);
                return false;
            }

            if (newRelease - lastRelease <= DOUBLE_PRESS_INTERVAL) {
                bDoubleTag = true;
                reset(newRelease, pointId);
            }

            reset(newRelease, pointId);

            return bDoubleTag;
        }

        void reset(long time, int pointId) {

            lastRelease = time;
            lastId = pointId;
        }
    }

    private class VelocityHelp {
        private VelocityTracker speedTracker;

        private int velocityEventCount;

        private float moveVelocity;

        private float lastVelocity;

        void reset(MotionEvent event) {

            velocityEventCount = 0;
            moveVelocity = 0;
            lastVelocity = 0;

            if (speedTracker == null) {
                speedTracker = VelocityTracker.obtain();
            } else {
                speedTracker.clear();
            }
            speedTracker.addMovement(event);
        }

        boolean hasVelocityMove() {

            return velocityEventCount >= 1;
        }

        float getVelocityMove() {

            return moveVelocity / velocityEventCount;
        }

        boolean isVelocityMoveAvailable() {

            return speedTracker != null;
        }

        void setVelocityMove(MotionEvent event, int pointerId) {

            speedTracker.addMovement(event);
            speedTracker.computeCurrentVelocity(1000);

            float velocity = VelocityTrackerCompat.getXVelocity(speedTracker, pointerId);
            if ((lastVelocity > 0 && velocity < 0) || (lastVelocity < 0 && velocity > 0)) {
                lastVelocity = 0;
                velocityEventCount = 0;
                moveVelocity = 0;
            }

            lastVelocity = velocity;
            moveVelocity += velocity;
            velocityEventCount++;
        }
    }
}
