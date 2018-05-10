package com.leedian.klozr.utils.exception;
/**
 * DomainExceptionHandler
 *
 * @author Franco
 */
public class DomainExceptionHandler {

    private ExceptionAuthEvent    authEvent;
    private ExceptionNetworkEvent networkEvent;
    private ExceptionFileEvent    fileEvent;

    public void setFileEvent(ExceptionFileEvent fileEvent) {

        this.fileEvent = fileEvent;
    }

    /**
     * set Auth Event
     *
     * @param authEvent
     */
    public void setAuthEvent(ExceptionAuthEvent authEvent) {

        this.authEvent = authEvent;
    }

    /**
     * set Network Event
     * @param networkEvent
     */
    public void setNetworkEvent(ExceptionNetworkEvent networkEvent) {

        this.networkEvent = networkEvent;
    }

    /**
     * handle Event
     *
     * @param e
     */
    public void handleEvent(Exception e) {

        if (e instanceof AuthException) {
            if (this.authEvent != null) { this.authEvent.onExceptionAuth(e); }
        }

        if (e instanceof NetworkConnectionException) {
            if (this.networkEvent != null) { this.networkEvent.onExceptionNetWork(e); }
        }

        if (e instanceof FileException) {
            if (this.fileEvent != null) { this.fileEvent.onExceptionFile(e); }
        }
    }


    public interface ExceptionAuthEvent {
        void onExceptionAuth(Exception exception);
    }

    public interface ExceptionNetworkEvent {
        void onExceptionNetWork(Exception exception);
    }

    public interface ExceptionFileEvent {
        void onExceptionFile(Exception exception);
    }
}
