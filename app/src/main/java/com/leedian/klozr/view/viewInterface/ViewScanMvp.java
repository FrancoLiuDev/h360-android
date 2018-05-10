package com.leedian.klozr.view.viewInterface;
import com.leedian.klozr.base.baseView.BaseMvpView;
import com.leedian.klozr.model.dataOut.OviewContentModel;

/**
 * ViewScanMvp
 *
 * @author Franco
 */
public interface ViewScanMvp
        extends BaseMvpView
{
    void navigateToOviewContentView(String zipKey);

    void navigateToLoginView();

    void RestartScan();

    void setDownloadProgress(int percent);

    void dismissProgressDialog();

    void showProgressDialog();

    void setOviewInformation(boolean isOnline, OviewContentModel info);
}
