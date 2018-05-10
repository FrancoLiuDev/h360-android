package com.leedian.klozr.utils.subscriptHelp;
/**
 * ObservableActionIdentifier
 *
 * @author Franco
 */
public class ObservableActionIdentifier<T> {
    private String action;
    private String content;
    private T      Object;

    public ObservableActionIdentifier(String action, String content) {

        this.action = action;
        this.content = content;
    }

    public ObservableActionIdentifier(String action, String content, T object) {

        this.action = action;
        this.content = content;
        this.Object = object;
    }

    public String getAction() {

        return action;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public T getObject() {

        return Object;
    }

    public void setObject(T object) {

        Object = object;
    }
}
