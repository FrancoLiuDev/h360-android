package com.leedian.klozr.model.cache.cacheImp;

import java.util.ArrayList;
import java.util.List;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.JsonSerializer;

import com.leedian.klozr.BuildConfig;
import com.leedian.klozr.OviewApp;
import com.leedian.klozr.model.cache.cacheInterface.OviewListCache;
import com.leedian.klozr.model.dataOut.OviewListModel;

/**
 * List cache for Oview list
 *
 * @author Franco
 */
public class OviewListCacheImp
        implements OviewListCache {
    private static final String EXTRA_ID_CACHE = "com.leedian.klozr.model.cache.cacheImp.KlozrListCacheImp";
    private final String LIST_CACHE = "list";
    private final String LIST_CACHE_BACKUP = "list_backup";
    private CacheSerializer<List> jsonSerializerList = new JsonSerializer<List>(List.class);
    private DualCache<List> listCache = new Builder<>(EXTRA_ID_CACHE, BuildConfig.VERSION_CODE, List.class)
            .enableLog()
            .useSerializerInRam(1024 * 10, jsonSerializerList)
            .useSerializerInDisk(1024 * 10, true, jsonSerializerList, OviewApp.getAppContext())
            .build();

    /**
     * Get current cache
     *
     * @return Cache List
     **/
    private List<OviewListModel> getCurrentCache() {

        List<OviewListModel> currentList;
        currentList = listCache.get(LIST_CACHE);

        if (currentList == null) {
            currentList = new ArrayList<OviewListModel>(0);
        }
        return currentList;
    }

    /**
     * Update current cache List
     *
     * @param listNewCache new cache
     **/
    private void putCurrentCache(List<OviewListModel> listNewCache) {

        listCache.put(LIST_CACHE,
                listNewCache);
    }

    /**
     * Get Current Backup Cache
     *
     * @return back up Cache List
     **/
    private List<OviewListModel> getCurrentBackupCache() {

        List<OviewListModel> backupList = listCache.get(LIST_CACHE_BACKUP);

        if (backupList == null) {
            backupList = new ArrayList<OviewListModel>(0);
        }

        return backupList;
    }

    /**
     * add a list of cache to current cache
     *
     * @param list a cache list
     **/
    @Override
    public void addCache(List<OviewListModel> list) {

        List<OviewListModel> currentList = getCurrentCache();
        currentList.addAll(list);
        writeCache(currentList);
    }

    /**
     * get curent cache
     *
     * @param list a cache list
     * @return Cache List
     **/
    @Override
    public List<OviewListModel> getCache() {

        return getCurrentCache();
    }

    /**
     * write list to current Cache
     *
     * @param list a cache list
     **/
    @Override
    public void writeCache(List<OviewListModel> list) {

        listCache.put(LIST_CACHE,
                list);
    }

    /**
     * clean Cache
     *
     **/
    @Override
    public void cleanCache() {

        listCache.delete(LIST_CACHE);
    }

    /**
     * backup cache to back up
     *
     **/
    @Override
    public void backupCache() {

        List<OviewListModel> ItemList = getCurrentCache();

        if (ItemList == null) {
            return;
        }

        listCache.delete(LIST_CACHE_BACKUP);
        listCache.put(LIST_CACHE_BACKUP,
                ItemList);
    }

    /**
     * restore Cache
     *
     **/
    @Override
    public void restoreCache() {

        listCache.delete(LIST_CACHE);
        List<OviewListModel> ItemList = getCurrentBackupCache();
        listCache.put(LIST_CACHE,
                ItemList);
    }

    /**
     * get new list after delete a zipkey in cache
     *
     * @param zipKey a zip key
     * @return Cache List
     **/
    @Override
    public List<OviewListModel> getCacheWithDeleteItem(String zipKey) {

        List<OviewListModel> ItemList = getCurrentCache();
        ArrayList<OviewListModel> ItemArray = new ArrayList<OviewListModel>(ItemList);

        for (OviewListModel item : ItemArray) {
            if (item.getZipkey()
                    .equals(zipKey)) {
                ItemArray.remove(item);
                break;
            }
        }

        putCurrentCache(ItemArray);
        return getCurrentCache();
    }
}
