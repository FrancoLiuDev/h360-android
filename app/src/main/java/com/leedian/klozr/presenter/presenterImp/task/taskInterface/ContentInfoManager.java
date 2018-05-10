package com.leedian.klozr.presenter.presenterImp.task.taskInterface;
import com.leedian.klozr.model.dataOut.OviewInfoNodeModel;
import com.leedian.klozr.utils.subscriptHelp.ObservableActionIdentifier;

import rx.Observable;

/**
 * ContentInfoManager
 *
 * @author Franco
 */
public interface ContentInfoManager {
    Observable<ObservableActionIdentifier> executeRequestContentInfo();

    Observable<ObservableActionIdentifier> executeFetchOviewInfoNodeData(int index);

    Observable<ObservableActionIdentifier> executeEditOviewContentName(String name);

    Observable<ObservableActionIdentifier> executeEditOviewContentDescription(String description);

    Observable<ObservableActionIdentifier> executeAddNewInfoNodeData(final OviewInfoNodeModel node);

    Observable<ObservableActionIdentifier> executeUpdateInfoNodeData(final int nIndex, final OviewInfoNodeModel node);

    Observable<ObservableActionIdentifier> executeRemoveInfoNodeData(final int nIndex);
}
