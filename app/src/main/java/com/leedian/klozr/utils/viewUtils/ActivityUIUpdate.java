package com.leedian.klozr.utils.viewUtils;
import android.support.annotation.Nullable;

import com.leedian.klozr.base.baseView.BaseMvpView;

import java.lang.ref.WeakReference;

/**
 * ActivityUIUpdate
 *
 * @author Franco
 */
public class ActivityUIUpdate<V extends BaseMvpView>
        implements Runnable
{
    final static public int UI_DOMAIN_ERROR             = 50;
    final static public int UI_UPDATE_SHOW_ERROR_STRING = UI_DOMAIN_ERROR + 0;
    final static public int UI_UPDATE_ON_INVALID_VIEW   = UI_DOMAIN_ERROR + 1;
    final static public int UI_UPDATE_SHOW_LOADING      = UI_DOMAIN_ERROR + 5;
    final static public int UI_UPDATE_HIDE_LOADING      = UI_DOMAIN_ERROR + 6;
    final static public int UI_SHOW_TIP_MASSAGE         = UI_DOMAIN_ERROR + 7;
    private int              UpdateId;
    private String           msg;
    private WeakReference<V> viewRef;
    onUpdateEvent event = new onUpdateEvent() {
        @Override
        public void onUpdateEvent(int event) {

            BaseMvpView view = getView();
            if (view == null) { return; }

            switch (event) {
                case UI_UPDATE_SHOW_ERROR_STRING:
                    view.displayErrorResponse(msg);
                    break;
                case UI_SHOW_TIP_MASSAGE:
                    view.displayTipMassage(msg);
                    break;
                case UI_UPDATE_ON_INVALID_VIEW:
                    view.reactiveAsInvalidView();
                    break;
                case UI_UPDATE_SHOW_LOADING:
                    view.showLoadingDialog();
                    break;
                case UI_UPDATE_HIDE_LOADING:
                    view.hideLoadingDialog();
                    break;
            }
            onUpdateEventChild(view, event);
        }
    };

    public ActivityUIUpdate(int UpdateId, V view) {

        this.UpdateId = UpdateId;
        viewRef = new WeakReference<V>(view);
    }

    public ActivityUIUpdate setShowMsg(String msg) {

        this.msg = msg;
        return this;
    }

    @Nullable
    public V getView() {

        return viewRef == null ?
                null :
                viewRef.get();
    }

    protected void onUpdateEventChild(BaseMvpView view, int event) {

    }

    public void setEventListener(onUpdateEvent event) {

        this.event = event;
    }

    @Override
    public void run() {

        event.onUpdateEvent(UpdateId);
    }

    public interface onUpdateEvent {
        void onUpdateEvent(int event);
    }
}
