package com.leedian.klozr.view.viewStates;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.leedian.klozr.view.viewInterface.ViewContentMvp;

/**
 * ViewContentState
 *
 * @author Franco
 */
public class ViewContentState
        implements RestorableViewState<ViewContentMvp>
{
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<ViewContentMvp> restoreInstanceState(Bundle in) {

        return null;
    }

    @Override
    public void apply(ViewContentMvp view, boolean retained) {

    }
}
