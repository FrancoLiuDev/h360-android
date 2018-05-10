package com.leedian.klozr.view.viewStates;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.leedian.klozr.view.viewInterface.ViewScanMvp;

/**
 * ViewContentScanState
 *
 * @author Franco
 */
public class ViewContentScanState
        implements RestorableViewState<ViewScanMvp>
{
    @Override
    public void saveInstanceState(@NonNull Bundle out) {

    }

    @Override
    public RestorableViewState<ViewScanMvp> restoreInstanceState(Bundle in) {

        return null;
    }

    @Override
    public void apply(ViewScanMvp view, boolean retained) {

    }
}
