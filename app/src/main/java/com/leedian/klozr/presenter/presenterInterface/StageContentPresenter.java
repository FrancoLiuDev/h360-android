package com.leedian.klozr.presenter.presenterInterface;
import com.leedian.klozr.model.dataOut.OviewInfoNodeModel;
import com.leedian.klozr.view.viewInterface.ViewContentMvp;

/**
 * StageContentPresenter
 *
 * @author Franco
 */
public interface StageContentPresenter
        extends BasePresenter<ViewContentMvp>
{
    void onBackButtonClicked();

    void onStartShowContentStage(String zipKey);

    void setContentLayoutSize(int layoutWidth, int layoutHeight);

    void onStartShowContent();

    void onEditInfoNodeContentDescription(String description);

    void onAddNewInfoNode(OviewInfoNodeModel node);

    void onUpdateInfoNode(int nodeIndex, OviewInfoNodeModel node);

    void onRemoveInfoNode();

    void onContentViewTouched();

    void onUpdateInfoNode(OviewInfoNodeModel node);

    void onInfoNodeButtonClicked(int Index);

    void onInfoNodeAddNewButtonClicked();

    void onInfoNodeEditButtonClicked();

    void onInfoNodeAddCloseClicked();

    void onTouchVelocityEvent(float Velocity);

    void onAutoRotateClicked();

    void onInfoDetailIconClicked();

    void showOviewContentBitmap(String zipKey, int width, int height);

    void onRequestScaleSizeImage(int index, int width, int height);

    void onEditInfoNodeContentName(String name);

    void showFirstBitmap();

    void showNextBitmap();

    void showPreviousBitmap();

    void onResume();
}
