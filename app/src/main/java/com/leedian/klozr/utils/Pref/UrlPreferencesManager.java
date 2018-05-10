package com.leedian.klozr.utils.Pref;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * UrlPreferencesManager
 *
 * @author Franco
 */
public class UrlPreferencesManager {
    private static final String PREFERENCES_NAME = "OVIEW_SERVER_IP";
    private static final String KEY              = "IP";
    private Notifier          ChangeEvent;
    private SharedPreferences sharedPreferences;

    public UrlPreferencesManager(Context context) {

        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setChangeEvent(Notifier changeEvent) {

        ChangeEvent = changeEvent;
    }

    public String getServerUrl() {

        return sharedPreferences.getString(KEY, "0.0.0.0");
    }

    public void setServerUrl(String ip) {

        sharedPreferences.edit().putString(KEY, ip).apply();
        ChangeEvent.onUrlChange(ip);
    }

    public interface Notifier {
        void onUrlChange(String data);
    }
}
