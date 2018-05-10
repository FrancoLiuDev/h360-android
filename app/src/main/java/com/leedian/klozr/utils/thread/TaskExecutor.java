package com.leedian.klozr.utils.thread;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * TaskExecutor
 *
 * @author Franco
 */
public class TaskExecutor {
    private final ThreadExecutor      threadExecutor;
    private final PostExecutionThread postExecutionThread;
    private Subscription subscription = Subscriptions.empty();
    private Observable   observable   = Observable.empty();

    public TaskExecutor(
            ThreadExecutor threadExecutor,
            PostExecutionThread postExecutionThread, Observable observable) {

        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
        this.observable = observable;
    }

    public void execute(Subscriber useCaseSubscriber) {

        this.subscription = this.observable
                .subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler())
                .subscribe(useCaseSubscriber);
    }

    public void unsubscribe() {

        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
