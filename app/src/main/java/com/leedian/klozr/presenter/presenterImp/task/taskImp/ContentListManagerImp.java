package com.leedian.klozr.presenter.presenterImp.task.taskImp;
import com.leedian.klozr.model.cache.cacheImp.OviewListCacheImp;
import com.leedian.klozr.model.cache.cacheInterface.OviewListCache;
import com.leedian.klozr.model.dataOut.OviewListModel;
import com.leedian.klozr.model.restapi.OviewApi;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.ContentListManager;
import com.leedian.klozr.utils.exception.DomainException;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * ContentListManagerImp
 *
 * @author Franco
 */
public class ContentListManagerImp
        implements ContentListManager
{
    private OviewListCache listCache = new OviewListCacheImp();

    private List<OviewListModel> oviewListBuffer = new ArrayList<OviewListModel>();

    private int coverRequestLimit = 10;

    private int coverRequestOffset = 0;

    /**
     * task to retrieve list data
     **/
    @Override public Observable<List<OviewListModel>> executeRequestOviewList(boolean loadMore) {

        if (!loadMore) {
            initBeforeRefresh();
        }

        return Observable.create(new Observable.OnSubscribe<List<OviewListModel>>() {
            @Override public void call(Subscriber<? super List<OviewListModel>> subscriber) {

                boolean  isSuccess;
                OviewApi HttpApi = new OviewApi();

                try {
                    isSuccess = HttpApi.retrieveOviewList(coverRequestLimit, coverRequestOffset);

                    if (!isSuccess) {
                        Exception exception = DomainException
                                .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                        .getMinor_code());

                        subscriber.onError(exception);
                        return;
                    }

                    addOviewList(HttpApi.getOviewList());
                    subscriber.onNext(oviewListBuffer);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(DomainException.getDomainException(e));
                }
            }
        });
    }

    /**
     * task to delete item by zip key
     **/
    @Override public Observable<List<OviewListModel>> executeDeleteOviewItem(final String zipKey) {

        return Observable.create(new Observable.OnSubscribe<List<OviewListModel>>() {
            List<OviewListModel> CoverList = null;

            @Override public void call(Subscriber<? super List<OviewListModel>> subscriber) {

                try {
                    OviewApi HttpApi = new OviewApi();

                    if (HttpApi.deleteItem(zipKey)) {
                        CoverList = deleteContentItemFromCache(zipKey);
                        subscriber.onNext(CoverList);
                        subscriber.onCompleted();
                    } else {
                        Exception exception = DomainException
                                .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                        .getMinor_code());
                        subscriber.onError(exception);
                    }
                } catch (Exception e) {
                    subscriber.onError(DomainException.getDomainException(e));
                }
            }
        });
    }

    /**
     * task to delete item by zip key
     **/
    @Override public List<OviewListModel> retrieveContentCachedList() {

        listCache.restoreCache();
        oviewListBuffer = listCache.getCache();
        coverRequestOffset = oviewListBuffer.size();
        return oviewListBuffer;
    }

    /**
     * task to get a item in the list buffer
     **/
    @Override public OviewListModel retrieveContentListItem(int Pos) {

        try {
            return oviewListBuffer.get(Pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * function to delete item and retrieve a new list
     **/
    private List<OviewListModel> deleteContentItemFromCache(String zipKey) {

        oviewListBuffer = listCache.getCacheWithDeleteItem(zipKey);
        coverRequestOffset = oviewListBuffer.size();
        return oviewListBuffer;
    }

    /**
     * function to add list  to the buffer
     **/
    private void addOviewList(List<OviewListModel> objects) {

        listCache.addCache(objects);
        oviewListBuffer = listCache.getCache();
        coverRequestOffset = oviewListBuffer.size();
    }

    private void initBeforeRefresh() {

        oviewListBuffer.clear();
        listCache.backupCache();
        listCache.cleanCache();
        coverRequestOffset = 0;
    }
}
