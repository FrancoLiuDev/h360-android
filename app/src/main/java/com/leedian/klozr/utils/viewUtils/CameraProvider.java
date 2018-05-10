package com.leedian.klozr.utils.viewUtils;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

/**
 * CameraProvider
 *
 * @author Franco
 */
public class CameraProvider {
    public static final int CAMERA_PERMISSIONS_REQUEST_ID = 0;
    static {
        System.loadLibrary("iconv");
    }
    private ViewGroup          scanLayoutContainer;
    private Activity           activity;
    private Camera             mCamera;
    private CameraPreview      mPreview;
    private Handler            autoFocusHandler;
    private ImageScanner       scanner;
    private CameraEventHandler handler;
    private boolean                barcodeScanned = false;
    private boolean                previewing     = true;
    private Camera.PreviewCallback previewCb      = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {

            Camera.Parameters parameters = camera.getParameters();
            Camera.Size       size       = parameters.getPreviewSize();
            Image             barcode    = new Image(size.width, size.height, "Y800");
            barcode.setData(data);
            int result = scanner.scanImage(barcode);
            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                SymbolSet symSet = scanner.getResults();
                for (Symbol sym : symSet) {
                    Log.i("<<<<<<Asset Code>>>>> ",
                          "<<<<Bar Code>>> " + sym.getData());
                    String scanResult = sym.getData().trim();
                    barcodeScanned = true;
                    handler.onReadData(scanResult);
                    break;
                }
            }
        }
    };
    private Runnable               doAutoFocus    = new Runnable() {
        public void run() {

            if (previewing) { mCamera.autoFocus(autoFocusCB); }
        }
    };
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {

            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private CameraProvider(Activity activity, ViewGroup container) {

        this.activity = activity;
        scanLayoutContainer = container;
    }

    private static Camera getCameraInstance() {

        Camera c = null;
        String str;
        try {
            c = Camera.open();
        } catch (Exception e) {
            str = e.getMessage();
        }
        return c;
    }

    private void setHandler(CameraEventHandler handler) {

        this.handler = handler;
    }

    public void initControls() {

        releaseCamera();
        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
        mPreview = new CameraPreview(this.activity, mCamera, previewCb,
                                     autoFocusCB);
        scanLayoutContainer.addView(mPreview);
    }

    public void releaseCamera() {

        if (mPreview != null) {
            scanLayoutContainer.removeView(mPreview);
            mPreview.getHolder().removeCallback(mPreview);
            mPreview = null;
        }
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mPreview = null;
        }
    }

    public void StartScan() {

        if (barcodeScanned && mCamera != null) {
            barcodeScanned = false;
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        }
    }

    public boolean isCameraPermission() {

        return ContextCompat.checkSelfPermission(
                this.activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public void RequestCameraPermission(int id) {

        if (ContextCompat.checkSelfPermission(
                this.activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity,
                                                                    Manifest.permission.CAMERA)) {
                handler.onErrorPermission();
            } else {
                ActivityCompat.requestPermissions(this.activity,
                                                  new String[]{Manifest.permission.CAMERA},
                                                  CAMERA_PERMISSIONS_REQUEST_ID);
            }
        }
    }

    public interface CameraEventHandler {
        void onReadData(String data);

        void onErrorPermission();
    }

    public static class Builder {
        CameraProvider provider;

        public Builder(Activity activity, ViewGroup container) {

            this.provider = new CameraProvider(activity, container);
        }

        public Builder setEventHandler(CameraEventHandler handler) {

            this.provider.setHandler(handler);
            return this;
        }

        public CameraProvider Build() {

            return provider;
        }
    }
}
