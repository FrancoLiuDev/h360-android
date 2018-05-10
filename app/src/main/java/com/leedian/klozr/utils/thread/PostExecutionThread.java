package com.leedian.klozr.utils.thread;
import rx.Scheduler;

/**
 * PostExecutionThread
 *
 * @author Franco
 */
public interface PostExecutionThread {
    Scheduler getScheduler();
}
