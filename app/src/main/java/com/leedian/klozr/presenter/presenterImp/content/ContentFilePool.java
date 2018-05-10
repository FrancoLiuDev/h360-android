package com.leedian.klozr.presenter.presenterImp.content;
import com.blankj.utilcode.utils.FileUtils;
import com.leedian.klozr.AppManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * A  file pool for contents
 *
 * @author Franco
 */
public class ContentFilePool {
    private String filePath = AppManager.getContentDownloadDir();

    /**
     * Constructor
     *
     */
    public ContentFilePool() {
        FileUtils.createOrExistsDir(filePath);
    }

    /**
     * Get Content Path String
     *
     * @param zipKey the zipKey
     * @return String
     */
    public String getContentPathString(String zipKey) {

        return filePath + "/" + zipKey;
    }

    /**
     * check if file is in pool
     *
     * @param zipKey  the zipKey
     * @return boolean
     */
    public boolean isFileInPool(String zipKey) {

        return FileUtils.isFileExists(getContentPathString(zipKey));
    }

    /**
     * Delete File In Pool
     *
     * @param zipKey the zipKey
     * @return boolean
     */
    public boolean deleteFileInPool(String zipKey) {

        if (!isFileInPool(zipKey)) { return true; }
        return FileUtils.deleteFile(getContentPathString(zipKey));
    }

    /**
     * Create Content Item File Output Stream for download
     *
     * @param zipKey
     * @return  OutputStream
     * @throws FileNotFoundException
     */
    public OutputStream createContentItemFileOutputStream(String zipKey) throws FileNotFoundException {

        OutputStream output;
        File         fullFilename = getContentItemFile(zipKey);
        output = new FileOutputStream(fullFilename);
        return output;
    }

    /**
     * Get Content Item File
     *
     * @param zipKey
     * @return File
     */
    private File getContentItemFile(String zipKey) {

        File file = new File(filePath);
        return new File(file, zipKey);
    }
}
