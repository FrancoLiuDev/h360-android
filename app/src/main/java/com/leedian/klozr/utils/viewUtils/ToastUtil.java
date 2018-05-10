package com.leedian.klozr.utils.viewUtils;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.View;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;

/**
 * ToastUtil
 *
 * @author Franco
 */
public class ToastUtil {

    public class ToastStyle {

        public static final int TYPE_WARNING = 1;
        public static final int TYPE_TIP = 2;
    }

    public static void ShowToastWithDismissIcon(Context context, String displayText, int type, int container) {

        int color;

        if (type == ToastStyle.TYPE_WARNING){
            color = AppResource.getColor(R.color.color_visual_popo_red);
        }else{
            color = AppResource.getColor(R.color.color_visual_popo_green);
        }

        SuperActivityToast.OnButtonClickListener onButtonClickListener = new SuperActivityToast.OnButtonClickListener() {

            @Override
            public void onClick(View view, Parcelable token) {

                SuperActivityToast.cancelAllSuperToasts();
            }
        };
        SuperActivityToast.create(context, new Style(), Style.TYPE_BUTTON,container)
                .setButtonText("CLOSE")
                .setButtonIconResource(R.drawable.status_close)
                .setOnButtonClickListener("good_tag_name", null, onButtonClickListener)
                .setProgressBarColor(Color.WHITE)
                .setText(displayText)
                .setDuration(Style.DURATION_LONG)
                .setFrame(Style.FRAME_KITKAT)
                .setColor(color)
                .setAnimations(Style.ANIMATIONS_POP).show();
    }
}
