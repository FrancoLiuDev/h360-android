package com.leedian.klozr.presenter.presenterImp;

import com.leedian.klozr.AppManager;
import com.leedian.klozr.base.basePresenter.OviewBasePresenter;
import com.leedian.klozr.base.baseView.BaseMvpView;
import com.leedian.klozr.model.dataOut.OviewContentModel;
import com.leedian.klozr.presenter.presenterImp.task.taskImp.ContentInfoManagerImp;
import com.leedian.klozr.presenter.presenterImp.task.taskImp.UserManagerImp;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.ContentInfoManager;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.UserManager;
import com.leedian.klozr.presenter.presenterInterface.ContentScanPresenter;
import com.leedian.klozr.utils.Validator;
import com.leedian.klozr.utils.subscriptHelp.ObservableActionIdentifier;
import com.leedian.klozr.utils.subscriptHelp.ObservableDownloadAction;
import com.leedian.klozr.utils.subscriptHelp.ObservableOviewInfoAction;
import com.leedian.klozr.utils.thread.JobExecutor;
import com.leedian.klozr.utils.thread.TaskExecutor;
import com.leedian.klozr.utils.thread.UIThread;
import com.leedian.klozr.utils.viewUtils.ActivityUIUpdate;
import com.leedian.klozr.view.viewInterface.ViewScanMvp;

import rx.Subscriber;

/**
 * ContentScanPresenterImp
 *
 * @author Franco
 */
public class ContentScanPresenterImp
        extends OviewBasePresenter<ViewScanMvp>
        implements ContentScanPresenter {
    private UserManager userManageTask = new UserManagerImp();

    /**
     * event on view scan a QR code
     **/
    @Override
    public void onScanContentCodeData(final String ScanData) {

        String zipKey;

        String[] Data = new String[2];
        if (!Validator.isMatchScanData(ScanData, Data)) {
            uiViewContinueScan();
            return;
        }

        zipKey = Data[1];
        AppManager.setPrefAddress(Data[0]);
        ContentInfoManager infoManager = new ContentInfoManagerImp(zipKey);

        Subscriber<ObservableActionIdentifier<OviewContentModel>> subscriber = new Subscriber<ObservableActionIdentifier<OviewContentModel>>() {
            @Override
            public void onStart() {

                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

                uiViewContinueScan();
            }

            @Override
            public void onNext(ObservableActionIdentifier<OviewContentModel> action) {

                doNextAction(action);
                request(1);
            }
        };

        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), infoManager
                .executeRequestContentInfo());
        taskCase.execute(subscriber);
    }

    /**
     * event on user icon clicked
     **/
    @Override
    public void onUserIconClicked() {

        if (!userManageTask.isUserLogin()) {
            uiViewNavigateToLoginView();
        }
    }

    private void doNextAction(ObservableActionIdentifier<OviewContentModel> action) {

        if (action.getAction().equals(ObservableDownloadAction.ACTION_START_DOWNLOAD)) {
            uiViewShowProgressDialog();
        }
        if (action.getAction().equals(ObservableDownloadAction.ACTION_FILE_DOWNLOADING)) {
            uiViewSetDownloadProgress(Integer.valueOf(action.getContent()));
        }
        if (action.getAction().equals(ObservableOviewInfoAction.ACTION_OVIEW_INFO_ONLINE)) {
            uiViewSetContentInformation(false, action.getObject());
        }
        if (action.getAction().equals(ObservableOviewInfoAction.ACTION_OVIEW_INFO_OFFLINE)) {
            uiViewSetContentInformation(true, action.getObject());
        }
    }

    private void uiViewNavigateToLoginView() {

        ViewScanMvp view = getView();
        if (view == null) {
            return;
        }

        ContentScanActivityUIUpdate uiUpdate = new ContentScanActivityUIUpdate(ContentScanActivityUIUpdate.UI_NAVIGATE_TO_LOGIN_VIEW, getView());
        getView().updateViewInMainThread(uiUpdate);
    }

    private void uiViewContinueScan() {

        ViewScanMvp view = getView();
        if (view == null) {
            return;
        }

        ContentScanActivityUIUpdate uiUpdate = new ContentScanActivityUIUpdate(ContentScanActivityUIUpdate.UI_RE_START_SCAN, getView());
        getView().updateViewInMainThread(uiUpdate);
    }

    private void uiViewSetDownloadProgress(int percent) {

        ViewScanMvp view = getView();
        if (view == null) {
            return;
        }

        ContentScanActivityUIUpdate uiUpdate = new ContentScanActivityUIUpdate(ContentScanActivityUIUpdate.UI_SET_DOWNLOAD_DIALOG_PERCENT, getView())
                .setPercent(percent);
        getView().updateViewInMainThread(uiUpdate);
    }

    private void uiViewSetContentInformation(boolean isOnline, OviewContentModel info) {

        ViewScanMvp view = getView();
        if (view == null) {
            return;
        }

        ContentScanActivityUIUpdate uiUpdate = new ContentScanActivityUIUpdate(ContentScanActivityUIUpdate.UI_UPDATE_LOAD_INFO, getView());
        uiUpdate.setOnlineInfo(isOnline);
        uiUpdate.setInfo(info);
        getView().updateViewInMainThread(uiUpdate);
    }

    private void uiViewShowProgressDialog() {

        ViewScanMvp view = getView();
        if (view == null) {
            return;
        }

        ContentScanActivityUIUpdate uiUpdate = new ContentScanActivityUIUpdate(ContentScanActivityUIUpdate.UI_SHOW_DOWNLOAD_DIALOG, getView());
        getView().updateViewInMainThread(uiUpdate);
    }

    private class ContentScanActivityUIUpdate
            extends ActivityUIUpdate<ViewScanMvp> {
        final static int UI_UPDATE_NAVIGATE_OVIEW = 0;

        final static int UI_NAVIGATE_TO_LOGIN_VIEW = 2;

        final static int UI_RE_START_SCAN = 3;

        final static int UI_SHOW_DOWNLOAD_DIALOG = 4;

        final static int UI_DISMISS_DOWNLOAD_DIALOG = 5;

        final static int UI_SET_DOWNLOAD_DIALOG_PERCENT = 6;

        final static int UI_UPDATE_LOAD_INFO = 10;

        String zipKey;

        int percent;

        boolean isOnlineInfo;

        OviewContentModel info;

        ContentScanActivityUIUpdate(int UpdateId, ViewScanMvp view) {

            super(UpdateId, view);
        }

        ContentScanActivityUIUpdate setPercent(int percent) {

            this.percent = percent;
            return this;
        }

        ContentScanActivityUIUpdate setZipKey(String zipKey) {

            this.zipKey = zipKey;
            return this;
        }

        OviewContentModel getInfo() {

            return info;
        }

        void setInfo(OviewContentModel info) {

            this.info = info;
        }

        void setOnlineInfo(boolean onlineInfo) {

            isOnlineInfo = onlineInfo;
        }

        @Override
        protected void onUpdateEventChild(BaseMvpView view, int event) {

            ViewScanMvp scanView = (ViewScanMvp) view;

            switch (event) {
                case UI_UPDATE_NAVIGATE_OVIEW:
                    scanView.navigateToOviewContentView(zipKey);
                    break;
                case UI_RE_START_SCAN:
                    scanView.RestartScan();
                    break;
                case UI_NAVIGATE_TO_LOGIN_VIEW:
                    scanView.navigateToLoginView();
                    break;
                case UI_SHOW_DOWNLOAD_DIALOG:
                    scanView.showProgressDialog();
                    break;
                case UI_DISMISS_DOWNLOAD_DIALOG:
                    scanView.dismissProgressDialog();
                    break;
                case UI_SET_DOWNLOAD_DIALOG_PERCENT:
                    scanView.setDownloadProgress(percent);
                    break;
                case UI_UPDATE_LOAD_INFO:
                    scanView.setOviewInformation(isOnlineInfo, info);
                    break;
            }
        }
    }
}
