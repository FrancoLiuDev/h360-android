package com.leedian.klozr.view.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.base.baseView.BaseActivity;
import com.leedian.klozr.model.dataOut.OviewListModel;
import com.leedian.klozr.presenter.presenterImp.ContentListPresenterImp;
import com.leedian.klozr.presenter.presenterInterface.ContentListPresenter;
import com.leedian.klozr.utils.DialogUtil;
import com.leedian.klozr.utils.viewUtils.ActionBarTop;
import com.leedian.klozr.utils.viewUtils.SpacesItemDecoration;
import com.leedian.klozr.utils.viewUtils.Utils;
import com.leedian.klozr.view.Adapter.ContentListAdapter;
import com.leedian.klozr.view.navigator.AppNavigator;
import com.leedian.klozr.view.viewInterface.ViewContentListMvp;
import com.leedian.klozr.view.viewStates.ViewContentListViewState;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import butterknife.Bind;

/**
 * ContentListActivity
 *
 * @author Franco
 */
public class ContentListActivity
        extends BaseActivity<ViewContentListMvp, ContentListPresenter>
        implements ViewContentListMvp,
        SwipyRefreshLayout.OnRefreshListener,
        ContentListAdapter.ContentListItemClickListener,
        ActionBarTop.ActionButtonEvent {
    /**
     * Top navigation bar
     */
    protected ActionBarTop actionBar;

    /**
     * recycle list view for list content
     */
    @Bind(R.id.cover_list_grid)
    RecyclerView cycle_listView;

    /**
     * refresh  animation layout
     */
    @Bind(R.id.refresh_content_layout)
    SwipyRefreshLayout refresh_content_layout;

    /**
     * loading more animation layout container
     */
    @Bind(R.id.load_more_layout)
    RelativeLayout load_more_layout;

    /**
     * loading more animation
     */
    @Bind(R.id.avi)
    AVLoadingIndicatorView loadMoreAnimation;

    /**
     * list  columns
     */
    private int recycle_layout_columns = 2;

    /**
     * LayoutManager for list layout
     */
    private StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(recycle_layout_columns, StaggeredGridLayoutManager.VERTICAL);

    /**
     * view  Navigator
     */
    private AppNavigator viewNavigator = new AppNavigator(this);

    private boolean isLoadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        initVal();
        initView();
        initData();
    }

    @Override
    protected int getContentResourceId() {

        return R.layout.activity_content_list;
    }

    @Override
    protected void initData() {

        presenter.onFetchListItems();
    }

    @Override
    public void initView() {

        cycle_listView.setLayoutManager(layoutManager);
        ContentListAdapter adapter = new ContentListAdapter(this, null);
        adapter.setContentListItemListener(this);
        cycle_listView.setAdapter(adapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(0);
        cycle_listView.addItemDecoration(decoration);
        cycle_listView.setHasFixedSize(true);
        cycle_listView.setItemViewCacheSize(20);
        cycle_listView.setDrawingCacheEnabled(true);
        cycle_listView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        cycle_listView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Utils.removeOnGlobalLayoutListener(cycle_listView, this);
                    }
                });
        cycle_listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int pastVisibleItems;

            int visibleItemCount;

            int totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    int[] into = new int[recycle_layout_columns];
                    pastVisibleItems = layoutManager.findFirstVisibleItemPositions(into)[0];
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        onRecycleScrollEnd();
                    }
                }
            }
        });
        refresh_content_layout.setOnRefreshListener(this);
        actionBar = new ActionBarTop.Builder(this, R.layout.actionbar_menu_bar)
                .setEventListener(this).setDebugMode(false)
                .setBackgroundColor(AppResource.getColor(R.color.colorActionBarBackground)).build();
        actionBar.setButtonImage(R.id.btn_left, R.drawable.icon_signout);
        actionBar.setButtonImage(R.id.btn_right, R.drawable.icon_scan);
        actionBar.setTextString(AppResource.getString(R.string.display_viewer_list), R.id.textView);
    }

    private void onRecycleScrollEnd() {

        if (isLoadingMore) {
            return;
        }
        showLoadingMore();
    }

    private void showLoadingMore() {

        loadMoreAnimation.show();
        isLoadingMore = true;
        presenter.onLoadMoreListItems();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                load_more_layout.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    private void hideLoadingMore() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                load_more_layout.setVisibility(View.GONE);
                loadMoreAnimation.hide();
                refresh_content_layout.setRefreshing(false);
                isLoadingMore = false;
            }
        }, 1600);
    }

    @NonNull
    @Override
    public ContentListPresenter createPresenter() {

        return new ContentListPresenterImp();
    }

    @NonNull
    @Override
    public ViewState<ViewContentListMvp> createViewState() {

        return new ViewContentListViewState();
    }

    @Override
    public void onNewViewStateInstance() {

    }

    @Override
    public void onRowClicked(int position) {

    }

    @Override
    public void onViewClicked(View v, int position) {

        presenter.onClickListItem(position);
    }

    @Override
    public void navigateToHomeViewOption() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        presenter.onClickConfirmLogout();
                        break;
                }
            }
        };
        DialogUtil.showConfirmYesNo(this, AppResource
                .getString(R.string.confirm_logout_option), AppResource
                .getString(R.string.confirm_ok), AppResource
                .getString(R.string.confirm_cancel), dialogClickListener);
    }

    @Override
    public void onViewDeleteItemClicked(View v, final int position) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        presenter.onClickDeleteListItem(position);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
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
    public void onUpdateViewDataSource(List<OviewListModel> data) {

        ContentListAdapter adapter = (ContentListAdapter) cycle_listView.getAdapter();
        adapter.setContentListDataAndNotifyView(data);
        hideLoadingMore();
    }

    @Override
    public void onUpdateViewDataFailure(String str) {

        hideLoadingMore();
        this.showActivityToast(str);
    }

    @Override
    public void showClickItemOption(final int position, OviewListModel item) {

        presenter.onClickOpenStageListItem(position);
    }

    @Override
    public void navigateToContentViewActivity(OviewListModel item) {

        viewNavigator.navigateOpenContentView(item.getZipkey());
    }

    @Override
    public void navigateToLoginView() {

        viewNavigator.navigateLoginView();
    }

    @Override
    public void navigateToHomeView() {

        viewNavigator.navigateHome();
    }

    @Override
    public void navigateToScanView() {

        viewNavigator.navigateToScanView();
    }

    @Override
    public void onActionButtonClicked(View view) {

        if (view.getId() == R.id.btn_left) {
            presenter.onClickUserIcon();
        }
        if (view.getId() == R.id.btn_right) {
            presenter.onClickScanIcon();
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

        if (isLoadingMore) {
            return;
        }
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            presenter.onFetchListItems();
        } else {
            presenter.onLoadMoreListItems();
        }
    }
}
