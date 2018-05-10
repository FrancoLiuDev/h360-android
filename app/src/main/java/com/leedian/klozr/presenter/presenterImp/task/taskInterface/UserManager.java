package com.leedian.klozr.presenter.presenterImp.task.taskInterface;

import com.leedian.klozr.model.dataIn.AuthCredentials;
import com.leedian.klozr.model.dataOut.UserInfoModel;
import com.leedian.klozr.utils.subscriptHelp.ObservableActionIdentifier;

import rx.Observable;

/**
 * Created by franco on 19/11/2016.
 */
public interface UserManager {

    boolean isUserLogin();

    void setUserLogin();

    void setUserLogout();

    UserInfoModel getUserLoginInfoData();

    void setUserLoginInfoData(UserInfoModel Info);

    Observable<ObservableActionIdentifier> executeRequestUserLogin(AuthCredentials Auth);
}
