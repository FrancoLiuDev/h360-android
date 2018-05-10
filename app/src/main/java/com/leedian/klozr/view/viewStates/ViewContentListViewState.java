package com.leedian.klozr.view.viewStates;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.leedian.klozr.view.viewInterface.ViewContentListMvp;

/**
 * ViewContentListViewState
 *
 * @author Franco
 */
public class ViewContentListViewState
        implements RestorableViewState<ViewContentListMvp>
{
    @Override
    public void apply(ViewContentListMvp view, boolean retained) {

    }

    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<ViewContentListMvp> restoreInstanceState(Bundle in) {

        return null;
    }
}

