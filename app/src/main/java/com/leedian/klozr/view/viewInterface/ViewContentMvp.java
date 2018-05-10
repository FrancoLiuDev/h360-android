package com.leedian.klozr.view.viewInterface;
import com.leedian.klozr.base.baseView.BaseMvpView;
import com.leedian.klozr.model.dataOut.OviewContentModel;
import com.leedian.klozr.model.dataOut.OviewInfoNodeModel;
import com.leedian.klozr.model.dataOut.ViewModelContentBitmap;

/**
 * ViewContentMvp
 *
 * @author Franco
 */
public interface ViewContentMvp
        extends BaseMvpView
{
    void setProgressPercent(int percent);

    void setProgressDownloadStart();

    void setProgressDownloadFinish();

    void setProgressDownloadComplete();

    void setImageViewBmp(ViewModelContentBitmap bitmap);

    void setImageBufferLoadSuccess(int imageCount);

    void setScaledImageLoadSuccess(ViewModelContentBitmap bitmap);

    void setOviewInformationLoadSuccess(boolean isOnline, OviewContentModel info);

    void setOviewInformationNode(int index, OviewInfoNodeModel node);

    void showOviewDetail(boolean show);

    void showAddInfoNode(OviewInfoNodeModel node);

    void showEditInfoNode(int nodeIndex, OviewInfoNodeModel node);

    void hideInfoNodeCircle();

    void resetContentScaleDefault();

    void disableContentScale();

    void enableContentScale();

    void resetInfoButtons();

    void toggleInfoButtonsAdd();

    void toggleInfoButtonsEdit();

    void showEditButtonBar(boolean isShow);

    void closeView();

    void setInterfaceClickable(boolean clickable);

    void setActionButtonVisible(boolean visible);
}

