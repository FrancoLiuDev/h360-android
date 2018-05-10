package com.leedian.klozr.view.viewInterface;
import com.leedian.klozr.base.baseView.BaseMvpView;

/**
 * ViewUserLoginMvp
 *
 * @author Franco
 */
public interface ViewUserLoginMvp
        extends BaseMvpView
{
    void showLoginForm();

    void showFailedError(String type);

    void navigateToListView();
}
