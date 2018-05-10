package com.leedian.klozr.utils.viewUtils.tools;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.utils.viewUtils.ActionBarBottom;

/**
 * ActionBarInfoNode
 *
 * @author Franco
 */
public class ActionBarInfoNode
        implements ActionBarBottom.BottomBarButtonEvent
{
    private int[] nodeIds = {R.id.btn_info_1, R.id.btn_info_2, R.id.btn_info_3, R.id.btn_info_4,
                             R.id.btn_info_5};
    private int                          itemCnt;
    private boolean                      isShowingDot;
    private boolean                      isAddMode;
    private boolean                      isClickable;
    private boolean                      isEditMode;
    private int                          dotIndex;
    private Context                      context;
    private ViewGroup                    parent;
    private ActionBarBottom              infoNodeBar;
    private InfoNodeBottomBarButtonEvent event;
    private ImageView                    addButton;
    private TextView                     editNodeButton;

    private ActionBarInfoNode(Context context, ViewGroup parent) {

        this.context = context;
        this.parent = parent;
    }

    public static ActionBarInfoNode buildInfoNodeBar(Context context, ViewGroup parent) {

        ActionBarInfoNode bar = new ActionBarInfoNode(context, parent);
        bar.init();
        return bar;
    }

    public void setEvent(InfoNodeBottomBarButtonEvent event) {

        this.event = event;
    }

    public void setBarVisible(boolean visible) {

        if (visible) { infoNodeBar.setVisibility(View.VISIBLE); } else {
            infoNodeBar.setVisibility(View.GONE);
        }
    }

    private void init() {

        infoNodeBar = new ActionBarBottom.Builder(context, R.layout.actionbar_infonode_command_bar)
                .setParent(parent)
                .setToolBarListener(this)
                .setDebugMode(false)
                .setBackgroundColor(AppResource.getColor(R.color.colorActionBarBackground))
                .build();

        infoNodeBar.setButtonImage(R.id.btn_toolbar_right, R.drawable.icon_infonode_edit);
        infoNodeBar.setButtonTextView(R.id.btn_edit_info);

        addButton = (ImageView) infoNodeBar.findViewById(R.id.btn_toolbar_right);
        addButton.setVisibility(View.INVISIBLE);

        editNodeButton = (TextView) infoNodeBar.findViewById(R.id.btn_edit_info);
        editNodeButton.setVisibility(View.INVISIBLE);
    }

    public void updateInfoDotButton(int index) {

        isShowingDot = true;
        dotIndex = index;
        isAddMode = false;
        updateBarDots();
        updateEditButton();
    }

    public void updateBarToAddMode() {

        isAddMode = true;
        infoNodeBar.setButtonImage(R.id.btn_toolbar_right, R.drawable.icon_infonode_edit_close);

        updateBarDots();
        updateEditButton();
    }

    public void reset() {

        isAddMode = false;
        isShowingDot = false;
        infoNodeBar.setButtonImage(R.id.btn_toolbar_right, R.drawable.icon_infonode_edit);

        updateBarDots();
        updateEditButton();
    }

    public void setIsEditor(boolean isEditor) {

        isEditMode = isEditor;
        updateEditButton();
    }

    public void setInfoNodeClickableItem(int cnt) {

        itemCnt = cnt;
        updateBarDots();
    }

    public void setButtonClickable(boolean clickable) {

        isClickable = clickable;

        updateEditButton();
        updateBarDots();
    }

    private void updateBarDots() {

        clearInfoNodeClickable();

        if (isAddMode) { return; }

        for (int i = 0; i < itemCnt; i++) {
            infoNodeBar.setButtonImage(nodeIds[i], R.drawable.icon_infonode);
            infoNodeBar.setButtonClickable(nodeIds[i], isClickable);
        }

        if (!isShowingDot) { return; }

        infoNodeBar.setButtonImage(nodeIds[dotIndex], R.drawable.icon_infonode_select);
        infoNodeBar.setButtonClickable(nodeIds[dotIndex], isClickable);
    }

    private void updateEditButton() {

        int showAdd  = View.GONE;
        int showEdit = View.GONE;

        do {

            if (!isEditMode) { break; }

            if (isShowingDot) {
                showAdd = View.GONE;
                showEdit = View.VISIBLE;
            } else {

                if (itemCnt < 5) {
                    showAdd = View.VISIBLE;
                } else {
                    showAdd = View.GONE;
                }

                showEdit = View.GONE;
            }

            break;
        } while (true);

        editNodeButton.setVisibility(showEdit);
        addButton.setVisibility(showAdd);
    }

    private void clearInfoNodeClickable() {

        for (int nodeId : nodeIds) {
            infoNodeBar.clearButtonImage(nodeId);
        }
    }

    @Override
    public void onButtonClicked(View view) {

        if (view.getId() == R.id.btn_info_1) {
            this.event.onNodeClicked(0);
        } else if (view.getId() == R.id.btn_info_2) {
            this.event.onNodeClicked(1);
        } else if (view.getId() == R.id.btn_info_3) {
            this.event.onNodeClicked(2);
        } else if (view.getId() == R.id.btn_info_4) {
            this.event.onNodeClicked(3);
        } else if (view.getId() == R.id.btn_info_5) {
            this.event.onNodeClicked(4);
        } else if (view.getId() == R.id.btn_toolbar_right) {

            if (isAddMode) { this.event.onCloseNodeAdd(); } else { this.event.onNodeAddNew(); }
        } else if (view.getId() == R.id.btn_edit_info) {
            this.event.onNodeEditNode();
        }
    }

    public interface InfoNodeBottomBarButtonEvent {
        void onNodeClicked(int index);

        void onNodeAddNew();

        void onCloseNodeAdd();

        void onNodeEditNode();
    }
}
