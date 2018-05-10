package com.leedian.klozr;
import android.app.Application;
import android.content.Context;

/**
 * OviewApp
 *
 * @author Franco
 */
public class OviewApp
        extends Application
{
    private static Context context;

    public static Context getAppContext() {

        return OviewApp.context;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        OviewApp.context = getApplicationContext();
        AppManager.initApp(getApplicationContext());
    }
}

