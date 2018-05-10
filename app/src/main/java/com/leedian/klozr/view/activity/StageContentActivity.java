package com.leedian.klozr.view.activity;

import java.util.List;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.wang.avi.AVLoadingIndicatorView;
import butterknife.Bind;

import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.base.baseView.BaseActivity;
import com.leedian.klozr.model.dataOut.OviewContentModel;
import com.leedian.klozr.model.dataOut.OviewInfoNodeModel;
import com.leedian.klozr.model.dataOut.ViewModelContentBitmap;
import com.leedian.klozr.presenter.presenterImp.StageContentPresenterImp;
import com.leedian.klozr.presenter.presenterInterface.StageContentPresenter;
import com.leedian.klozr.utils.DialogUtil;
import com.leedian.klozr.utils.ImageBlur;
import com.leedian.klozr.utils.viewUtils.ActionBarTop;
import com.leedian.klozr.utils.viewUtils.InfoNode.InfoNodeContentView;
import com.leedian.klozr.utils.viewUtils.InfoNode.view.ImagePosView;
import com.leedian.klozr.utils.viewUtils.Utils;
import com.leedian.klozr.utils.viewUtils.tools.ActionBarInfoNode;
import com.leedian.klozr.utils.viewUtils.tools.ActionBarInfoNodeEdit;
import com.leedian.klozr.utils.viewUtils.tools.ImageViewPositionHandler;
import com.leedian.klozr.utils.viewUtils.tools.ImageViewTouchHandler;
import com.leedian.klozr.view.navigator.AppNavigator;
import com.leedian.klozr.view.viewInterface.ViewContentMvp;
import com.leedian.klozr.view.viewStates.ViewContentState;

/**
 * StageContentActivity
 *
 * @author Franco
 */
public class StageContentActivity
        extends BaseActivity<ViewContentMvp, StageContentPresenter>
        implements ViewContentMvp,
        ImageViewTouchHandler.OnViewEventListener,
        ActionBarTop.ActionButtonEvent,
        ActionBarInfoNode.InfoNodeBottomBarButtonEvent,
        ActionBarInfoNodeEdit.InfoNodeEditButtonEvent,
        View.OnClickListener,
        InfoNodeContentView.OnInfoNodeEventListener {
    /**
     * Oview content image view
     */
    @Bind(R.id.oview_imageView)
    ImagePosView oviewImageView;

    /**
     * Infonode related view
     */
    @Bind(R.id.layout_info_view_container)
    InfoNodeContentView infoViewContainer;

    /**
     * Infonode related view
     */
    //refactor
    @Bind(R.id.layout_info_tool_bar)
    LinearLayout infoViewToolBarLayout;

    /**
     * oview detail information layout
     */
    @Bind(R.id.layout_info_detail_layout)
    LinearLayout infoViewDetailLayout;

    /**
     * oview detail name text view
     */
    @Bind(R.id.info_node_name)
    TextView contentDetailNameText;

    /**
     * oview detail Description text view
     */
    @Bind(R.id.info_node_description)
    TextView contentDetailDescriptionText;

    /**
     * Icon button to edit content name
     */
    @Bind(R.id.icon_edit_info_name)
    ImageView detailEditNameIcon;

    /**
     * Icon button to edit content DescriptionIcon
     */
    @Bind(R.id.icon_edit_info_description)
    ImageView detailEditDescriptionIcon;

    /**
     * Icon button to close detail layout
     */
    @Bind(R.id.image_info_detail_close_icon)
    ImageView closeDetailViewIcon;

    /**
     * content loading animation container layout
     */
    @Bind(R.id.loadingProgressLayout)
    RelativeLayout loadingContainerLayout;

    /**
     * content loading animation view
     */
    @Bind(R.id.avi)
    AVLoadingIndicatorView loadingAnimationView;

    /**
     * Percentage Progress progress
     */
    @Bind(R.id.progress_container)
    RelativeLayout progressContainerLayout;

    DonutProgress contentProgressBar;

    String contentZipKey = "";

    ViewModelContentBitmap viewModelBitmap = null;

    boolean isLogin = false;

    /**
     * Top navigation bar
     */
    private ActionBarTop actionBar;

    /**
     * InfoNode Action tool bottom bar
     */
    private ActionBarInfoNode infoActionBar;

    /**
     * InfoNode Edit(delete/save) tool bottom bar
     */
    private ActionBarInfoNodeEdit infoNodeEditBar;

    /**
     * Content view  Touch  handler
     */
    private ImageViewTouchHandler contentTouchHandler;

    /**
     * Content view  position handler
     */
    private ImageViewPositionHandler contentPositionHandler;

    /**
     * Oview content detail information
     */
    private OviewContentModel currentContent;

    @Override
    protected int getContentResourceId() {

        return R.layout.activity_stage_content;
    }

    @Override
    protected int getToastActivityContainer() {

        return R.id.toast_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        contentZipKey = bundle.getString(AppNavigator.BUNDLE_CONTENT_ZIP_KEY);
        initView();
    }

    @Override
    protected void onStop() {

        super.onStop();
        contentTouchHandler.dismiss();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        releaseProgressDialog();
    }

    @Override
    protected void onResume() {

        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onInvalidView() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();
            }
        }, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            presenter.onBackButtonClicked();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initView() {

        super.initView();
        isLogin = presenter.getIsUserLogin();
        contentTouchHandler = new ImageViewTouchHandler();
        contentTouchHandler.setOnViewEventListener(this);
        contentPositionHandler = new ImageViewPositionHandler(oviewImageView, oviewImageView
                .getViewParentSizeRectF(), new RectF());
        oviewImageView.setOnTouchListener(contentTouchHandler);
        oviewImageView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        initData();
                        Utils.removeOnGlobalLayoutListener(oviewImageView, this);
                    }
                });

        actionBar = new ActionBarTop.Builder(this, R.layout.actionbar_menu_bar)
                .setEventListener(this).setDebugMode(false)
                .setBackgroundColor(AppResource.getColor(R.color.colorActionBarBackground)).build();
        actionBar.setButtonImage(R.id.btn_left, R.drawable.icon_toolbar_back);
        actionBarShowContentActionVisible(false);

        infoActionBar = ActionBarInfoNode.buildInfoNodeBar(this, infoViewToolBarLayout);
        infoActionBar.setEvent(this);
        infoActionBar.setBarVisible(true);

        infoNodeEditBar = ActionBarInfoNodeEdit.buildInfoNodeBar(this, infoViewToolBarLayout);
        infoNodeEditBar.setEvent(this);
        infoNodeEditBar.setBarVisible(false);

        detailEditNameIcon.setOnClickListener(this);
        detailEditDescriptionIcon.setOnClickListener(this);

        infoViewContainer.setTargetImageView(oviewImageView);
        infoViewContainer.setActionListener(this);
        closeDetailViewIcon.setOnClickListener(this);

        if (!isLogin) {
            detailEditNameIcon.setVisibility(View.INVISIBLE);
            detailEditDescriptionIcon.setVisibility(View.INVISIBLE);
        }

        progressContainerLayout.setVisibility(View.GONE);
        progressContainerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });

        loadingContainerLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {

        MaterialDialog.InputCallback inputName = new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                presenter.onEditInfoNodeContentName(input.toString());
            }
        };
        MaterialDialog.InputCallback inputDescription = new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                presenter.onEditInfoNodeContentDescription(input.toString());
            }
        };
        if (v.equals(detailEditNameIcon)) {
            DialogUtil.showInputDialog(this, AppResource
                    .getString(R.string.content_item_name), AppResource
                    .getString(R.string.please_input), "", contentDetailNameText
                    .getText().toString(), AppResource
                    .getString(R.string.confirm_ok), AppResource
                    .getString(R.string.confirm_cancel), 2, AppResource
                    .getValue(R.integer.content_name_string_max), inputName);
        }
        if (v.equals(detailEditDescriptionIcon)) {
            DialogUtil.showInputDialog(this, AppResource
                    .getString(R.string.content_item_description), AppResource
                    .getString(R.string.please_input), "", contentDetailDescriptionText
                    .getText().toString(), AppResource
                    .getString(R.string.confirm_ok), AppResource
                    .getString(R.string.confirm_cancel), 2, AppResource
                    .getValue(R.integer.content_description_string_max), inputDescription);
        }
        if (v.equals(closeDetailViewIcon)) {
            presenter.onInfoDetailIconClicked();
        }
    }

    @NonNull
    @Override
    public StageContentPresenter createPresenter() {

        return new StageContentPresenterImp();
    }

    @NonNull
    @Override
    public ViewState<ViewContentMvp> createViewState() {

        return new ViewContentState();
    }

    @Override
    public void onNewViewStateInstance() {

    }

    @Override
    protected void initData() {

        int containWidth = oviewImageView.getWidth();
        int containHeight = oviewImageView.getHeight();

        presenter.setContentLayoutSize(containWidth, containHeight);
        presenter.onStartShowContentStage(contentZipKey);
    }

    @Override
    public void setImageViewBmp(ViewModelContentBitmap bitmap) {

        viewModelBitmap = bitmap;
        contentPositionHandler.setNotifyBitmapSize(bitmap.getSize());
        oviewImageView.setContentImage(viewModelBitmap.getBitmap());
        contentTouchHandler.setIsReady(true);
    }

    @Override
    public void setImageBufferLoadSuccess(int imageCount) {

        presenter.showFirstBitmap();
    }

    @Override
    public void setScaledImageLoadSuccess(ViewModelContentBitmap bitmap) {

        if (viewModelBitmap.getIndex() != bitmap.getIndex()) {
            return;
        }
        if (oviewImageView.isMatrixScaleMode()) {
            contentPositionHandler.setNotifyBitmapSize(bitmap.getSize());
            oviewImageView.setContentImage(bitmap.getBitmap());
        }
    }

    public void showInfoNode(String text, PointF pos, float radius) {

        infoViewContainer.setInfoNodeInfo(text, pos.x, pos.y, radius);
        infoViewContainer.setInfoNodeEditor(false);
        showInfoNodeBg(true);
        showInfoNodeCircle();
    }

    public void showInfoNodeEdit(String text, PointF pos, float radius, boolean addMode) {

        infoViewContainer.setInfoNodeInfo(text, pos.x, pos.y, radius);
        infoViewContainer.setInfoNodeEditor(isLogin);
        infoViewContainer.setAddMode(addMode);
        showInfoNodeBg(false);
        showInfoNodeEditCircle();
    }

    public void showInfoNodeBg(boolean blur) {

        Bitmap showBitmap;
        Bitmap blurImg;

        Canvas blueCanvas;
        Paint eraser = new Paint();
        eraser.setColor(0xFFFFFFFF);
        eraser.setFlags(Paint.ANTI_ALIAS_FLAG);
        Bitmap source = viewModelBitmap.getBitmap();
        Bitmap infoBitmap = source.copy(Bitmap.Config.ARGB_8888, true);
        showBitmap = infoBitmap;
        if (blur) {
            blurImg = ImageBlur.fastBlur(this, infoBitmap, 22);
            blueCanvas = new Canvas(blurImg);
            blueCanvas.drawColor(0x22000000);
            showBitmap = blurImg;
        }

        oviewImageView.setContentImage(showBitmap);
    }

    public void showInfoNodeEditCircle() {

        infoViewContainer.showInfoEditCircle();
    }

    public void showInfoNodeCircle() {

        infoViewContainer.showInfoCircle(viewModelBitmap.getBitmap());
    }

    @Override
    public void showOviewDetail(boolean visible) {

        if (visible) {
            infoViewDetailLayout.setVisibility(View.VISIBLE);
            infoViewToolBarLayout.setVisibility(View.GONE);
        } else {
            infoViewDetailLayout.setVisibility(View.GONE);
            infoViewToolBarLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideInfoNodeCircle() {

        infoViewContainer.dismissInfoNode();

        if (viewModelBitmap != null) {
            oviewImageView.setContentImage(viewModelBitmap.getBitmap());
        }
    }

    @Override
    public void resetContentScaleDefault() {

        oviewImageView.setScaleTypeDefault();
        oviewImageView.setContentImage(viewModelBitmap.getBitmap());
    }

    @Override
    public void disableContentScale() {

        contentTouchHandler.setScalable(false);
    }

    @Override
    public void enableContentScale() {

        contentTouchHandler.setScalable(true);
    }

    @Override
    public void resetInfoButtons() {

        infoActionBar.reset();
    }

    @Override
    public void toggleInfoButtonsAdd() {

        infoActionBar.updateBarToAddMode();
    }

    @Override
    public void toggleInfoButtonsEdit() {

    }

    private void actionBarShowContentActionVisible(boolean visible) {

        if (visible) {
            actionBar.setButtonImage(R.id.btn_right, R.drawable.information);
            actionBar.setButtonImage(R.id.btn_right2, R.drawable.icon_auto_rotation);
        } else {
            actionBar.clearButtonImage(R.id.btn_right);
            actionBar.clearButtonImage(R.id.btn_right2);
        }
    }

    @Override
    public void showEditButtonBar(boolean isShow) {

        if (isShow) {
            infoNodeEditBar.setBarVisible(true);
            infoActionBar.setBarVisible(false);
        } else {
            infoNodeEditBar.setBarVisible(false);
            infoActionBar.setBarVisible(true);
        }
    }

    @Override
    public void closeView() {

        this.finish();
    }

    @Override
    public void setInterfaceClickable(boolean clickable) {

        actionBarShowContentActionVisible(clickable);
        contentTouchHandler.setIsReady(clickable);
        infoActionBar.setButtonClickable(clickable);
        infoNodeEditBar.setButtonClickable(clickable);
    }

    @Override
    public void setActionButtonVisible(boolean visible) {

        actionBarShowContentActionVisible(visible);
    }

    @Override
    public void onBmpViewStartTouch(float posX, float posY) {

        presenter.onContentViewTouched();
    }

    @Override
    public void onBmpViewStartPointTouch(float posX, float posY, float distance) {

        if (!oviewImageView.isMatrixScaleMode()) {
            setImageToScaleMode();
        }
    }

    private void setImageToScaleMode() {

        oviewImageView.setScaleTypeMatrix();
        contentPositionHandler = new ImageViewPositionHandler(oviewImageView, oviewImageView
                .getViewParentSizeRectF(), oviewImageView.getBitmapSizeRectF());
        contentPositionHandler.setScaleMode(true);
        contentPositionHandler.initCorpCenter();
        contentPositionHandler.applyMatrix(oviewImageView);
    }

    @Override
    public void onBmpViewStartScale(PointF mid) {

        if (contentPositionHandler != null) {
            contentPositionHandler.setScaleMid(mid);
            contentPositionHandler.applyMatrix(oviewImageView);
        }
    }

    @Override
    public void onBmpViewStartScroll(PointF mid) {

    }

    @Override
    public void onBmpViewDrag(float DisX, float DisY) {

        if (contentPositionHandler == null) {
            return;
        }
        contentPositionHandler.onTriggerBmpViewPosMove(DisX, DisY);
        contentPositionHandler.applyMatrix(oviewImageView);
    }

    @Override
    public void onBmpViewZoom(float scale, float midX, float midY) {

        if (contentPositionHandler == null) {
            return;
        }
        contentPositionHandler.onBmpViewScaleChange(scale);
        contentPositionHandler.applyMatrix(oviewImageView);
    }

    @Override
    public void onBmpViewScrollRight() {

        presenter.showNextBitmap();
    }

    @Override
    public void onBmpViewScrollLeft() {

        presenter.showPreviousBitmap();
    }

    @Override
    public void onBmpViewTouchRelease() {

        if (contentPositionHandler == null) {
            return;
        }
        contentPositionHandler.onBmpViewTouchRelease();
    }

    @Override
    public void onVelocityEvent(float Velocity) {

        presenter.onTouchVelocityEvent(Velocity);
    }

    @Override
    public void onDoubleTab() {

        if (oviewImageView.isMatrixScaleMode()) {
            oviewImageView.setScaleTypeDefault();
        }
    }

    @Override
    public void onNodeClicked(int index) {

        presenter.onInfoNodeButtonClicked(index);
    }

    @Override
    public void onNodeAddNew() {

        presenter.onInfoNodeAddNewButtonClicked();
    }

    @Override
    public void onCloseNodeAdd() {

        presenter.onInfoNodeAddCloseClicked();
    }

    @Override
    public void onNodeEditNode() {

        presenter.onInfoNodeEditButtonClicked();
    }

    @Override
    public void showAddInfoNode(OviewInfoNodeModel NewNode) {

        PointF pos = new PointF(Float.parseFloat(NewNode.getX()), Float.parseFloat(NewNode.getY()));
        float radius = Float.parseFloat(NewNode.getR());

        showInfoNodeEdit(NewNode.getInfo(), pos, radius, true);
        showActivityToastTips(AppResource.getString(R.string.tip_add_info_node));
    }

    @Override
    public void showEditInfoNode(int nodeIndex, OviewInfoNodeModel node) {

        PointF pos = new PointF(Float.parseFloat(node.getX()), Float.parseFloat(node.getY()));
        float radius = Float.parseFloat(node.getR());

        showInfoNodeEdit(node.getInfo(), pos, radius, false);
    }

    @Override
    public void onActionButtonClicked(View view) {

        if (view.getId() == R.id.btn_left) {
            presenter.onBackButtonClicked();
        }
        if (view.getId() == R.id.btn_right2) {
            presenter.onAutoRotateClicked();
        }
        if (view.getId() == R.id.btn_right) {
            presenter.onInfoDetailIconClicked();
        }
    }

    @Override
    protected void showActivityLoading() {

        loadingContainerLayout.setVisibility(View.VISIBLE);
        loadingAnimationView.show();
    }

    @Override
    protected void hideActivityLoading() {

        loadingContainerLayout.setVisibility(View.GONE);
        loadingAnimationView.hide();
    }

    private void releaseProgressDialog() {

        progressContainerLayout.setVisibility(View.GONE);
        dismissProgressDialog();
    }

    public void setDownloadProgress(int percent) {

        contentProgressBar.setProgress(percent);
    }

    public void dismissProgressDialog() {

        contentProgressBar = null;
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
    public void setOviewInformationLoadSuccess(boolean isOnline, OviewContentModel info) {

        int nodeCnt;

        currentContent = info;
        updateInfoNodeDisplayData(info);
        List<OviewInfoNodeModel> nodeList = info.getTrait();

        nodeCnt = (nodeList != null) ?
                (nodeList.size()) :
                (0);
        infoActionBar.setInfoNodeClickableItem(nodeCnt);

        if ((!isLogin)) {
            infoActionBar.setIsEditor(false);
        } else {
            infoActionBar.setIsEditor(true);
        }

        presenter.onStartShowContent();
    }

    private void updateInfoNodeDisplayData(OviewContentModel info) {

        actionBar.setTextString(info.getName(), R.id.textView);
        contentDetailNameText.setText(currentContent.getName());
        contentDetailDescriptionText.setText(currentContent.getAbout());
    }

    @Override
    public void setOviewInformationNode(int index, OviewInfoNodeModel node) {

        PointF pos = new PointF(Float.parseFloat(node.getX()), Float.parseFloat(node.getY()));
        float radius = Float.parseFloat(node.getR());

        showUpdateInfoNodeDot(index);
        updateInterfaceInfoNode();
        showInfoNode(node.getInfo(), pos, radius);
    }

    private void updateInterfaceInfoNode() {

    }

    private void showUpdateInfoNodeDot(int index) {

        infoActionBar.updateInfoDotButton(index);
    }

    @Override
    public void setProgressPercent(int percent) {

        setDownloadProgress(percent);
    }

    @Override
    public void setProgressDownloadStart() {

        showProgressDialog();
    }

    @Override
    public void setProgressDownloadFinish() {

        dismissProgressDialog();
    }

    @Override
    public void setProgressDownloadComplete() {

        releaseProgressDialog();
    }

    @Override
    public boolean onInfoAddFinishEdit() {

        OviewInfoNodeModel model = infoViewContainer
                .makeInfoNodeModelByBitmapIndex(this.viewModelBitmap.getIndex());
        presenter.onAddNewInfoNode(model);
        return false;
    }

    @Override
    public void onNodeRemove() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        presenter.onRemoveInfoNode();
                        break;
                }
            }
        };
        DialogUtil.showConfirmYesNo(this, AppResource
                .getString(R.string.confirm_delete_option), AppResource
                .getString(R.string.confirm_ok), AppResource
                .getString(R.string.confirm_cancel), dialogClickListener);
    }

    @Override
    public void onNodeFinishEdit() {

        OviewInfoNodeModel model = infoViewContainer
                .makeInfoNodeModelByBitmapIndex(this.viewModelBitmap.getIndex());

        presenter.onUpdateInfoNode(model);
    }
}



