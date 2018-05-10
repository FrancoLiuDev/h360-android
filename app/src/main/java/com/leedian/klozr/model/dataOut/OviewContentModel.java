package com.leedian.klozr.model.dataOut;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.leedian.klozr.utils.JacksonConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model for a H360 content
 *
 * @author Franco
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public class OviewContentModel
        implements Serializable
{
    private static JacksonConverter<OviewContentModel> converter = new JacksonConverter<OviewContentModel>(OviewContentModel.class);
    @JsonProperty("id")
    private String uid;
    @JsonProperty("ar")
    private String ar;
    @JsonProperty("zipkey")
    private String zipkey;
    @JsonProperty("name")
    private String name;
    @JsonProperty("about")
    private String about;
    @JsonProperty("trait")
    private List<OviewInfoNodeModel> trait = new ArrayList<OviewInfoNodeModel>();
    @JsonProperty("md5")
    private String  md5;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("ctime")
    private String  ctime;
    @JsonProperty("owner")
    private String  owner;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public static OviewContentModel getModelFromJsonNode(String json, String node) throws Exception {

        String nodeJson;
        nodeJson = converter.getJsonStringFromJsonNodeName(node, json);
        return getOviewEntitiesFromString(nodeJson);
    }

    public static OviewContentModel getOviewEntitiesFromString(String json) throws Exception {

        Object                              object;
        JacksonConverter<OviewContentModel> converter = new JacksonConverter<OviewContentModel>(OviewContentModel.class);
        object = converter.getJsonObject(json);
        return converter.getJsonObject(object, OviewContentModel.class);
    }

    /**
     * @return The uid
     */
    @JsonProperty("id")
    public String getUid() {

        return uid;
    }

    /**
     * @param uid The uid
     */
    @JsonProperty("id")
    public void setUid(String uid) {

        this.uid = uid;
    }

    @JsonProperty("zipkey")
    public String getZipkey() {

        return zipkey;
    }

    @JsonProperty("zipkey")
    public void setZipkey(String zipkey) {

        this.zipkey = zipkey;
    }

    /**
     * @return The ar
     */
    @JsonProperty("ar")
    public String getAr() {

        return ar;
    }

    /**
     * @param ar The ar
     */
    @JsonProperty("ar")
    public void setAr(String ar) {

        this.ar = ar;
    }

    /**
     * @return The name
     */
    @JsonProperty("name")
    public String getName() {

        return name;
    }

    /**
     * @param name The name
     */
    @JsonProperty("name")
    public void setName(String name) {

        this.name = name;
    }

    /**
     * @return The about
     */
    @JsonProperty("about")
    public String getAbout() {

        return about;
    }

    /**
     * @param about The about
     */
    @JsonProperty("about")
    public void setAbout(String about) {

        this.about = about;
    }

    /**
     * @return The trait
     */
    @JsonProperty("trait")
    public List<OviewInfoNodeModel> getTrait() {

        return trait;
    }

    /**
     * @param trait The trait
     */
    @JsonProperty("trait")
    public void setTrait(List<OviewInfoNodeModel> trait) {

        this.trait = trait;
    }

    /**
     * @return The md5
     */
    @JsonProperty("md5")
    public String getMd5() {

        return md5;
    }

    /**
     * @param md5 The md5
     */
    @JsonProperty("md5")
    public void setMd5(String md5) {

        this.md5 = md5;
    }

    /**
     * @return The size
     */
    @JsonProperty("size")
    public Integer getSize() {

        return size;
    }

    /**
     * @param size The size
     */
    @JsonProperty("size")
    public void setSize(Integer size) {

        this.size = size;
    }

    /**
     * @return The ctime
     */
    @JsonProperty("ctime")
    public String getCtime() {

        return ctime;
    }

    /**
     * @param ctime The ctime
     */
    @JsonProperty("ctime")
    public void setCtime(String ctime) {

        this.ctime = ctime;
    }

    /**
     * @return The owner
     */
    @JsonProperty("owner")
    public String getOwner() {

        return owner;
    }

    /**
     * @param owner The owner
     */
    @JsonProperty("owner")
    public void setOwner(String owner) {

        this.owner = owner;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {

        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {

        this.additionalProperties.put(name, value);
    }
}
