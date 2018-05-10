package com.leedian.klozr;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * AppResource
 *
 * @author Franco
 */
public class AppResource {

    static Context context = OviewApp.getAppContext();

    static public String getString(int id) {

        return context.getResources().getString(id);
    }

    static public int getValue(int id) {

        return context.getResources().getInteger(id);
    }

    static public int getColor(int id) {

        return ContextCompat.getColor(context, id);
    }

    static public float Dimens(int id) {

        return context.getResources().getDimension(id);
    }

    static public Drawable getDrawable(int id) {

        return ContextCompat.getDrawable(context,id);
    }
}
