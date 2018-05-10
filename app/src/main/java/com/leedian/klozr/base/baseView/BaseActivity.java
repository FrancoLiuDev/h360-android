package com.leedian.klozr.base.baseView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.hannesdorfmann.mosby.mvp.MvpView;

import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.presenter.presenterInterface.BasePresenter;
import com.leedian.klozr.utils.DialogUtil;
import com.leedian.klozr.utils.viewUtils.ActivityUIUpdate;
import com.leedian.klozr.utils.viewUtils.ToastUtil;
import com.leedian.klozr.utils.viewUtils.Utils;
import com.leedian.klozr.view.navigator.AppNavigator;

/**
 * Activity for Mvc Activity in this App
 * <p>
 * This activity is used as Base Activity .
 *
 * @author Franco
 */
public abstract class BaseActivity<V extends MvpView, P extends BasePresenter<V>>
        extends BaseViewStateActivity<V, P>
        implements BaseMvpView

{

    private boolean keyboardShown = false;
    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;
    private ProgressDialog progress;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            int heightDiff = rootLayout.getRootView()
                    .getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT)
                    .getTop();
            LocalBroadcastManager broadcastManager = LocalBroadcastManager
                    .getInstance(BaseActivity.this);

            if (heightDiff < 300) {
                if (!keyboardShown) return;
                onKeyBoardHide();
                keyboardShown = false;
                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                if (keyboardShown) return;
                int keyboardHeight = heightDiff - contentViewTop;
                onKeyBoardShown();
                keyboardShown = true;
                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight",
                        keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };

    /**
     * super Activity onCreate
     *
     * @param savedInstanceState Bundle
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(getContentResourceId());
    }

    /**
     * onDestroy
     **/
    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (keyboardListenersAttached) {
            Utils.removeOnGlobalLayoutListener(rootLayout, keyboardLayoutListener);
        }
    }

    /**
     * abstract function get resource id
     **/
    protected abstract int getContentResourceId();

    /**
     * initial keyboard config
     **/
    protected void initKeyboardConfig(int Id) {

        if (keyboardListenersAttached) {
            return;
        }
        rootLayout = (ViewGroup) findViewById(Id);
        rootLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(keyboardLayoutListener);
        keyboardListenersAttached = true;
    }

    /*protected void registerKeyboardCallBack() {

    }*/

    /**
     * Navigate To guest home page
     **/
    @Override
    public void navigateToAppHomeActivity() {

        AppNavigator viewNavigator = new AppNavigator(BaseActivity.this);
        viewNavigator.navigateHome();
    }

    /**
     * fire from presenter to determine if a view is Invalid
     **/
    @Override
    public void reactiveAsInvalidView() {

        onInvalidView();
    }

    /**
     * Navigate to home activity with confirm dialog
     **/
    @Override
    public void navigateToAppHomeActivityCauseReAuth() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        AppNavigator viewNavigator = new AppNavigator(BaseActivity.this);
                        viewNavigator.navigateHome();
                        break;
                }
            }
        };
        DialogUtil.showConfirmOnlyConfirm(this,
                AppResource.getString(R.string.confirm_logout_confirm),
                AppResource.getString(R.string.confirm_ok),
                dialogClickListener
        );
    }

    /**
     * show Progress Dialog from presenter
     **/
    @Override
    public void showLoadingDialog() {

        showActivityLoading();
    }

    /**
     * hide Progress Dialog from presenter
     **/
    @Override
    public void hideLoadingDialog() {

        hideActivityLoading();
    }

    /**
     * show Progress Dialog from presenter
     **/
    protected void showActivityLoading() {

        if (progress == null) {
            progress = new ProgressDialog(this);
        }

        progress.setTitle("執行中");
        progress.setMessage("請稍後...");
        progress.setCancelable(false);
        progress.show();
    }

    /**
     * hide Progress Dialog from presenter
     **/
    protected void hideActivityLoading() {

        progress.dismiss();
    }

    /**
     * display Error Response massage
     *
     * @param msg massage
     **/
    @Override
    public void displayErrorResponse(String msg) {

        showActivityToast(msg);
    }

    /**
     * display tip Toast  massage
     *
     * @param msg massage
     **/
    @Override
    public void displayTipMassage(String msg) {

        showActivityToastTips(msg);
    }

    /**
     * show Activity Toast
     *
     * @param msg massage
     **/
    protected void showActivityToast(String msg) {

        ToastUtil.ShowToastWithDismissIcon(this,
                msg,
                ToastUtil.ToastStyle.TYPE_WARNING,
                getToastActivityContainer());
    }

    /**
     * show Activity Toast Tips
     *
     * @param msg massage
     **/
    protected void showActivityToastTips(String msg) {

        ToastUtil.ShowToastWithDismissIcon(this,
                msg,
                ToastUtil.ToastStyle.TYPE_TIP,
                getToastActivityContainer());
    }

    /**
     * get id for Toast
     **/
    protected int getToastActivityContainer() {

        return 0;
    }

    /**
     * start Runnable thread  from presenter
     *
     * @param msg massage
     **/
    public void updateViewInMainThread(ActivityUIUpdate Runnable) {

        runOnUiThread(Runnable);
    }

    /**
     * base initVal
     **/
    protected void initVal() {

    }

    /**
     * base initView
     **/
    protected void initView() {

    }

    /**
     * base initData
     **/
    protected void initData() {

    }

    /**
     * base onKeyBoardShown
     **/
    protected void onKeyBoardShown() {

    }

    /**
     * base onKeyBoardHide
     **/
    protected void onKeyBoardHide() {

    }

    /**
     * base onInvalidView
     **/
    protected void onInvalidView() {

    }
}
