package com.leedian.klozr.view.activity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

import butterknife.Bind;
import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.base.baseView.BaseActivity;
import com.leedian.klozr.model.dataOut.OviewContentModel;
import com.leedian.klozr.presenter.presenterImp.ContentScanPresenterImp;
import com.leedian.klozr.presenter.presenterInterface.ContentScanPresenter;
import com.leedian.klozr.utils.viewUtils.ActionBarTop;
import com.leedian.klozr.utils.viewUtils.CameraProvider;
import com.leedian.klozr.utils.viewUtils.Utils;
import com.leedian.klozr.view.navigator.AppNavigator;
import com.leedian.klozr.view.viewInterface.ViewScanMvp;
import com.leedian.klozr.view.viewStates.ViewContentScanState;

import static com.leedian.klozr.view.navigator.AppNavigator.BUNDLE_HAS_PARENT_ACTIVITY;

/**
 * ContentScanActivity
 *
 * @author Franco
 */
public class ContentScanActivity
        extends BaseActivity<ViewScanMvp, ContentScanPresenter>
        implements ViewScanMvp,
        View.OnClickListener,
        ActionBarTop.ActionButtonEvent,
        CameraProvider.CameraEventHandler {
    /**
     * Top navigation bar
     */
    protected ActionBarTop actionBar;

    /**
     * logo image
     */
    @Bind(R.id.Logo_image)
    ImageView logo_image;

    /**
     * camera preview container
     */
    @Bind(R.id.layout_scan_container)
    RelativeLayout preview_container_layout;

    /**
     * progress percentage bar container
     */
    @Bind(R.id.progress_container)
    RelativeLayout progressContainerLayout;

    /**
     * progress bar
     */
    DonutProgress contentProgressBar;

    private AppNavigator viewNavigator = new AppNavigator(this);

    /**
     * Activity class calling this activity
     */
    private String parentView = "";

    private CameraProvider camera;

    private boolean isHomePage = false;

    View.OnClickListener logo_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (isHomePage) {
                presenter.onUserIconClicked();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            parentView = extras.getString(BUNDLE_HAS_PARENT_ACTIVITY);
        }
        initVal();
        initView();
        initData();
    }

    @Override
    protected int getContentResourceId() {

        return R.layout.activity_scaner_view;
    }

    @Override
    protected void initView() {

        super.initView();
        isHomePage = parentView.equals("");
        actionBar = new ActionBarTop.Builder(this, R.layout.actionbar_menu_bar)
                .setEventListener(this).setDebugMode(false)
                .setBackgroundColor(Color.parseColor("#00000000")).build();
        if (!isHomePage) {
            actionBar.setButtonImage(R.id.btn_left, R.drawable.icon_back);
        }
        camera = new CameraProvider.Builder(this, preview_container_layout).setEventHandler(this)
                .Build();
        logo_image.setOnClickListener(logo_clicked);
        progressContainerLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (camera.isCameraPermission()) {
            startActivityScan();
        } else {
            camera.RequestCameraPermission(CameraProvider.CAMERA_PERMISSIONS_REQUEST_ID);
        }
    }

    @NonNull
    @Override
    public ContentScanPresenter createPresenter() {

        return new ContentScanPresenterImp();
    }

    public void startActivityScan() {

        camera.initControls();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setOviewInformation(boolean isOnline, OviewContentModel info) {

        camera.releaseCamera();
        viewNavigator.navigateOpenContentView(info.getZipkey());
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (requestCode == CameraProvider.CAMERA_PERMISSIONS_REQUEST_ID) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startActivityScan();
            }
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        camera.releaseCamera();
    }

    @Override
    protected void onPause() {

        super.onPause();
        camera.releaseCamera();
    }

    @NonNull
    @Override
    public ViewState<ViewScanMvp> createViewState() {

        return new ViewContentScanState();
    }

    @Override
    public void onNewViewStateInstance() {

    }

    @Override
    public void navigateToOviewContentView(String zipKey) {

        camera.releaseCamera();
        viewNavigator.navigateOpenContentView(zipKey);
    }

    @Override
    public void navigateToLoginView() {

        camera.releaseCamera();
        viewNavigator.navigateLoginView();
    }

    @Override
    public void RestartScan() {

        camera.StartScan();
    }

    @Override
    public void onActionButtonClicked(View view) {

        if (view.getId() == R.id.btn_left) {
            if (!isHomePage) {
                this.finish();
            }
        }
    }

    public void setDownloadProgress(int percent) {

        if (contentProgressBar == null) {
            return;
        }

        contentProgressBar.setProgress(percent);
    }

    public void dismissProgressDialog() {

        if (contentProgressBar == null) {
            return;
        }

        progressContainerLayout.removeView(contentProgressBar);
    }

    public void showProgressDialog() {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Utils.dpToPx(200), Utils
                .dpToPx(200));
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        contentProgressBar = new DonutProgress(this);
        contentProgressBar.setLayoutParams(params);

        contentProgressBar.setFinishedStrokeColor(AppResource.getColor(R.color.color_accent_1));
        contentProgressBar.setFinishedStrokeWidth(Utils.dpToPx(5));

        contentProgressBar.setUnfinishedStrokeColor(AppResource.getColor(R.color.color_white));
        contentProgressBar.setUnfinishedStrokeWidth(Utils.dpToPx(5));

        contentProgressBar.setProgress(0);

        progressContainerLayout.addView(contentProgressBar);
        progressContainerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReadData(String data) {

        presenter.onScanContentCodeData(data);
    }

    @Override
    public void onErrorPermission() {

        showActivityToast("Camera error");
        finish();
    }
}
