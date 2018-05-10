package com.leedian.klozr.utils.viewUtils.tools;
import android.os.SystemClock;

import static java.lang.Math.abs;

/**
 * TouchVelocityThread
 *
 * @author Franco
 */
public class TouchVelocityThread
        extends Thread
{
    private static final float minStartSpeed = 500;
    private static final int   RateUnit      = 1850;
    private static       long  start_time    = 0;
    private static TouchVelocityThread velocityThread;
    private        float               Velocity;
    private        boolean             isContinues;
    private        boolean             isAutoRote;
    private        ActionVelocityEvent event;

    synchronized static public void threadStart(float velocity, ActionVelocityEvent event, boolean auto) {

        if (!checkDelay()) { return; }

        threadRelease();
        velocityThread = new TouchVelocityThread();
        velocityThread.setVelocity(velocity);
        velocityThread.setAutoRote(auto);
        velocityThread.setEvent(event);
        velocityThread.start();
    }

    static public void threadRelease() {

        if (velocityThread != null) {
            velocityThread.setContinues(false);

            while (velocityThread.getIsContinues()) {
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            velocityThread = null;
        }
    }

    private static boolean checkDelay() {

        long tick_count = SystemClock.uptimeMillis();

        if ((tick_count - start_time) > 300) {

            start_time = tick_count;
            return true;
        }

        return false;
    }

    public void setEvent(ActionVelocityEvent event) {

        this.event = event;
    }

    private void setAutoRote(boolean autoRote) {

        isAutoRote = autoRote;
    }

    private float getVelocity() {

        return Velocity;
    }

    private void setVelocity(float velocity) {

        Velocity = velocity;
    }

    private boolean getIsContinues() {

        return isContinues;
    }

    private void setContinues(boolean continues) {

        isContinues = continues;
    }

    @Override
    public void run() {

        float absVelocity = abs(getVelocity());

        if (absVelocity < minStartSpeed) { return; }

        isContinues = true;

        while (isContinues) {
            try {
                loopThreadLoop();
            } catch (InterruptedException e) {
                isContinues = false;
                e.printStackTrace();
            }
        }

        System.out.println("sleep....going to not runnable");
    }

    private void loopThreadLoop() throws InterruptedException {

        float absVelocity = abs(Velocity);

        if (absVelocity > 1600) {
            Thread.sleep(10);
            absVelocity = 1600;
        }

        if (absVelocity == 0) {
            stopLoop();
            return;
        }

        long sleep = ((long) (RateUnit / absVelocity) * 10);

        if (sleep > 0) { Thread.sleep((long) (RateUnit / absVelocity) * 10); }

        if (Velocity > 0) { event.onNextStepRight(); } else { event.onNextStepLeft(); }

        absVelocity -= 5;

        if (isAutoRote && (absVelocity < 200)) { absVelocity = 200; } else if (absVelocity < 200) {
            stopLoop();
        }

        if (Velocity > 0) { Velocity = absVelocity; } else { Velocity = 0 - absVelocity; }
    }

    private void stopLoop() throws InterruptedException {

        isContinues = false;
    }

    public interface ActionVelocityEvent {
        void onNextStepRight();

        void onNextStepLeft();
    }
}
