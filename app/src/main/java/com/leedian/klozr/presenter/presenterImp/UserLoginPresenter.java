package com.leedian.klozr.presenter.presenterImp;
import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.base.basePresenter.OviewBasePresenter;
import com.leedian.klozr.base.baseView.BaseMvpView;
import com.leedian.klozr.model.dataIn.AuthCredentials;
import com.leedian.klozr.presenter.presenterImp.task.taskImp.UserManagerImp;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.UserManager;
import com.leedian.klozr.utils.exception.AuthException;
import com.leedian.klozr.utils.exception.DomainException;
import com.leedian.klozr.utils.subscriptHelp.ObservableActionIdentifier;
import com.leedian.klozr.utils.thread.JobExecutor;
import com.leedian.klozr.utils.thread.TaskExecutor;
import com.leedian.klozr.utils.thread.UIThread;
import com.leedian.klozr.utils.viewUtils.ActivityUIUpdate;
import com.leedian.klozr.view.viewInterface.ViewUserLoginMvp;

import rx.Subscriber;

/**
 * UserLoginPresenter
 *
 * @author Franco
 */
public class UserLoginPresenter
        extends OviewBasePresenter<ViewUserLoginMvp>
        implements com.leedian.klozr.presenter.presenterInterface.UserLoginPresenter
{
    private UserManager userManageTask = new UserManagerImp();

    /**
     * process user login
     *
     * @param name     Somebody's name.
     * @param password Somebody's password.
     **/
    @Override public void doUserLogin(String name, String password) {

        final AuthCredentials credentials = new AuthCredentials(name, password);
        if (name.length() < 1) {
            showUiMessage(AppResource.getString(R.string.please_input_user_name));
            uiViewShowError("name");
            return;
        }
        if (password.length() < 1) {
            showUiMessage(AppResource.getString(R.string.please_input_user_password));
            uiViewShowError("password");
            return;
        }
        Subscriber<ObservableActionIdentifier> subscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override public void onStart() {

                showLoadingDialog();
                request(1);
            }

            @Override public void onCompleted() {

                setLoginSuccess();
                hideLoadingDialog();
            }

            @Override public void onError(Throwable e) {

                hideLoadingDialog();
                HandleOnException(e);
            }

            @Override public void onNext(ObservableActionIdentifier action) {

                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), userManageTask
                .executeRequestUserLogin(credentials));
        taskCase.execute(subscriber);
    }

    /**
     * handle on domain exception
     *
     * @param e domain Throwable
     **/
    @Override protected void onDomainException(Throwable e) {

        DomainException exception = (DomainException) e;

        if (DomainException.isExceptionAuth(exception)) {
            AuthException exceptionAuth = (AuthException) exception;

            if (exceptionAuth.isAuthFailName()) {
                uiViewShowError("name");
            }

            if (exceptionAuth.isAuthFailPassword()) {
                uiViewShowError("password");
            }
        }

        showUiMessage(exception.getMessage());
        super.onDomainException(e);
    }

    /**
     * fire event when login in success
     **/
    private void setLoginSuccess() {

        ViewUserLoginMvp view = this.getView();
        if (view == null) {
            return;
        }

        OVLoginActivityUIUpdate uiUpdate = new OVLoginActivityUIUpdate(OVLoginActivityUIUpdate.UI_UPDATE_NAVIGATE_OVIEW_LIST, view);
        view.updateViewInMainThread(uiUpdate);
    }

    private void uiViewShowError(String type) {

        ViewUserLoginMvp view = this.getView();
        if (view == null) {
            return;
        }

        OVLoginActivityUIUpdate uiUpdate = new OVLoginActivityUIUpdate(OVLoginActivityUIUpdate.UI_UPDATE_SHOW_ERROR, view);
        uiUpdate.setFieldType(type);
        view.updateViewInMainThread(uiUpdate);
    }

    public class OVLoginActivityUIUpdate
            extends ActivityUIUpdate<ViewUserLoginMvp>
    {
        final static int UI_UPDATE_NAVIGATE_OVIEW_LIST = 0;

        final static int UI_UPDATE_SHOW_ERROR = 1;

        String fieldType = "";

        OVLoginActivityUIUpdate(int UpdateId, ViewUserLoginMvp view) {

            super(UpdateId, view);
        }

        void setFieldType(String fieldType) {

            this.fieldType = fieldType;
        }

        @Override protected void onUpdateEventChild(BaseMvpView view, int event) {

            ViewUserLoginMvp loginView = (ViewUserLoginMvp) view;

            switch (event) {
                case UI_UPDATE_NAVIGATE_OVIEW_LIST:
                    loginView.navigateToListView();
                    break;
                case UI_UPDATE_SHOW_ERROR:
                    loginView.showFailedError(fieldType);
                    break;
            }
        }
    }
}
