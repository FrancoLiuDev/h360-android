package com.leedian.klozr.view.viewStates;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.leedian.klozr.view.viewInterface.ViewUserLoginMvp;

/**
 * ViewUserLoginState
 *
 * @author Franco
 */
public class ViewUserLoginState
        implements RestorableViewState<ViewUserLoginMvp>
{
    @Override
    public void apply(ViewUserLoginMvp view, boolean retained) {

    }

    public void setShowLoginForm() {

    }

    public void setShowError() {

    }

    public void setShowLoading() {

    }

    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<ViewUserLoginMvp> restoreInstanceState(Bundle in) {

        return null;
    }
}

