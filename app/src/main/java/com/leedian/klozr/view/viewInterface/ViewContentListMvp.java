package com.leedian.klozr.view.viewInterface;
/**
 * Created by franco on 2016/10/18.
 */
import com.leedian.klozr.base.baseView.BaseMvpView;
import com.leedian.klozr.model.dataOut.OviewListModel;

import java.util.List;

/**
 * ViewContentListMvp
 *
 * @author Franco
 */
public interface ViewContentListMvp
        extends BaseMvpView
{
    void onUpdateViewDataSource(List<OviewListModel> data);

    void onUpdateViewDataFailure(String str);

    void showClickItemOption(int position, OviewListModel item);

    void navigateToContentViewActivity(OviewListModel item);

    void navigateToHomeViewOption();

    void navigateToLoginView();

    void navigateToHomeView();

    void navigateToScanView();
}
