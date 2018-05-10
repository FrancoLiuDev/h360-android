package com.leedian.klozr.presenter.presenterImp;
import android.graphics.Bitmap;
import android.util.Size;

import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.base.basePresenter.OviewBasePresenter;
import com.leedian.klozr.base.baseView.BaseMvpView;
import com.leedian.klozr.model.dataOut.OviewContentModel;
import com.leedian.klozr.model.dataOut.OviewInfoNodeModel;
import com.leedian.klozr.model.dataOut.ViewModelContentBitmap;
import com.leedian.klozr.presenter.presenterImp.content.ContentBitmapProvider;
import com.leedian.klozr.presenter.presenterImp.task.taskImp.ContentInfoManagerImp;
import com.leedian.klozr.presenter.presenterImp.task.taskImp.StageContentManagerImp;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.ContentInfoManager;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.StageContentManager;
import com.leedian.klozr.presenter.presenterInterface.StageContentPresenter;
import com.leedian.klozr.utils.exception.DomainExceptionHandler;
import com.leedian.klozr.utils.subscriptHelp.ObservableActionIdentifier;
import com.leedian.klozr.utils.subscriptHelp.ObservableDownloadAction;
import com.leedian.klozr.utils.subscriptHelp.ObservableOviewInfoAction;
import com.leedian.klozr.utils.thread.JobExecutor;
import com.leedian.klozr.utils.thread.TaskExecutor;
import com.leedian.klozr.utils.thread.UIThread;
import com.leedian.klozr.utils.viewUtils.ActivityUIUpdate;
import com.leedian.klozr.utils.viewUtils.tools.TouchVelocityThread;
import com.leedian.klozr.view.viewInterface.ViewContentMvp;

import rx.Subscriber;

/**
 * Created by franco liu on 09/11/2016.
 * Service for Content Activity
 */
public class StageContentPresenterImp
        extends OviewBasePresenter<ViewContentMvp>
        implements StageContentPresenter,
                   TouchVelocityThread.ActionVelocityEvent
{
    private ContentInfoManager infoManager;

    private StageContentManager contentManager = null;

    private ContentBitmapProvider bitmapProvider = null;

    private int layoutHeight;

    private int layoutWidth;

    private boolean stopFlag;

    private int infonodeN;

    private int infonodeIndex;

    private OviewInfoNodeModel infonodeNode;

    private boolean isShowingInfoDetail = false;

    private boolean isShowingInfoNoteEditCircle = false;

    private boolean isShowingInfoNoteCircle = false;

    private boolean isBackButtonLock = false;

    private String contentZipKey = "";

    private DomainExceptionHandler.ExceptionNetworkEvent networkEvent = new DomainExceptionHandler.ExceptionNetworkEvent() {
        @Override public void onExceptionNetWork(Exception exception) {

            ViewContentMvp view = getView();
            if (view == null) {
                return;
            }

            ActivityUIUpdate uiUpdate = new ContentUIUpdate(ActivityUIUpdate.UI_UPDATE_SHOW_ERROR_STRING, view)
                    .setShowMsg(exception.getMessage());
            view.updateViewInMainThread(uiUpdate);
        }
    };

    private DomainExceptionHandler.ExceptionFileEvent fileEvent = new DomainExceptionHandler.ExceptionFileEvent() {
        @Override public void onExceptionFile(Exception exception) {

            ViewContentMvp view = getView();
            if (view == null) {
                return;
            }

            uiViewUpdateOnInvalidView();
            ActivityUIUpdate uiUpdate = new ContentUIUpdate(ActivityUIUpdate.UI_UPDATE_SHOW_ERROR_STRING, view)
                    .setShowMsg(exception.getMessage());
            view.updateViewInMainThread(uiUpdate);
        }
    };

    public StageContentPresenterImp() {

    }

    private boolean isShowingInfoNoteEditCircle() {

        return isShowingInfoNoteEditCircle;
    }

    private void setShowingInfoNoteEditCircle(boolean showingInfoNoteEditCircle) {

        isShowingInfoNoteEditCircle = showingInfoNoteEditCircle;
    }

    private boolean isShowingInfoNoteCircle() {

        return isShowingInfoNoteCircle;
    }

    private void setShowingInfoNoteCircle(boolean showingInfoNoteCircle) {

        isShowingInfoNoteCircle = showingInfoNoteCircle;
    }

    private boolean isBackButtonLock() {

        return this.isBackButtonLock;
    }

    private void setBackButtonLock(boolean lock) {

        this.isBackButtonLock = lock;
    }

    /**
     * event on back button clicked
     **/
    @Override public void onBackButtonClicked() {

        if (isBackButtonLock()) {
            return;
        }
        if (isShowingInfoNoteEditCircle()) {
            hideAddEditCircle();
        } else if (isShowingInfoNoteCircle()) {
            hideEditCircle();
        } else {
            uiViewCloseView();
        }
    }

    /**
     * event on view require a show stage
     *
     * @param zipKey zipKey.
     **/
    @Override public void onStartShowContentStage(final String zipKey) {

        final DomainExceptionHandler handler = new DomainExceptionHandler();

        contentZipKey = zipKey;
        contentManager = new StageContentManagerImp(contentZipKey);
        infoManager = new ContentInfoManagerImp(contentZipKey);

        handler.setNetworkEvent(networkEvent);

        Subscriber<ObservableActionIdentifier<OviewContentModel>> subscriber = new Subscriber<ObservableActionIdentifier<OviewContentModel>>() {
            @Override public void onStart() {

                uiViewSetClickable(false);
                request(1);
            }

            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {

                HandleOnException(e, handler);
            }

            @Override public void onNext(ObservableActionIdentifier<OviewContentModel> action) {

                doNextAction(action);
                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), infoManager
                .executeRequestContentInfo());
        taskCase.execute(subscriber);
    }

    /**
     * set Content require size for present to a view
     **/
    @Override public void setContentLayoutSize(int layoutWidth, int layoutHeight) {

        this.layoutWidth = layoutWidth;
        this.layoutHeight = layoutHeight;
    }

    /**
     * create a task for showing content from a zipkey file
     **/
    @Override public void onStartShowContent() {

        final DomainExceptionHandler handler = new DomainExceptionHandler();
        handler.setNetworkEvent(networkEvent);
        Subscriber<ObservableActionIdentifier> subscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override public void onStart() {

                setBackButtonLock(true);
                uiViewSetClickable(false);
                request(1);
            }

            @Override public void onCompleted() {

                uiViewSetClickable(true);
                uiViewUpdateProgressDownloadComplete();
                showOviewContentBitmap(contentZipKey, layoutWidth, layoutHeight);
            }

            @Override public void onError(Throwable e) {

                uiViewSetClickable(true);
                uiViewUpdateProgressDownloadFinish();
                setBackButtonLock(false);
                HandleOnException(e, handler);
            }

            @Override public void onNext(ObservableActionIdentifier action) {

                if (action != null) {
                    doNextAction(action);
                }
                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), contentManager
                .executeRequestOviewShowStage());
        taskCase.execute(subscriber);
    }

    /**
     * a task for update content description
     *
     * @param description new description.
     **/
    @Override public void onEditInfoNodeContentDescription(String description) {

        stopRollingThread();

        final DomainExceptionHandler handler = new DomainExceptionHandler();
        handler.setNetworkEvent(networkEvent);

        Subscriber<ObservableActionIdentifier<OviewContentModel>> subscriber = new Subscriber<ObservableActionIdentifier<OviewContentModel>>() {
            @Override public void onStart() {

                request(1);
            }

            @Override public void onCompleted() {

                onStartShowContentStage(contentZipKey);
            }

            @Override public void onError(Throwable e) {

                HandleOnException(e, handler);
            }

            @Override public void onNext(ObservableActionIdentifier<OviewContentModel> action) {

                doNextAction(action);
                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), infoManager
                .executeEditOviewContentDescription(description));
        taskCase.execute(subscriber);
    }

    /**
     * a task for add a new infonode
     *
     * @param node new node.
     **/
    @Override public void onAddNewInfoNode(OviewInfoNodeModel node) {

        stopRollingThread();
        final DomainExceptionHandler handler = new DomainExceptionHandler();
        handler.setNetworkEvent(networkEvent);
        Subscriber<ObservableActionIdentifier<OviewContentModel>> subscriber = new Subscriber<ObservableActionIdentifier<OviewContentModel>>() {
            @Override public void onStart() {

                uiViewSetClickable(false);
                hideEditCircle();
                request(1);
            }

            @Override public void onCompleted() {

                showTipMassage(AppResource.getString(R.string.process_complete));
                onStartShowContentStage(contentZipKey);
            }

            @Override public void onError(Throwable e) {

                uiViewSetClickable(true);
                HandleOnException(e, handler);
            }

            @Override public void onNext(ObservableActionIdentifier<OviewContentModel> action) {

                doNextAction(action);
                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), infoManager
                .executeAddNewInfoNodeData(node));
        taskCase.execute(subscriber);
    }

    /**
     * a task for edit a exist infonode
     *
     * @param nodeIndex the particular node.
     * @param node      new node.
     **/
    @Override public void onUpdateInfoNode(int nodeIndex, OviewInfoNodeModel node) {

        final DomainExceptionHandler handler = new DomainExceptionHandler();
        handler.setNetworkEvent(networkEvent);
        Subscriber<ObservableActionIdentifier<OviewContentModel>> subscriber = new Subscriber<ObservableActionIdentifier<OviewContentModel>>() {
            @Override public void onStart() {

                uiViewSetClickable(false);
                request(1);
            }

            @Override public void onCompleted() {

                hideEditCircle();
                uiViewSetClickable(true);
                showTipMassage(AppResource.getString(R.string.process_complete));
                onStartShowContentStage(contentZipKey);
            }

            @Override public void onError(Throwable e) {

                uiViewSetClickable(true);
                hideEditCircle();
                HandleOnException(e, handler);
            }

            @Override public void onNext(ObservableActionIdentifier<OviewContentModel> action) {

                doNextAction(action);
                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), infoManager
                .executeUpdateInfoNodeData(nodeIndex, node));
        taskCase.execute(subscriber);
    }

    /**
     * a task for remove a node
     **/
    @Override public void onRemoveInfoNode() {

        int nodeIndex = infonodeIndex;
        stopRollingThread();
        final DomainExceptionHandler handler = new DomainExceptionHandler();
        handler.setNetworkEvent(networkEvent);
        Subscriber<ObservableActionIdentifier<OviewContentModel>> subscriber = new Subscriber<ObservableActionIdentifier<OviewContentModel>>() {
            @Override public void onStart() {

                uiViewSetClickable(false);
                request(1);
            }

            @Override public void onCompleted() {

                hideEditCircle();
                showTipMassage(AppResource.getString(R.string.process_complete));
                onStartShowContentStage(contentZipKey);
            }

            @Override public void onError(Throwable e) {

                uiViewSetClickable(true);
                hideEditCircle();
                HandleOnException(e, handler);
            }

            @Override public void onNext(ObservableActionIdentifier<OviewContentModel> action) {

                doNextAction(action);
                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), infoManager
                .executeRemoveInfoNodeData(nodeIndex));
        taskCase.execute(subscriber);
    }

    /**
     * event on content view has been touched
     **/
    @Override public void onContentViewTouched() {

        stopRollingThread();
    }

    @Override public void onUpdateInfoNode(OviewInfoNodeModel node) {

        stopRollingThread();
        final DomainExceptionHandler handler = new DomainExceptionHandler();
        handler.setNetworkEvent(networkEvent);
        Subscriber<ObservableActionIdentifier<OviewContentModel>> subscriber = new Subscriber<ObservableActionIdentifier<OviewContentModel>>() {
            @Override public void onStart() {

                uiViewSetClickable(false);
                hideEditCircle();
                request(1);
            }

            @Override public void onCompleted() {

                hideEditCircle();
                showTipMassage(AppResource.getString(R.string.process_complete));
                onStartShowContentStage(contentZipKey);
            }

            @Override public void onError(Throwable e) {

                uiViewSetClickable(true);
                hideEditCircle();
                HandleOnException(e, handler);
            }

            @Override public void onNext(ObservableActionIdentifier<OviewContentModel> action) {

                doNextAction(action);
                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), infoManager
                .executeUpdateInfoNodeData(infonodeIndex, node));
        taskCase.execute(subscriber);
    }

    /**
     * event on a info node dot clicked
     **/
    @Override public void onInfoNodeButtonClicked(final int Index) {

        if (isShowingInfoNoteCircle() && (infonodeIndex == Index)) {
            return;
        }
        hideInfoNode();
        uiViewResetImageScale();
        Subscriber<ObservableActionIdentifier<OviewContentModel>> subscriber = new Subscriber<ObservableActionIdentifier<OviewContentModel>>() {
            @Override public void onStart() {

                request(1);
            }

            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {

                HandleOnException(e);
            }

            @Override public void onNext(ObservableActionIdentifier<OviewContentModel> action) {

                doNextActionInfoNode(Index, action);
                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), infoManager
                .executeFetchOviewInfoNodeData(Index));
        taskCase.execute(subscriber);
    }

    /**
     * event on a info node add new button clicked
     **/
    @Override public void onInfoNodeAddNewButtonClicked() {

        stopRollingThread();
        showAddEditCircle();
        OviewInfoNodeModel newNode;
        newNode = new OviewInfoNodeModel();
        newNode.setR("0.100");
        newNode.setX("0.500");
        newNode.setY("0.500");
        uiViewShowAddInfonodeCircle(newNode);
    }

    /**
     * event on a particular info node edit button clicked
     **/
    @Override public void onInfoNodeEditButtonClicked() {

        resetShowingInfoNoteCircle();
        showEditCircle();
        uiViewShowEditInfonodeCircle(infonodeIndex, infonodeNode);
    }

    /**
     * event on close edit mode button clicked
     **/
    @Override public void onInfoNodeAddCloseClicked() {

        if (isShowingInfoNoteEditCircle()) {
            hideAddEditCircle();
        }
    }

    /**
     * event on a touch velocity event triggered
     **/
    @Override public void onTouchVelocityEvent(float Velocity) {

        startRollingThread(Velocity);
    }

    /**
     * event on a touch velocity event triggered
     **/
    @Override public void onAutoRotateClicked() {

        hideInfoNode();
        startAutoRollingThread(false, true);
    }

    /**
     * event on content detail button clicked
     **/
    @Override public void onInfoDetailIconClicked() {

        uiViewShowEditInfonodeCircle(!isShowingInfoDetail);
        hideInfoNode();
        uiViewResetImageScale();
        isShowingInfoDetail = !isShowingInfoDetail;
    }

    /**
     * start show render bitmap from content files
     **/
    @Override public void showOviewContentBitmap(String zipKey, int width, int height) {

        stopRollingThread();
        final DomainExceptionHandler handler = new DomainExceptionHandler();
        handler.setNetworkEvent(networkEvent);
        handler.setFileEvent(fileEvent);
        Subscriber<Bitmap[]> subscriber = new Subscriber<Bitmap[]>() {
            @Override public void onStart() {

                setBackButtonLock(true);
                showLoadingDialog();
                uiViewSetClickable(false);
                releaseBmpProvider();
                request(1);
            }

            @Override public void onCompleted() {

                setBackButtonLock(false);
                hideLoadingDialog();
                uiViewSetClickable(true);
            }

            @Override public void onError(Throwable e) {

                hideLoadingDialog();
                uiViewSetClickable(true);
                HandleOnException(e, handler);
            }

            @Override public void onNext(Bitmap[] Bitmaps) {

                releaseBmpProvider();
                bitmapProvider = new ContentBitmapProvider(Bitmaps);
                uiViewSetContentBmpLoaded(bitmapProvider.getSize());
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), contentManager
                .getKlozrViewBitmap(width, height));
        taskCase.execute(subscriber);
    }

    /**
     * on view need a big size image
     **/
    @Override public void onRequestScaleSizeImage(int index, int width, int height) {

        Subscriber<ObservableActionIdentifier<Bitmap>> subscriber = new Subscriber<ObservableActionIdentifier<Bitmap>>() {
            @Override public void onStart() {

                request(1);
            }

            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {

                HandleOnException(e);
            }

            @Override public void onNext(ObservableActionIdentifier<Bitmap> actResult) {

                Bitmap                 bmp     = actResult.getObject();
                ViewModelContentBitmap bmpInfo = new ViewModelContentBitmap();
                bmpInfo.setSize(new Size(bmp.getWidth(), bmp.getHeight()));
                bmpInfo.setBitmap(bmp);
                bmpInfo.setIndex(Integer.parseInt(actResult.getContent()));
                uiViewSetDoubleSizeImage(bmpInfo);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), contentManager
                .getKlozrViewSingleBitmap(index, width, height));
        taskCase.execute(subscriber);
    }

    /**
     * on edit infonode content name
     **/
    @Override public void onEditInfoNodeContentName(String name) {

        stopRollingThread();

        final DomainExceptionHandler handler = new DomainExceptionHandler();
        handler.setNetworkEvent(networkEvent);
        Subscriber<ObservableActionIdentifier<OviewContentModel>> subscriber = new Subscriber<ObservableActionIdentifier<OviewContentModel>>() {
            @Override public void onStart() {

                request(1);
            }

            @Override public void onCompleted() {

                onStartShowContentStage(contentZipKey);
            }

            @Override public void onError(Throwable e) {

                HandleOnException(e, handler);
            }

            @Override public void onNext(ObservableActionIdentifier<OviewContentModel> action) {

                doNextAction(action);
                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), infoManager
                .executeEditOviewContentName(name));
        taskCase.execute(subscriber);
    }

    /**
     * on view require first bitmap
     **/
    @Override public void showFirstBitmap() {

        if (bitmapProvider == null) {
            return;
        }
        resetShowingInfoNoteCircle();
        ViewModelContentBitmap oviewBitmapInfo = bitmapProvider.getFirst();
        if (oviewBitmapInfo.getBitmap() != null) {
            updateNewImage(oviewBitmapInfo);
        }
    }

    /**
     * on view require next bitmap
     **/
    @Override public void showNextBitmap() {

        if (bitmapProvider == null) {
            return;
        }
        ViewModelContentBitmap oviewBitmapInfo = bitmapProvider.getNext();
        if (oviewBitmapInfo.getBitmap() != null) {
            updateNewImage(oviewBitmapInfo);
        }
    }

    /**
     * on view require previous bitmap
     **/
    @Override public void showPreviousBitmap() {

        if (bitmapProvider == null) {
            return;
        }
        ViewModelContentBitmap oviewBitmapInfo = bitmapProvider.getPrevious();
        if (oviewBitmapInfo.getBitmap() != null) {
            updateNewImage(oviewBitmapInfo);
        }
    }

    @Override public void onResume() {

    }

    private void doNextActionInfoNode(int index, ObservableActionIdentifier action) {

        if (action.getAction().equals(ObservableOviewInfoAction.ACTION_OVIEW_GET_INFO_NODE)) {
            doAutoRollingToInfoNode(index, (OviewInfoNodeModel) action.getObject());
        }
    }

    private void doNextAction(ObservableActionIdentifier action) {

        if (action.getAction().equals(ObservableDownloadAction.ACTION_START_DOWNLOAD)) {
            uiViewUpdateProgressDownloadStart();
        }
        if (action.getAction().equals(ObservableDownloadAction.ACTION_FILE_DOWNLOADING)) {
            uiViewUpdateProgressDownloading(Integer.valueOf(action.getContent()));
        }
        if (action.getAction().equals(ObservableOviewInfoAction.ACTION_OVIEW_INFO_ONLINE)) {

            uiViewUpdateOviewInformation(true, (OviewContentModel) action.getObject());
        }
        if (action.getAction().equals(ObservableOviewInfoAction.ACTION_OVIEW_INFO_OFFLINE)) {

            uiViewUpdateOviewInformation(false, (OviewContentModel) action.getObject());
        }
    }

    /**
     * on rolling thread require next bitmap
     **/
    @Override public void onNextStepRight() {

        showNextBitmap();
    }

    /**
     * on rolling thread require previous bitmap
     **/
    @Override public void onNextStepLeft() {

        showPreviousBitmap();
    }

    /**
     * release oview images
     **/
    private void releaseBmpProvider() {

        if (bitmapProvider == null) {
            return;
        }
        bitmapProvider.releaseBmpInfo();
        bitmapProvider = null;
    }

    /**
     * if infonode is showing , hide it.
     **/
    private void resetShowingInfoNoteCircle() {

        if (isShowingInfoNoteCircle()) {
            hideInfoNode();
        }
    }

    /**
     * update a new image to view
     **/
    private void updateNewImage(ViewModelContentBitmap oviewBitmapInfo) {

        resetShowingInfoNoteCircle();
        uiViewUpdateContentImage(oviewBitmapInfo);

        if (!stopFlag) {
            return;
        }

        if (infonodeN == oviewBitmapInfo.getIndex()) {
            stopFlag = false;
            stopRollingThread();
            showInfoNode();
            uiViewUpdateOviewInfoNode(infonodeIndex, infonodeNode);
        }
    }

    /**
     * on view is dismiss
     **/
    @Override public void detachView(boolean retainInstance) {

        stopRollingThread();
        releaseBmpProvider();
        super.detachView(retainInstance);
    }

    /**
     * trigger a Velocity event
     **/
    private void startRollingThread(float Velocity) {

        TouchVelocityThread.threadStart(Velocity, this, false);
    }

    /**
     * start auto roll
     **/
    private void startAutoRollingThread(boolean isReleaseByFlag, boolean toRight) {

        if (stopFlag) {
            return;
        }

        stopFlag = isReleaseByFlag;

        if (toRight) {
            TouchVelocityThread.threadStart(800, this, true);
        } else {
            TouchVelocityThread.threadStart(-800, this, true);
        }
    }

    /**
     * stop auto roll
     **/
    private void stopRollingThread() {

        if (!stopFlag) {
            TouchVelocityThread.threadRelease();
        }
    }

    /**
     * roll image content to next node
     **/
    private void doAutoRollingToInfoNode(int nodeIndex, OviewInfoNodeModel node) {

        int index        = Integer.parseInt(node.getN());
        int indexCurrent = bitmapProvider.getCurrent().getIndex();

        infonodeIndex = nodeIndex;
        infonodeN = index;
        infonodeNode = node;

        stopRollingThread();

        if (infonodeN == indexCurrent) {

            stopFlag = false;
            stopRollingThread();
            showInfoNode();
            uiViewUpdateOviewInfoNode(infonodeIndex, infonodeNode);
        } else {

            if (infonodeN > indexCurrent) {
                if ((infonodeN - indexCurrent) > 23) {
                    startAutoRollingThread(true, false);
                } else {
                    startAutoRollingThread(true, true);
                }
            } else {
                if ((indexCurrent - infonodeN) > 23) {
                    startAutoRollingThread(true, true);
                } else {
                    startAutoRollingThread(true, false);
                }
            }
        }
    }

    /**
     * show infonode add edit circle
     **/
    private void showAddEditCircle() {

        uiViewSetContentActionButton(false);
        uiViewDisableImageScale();
        uiViewToggleButtonsAdd();
        setShowingInfoNoteEditCircle(true);
    }

    /**
     * hide infonode add edit circle
     **/
    private void hideAddEditCircle() {

        hideInfoNode();
        uiViewSetContentActionButton(true);
        uiViewEnableImageScale();
        uiViewHideEditButtonBar();
        uiViewResetInfoNodeButtons();
        setShowingInfoNoteEditCircle(false);
    }

    /**
     * show infonode edit circle
     **/
    private void showEditCircle() {

        uiViewSetContentActionButton(false);
        uiViewDisableImageScale();
        uiViewShowEditButtonBar();
        setShowingInfoNoteEditCircle(true);
    }

    /**
     * hide infonode edit circle
     **/
    private void hideEditCircle() {

        hideInfoNode();
        uiViewSetContentActionButton(true);
        uiViewEnableImageScale();
        uiViewHideEditButtonBar();
        uiViewResetInfoNodeButtons();
        setShowingInfoNoteCircle(false);
    }

    /**
     * show infonode circle
     **/
    private void showInfoNode() {

        uiViewResetImageScale();
        uiViewDisableImageScale();
        setShowingInfoNoteCircle(true);
    }

    /**
     * hide infonode circle
     **/
    private void hideInfoNode() {

        uiViewHideInfoNode();
        uiViewEnableImageScale();
        uiViewResetInfoNodeButtons();
        setShowingInfoNoteCircle(false);
    }

    private void uiViewUpdateOnInvalidView() {

        ViewContentMvp view = this.getView();
        if (view == null) {
            return;
        }

        ActivityUIUpdate uiUpdate = new ContentUIUpdate(ActivityUIUpdate.UI_UPDATE_ON_INVALID_VIEW, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewUpdateProgressDownloadStart() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_SETSTART_DOWN_LOAD, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewUpdateProgressDownloadFinish() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_SETFINISH_DOWNLOADING, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewUpdateProgressDownloadComplete() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_SETCOMPLETE_DOWNLOAD, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewUpdateContentImage(ViewModelContentBitmap contentImage) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_CONTENT_IMAGE, view);
        uiUpdate.setContentImage(contentImage);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewUpdateProgressDownloading(int percent) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_SETPROGRESS, view)
                .setPercent(percent);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewUpdateOviewInformation(boolean isOnline, OviewContentModel info) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_LOAD_CONTENT_INFO, view);
        uiUpdate.setInfo(info);
        uiUpdate.setOnlineInfo(isOnline);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewUpdateOviewInfoNode(int index, OviewInfoNodeModel node) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_INFONODE, view);
        uiUpdate.setNode(node);
        uiUpdate.setNodeIndex(index);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewCloseView() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_CLOSE_VIEW, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewHideInfoNode() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_HIDE_INFONODE, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewResetInfoNodeButtons() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_RESET_INFO_NODE_BUTTON, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewToggleButtonsAdd() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_INFO_NODE_BUTTON_ADD, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewResetImageScale() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_RESET_DEFAULT_SCALE, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewDisableImageScale() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_DISABLE_IMAGE_SCALE, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewEnableImageScale() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_ENABLE_IMAGE_SCALE, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewShowEditButtonBar() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_BOTTOM_BAR_EDIT, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewHideEditButtonBar() {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_BOTTOM_BAR_DEFAULT, view);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewSetClickable(boolean clickable) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_SET_ENABLE, view);
        uiUpdate.setClickable(clickable);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewSetContentActionButton(boolean clickable) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_UPDATE_INFONODE_ACTIONBUTTON, view);
        uiUpdate.setClickable(clickable);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewSetContentBmpLoaded(int cnt) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_SER_CONTENT_LOAD_SUCCESS, view);
        uiUpdate.setBmpSize(cnt);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewSetDoubleSizeImage(ViewModelContentBitmap image) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_LOAD_DOUBLE_SIZE_IMAGE, view);
        uiUpdate.setContentImage(image);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewShowAddInfonodeCircle(OviewInfoNodeModel newNode) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_SHOW_ADD_INFONODE_CIRCLE, view);
        uiUpdate.setNode(newNode);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewShowEditInfonodeCircle(int nodeIndex, OviewInfoNodeModel node) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_SHOW_EDIT_INFONODE_CIRCLE, view);
        uiUpdate.setNode(node);
        uiUpdate.setNodeIndex(nodeIndex);
        startUpdateUiThread(uiUpdate);
    }

    private void uiViewShowEditInfonodeCircle(boolean visible) {

        ViewContentMvp view = getView();
        if (view == null) {
            return;
        }

        ContentUIUpdate uiUpdate = new ContentUIUpdate(ContentUIUpdate.UI_SHOW_CONTENT_DETAIL_VIEW, view);
        uiUpdate.setVisible(visible);

        startUpdateUiThread(uiUpdate);
    }

    private class ContentUIUpdate
            extends ActivityUIUpdate<ViewContentMvp>
    {
        final static int UI_UPDATE_SETPROGRESS = 0;

        final static int UI_UPDATE_SETSTART_DOWN_LOAD = 1;

        final static int UI_UPDATE_SETCOMPLETE_DOWNLOAD = 3;

        final static int UI_UPDATE_SETFINISH_DOWNLOADING = 4;

        final static int UI_UPDATE_CONTENT_IMAGE = 5;

        final static int UI_UPDATE_LOAD_CONTENT_INFO = 10;

        final static int UI_UPDATE_INFONODE = 11;

        final static int UI_HIDE_INFONODE = 12;

        final static int UI_RESET_DEFAULT_SCALE = 13;

        final static int UI_DISABLE_IMAGE_SCALE = 14;

        final static int UI_ENABLE_IMAGE_SCALE = 15;

        final static int UI_RESET_INFO_NODE_BUTTON = 16;

        final static int UI_UPDATE_INFO_NODE_BUTTON_ADD = 17;

        final static int UI_UPDATE_BOTTOM_BAR_EDIT = 18;

        final static int UI_UPDATE_BOTTOM_BAR_DEFAULT = 19;

        final static int UI_CLOSE_VIEW = 20;

        final static int UI_SET_ENABLE = 21;

        final static int UI_UPDATE_INFONODE_ACTIONBUTTON = 22;

        final static int UI_SER_CONTENT_LOAD_SUCCESS = 23;

        final static int UI_LOAD_DOUBLE_SIZE_IMAGE = 24;

        final static int UI_SHOW_ADD_INFONODE_CIRCLE = 25;

        final static int UI_SHOW_EDIT_INFONODE_CIRCLE = 26;

        final static int UI_SHOW_CONTENT_DETAIL_VIEW = 27;

        int percent;

        int bmpSize;

        int nodeIndex;

        boolean isOnlineInfo;

        boolean isVisible;

        boolean clickable;

        OviewContentModel info;

        OviewInfoNodeModel node;

        ViewModelContentBitmap ContentImage;

        private ContentUIUpdate(int UpdateId, ViewContentMvp view) {

            super(UpdateId, view);
        }

        private void setClickable(boolean clickable) {

            this.clickable = clickable;
        }

        private void setVisible(boolean visible) {

            isVisible = visible;
        }

        private void setContentImage(ViewModelContentBitmap contentImage) {ContentImage = contentImage;}

        private void setBmpSize(int bmpSize) {

            this.bmpSize = bmpSize;
        }

        private void setInfo(OviewContentModel info) {

            this.info = info;
        }

        private void setNodeIndex(int nodeIndex) {

            this.nodeIndex = nodeIndex;
        }

        private void setNode(OviewInfoNodeModel node) {

            this.node = node;
        }

        private ContentUIUpdate setPercent(int percent) {

            this.percent = percent;
            return this;
        }

        private void setOnlineInfo(boolean onlineInfo) {

            isOnlineInfo = onlineInfo;
        }

        @Override protected void onUpdateEventChild(BaseMvpView view, int event) {

            ViewContentMvp contentView = (ViewContentMvp) view;

            switch (event) {
                case UI_UPDATE_SETPROGRESS:
                    contentView.setProgressPercent(Integer.parseInt(String.valueOf(percent)));
                    break;
                case UI_UPDATE_SETSTART_DOWN_LOAD:
                    contentView.setProgressDownloadStart();
                    break;
                case UI_UPDATE_SETCOMPLETE_DOWNLOAD:
                    contentView.setProgressDownloadComplete();
                    break;
                case UI_UPDATE_SETFINISH_DOWNLOADING:
                    contentView.setProgressDownloadFinish();
                    break;
                case UI_UPDATE_LOAD_CONTENT_INFO:
                    contentView.setOviewInformationLoadSuccess(isOnlineInfo, info);
                    break;
                case UI_UPDATE_INFONODE:
                    contentView.setOviewInformationNode(nodeIndex, node);
                    break;
                case UI_UPDATE_CONTENT_IMAGE:
                    contentView.setImageViewBmp(ContentImage);
                    break;
                case UI_HIDE_INFONODE:
                    contentView.hideInfoNodeCircle();
                    break;
                case UI_RESET_DEFAULT_SCALE:
                    contentView.resetContentScaleDefault();
                    break;
                case UI_DISABLE_IMAGE_SCALE:
                    contentView.disableContentScale();
                    break;
                case UI_ENABLE_IMAGE_SCALE:
                    contentView.enableContentScale();
                    break;
                case UI_RESET_INFO_NODE_BUTTON:
                    contentView.resetInfoButtons();
                    break;
                case UI_UPDATE_INFO_NODE_BUTTON_ADD:
                    contentView.toggleInfoButtonsAdd();
                    break;
                case UI_UPDATE_BOTTOM_BAR_EDIT:
                    contentView.showEditButtonBar(true);
                    break;
                case UI_UPDATE_BOTTOM_BAR_DEFAULT:
                    contentView.showEditButtonBar(false);
                    break;
                case UI_CLOSE_VIEW:
                    contentView.closeView();
                    break;
                case UI_SET_ENABLE:
                    contentView.setInterfaceClickable(clickable);
                    break;
                case UI_UPDATE_INFONODE_ACTIONBUTTON:
                    contentView.setActionButtonVisible(clickable);
                    break;
                case UI_SER_CONTENT_LOAD_SUCCESS:
                    contentView.setImageBufferLoadSuccess(bmpSize);
                    break;
                case UI_LOAD_DOUBLE_SIZE_IMAGE:
                    contentView.setScaledImageLoadSuccess(ContentImage);
                    break;
                case UI_SHOW_ADD_INFONODE_CIRCLE:
                    contentView.showAddInfoNode(node);
                    break;
                case UI_SHOW_EDIT_INFONODE_CIRCLE:
                    contentView.showEditInfoNode(nodeIndex, node);
                    break;
                case UI_SHOW_CONTENT_DETAIL_VIEW:
                    contentView.showOviewDetail(isVisible);
                    break;
            }
        }
    }
}
