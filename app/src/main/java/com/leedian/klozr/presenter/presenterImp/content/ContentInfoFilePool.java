package com.leedian.klozr.presenter.presenterImp.content;
import com.blankj.utilcode.utils.FileUtils;
import com.leedian.klozr.AppManager;
import com.leedian.klozr.model.dataOut.OviewContentModel;
import com.leedian.klozr.model.dataOut.OviewInfoNodeModel;
import com.leedian.klozr.utils.JacksonConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.blankj.utilcode.utils.FileUtils.isFileExists;

/**
 * A  file pool for content information
 *
 * @author Franco
 */
public class ContentInfoFilePool {

    private String filePath = AppManager.getOviewInfoFileDir();

    /**
     * Constructor
     */
    public ContentInfoFilePool() {

        FileUtils.createOrExistsDir(filePath);
    }

    /**
     * Get Content Path String
     *
     * @param zipKey
     * @return
     */
    private String getContentPathString(String zipKey) {

        return filePath + "/" + zipKey;
    }

    /**
     * Get if file In Pool
     * @param zipKey
     * @return boolean
     */
    public boolean isFileInPool(String zipKey) {

        return FileUtils.isFileExists(getContentPathString(zipKey));
    }

    /**
     * Get Info File Handle
     *
     * @param zipKey the zipKey
     * @return  File
     */
    private File getInfoFileHandle(String zipKey) {

        FileUtils.deleteFile(getContentPathString(zipKey));
        FileUtils.createOrExistsFile(getContentPathString(zipKey));
        return new File(getContentPathString(zipKey));
    }

    /**
     * Get Oview Content Information
     *
     * @param zipKey the zipKey
     * @return OviewContentModel
     */
    public OviewContentModel getOviewContentInformation(String zipKey) {

        OviewContentModel model;

        File file = new File(getContentPathString(zipKey));

        if (!isFileExists(file)) { return null; }

        byte[] data = FileUtils.readFile2Bytes(file);
        String str  = new String(data);

        try {
            model = OviewContentModel.getOviewEntitiesFromString(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return model;
    }

    /**
     * Save Content Information
     *
     * @param zipKey the zipKey
     * @param info  the info
     * @return boolean
     */
    public boolean saveContentInformation(String zipKey, OviewContentModel info) {

        String           data;
        File             infoFile  = getInfoFileHandle(zipKey);
        JacksonConverter converter = new JacksonConverter<OviewContentModel>(OviewContentModel.class);

        try {
            data = converter.getJsonString(info);
            FileUtils.writeFileFromString(infoFile, data, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Get Oview Add Traits
     * @param zipKey the zipKey
     * @param model the model
     * @return List
     */
    public List<OviewInfoNodeModel> gatOviewAddTraits(String zipKey, OviewInfoNodeModel model) {

        OviewContentModel info = getOviewContentInformation(zipKey);
        if (info == null) return null;

        List<OviewInfoNodeModel> traitList = info.getTrait();

        if (traitList == null) { traitList = new ArrayList<OviewInfoNodeModel>(); }

        traitList.add(model);

        return traitList;
    }

    /**
     * get Oview Update Traits
     *
     * @param zipKey the zipKey
     * @param n      the n
     * @param model  the model
     * @return List
     */
    public List<OviewInfoNodeModel> gatOviewUpdateTraits(String zipKey, int n, OviewInfoNodeModel model) {

        OviewContentModel info = getOviewContentInformation(zipKey);
        if (info == null) return null;

        List<OviewInfoNodeModel> traitList = info.getTrait();
        if (traitList == null) return null;

        traitList.set(n, model);

        return traitList;
    }

    /**
     * Get Oview Remove Traits
     *
     * @param zipKey the zipKey
     * @param n the n
     * @return List
     */
    public List<OviewInfoNodeModel> gatOviewRemoveTraits(String zipKey, int n) {

        OviewContentModel info = getOviewContentInformation(zipKey);
        if (info == null) return null;

        List<OviewInfoNodeModel> traitList = info.getTrait();
        if (traitList == null) return null;

        traitList.remove(n);

        return traitList;
    }
}
