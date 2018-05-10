package com.leedian.klozr.presenter.presenterImp.task.taskInterface;

import com.leedian.klozr.model.dataOut.OviewListModel;

import java.util.List;

import rx.Observable;

/**
 * ContentListManager
 *
 * @author Franco
 */
public interface ContentListManager {

    Observable<List<OviewListModel>> executeRequestOviewList(boolean isLoadMore);

    Observable executeDeleteOviewItem(final String zipKey);

    List<OviewListModel> retrieveContentCachedList();

    OviewListModel retrieveContentListItem(int Pos);
}
