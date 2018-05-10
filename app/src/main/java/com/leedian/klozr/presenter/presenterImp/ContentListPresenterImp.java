package com.leedian.klozr.presenter.presenterImp;
import com.leedian.klozr.base.basePresenter.OviewBasePresenter;
import com.leedian.klozr.base.baseView.BaseMvpView;
import com.leedian.klozr.model.dataOut.OviewListModel;
import com.leedian.klozr.presenter.presenterImp.task.taskImp.ContentListManagerImp;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.ContentListManager;
import com.leedian.klozr.presenter.presenterInterface.ContentListPresenter;
import com.leedian.klozr.utils.thread.JobExecutor;
import com.leedian.klozr.utils.thread.TaskExecutor;
import com.leedian.klozr.utils.thread.UIThread;
import com.leedian.klozr.utils.viewUtils.ActivityUIUpdate;
import com.leedian.klozr.view.viewInterface.ViewContentListMvp;

import java.util.List;

import rx.Subscriber;

/**
 * ContentListPresenterImp
 *
 * @author Franco
 */
public class ContentListPresenterImp
        extends OviewBasePresenter<ViewContentListMvp>
        implements ContentListPresenter
{
    private ContentListManager oviewListManager = new ContentListManagerImp();

    public ContentListPresenterImp() {

    }

    /**
     * create a task to get list data
     **/
    private void executeRequestContentList(boolean isLoadMore) {

        Subscriber<List<OviewListModel>> subscriber = new Subscriber<List<OviewListModel>>() {
            @Override public void onCompleted() {

            }

            @Override public void onNext(List<OviewListModel> models) {

                uiViewUpdateViewDataSource(models);
            }

            @Override public void onError(Throwable e) {

                List<OviewListModel> models = oviewListManager.retrieveContentCachedList();
                uiViewUpdateViewDataSource(models);
                HandleOnException(e);
            }
        };

        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), oviewListManager
                .executeRequestOviewList(isLoadMore));

        taskCase.execute(subscriber);
    }

    @Override protected void onDomainException(Throwable e) {

        // refactor
        Exception        exception = (Exception) e;
        ListViewUIUpdate uiUpdate  = new ListViewUIUpdate(ListViewUIUpdate.UI_UPDATE_LIST＿ERROR, getView());
        uiUpdate.setMsg(exception.getMessage());
        getView().updateViewInMainThread(uiUpdate);
        super.onDomainException(e);
    }

    /**
     * on view fetch a  list
     **/
    @Override public void onFetchListItems() {

        executeRequestContentList(false);
    }

    /**
     * on view load more data
     **/
    @Override public void onLoadMoreListItems() {

        executeRequestContentList(true);
    }

    /**
     * on view list item clicked
     **/
    @Override public void onClickListItem(int position) {

        // refactor
        OviewListModel item = oviewListManager.retrieveContentListItem(position);

        if (item == null) {
            return;
        }

        getView().showClickItemOption(position, item);
    }

    /**
     * on view item delete clicked
     **/
    @Override public void onClickDeleteListItem(int position) {

        final OviewListModel item = oviewListManager.retrieveContentListItem(position);
        if (item == null) {
            return;
        }

        Subscriber<List<OviewListModel>> subscriber = new Subscriber<List<OviewListModel>>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {

                HandleOnException(e);
            }

            @Override public void onNext(List<OviewListModel> ContentListItemModels) {

                uiViewUpdateViewDataSource(ContentListItemModels);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), oviewListManager
                .executeDeleteOviewItem(item.getZipkey()));
        taskCase.execute(subscriber);
    }


    /**
     * on click a item to be show stage
     **/
    @Override public void onClickOpenStageListItem(int position) {

        OviewListModel item = oviewListManager.retrieveContentListItem(position);

        if (item == null) {
            return;
        }

        getView().navigateToContentViewActivity(item);
    }

    /**
     * on click a item to be show stage
     **/
    @Override public void onClickUserIcon() {

        if (userManager.isUserLogin()) {
            uiViewNavigateToHomeView();
        } else {
            uiViewNavigateToLoginView();
        }
    }

    /**
     * on click open scan view
     **/
    @Override public void onClickScanIcon() {

        getView().navigateToScanView();
    }


    private void uiViewUpdateViewDataSource(List<OviewListModel> data) {

        ViewContentListMvp view = getView();
        if (view == null) {
            return;
        }

        ListViewUIUpdate uiUpdate = new ListViewUIUpdate(ListViewUIUpdate.UI_UPDATE_LIST, view);
        uiUpdate.setListData(data);
        view.updateViewInMainThread(uiUpdate);
    }

    private void uiViewNavigateToLoginView() {

        ViewContentListMvp view = getView();
        if (view == null) {
            return;
        }

        ListViewUIUpdate uiUpdate = new ListViewUIUpdate(ListViewUIUpdate.UI_NAVIGATE_TO_LOGIN_VIEW, view);
        view.updateViewInMainThread(uiUpdate);
    }

    private void uiViewNavigateToHomeView() {

        ViewContentListMvp view = getView();
        if (view == null) {
            return;
        }

        ListViewUIUpdate uiUpdate = new ListViewUIUpdate(ListViewUIUpdate.UI_NAVIGATE_TO_HOME_VIEW, view);
        view.updateViewInMainThread(uiUpdate);
    }

    private class ListViewUIUpdate
            extends ActivityUIUpdate<ViewContentListMvp>
    {
        final static int UI_UPDATE_LIST = 0;

        final static int UI_UPDATE_LIST＿ERROR = 1;

        final static int UI_NAVIGATE_TO_LOGIN_VIEW = 2;

        final static int UI_NAVIGATE_TO_HOME_VIEW = 3;

        final static int UI_NAVIGATE_TO_SCAN_VIEW = 4;

        final static int UI_SHOW_LOGOUT_OPTION = 5;

        String msg;

        List<OviewListModel> data;

        ListViewUIUpdate(int UpdateId, ViewContentListMvp view) {

            super(UpdateId, view);
        }

        void setListData(List<OviewListModel> data) {

            this.data = data;
        }

        void setMsg(String msg) {

            this.msg = msg;
        }

        @Override protected void onUpdateEventChild(BaseMvpView view, int event) {

            ViewContentListMvp listView = (ViewContentListMvp) view;

            switch (event) {
                case UI_UPDATE_LIST:
                    listView.onUpdateViewDataSource(data);
                    break;
                case UI_UPDATE_LIST＿ERROR:
                    listView.onUpdateViewDataFailure(this.msg);
                    break;
                case UI_NAVIGATE_TO_HOME_VIEW:
                    listView.navigateToHomeViewOption();
                    break;
                case UI_SHOW_LOGOUT_OPTION:
                    listView.navigateToAppHomeActivity();
                    break;
                case UI_NAVIGATE_TO_LOGIN_VIEW:
                    listView.navigateToLoginView();
                    break;
                case UI_NAVIGATE_TO_SCAN_VIEW:
                    listView.navigateToScanView();
                    break;
            }
        }
    }
}
