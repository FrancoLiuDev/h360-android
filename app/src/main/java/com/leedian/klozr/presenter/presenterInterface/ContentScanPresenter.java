package com.leedian.klozr.presenter.presenterInterface;
import com.leedian.klozr.view.viewInterface.ViewScanMvp;

/**
 * ContentListPresenter
 *
 * @author Franco
 */
public interface ContentScanPresenter
        extends BasePresenter<ViewScanMvp>
{
    void onScanContentCodeData(String str);

    void onUserIconClicked();
}
