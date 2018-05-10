package com.leedian.klozr.presenter.presenterInterface;
import com.leedian.klozr.view.viewInterface.ViewContentListMvp;

/**
 * ContentListPresenter
 *
 * @author Franco
 */
public interface ContentListPresenter
        extends BasePresenter<ViewContentListMvp>
{
    void onFetchListItems();

    void onLoadMoreListItems();

    void onClickListItem(int position);

    void onClickDeleteListItem(int position);

    void onClickOpenStageListItem(int position);

    void onClickUserIcon();

    void onClickConfirmLogout();

    void onClickScanIcon();
}