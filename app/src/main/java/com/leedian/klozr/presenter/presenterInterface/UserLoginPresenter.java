package com.leedian.klozr.presenter.presenterInterface;

import com.leedian.klozr.view.viewInterface.ViewUserLoginMvp;

/**
 * UserLoginPresenter
 *
 * @author Franco
 */
public interface UserLoginPresenter extends BasePresenter<ViewUserLoginMvp> {

    void doUserLogin(String name, String password);
}
