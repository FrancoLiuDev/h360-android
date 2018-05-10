package com.leedian.klozr.presenter.presenterImp.task.taskImp;
import com.leedian.klozr.model.dataOut.OviewContentModel;
import com.leedian.klozr.model.dataOut.OviewInfoNodeModel;
import com.leedian.klozr.model.restapi.OviewApi;
import com.leedian.klozr.presenter.presenterImp.content.ContentInfoFilePool;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.ContentInfoManager;
import com.leedian.klozr.utils.exception.DomainException;
import com.leedian.klozr.utils.subscriptHelp.ObservableActionIdentifier;
import com.leedian.klozr.utils.subscriptHelp.ObservableOviewInfoAction;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * ContentInfoManagerImp
 *
 * @author Franco
 */
public class ContentInfoManagerImp
        implements ContentInfoManager
{
    private String infoZipKey;

    private ContentInfoFilePool contentInfoPool = new ContentInfoFilePool();

    public ContentInfoManagerImp(String zipKey) {

        this.infoZipKey = zipKey;
    }

    /**
     * task to fetch content information data
     **/
    @Override public Observable<ObservableActionIdentifier> executeRequestContentInfo() {

        final boolean exists = contentInfoPool.isFileInPool(this.infoZipKey);

        return Observable.create(new Observable.OnSubscribe<ObservableActionIdentifier>() {
            OviewContentModel info;

            @Override
            public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                String zipKey = ContentInfoManagerImp.this.infoZipKey;

                if (exists) {
                    info = contentInfoPool.getOviewContentInformation(zipKey);
                }

                try {
                    boolean  isSuccess;
                    OviewApi HttpApi = new OviewApi();
                    isSuccess = HttpApi.getItemInformation(zipKey);

                    if (!isSuccess && !exists) {
                        Exception exception = DomainException
                                .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                        .getMinor_code());

                        subscriber.onError(exception);
                        return;
                    }

                    if (!isSuccess) {
                        subscriber
                                .onNext(new ObservableActionIdentifier<OviewContentModel>(ObservableOviewInfoAction.ACTION_OVIEW_INFO_OFFLINE, "", info));
                        subscriber.onCompleted();
                        return;
                    }

                    info = HttpApi.getOviewInfo();
                    contentInfoPool.saveContentInformation(zipKey, info);

                    subscriber
                            .onNext(new ObservableActionIdentifier<OviewContentModel>(ObservableOviewInfoAction.ACTION_OVIEW_INFO_ONLINE, "", info));
                } catch (Exception e) {
                    if (exists) {
                        subscriber
                                .onNext(new ObservableActionIdentifier<OviewContentModel>(ObservableOviewInfoAction.ACTION_OVIEW_INFO_OFFLINE, "", info));
                    } else {
                        subscriber.onError(DomainException.getDomainException(e));
                    }
                }
                subscriber.onCompleted();
            }
        });
    }

    /**
     * task to fetch a infonode data
     **/
    @Override
    public Observable<ObservableActionIdentifier> executeFetchOviewInfoNodeData(final int index) {

        final boolean exists = contentInfoPool.isFileInPool(this.infoZipKey);

        return Observable.create(new Observable.OnSubscribe<ObservableActionIdentifier>() {
            @Override
            public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                String zipkey = ContentInfoManagerImp.this.infoZipKey;

                if (!exists) {
                    Exception exception = DomainException.buildFileException();
                    subscriber.onError(exception);
                }

                try {
                    OviewContentModel info = contentInfoPool.getOviewContentInformation(zipkey);
                    List<OviewInfoNodeModel> list  = info.getTrait();
                    OviewInfoNodeModel       model = list.get(index);

                    subscriber
                            .onNext(new ObservableActionIdentifier<OviewInfoNodeModel>(ObservableOviewInfoAction.ACTION_OVIEW_GET_INFO_NODE, "", model));
                } catch (Exception e) {
                    Exception exception = DomainException.buildFileException();
                    subscriber.onError(exception);
                }
                subscriber.onCompleted();
            }
        });
    }

    /**
     * task to update a content name
     **/
    @Override
    public Observable<ObservableActionIdentifier> executeEditOviewContentName(final String name) {

        final boolean exists = contentInfoPool.isFileInPool(this.infoZipKey);

        return Observable.create(new Observable.OnSubscribe<ObservableActionIdentifier>() {
            @Override
            public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                String zipKey = ContentInfoManagerImp.this.infoZipKey;

                if (!exists) {
                    Exception exception = DomainException.buildFileException();
                    subscriber.onError(exception);
                }

                try {
                    boolean  isSuccess;
                    OviewApi HttpApi = new OviewApi();
                    isSuccess = HttpApi.updateItemName(zipKey, name);

                    if (!isSuccess) {
                        Exception exception = DomainException
                                .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                        .getMinor_code());
                        subscriber.onError(exception);
                        return;
                    }
                } catch (Exception e) {
                    subscriber.onError(DomainException.getDomainException(e));
                }

                subscriber.onCompleted();
            }
        });
    }

    /**
     * task to update a content description
     **/
    @Override
    public Observable<ObservableActionIdentifier> executeEditOviewContentDescription(final String description) {

        final boolean exists = contentInfoPool.isFileInPool(this.infoZipKey);
        return Observable.create(new Observable.OnSubscribe<ObservableActionIdentifier>() {
            @Override
            public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                String zipKey = ContentInfoManagerImp.this.infoZipKey;

                if (!exists) {
                    Exception exception = DomainException.buildFileException();
                    subscriber.onError(exception);
                }

                try {
                    boolean  isSuccess;
                    OviewApi HttpApi = new OviewApi();
                    isSuccess = HttpApi.updateItemDescription(zipKey, description);

                    if (!isSuccess) {
                        Exception exception = DomainException
                                .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                        .getMinor_code());
                        subscriber.onError(exception);
                        return;
                    }
                } catch (Exception e) {
                    subscriber.onError(DomainException.getDomainException(e));
                }
                subscriber.onCompleted();
            }
        });
    }

    /**
     * task to update a content description
     **/
    @Override
    public Observable<ObservableActionIdentifier> executeAddNewInfoNodeData(final OviewInfoNodeModel node) {

        return Observable.create(new Observable.OnSubscribe<ObservableActionIdentifier>() {
            @Override
            public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                String                   zipKey  = ContentInfoManagerImp.this.infoZipKey;
                List<OviewInfoNodeModel> newList = contentInfoPool.gatOviewAddTraits(zipKey, node);

                try {
                    boolean  isSuccess;
                    OviewApi HttpApi = new OviewApi();
                    isSuccess = HttpApi.updateItemTraits(zipKey, newList);

                    if (!isSuccess) {
                        Exception exception = DomainException
                                .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                        .getMinor_code());
                        subscriber.onError(exception);
                        return;
                    }
                } catch (Exception e) {
                    subscriber.onError(DomainException.getDomainException(e));
                }
                subscriber.onCompleted();
            }
        });
    }

    /**
     * task to update a infonode data
     **/
    @Override
    public Observable<ObservableActionIdentifier> executeUpdateInfoNodeData(final int nIndex, final OviewInfoNodeModel node) {

        return Observable.create(new Observable.OnSubscribe<ObservableActionIdentifier>() {
            @Override
            public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                String zipKey = ContentInfoManagerImp.this.infoZipKey;

                List<OviewInfoNodeModel> newList = contentInfoPool
                        .gatOviewUpdateTraits(zipKey, nIndex, node);

                try {
                    boolean  isSuccess;
                    OviewApi HttpApi = new OviewApi();
                    isSuccess = HttpApi.updateItemTraits(zipKey, newList);

                    if (!isSuccess) {
                        Exception exception = DomainException
                                .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                        .getMinor_code());
                        subscriber.onError(exception);
                        return;
                    }
                } catch (Exception e) {
                    subscriber.onError(DomainException.getDomainException(e));
                }

                subscriber.onCompleted();
            }
        });
    }

    /**
     * task to remove  a infonode  data
     **/
    @Override
    public Observable<ObservableActionIdentifier> executeRemoveInfoNodeData(final int nIndex) {/////

        return Observable.create(new Observable.OnSubscribe<ObservableActionIdentifier>() {
            @Override
            public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                String zipKey = ContentInfoManagerImp.this.infoZipKey;
                List<OviewInfoNodeModel> newList = contentInfoPool
                        .gatOviewRemoveTraits(zipKey, nIndex);

                try {
                    boolean  isSuccess;
                    OviewApi HttpApi = new OviewApi();
                    isSuccess = HttpApi.updateItemTraits(zipKey, newList);

                    if (!isSuccess) {
                        Exception exception = DomainException
                                .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                        .getMinor_code());
                        subscriber.onError(exception);
                        return;
                    }
                } catch (Exception e) {
                    subscriber.onError(DomainException.getDomainException(e));
                }
                subscriber.onCompleted();
            }
        });
    }
}

