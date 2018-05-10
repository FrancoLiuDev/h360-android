package com.leedian.klozr.presenter.presenterImp.content;
import android.graphics.Bitmap;
import android.graphics.Point;

import com.blankj.utilcode.utils.ZipUtils;
import com.leedian.HSImgs.DecodeJpg;
import com.leedian.HSImgs.GetBitmaps;
import com.leedian.klozr.AppManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.blankj.utilcode.utils.FileUtils.createOrExistsDir;
import static com.blankj.utilcode.utils.FileUtils.createOrExistsFile;
import static com.blankj.utilcode.utils.FileUtils.deleteDir;
import static com.blankj.utilcode.utils.FileUtils.isFileExists;
import static com.blankj.utilcode.utils.FileUtils.listFilesInDir;

/**
 * A  Content object operate a content folder
 *
 * @author Franco
 */
public class StageContent {
    private final int IMAGE_COUNT = 48;
    private Bitmap[] BitmapBuffer;
    private String OviewPath   = AppManager.getOviewContentDir();
    private File   ContentRoot = new File(OviewPath);
    private String zipFilePath;

    /**
     * Constructor
     *
     * @param filePath
     */
    public StageContent(String filePath) {

        this.zipFilePath = filePath;
    }

    /**
     * Get oview image count
     *
     * @return int
     */
    private int KlozrOviewImageCount() {

        return IMAGE_COUNT;
    }

    /**
     * Get oview Bitmap Buffer
     *
     * @return Bitmap[]
     */
    private Bitmap[] getBitmapBuffer() {

        return BitmapBuffer;
    }

    /**
     *
     * @return
     */
    private Bitmap[] createBmpBuffer() {

        BitmapBuffer = null;
        BitmapBuffer = new Bitmap[IMAGE_COUNT];
        return BitmapBuffer;
    }

    public Bitmap[] ConvertBitmapsFormContent(int width, int height) throws InterruptedException {

        Point      size    = new Point(width, height);
        String     path    = getOviewPhotosDirPath();
        GetBitmaps bitmaps = new GetBitmaps(createBmpBuffer(), KlozrOviewImageCount(), 2, path, size);

        bitmaps.getAllPics();

        while (!bitmaps.isDecodeComplete()) {
            Thread.sleep(0);
        }

        return this.getBitmapBuffer();
    }

    public Bitmap ConvertSingleBitmapsFormContent(int start, int width, int height) throws InterruptedException {

        Point  size     = new Point(width, height);
        String path     = getOviewZoomDirPath();
        String filename = String.format("/%03d.jpg", start + 1);

        return DecodeJpg.getinstance()
                        .decodeSampledBitmapFromResource(path + filename, size.x, size.y);
    }

    public boolean reCreateKlozrOviewContent() {

        reCreateKlozrOviewFolder();

        try {
            if (!ZipUtils.unzipFile(this.zipFilePath, OviewPath)) { return false; }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    private void reCreateKlozrOviewFolder() {

        deleteDir(OviewPath);
        createOrExistsDir(OviewPath);
    }

    public void writeContentTagName(String name) {

        String rootPath    = getOviewContentFile().getPath();
        String tagFilePath = rootPath + "/" + name;

        createOrExistsFile(tagFilePath);
        isFileExists(tagFilePath);
    }

    public boolean isContentTagExist(String name) {

        String rootPath    = getOviewContentFile().getPath();
        String tagFilePath = rootPath + "/" + name;
        return isFileExists(tagFilePath);
    }

    private String getOviewPhotosDirPath() {

        String rootPath = getOviewContentFile().getPath();
        return rootPath + "/photo";
    }

    private String getOviewZoomDirPath() {

        String rootPath = getOviewContentFile().getPath();
        return rootPath + "/zoom";
    }

    private File getOviewContentFile() {

        List<File> files = listFilesInDir(OviewPath, false);
        if (files.size() != 1) {
            return ContentRoot;
        }
        return files.get(0);
    }
}
