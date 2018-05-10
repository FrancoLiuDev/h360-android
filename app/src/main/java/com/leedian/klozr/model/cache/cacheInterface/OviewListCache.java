package com.leedian.klozr.model.cache.cacheInterface;
import com.leedian.klozr.model.dataOut.OviewListModel;

import java.util.List;

/**
 * List cache interface for Oview list
 *
 * @author Franco
 */
public interface OviewListCache {
    List<OviewListModel> getCache();

    List<OviewListModel> getCacheWithDeleteItem(String zipKey);

    void addCache(List<OviewListModel> list);

    void writeCache(List<OviewListModel> list);

    void cleanCache();

    void backupCache();

    void restoreCache();
}
