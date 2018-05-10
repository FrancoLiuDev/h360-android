package com.leedian.klozr.base.baseView;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateFragment;
import butterknife.ButterKnife;

import icepick.Icepick;

/**
 * MvpViewStateFragment for Mvc MvpViewStateFragment in this App
 * <p>
 * This BaseViewStateFragment is used as Base BaseViewStateFragment .
 *
 * @author Franco
 */
public abstract class BaseViewStateFragment<V extends MvpView, P extends MvpPresenter<V>>
        extends MvpViewStateFragment<V, P> {
    /**
     * onCreate
     *
     * @param savedInstanceState Bundle
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FragmentArgs.inject(this);
    }

    /**
     * get Layout Res
     **/
    @LayoutRes
    protected abstract int getLayoutRes();

    /**
     * onCreateView
     *
     * @param savedInstanceState Bundle
     **/
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        Icepick.restoreInstanceState(this, savedInstanceState);
        return inflater.inflate(getLayoutRes(),
                container,
                false);
    }

    /**
     * onSaveInstanceState
     *
     * @param outState Bundle
     **/
    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    /**
     * onViewCreated
     *
     * @param view               View
     * @param savedInstanceState Bundle
     **/
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        injectDependencies();
        super.onViewCreated(view,
                savedInstanceState);
        ButterKnife.bind(this,
                view);
    }

    /**
     * on Destroy View
     **/
    @Override
    public void onDestroyView() {

        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * injectDependencies
     **/
    protected void injectDependencies() {

    }
}