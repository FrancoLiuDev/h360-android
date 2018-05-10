package com.leedian.klozr;
import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;

import com.blankj.utilcode.utils.FileUtils;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.leedian.klozr.model.dataOut.UserInfoModel;
import com.leedian.klozr.model.restapi.UrlMessagesConstants;
import com.leedian.klozr.presenter.presenterImp.task.taskImp.UserManagerImp;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.UserManager;
import com.leedian.klozr.utils.Pref.UrlPreferencesManager;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * AppManager
 *
 * @author Franco
 */
public class AppManager {
    static Context               AppContext;
    static UrlPreferencesManager ServerIpPref;

    static void initApp(Context context) {

        //refactor
        AppContext = context;

        initPicasso();
        initPreferences();

        UrlMessagesConstants.init();
        UrlMessagesConstants.setStrHttpServiceRoot(ServerIpPref.getServerUrl());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private static void initPreferences() {

        ServerIpPref = new UrlPreferencesManager(AppContext);
        ServerIpPref.setChangeEvent(new UrlPreferencesManager.Notifier() {
            @Override
            public void onUrlChange(String data) {

                UrlMessagesConstants.setStrHttpServiceRoot(ServerIpPref.getServerUrl());
            }
        });
    }

    static public String getPrefAddress() {

        return ServerIpPref.getServerUrl();
    }

    static public void setPrefAddress(String ip) {

        ServerIpPref.setServerUrl(ip);
    }

    private static void initPicasso() {

        int                        cacheSize   = 10 * 1024 * 1024;
        com.squareup.picasso.Cache memoryCache = new LruCache(Integer.MAX_VALUE);
        Cache                      cache       = new Cache(getHttpCacheDir(), cacheSize);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        Request newRequest = chain.request().newBuilder()
                                                  .addHeader("k-Token", "abc123")
                                                  .build();
                        return chain.proceed(newRequest);
                    }
                }).cache(cache)
                .build();
        Picasso.Builder builder = new Picasso.Builder(AppContext).memoryCache(memoryCache);
        builder.downloader(new OkHttp3Downloader(client));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }

    static public boolean isUserLogin() {

        UserManager userManageTask = new UserManagerImp();
        return userManageTask.isUserLogin();
    }

    static public UserInfoModel getUserLoginInfoData() {

        UserManager userManageTask = new UserManagerImp();
        return userManageTask.getUserLoginInfoData();
    }

    static public String getContentDownloadDir() {

        if (!isDevelopFileFolderDebugMode()) {
            return AppContext.getFilesDir().getAbsolutePath() + "/download";
        } else {
            return Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath() + "/Oview";
        }
    }

    static public String getOviewInfoFileDir() {

        if (!isDevelopFileFolderDebugMode()) {
            return AppContext.getFilesDir().getAbsolutePath() + "/Info";
        } else {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                              .getPath() + "/OviewInfo";
        }
    }

    static public String getOviewContentDir() {

        if (!isDevelopFileFolderDebugMode()) {
            return AppContext.getFilesDir().getAbsolutePath() + "/content";
        } else {
            return Environment
                           .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Oview";
        }
    }

    static public File getHttpCacheDir() {

        String Dir = "";
        if (!isDevelopFileFolderDebugMode()) {
            Dir = AppContext.getFilesDir().getAbsolutePath() + "/HttpCache";
        } else {
            Dir = Environment
                          .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/HttpCache";
        }
        FileUtils.createOrExistsDir(Dir);
        return new File(Dir);
    }

    private static boolean isDevelopFileFolderDebugMode() {

        return false;
    }
}
