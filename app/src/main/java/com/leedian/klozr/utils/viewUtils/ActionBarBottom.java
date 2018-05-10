package com.leedian.klozr.utils.viewUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;

/**
 * ActionBarBottom
 *
 * @author Franco
 */
public class ActionBarBottom extends RelativeLayout implements View.OnClickListener {

    private boolean debugMode = false;

    private int backgroundColor = AppResource.getColor(R.color.color_transparent);

    private BottomBarButtonEvent event;

    public ActionBarBottom(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public ActionBarBottom(Context context) {

        super(context);
    }

    static public ActionBarBottom createNewToolBar(Context context, int resource) {

        return (ActionBarBottom) LayoutInflater.from(context).inflate(resource, null);
    }

    public void setEventListener(BottomBarButtonEvent event) {

        this.event = event;
    }

    public void setBackgroundColor(int backgroundColor) {

        this.backgroundColor = backgroundColor;
    }

    public void setDebugMode(boolean debugMode) {

        this.debugMode = debugMode;
    }

    public void setButtonImage(int viewResource, int ImageResource) {

        ImageView imageView = (ImageView) this.findViewById(viewResource);
        imageView.setImageResource(ImageResource);
        imageView.setOnClickListener(this);
    }

    public void setButtonClickable(int viewResource, boolean clickable) {

        View v = this.findViewById(viewResource);
        v.setClickable(clickable);
    }
    public void setButtonTextView(int viewResource) {

        TextView View = (TextView) this.findViewById(viewResource);
        View.setOnClickListener(this);
    }

    public void clearButtonImage(int viewResource) {

        ImageView imageView = (ImageView) this.findViewById(viewResource);
        imageView.setImageResource(0);
        imageView.setOnClickListener(null);
    }

    public LayoutParams getInitLayoutStyle() {

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        return lp;
    }

    private void initButtons(ViewGroup viewgroup) {

        int childCount = viewgroup.getChildCount();
        for (int i = 0; i <= childCount - 1; i++) {
            View v = viewgroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                initButtons((ViewGroup) v);
            }
            if (v instanceof ImageView) {
                ImageView imageView = (ImageView) v;
                if (debugMode) {
                    imageView.setImageDrawable(new ColorDrawable(Color.parseColor("#990000ff")));
                    imageView.setBackgroundColor(Color.parseColor("#9900ff00"));
                }
            }
        }
    }

    public void InitBottomBar() {

        if (debugMode)
            this.setBackgroundColor(Color.parseColor("#99FF0000"));
        else
            this.setBackgroundColor(backgroundColor);
        initButtons(this);
    }

    @Override
    public void onClick(View view) {

        event.onButtonClicked(view);
    }

    public interface BottomBarButtonEvent {

        void onButtonClicked(View view);
    }

    public static class Builder {

        ActionBarBottom bottomToolBar;

        Context context;

        public Builder(Context context, int resource) {

            this.bottomToolBar = createNewToolBar(context, resource);
            this.context = context;
        }

        public Builder setParent(ViewGroup View) {

            View.addView(bottomToolBar, bottomToolBar.getInitLayoutStyle());
            return this;
        }

        public Builder setBackgroundColor(int color) {

            bottomToolBar.setBackgroundColor(color);
            return this;
        }

        public Builder setDebugMode(boolean mode) {

            bottomToolBar.setDebugMode(mode);
            return this;
        }

        public Builder setToolBarListener(BottomBarButtonEvent event) {

            bottomToolBar.setEventListener(event);
            return this;
        }

        public ActionBarBottom build() {

            bottomToolBar.InitBottomBar();
            return bottomToolBar;
        }
    }
}
