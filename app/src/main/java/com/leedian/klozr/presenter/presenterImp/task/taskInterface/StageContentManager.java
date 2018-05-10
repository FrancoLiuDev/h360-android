package com.leedian.klozr.presenter.presenterImp.task.taskInterface;

import android.graphics.Bitmap;

import com.leedian.klozr.utils.subscriptHelp.ObservableActionIdentifier;

import rx.Observable;

/**
 * StageContentManager
 *
 * @author Franco
 */
public interface StageContentManager {

    Observable<ObservableActionIdentifier> executeRequestOviewShowStage();

    Observable<Bitmap[]> getKlozrViewBitmap(int width, int height);

    Observable<Bitmap[]> getKlozrViewBitmapLocal(int width, int height);

    Observable<ObservableActionIdentifier<Bitmap>> getKlozrViewSingleBitmap(final int index, final int width, final int height);
}
