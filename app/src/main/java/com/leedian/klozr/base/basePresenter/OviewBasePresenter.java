package com.leedian.klozr.base.basePresenter;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import com.leedian.klozr.base.baseView.BaseMvpView;
import com.leedian.klozr.presenter.presenterImp.task.taskImp.UserManagerImp;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.UserManager;
import com.leedian.klozr.presenter.presenterInterface.BasePresenter;
import com.leedian.klozr.utils.exception.AuthException;
import com.leedian.klozr.utils.exception.DomainException;
import com.leedian.klozr.utils.exception.DomainExceptionHandler;
import com.leedian.klozr.utils.viewUtils.ActivityUIUpdate;

/**
 * BasePresenter for Mvc BasePresenter in this App
 * <p>
 * This BasePresenter is used as Base Presenter .
 *
 * @author Franco
 */
public class OviewBasePresenter<V extends BaseMvpView>
        extends MvpBasePresenter<V>
        implements BasePresenter<V> {

    protected UserManager userManager = new UserManagerImp();

    /**
     * An event after user confirm to logout
     * set user manager as logout
     **/
    public void onClickConfirmLogout() {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        userManager.setUserLogout();
        view.navigateToAppHomeActivity();
    }

    /**
     * fire this function from presenter to create a UI thread to update View
     *
     * @param runnable a runnable instance
     **/
    protected void startUpdateUiThread(ActivityUIUpdate runnable) {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        view.updateViewInMainThread(runnable);
    }

    /**
     * fire this function from presenter to show a Loading dialog
     **/
    public void showLoadingDialog() {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        ActivityUIUpdate<BaseMvpView> uiUpdate = new ActivityUIUpdate<>(ActivityUIUpdate.UI_UPDATE_SHOW_LOADING,
                view);
        view.updateViewInMainThread(uiUpdate);
    }

    /**
     * fire this function from presenter to hide a Loading dialog
     **/
    public void hideLoadingDialog() {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        ActivityUIUpdate<BaseMvpView> uiUpdate = new ActivityUIUpdate<>(ActivityUIUpdate.UI_UPDATE_HIDE_LOADING,
                view);
        view.updateViewInMainThread(uiUpdate);
    }

    /**
     * show massage to view , message with red background
     *
     * @param msg show massage
     **/
    protected void showUiMessage(String msg) {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        ActivityUIUpdate uiUpdate = new ActivityUIUpdate<>(ActivityUIUpdate.UI_UPDATE_SHOW_ERROR_STRING,
                view).setShowMsg(msg);
        view.updateViewInMainThread(uiUpdate);
    }

    /**
     * show a tip massage to view , message with green background
     *
     * @param msg show massage
     **/
    public void showTipMassage(String msg) {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        ActivityUIUpdate uiUpdate = new ActivityUIUpdate<>(ActivityUIUpdate.UI_SHOW_TIP_MASSAGE,
                view).setShowMsg(msg);
        view.updateViewInMainThread(uiUpdate);
    }

    /**
     * handle exception within presenter
     *
     * @param e Throwable
     **/
    protected void HandleOnException(Throwable e) {

        if (e instanceof DomainException) {
            onDomainException(e);
        }
    }

    /**
     * handle exception within presenter
     *
     * @param e Throwable
     **/
    protected void HandleOnException(Throwable e, DomainExceptionHandler handler) {

        if (e instanceof DomainException) {
            onDomainException(e, handler);
        }
    }

    /**
     * handle Domain exception in base class if logout  action is need ,
     * then do logout and navigate to guest home page
     *
     * @param e Throwable
     **/
    protected void onDomainException(Throwable e) {

        int action;
        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        if (e instanceof AuthException) {
            AuthException domainException = (AuthException) e;
            action = domainException.getExceptionActionCode();
            if (action == DomainException.EXCEPTION_ACTION_RESTART_APP) {
                userManager.setUserLogout();
                view.navigateToAppHomeActivityCauseReAuth();
            }
        }
    }

    /**
     * handle Domain exception in base class if logout  action is need ,
     * then do logout and navigate to guest home page
     *
     * @param e Throwable
     * @param handler a change giving to child presenter to handle other reaction
     **/
    private void onDomainException(Throwable e, DomainExceptionHandler handler) {

        int action;
        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        if (e instanceof AuthException) {
            AuthException domainException = (AuthException) e;
            action = domainException.getExceptionActionCode();
            if (action == DomainException.EXCEPTION_ACTION_RESTART_APP) {
                userManager.setUserLogout();
                view.navigateToAppHomeActivityCauseReAuth();
            }
        }
        handler.handleEvent((Exception) e);
    }

    /**
     * get if user has login
     *
     **/
    @Override
    public boolean getIsUserLogin() {

        return userManager.isUserLogin();
    }
}
