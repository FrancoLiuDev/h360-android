package com.leedian.klozr.presenter.presenterImp.task.taskImp;
import android.graphics.Bitmap;

import com.leedian.klozr.model.restapi.OviewApi;
import com.leedian.klozr.presenter.presenterImp.content.ContentFilePool;
import com.leedian.klozr.presenter.presenterImp.content.StageContent;
import com.leedian.klozr.presenter.presenterImp.task.taskInterface.StageContentManager;
import com.leedian.klozr.utils.exception.DomainException;
import com.leedian.klozr.utils.exception.FileException;
import com.leedian.klozr.utils.exception.NetworkConnectionException;
import com.leedian.klozr.utils.subscriptHelp.ObservableActionIdentifier;
import com.leedian.klozr.utils.subscriptHelp.ObservableDownloadAction;
import com.leedian.klozr.utils.subscriptHelp.ObservableOviewBmpAction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * StageContentManagerImp
 *
 * @author Franco
 */
public class StageContentManagerImp
        implements StageContentManager
{
    private static StageContent oviewInfo;

    private Bitmap[] bitmapBuffer;

    private String contentZipKey;

    private ContentFilePool contentFilePool = new ContentFilePool();

    public StageContentManagerImp(String contentZipKey) {

        this.contentZipKey = contentZipKey;
    }

    /**
     * function to add list  to the buffer
     **/
    @Override public Observable<ObservableActionIdentifier> executeRequestOviewShowStage() {

        boolean exists = contentFilePool.isFileInPool(this.contentZipKey);
        if (exists) {
            return Observable.create(new Observable.OnSubscribe<ObservableActionIdentifier>() {
                @Override
                public void call(final Subscriber<? super ObservableActionIdentifier> sub) {

                    sub.onNext(null);
                    sub.onCompleted();
                }
            });
        } else {
            return executeRequestOviewShowStageOnline();
        }
    }

    @Override public Observable<Bitmap[]> getKlozrViewBitmap(final int width, final int height) {

        final String zipkey = this.contentZipKey;

        oviewInfo = new StageContent(contentFilePool.getContentPathString(this.contentZipKey));

        return Observable.create(new Observable.OnSubscribe<Bitmap[]>() {
            @Override public void call(final Subscriber<? super Bitmap[]> sub) {

                if (oviewInfo == null) {
                    sub.onError(new FileException("Oview info is null , call getKlozrOviewInformation first"));
                    return;
                }

                try {
                    boolean isSuccess;
                    isSuccess = oviewInfo.reCreateKlozrOviewContent();

                    if (!isSuccess) {
                        contentFilePool.deleteFileInPool(zipkey);
                        Exception exception = DomainException.buildFileException();
                        sub.onError(exception);
                    }

                    oviewInfo.writeContentTagName(zipkey);
                    bitmapBuffer = oviewInfo.ConvertBitmapsFormContent(width, height);
                    isSuccess = true;

                    for (int i = 0; i < bitmapBuffer.length; i++) {
                        if (bitmapBuffer[i] == null) {
                            isSuccess = false;
                            break;
                        }
                    }

                    if (!isSuccess) {
                        contentFilePool.deleteFileInPool(zipkey);
                        Exception exception = DomainException.buildFileException();
                        sub.onError(exception);
                    }

                    sub.onNext(bitmapBuffer);
                    sub.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    sub.onError(DomainException.getDomainException(e));
                }
            }
        });
    }

    @Override
    public Observable<Bitmap[]> getKlozrViewBitmapLocal(final int width, final int height) {

        final String zipkey = this.contentZipKey;
        oviewInfo = new StageContent(contentFilePool.getContentPathString(this.contentZipKey));
        return Observable.create(new Observable.OnSubscribe<Bitmap[]>() {
            @Override public void call(final Subscriber<? super Bitmap[]> sub) {

                if (oviewInfo == null) {
                    sub.onError(new FileException("Oview info is null , call getKlozrOviewInformation first"));
                    return;
                }
                try {
                    boolean isSuccess;
                    boolean isContentExist = oviewInfo.isContentTagExist(zipkey);

                    if (!isContentExist) {

                        isSuccess = oviewInfo.reCreateKlozrOviewContent();
                        if (!isSuccess) {
                            contentFilePool.deleteFileInPool(zipkey);
                            Exception exception = DomainException.buildFileException();
                            sub.onError(exception);
                        }
                    }

                    bitmapBuffer = oviewInfo.ConvertBitmapsFormContent(width, height);

                    isSuccess = true;
                    for (int i = 0; i < bitmapBuffer.length; i++) {
                        if (bitmapBuffer[i] == null) {
                            isSuccess = false;
                            break;
                        }
                    }

                    if (!isSuccess) {
                        contentFilePool.deleteFileInPool(zipkey);
                        Exception exception = DomainException.buildFileException();
                        sub.onError(exception);
                    }

                    sub.onNext(bitmapBuffer);
                    sub.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    sub.onError(DomainException.getDomainException(e));
                }
            }
        });
    }

    @Override
    public Observable<ObservableActionIdentifier<Bitmap>> getKlozrViewSingleBitmap(final int index, final int width, final int height) {

        return Observable.create(new Observable.OnSubscribe<ObservableActionIdentifier<Bitmap>>() {
            @Override
            public void call(Subscriber<? super ObservableActionIdentifier<Bitmap>> subscriber) {

                if (oviewInfo == null) {
                    subscriber
                            .onError(new FileException("Oview info is null , call getKlozrOviewInformation first"));
                    return;
                }

                try {
                    Bitmap bitmap = oviewInfo.ConvertSingleBitmapsFormContent(index, width, height);
                    String strIndex = String.format("%d", index);

                    ObservableActionIdentifier<Bitmap> actionResult = new ObservableActionIdentifier(ObservableOviewBmpAction.ACTION_BOP_CONVERT_SUCCESS, strIndex);

                    actionResult.setObject(bitmap);
                    subscriber.onNext(actionResult);
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Observable<ObservableActionIdentifier> executeRequestOviewShowStageOnline() {

        return Observable.create(new Observable.OnSubscribe<ObservableActionIdentifier>() {
            @Override public void call(final Subscriber<? super ObservableActionIdentifier> sub) {

                downloadSubscriberOnCall(StageContentManagerImp.this.contentZipKey, sub);
            }
        }).onBackpressureDrop().sample(500, TimeUnit.MILLISECONDS)
                         .delay(1, TimeUnit.MILLISECONDS, Schedulers.immediate());
    }

    private void writeStreamToFile(InputStream input, OutputStream fileStream, long tLength, ProcessResponseHandler handler) throws IOException, InterruptedException {

        byte data[] = new byte[1024];
        long total  = 0;
        int  count;

        handler.onProcess("0");

        while ((count = input.read(data)) != -1) {
            total += count;
            long   percent    = (total * 100 / tLength);
            String percentStr = String.valueOf(percent);
            handler.onProcess(percentStr);
            fileStream.write(data, 0, count);
        }

        fileStream.flush();
        fileStream.close();
        input.close();
        handler.onProcess("100");
        handler.onSuccess();
    }

    private void downloadSubscriberOnCall(final String zipKey, final Subscriber<? super ObservableActionIdentifier> sub) {

        InputStream  input  = null;
        OutputStream output = null;
        long         tLength;

        try {

            OviewApi HttpApi   = new OviewApi();
            boolean  isSuccess = HttpApi.downloadItemContent(zipKey);

            if (!isSuccess) {
                sub.onError(new NetworkConnectionException());
                return;
            }

            sub.onNext(new ObservableActionIdentifier(ObservableDownloadAction.ACTION_START_DOWNLOAD, ""));

            Thread.sleep(600);
            tLength = HttpApi.getResponseContentLength();
            input = HttpApi.getInputStream();
            output = contentFilePool.createContentItemFileOutputStream(zipKey);

            writeStreamToFile(input, output, tLength, new ProcessResponseHandler() {
                @Override public void onProcess(String progress) throws InterruptedException {

                    sub.onNext(new ObservableActionIdentifier(ObservableDownloadAction.ACTION_FILE_DOWNLOADING, progress));
                }

                @Override public void onSuccess() {

                    sub.onCompleted();
                }
            });
        } catch (Exception e) {
            sub.onError(e);
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    interface ProcessResponseHandler {
        void onProcess(String progress) throws InterruptedException;

        void onSuccess();
    }
}
