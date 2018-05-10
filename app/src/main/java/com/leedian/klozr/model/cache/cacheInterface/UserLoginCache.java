package com.leedian.klozr.model.cache.cacheInterface;
import com.leedian.klozr.model.dataOut.UserInfoModel;

/**
 * User cache interface
 *
 * @author Franco
 */
public interface UserLoginCache {
    UserInfoModel getCache();

    void writeCache(UserInfoModel user);

    void cleanCache();

    boolean isCached();
}
