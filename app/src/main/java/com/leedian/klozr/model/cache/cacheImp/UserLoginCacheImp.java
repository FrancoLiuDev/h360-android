package com.leedian.klozr.model.cache.cacheImp;
import com.leedian.klozr.BuildConfig;
import com.leedian.klozr.OviewApp;
import com.leedian.klozr.model.cache.cacheInterface.UserLoginCache;
import com.leedian.klozr.model.dataOut.UserInfoModel;
import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.JsonSerializer;

/**
 * user cache for Oview
 *
 * @author Franco
 */
public class UserLoginCacheImp
        implements UserLoginCache
{
    private static final String                         EXTRA_ID_CACHE = "com.leedian.klozr.model.cache.cacheImp.LoginCacheImp";
    private              CacheSerializer<UserInfoModel> jsonSerializer = new JsonSerializer<>(UserInfoModel.class);
    private              DualCache<UserInfoModel>       cache          = new Builder<>(EXTRA_ID_CACHE, BuildConfig.VERSION_CODE, UserInfoModel.class)
            .enableLog()
            .useSerializerInRam(1024 * 10, jsonSerializer)
            .useSerializerInDisk(1024 * 10, true, jsonSerializer, OviewApp.getAppContext())
            .build();

    /**
     * get  cache
     *
     * @return UserInfoModel
     **/
    @Override
    public UserInfoModel getCache() {

        UserInfoModel user;
        user = cache.get("user");
        return user;
    }

    /**
     * write Cache
     *
     * @return UserInfoModel
     **/
    @Override
    public void writeCache(UserInfoModel user) {

        cache.put("user", user);
    }

    /**
     * clean Cache
     *
     **/
    @Override
    public void cleanCache() {

        cache.delete("user");
    }

    /**
     * get if is cached
     * @return boolean
     **/
    @Override
    public boolean isCached() {

        UserInfoModel user;
        user = cache.get("user");
        return user != null;
    }
}
