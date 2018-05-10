package com.leedian.klozr.base.baseView;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.leedian.klozr.utils.viewUtils.ActivityUIUpdate;

/**
 * MvpView for Mvc View in this App
 *
 * This MvpView is used as Base MvpView .
 *
 * @author Franco
 */
public interface BaseMvpView
        extends MvpView
{
    void updateViewInMainThread(ActivityUIUpdate viewUpdate);

    void navigateToAppHomeActivityCauseReAuth();

    void navigateToAppHomeActivity();

    void displayErrorResponse(String msg);

    void displayTipMassage(String msg);

    void reactiveAsInvalidView();

    void showLoadingDialog();

    void hideLoadingDialog();
}
