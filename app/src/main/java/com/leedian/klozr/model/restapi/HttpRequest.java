package com.leedian.klozr.model.restapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.blankj.utilcode.utils.EncryptUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.leedian.klozr.model.dataIn.AuthCredentials;
import com.leedian.klozr.model.dataOut.OviewInfoNodeModel;
import com.leedian.klozr.model.net.ApiConnection;
import com.leedian.klozr.utils.EncryptionHelper;

/**
 * a Http Request creator
 *
 * @author Franco
 */
class HttpRequest {
    private static final MediaType JSON_TYPE;

    static {
        JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    }

    private ApiConnection conn = null;

    HttpRequest() {

    }

    /**
     * Http Login Request
     *
     * @param credentials AuthCredentials model
     **/
    HttpRequest loginRequest(AuthCredentials credentials) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_OVIEW_LOGIN);

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        String key = UrlMessagesConstants.StrHttpServiceTripleDesKey;
        byte[] bytes = credentials.getPassword().getBytes("UTF-8");

        String mdString = EncryptUtils.encryptMD5ToString(bytes)
                .toLowerCase();
        String encryptedPassword = EncryptionHelper
                .des3EncodeECBtoBase64(key, mdString);

        root.put("user", credentials.getUsername());
        root.put("pwd", encryptedPassword);
        root.put("brand", UrlMessagesConstants.StrHttpServiceBrandName);
        RequestBody body = RequestBody.create(JSON_TYPE, getJson(root));
        conn = ApiConnection.instance(url, "Post", body);
        return this;
    }

    /**
     * Get Oview List
     *
     * @param limit the limit
     * @param offset the offset
     **/
    HttpRequest getOviewPackageList(int limit, int offset) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(true, UrlMessagesConstants.IDENTIFIER_CONTENT_PACK_LIST) +
                getLimitOffsetString(limit, offset);
        conn = ApiConnection.instance(url, "Get", null);
        return this;
    }

    /**
     * Delete Oview item in cloud
     *
     * @param zipKey the zipKey item
     * @param status the status
     **/
    HttpRequest deleteOviewItem(String zipKey, String status) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants
                        .getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_CONTENT_ITEM) + "/" +
                zipKey;

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("status", status);
        String jBody = getJson(root);
        RequestBody body = RequestBody.create(JSON_TYPE, jBody);
        conn = ApiConnection.instance(url, "Delete", body);
        return this;
    }

    /**
     * Download Content Images
     *
     * @param zipKey the zipKey item
     **/
    HttpRequest downloadContentImages(String zipKey) throws Exception {

        String url = UrlMessagesConstants.getFileDownloadRestUrl(zipKey);
        conn = ApiConnection.instance(url, "Download", null);
        return this;
    }

    /**
     * Request Oview Item Detail
     *
     * @param zipKey the zipKey item
     **/
    HttpRequest requestOviewItemDetail(String zipKey) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants
                        .getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_CONTENT_ITEM) + "/" +
                zipKey;
        conn = ApiConnection.instance(url, "Get", null);
        return this;
    }

    /**
     * Update Oview item name
     *
     * @param zipKey the zipKey item
     * @param name the zipKey name
     **/
    HttpRequest updateOviewItemName(String zipKey, String name) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_CONTENT_ITEM) +
                "/" + zipKey;

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("name", name);
        String jBody = getJson(root);
        RequestBody body = RequestBody.create(JSON_TYPE, jBody);
        conn = ApiConnection.instance(url, "Patch", body);
        return this;
    }

    /**
     * Update  Oview item description
     *
     * @param zipKey the zipKey item
     * @param description the zipKey description
     **/
    HttpRequest updateObieItemDescription(String zipKey, String description) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_CONTENT_ITEM) +
                "/" + zipKey;

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("about", description);
        String jBody = getJson(root);
        RequestBody body = RequestBody.create(JSON_TYPE, jBody);
        conn = ApiConnection.instance(url, "Patch", body);
        return this;
    }

    /**
     * Update  Oview item Traits
     *
     * @param zipKey the zipKey item
     * @param list the Traits list
     **/
    HttpRequest updateOviewItemTraits(String zipKey, List<OviewInfoNodeModel> list) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants
                        .getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_OVIEW_INFO) + "/" +
                zipKey;
        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("trait", getJsonList(list));
        String jBody = getJson(root);
        RequestBody body = RequestBody.create(JSON_TYPE, jBody);
        conn = ApiConnection.instance(url, "Patch", body);
        return this;
    }

    /**
     * Get response
     *
     **/
    Response getResponse() throws Exception {

        return conn.GetResponse();
    }

    /**
     * Get response content length
     *
     **/
    Long getResponseContentLength() throws Exception {

        return this.getResponse().body().contentLength();
    }

    /**
     * Get response content stream
     *
     **/
    InputStream getResponseStream() throws Exception {

        return this.getResponse().body().byteStream();
    }

    /**
     * Connect Sync
     *
     **/
    boolean connectSync() throws Exception {

        return conn.connectToApi();
    }

    /**
     * Get Limit & Offset string
     *
     * @param limit the limit
     * @param offset the offset
     **/
    private String getLimitOffsetString(int limit, int offset) {

        return "limit=" + Integer.toString(limit) + "&" + "offset=" + Integer.toString(offset);
    }

    /**
     * convert  ObjectNode to Json string
     *
     * @param dic the dic
     *            @return String
     **/
    private String getJson(ObjectNode dic) {

        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(dic);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * convert  List to Json string
     *
     * @param list the list
     * @return String
     **/
    private String getJsonList(List list) {

        final OutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            mapper.writeValue(out, list);
            json = String.valueOf(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
