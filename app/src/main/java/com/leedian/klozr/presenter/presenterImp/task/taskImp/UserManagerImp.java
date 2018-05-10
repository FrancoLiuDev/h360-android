package com.leedian.klozr.presenter.presenterImp.task.taskImp;
import com.leedian.klozr.model.cache.cacheImp.UserLoginCacheImp;
import com.leedian.klozr.model.cache.cacheInterface.UserLoginCache;
import com.leedian.klozr.model.dataIn.AuthCredentials;
import com.leedian.klozr.model.dataOut.UserInfoModel;
import com.leedian.klozr.model.restapi.OviewUserApi;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.UserManager;
import com.leedian.klozr.utils.exception.DomainException;
import com.leedian.klozr.utils.subscriptHelp.ObservableActionIdentifier;

import rx.Observable;
import rx.Subscriber;

/**
 * UserManagerImp
 *
 * @author Franco
 */
public class UserManagerImp
        implements UserManager
{
    private UserLoginCache userCache = new UserLoginCacheImp();

    @Override
    public boolean isUserLogin() {

        return userCache.isCached();
    }

    @Override
    public void setUserLogin() {

    }

    @Override
    public void setUserLogout() {

        userCache.cleanCache();
    }

    @Override
    public UserInfoModel getUserLoginInfoData() {

        return userCache.getCache();
    }

    @Override
    public void setUserLoginInfoData(UserInfoModel info) {

        userCache.writeCache(info);
    }

    @Override
    public Observable<ObservableActionIdentifier> executeRequestUserLogin(final AuthCredentials Auth) {

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {

                        boolean      isSuccess;
                        OviewUserApi HttpApi = new OviewUserApi();

                        try {
                            isSuccess = HttpApi.requestSignIn(Auth);

                            if (!isSuccess) {
                                Exception exception = DomainException
                                        .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                                .getMinor_code());
                                subscriber.onError(exception);
                                return;
                            }

                            UserInfoModel model = HttpApi.getUserModel();
                            UserManagerImp.this.setUserLoginInfoData(model);
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(DomainException.getDomainException(e));
                        }
                    }
                });
    }
}
