package com.leedian.klozr.view.navigator;
import android.app.Activity;
import android.content.Intent;

import com.leedian.klozr.view.activity.ContentListActivity;
import com.leedian.klozr.view.activity.ContentScanActivity;
import com.leedian.klozr.view.activity.StageContentActivity;
import com.leedian.klozr.view.activity.UserLoginActivity;

/**
 * AppNavigator
 *
 * @author Franco
 */
public class AppNavigator {
    static public final String   BUNDLE_CONTENT_ZIP_KEY     = "BUNDLE_CONTENT_ZIP_KEY";
    static public final String   BUNDLE_HAS_PARENT_ACTIVITY = "BUNDLE_HAS_PARENT_ACTIVITY";
    private             Activity viewActivity               = null;

    public AppNavigator(Activity activity) {

        viewActivity = activity;
    }

    public Activity getActivity() {

        return viewActivity;
    }

    public void navigateOpenContentView(String zipKey) {

        Intent intent = new Intent(getActivity(), StageContentActivity.class);
        intent.putExtra(BUNDLE_CONTENT_ZIP_KEY, zipKey);

        getActivity().startActivity(intent);
    }

    public void navigateLoginView() {

        Intent intent = new Intent(getActivity(), UserLoginActivity.class);
        getActivity().startActivity(intent);
    }

    public void navigateHome() {

        Intent intent = new Intent(getActivity(), ContentScanActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    public void navigateOViewList() {

        Intent intent = new Intent(getActivity(), ContentListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    public void navigateToScanView() {

        Intent intent = new Intent(getActivity(), ContentScanActivity.class);
        intent.putExtra(BUNDLE_HAS_PARENT_ACTIVITY, getActivity().getClass().toString());
        getActivity().startActivity(intent);
    }
}
