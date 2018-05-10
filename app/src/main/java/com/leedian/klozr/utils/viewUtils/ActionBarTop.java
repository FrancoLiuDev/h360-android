package com.leedian.klozr.utils.viewUtils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ActionBarBottom
 *
 * @author Franco
 */
public class ActionBarTop
        implements View.OnClickListener
{
    private boolean debugMode = false;
    private AppCompatActivity activity;
    private ActionButtonEvent Event;
    private View              ActionBarView;
    private int               MenuResourceID;
    private int backgroundColor = Color.parseColor("#99ff0000");

    private ActionBarTop(AppCompatActivity activity, int resource) {

        this.activity = activity;
        this.MenuResourceID = resource;
    }

    private void setDebugMode(boolean debugMode) {

        this.debugMode = debugMode;
    }

    private void setBackgroundColor(int backgroundColor) {

        this.backgroundColor = backgroundColor;
    }

    private void setEventListener(ActionButtonEvent event) {

        Event = event;
    }

    private ActionBar getActionBar() {

        return activity.getSupportActionBar();
    }

    private void CreateActionBar() {

        ActionBar actionBar = getActionBar();
        View      view      = createActionBarView();
        if (debugMode) { view.setBackgroundColor(Color.parseColor("#99FF0000")); } else {
            view.setBackgroundColor(backgroundColor);
        }
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layout);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setShowHideAnimationEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(backgroundColor));
        initActionBarView(view);
    }

    private View createActionBarView() {

        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ActionBarView = inflater.inflate(MenuResourceID, null);
        return this.ActionBarView;
    }

    private View getActionBarView() {

        return this.ActionBarView;
    }

    private View initActionBarView(View view) {

        view.setOnClickListener(this);
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        parent.setContentInsetsRelative(0, 0);
        initActionBarViewListener(view);
        return view;
    }

    private void initActionBarViewListener(View view) {

        ViewGroup viewgroup  = (ViewGroup) view;
        int       childCount = viewgroup.getChildCount();
        for (int i = 0; i <= childCount - 1; i++) {
            View v = viewgroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                initActionBarViewListener(v);
            }
            if (v instanceof ImageView) {
                ImageView imageView = (ImageView) v;
                imageView.setOnClickListener(this);
                if (debugMode) {
                    imageView.setImageDrawable(new ColorDrawable(Color.parseColor("#990000ff")));
                    imageView.setBackgroundColor(Color.parseColor("#9900ff00"));
                }
            }
        }
    }

    public void setButtonImage(int viewResource, int ImageResource) {

        ImageView imageView = (ImageView) getActionBarView().findViewById(viewResource);
        imageView.setImageResource(ImageResource);
        imageView.setOnClickListener(this);
    }

    public void clearButtonImage(int viewResource) {

        ImageView imageView = (ImageView) getActionBarView().findViewById(viewResource);
        imageView.setImageResource(0);
        imageView.setOnClickListener(null);
    }

    public void setTextString(String str, int textViewResource) {

        TextView View = (TextView) getActionBarView().findViewById(textViewResource);
        View.setText(str);
    }

    @Override
    public void onClick(View view) {

        Event.onActionButtonClicked(view);
    }

    public interface ActionButtonEvent {
        void onActionButtonClicked(View view);
    }

    public static class Builder {
        ActionBarTop mActionBar;

        public Builder(AppCompatActivity appCompatActivity, int resource) {

            mActionBar = new ActionBarTop(appCompatActivity, resource);
        }

        public Builder setEventListener(ActionButtonEvent event) {

            mActionBar.setEventListener(event);
            return this;
        }

        public Builder setBackgroundColor(int color) {

            mActionBar.setBackgroundColor(color);
            return this;
        }

        public Builder setDebugMode(boolean mode) {

            mActionBar.setDebugMode(mode);
            return this;
        }

        public ActionBarTop build() {

            mActionBar.CreateActionBar();
            return mActionBar;
        }
    }
}
