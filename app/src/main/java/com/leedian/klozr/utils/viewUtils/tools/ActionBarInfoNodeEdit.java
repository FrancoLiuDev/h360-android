package com.leedian.klozr.utils.viewUtils.tools;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.utils.viewUtils.ActionBarBottom;

/**
 * ActionBarInfoNodeEdit
 *
 * @author Franco
 */
public class ActionBarInfoNodeEdit implements ActionBarBottom.BottomBarButtonEvent {

    private Context context;

    private ViewGroup parent;

    private ActionBarBottom infoNodeEditBar;

    private InfoNodeEditButtonEvent event;

    private ActionBarInfoNodeEdit(Context context, ViewGroup parent) {

        this.context = context;
        this.parent = parent;
    }

    public static ActionBarInfoNodeEdit buildInfoNodeBar(Context context, ViewGroup parent) {

        ActionBarInfoNodeEdit bar = new ActionBarInfoNodeEdit(context, parent);
        bar.init();
        return bar;
    }

    public void setEvent(InfoNodeEditButtonEvent event) {

        this.event = event;
    }

    public void setBarVisible(boolean visible) {

        if (visible)
            infoNodeEditBar.setVisibility(View.VISIBLE);
        else
            infoNodeEditBar.setVisibility(View.GONE);
    }

    public void setButtonClickable(boolean clickable) {

        infoNodeEditBar.setEnabled(clickable);
    }

    private void init() {

        infoNodeEditBar = new ActionBarBottom.Builder(context, R.layout.actionbar_infonode_edit_bar)
                .setParent(parent)
                .setToolBarListener(this)
                .setDebugMode(false)
                .setBackgroundColor(AppResource.getColor(R.color.colorActionBarBackground))
                .build();

        infoNodeEditBar.setButtonTextView(R.id.btn_node_edit_complete);
        infoNodeEditBar.setButtonTextView(R.id.btn_node_edit_delete);

        TextView nodeEditCompleteButton = (TextView) infoNodeEditBar
                .findViewById(R.id.btn_node_edit_complete);

        nodeEditCompleteButton.setVisibility(View.VISIBLE);

        TextView nodeEditDeleteButton = (TextView) infoNodeEditBar
                .findViewById(R.id.btn_node_edit_delete);

        nodeEditDeleteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onButtonClicked(View view) {

        if (view.getId() == R.id.btn_node_edit_complete) {
            event.onNodeFinishEdit();
        }
        if (view.getId() == R.id.btn_node_edit_delete) {
            event.onNodeRemove();
        }
    }

    public interface InfoNodeEditButtonEvent {

        void onNodeRemove();

        void onNodeFinishEdit();
    }
}
