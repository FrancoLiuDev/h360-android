package com.leedian.klozr.model.dataOut;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Model for Oview content list
 *
 * @author Franco
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public class OviewListModel
        implements Serializable
{

    @JsonProperty("name")
    private String name;
    @JsonProperty("thumbkey")
    private String thumbkey;
    @JsonProperty("id")
    private int    index;
    @JsonProperty("zipkey")
    private String zipkey;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public OviewListModel() {

    }

    /**
     *
     * @param thumbkey The thumbkey
     * @param name The name
     */
    public OviewListModel(String thumbkey, String name) {

        this.thumbkey = thumbkey;
        this.name = name;
    }

    /**
     * get index
     *
     **/
    public int getIndex() {

        return index;
    }

    /**
     * get index
     *
     * @param index The index
     **/
    public void setIndex(int index) {

        this.index = index;
    }

    /**
     * get name
     *
     **/
    public String getName() {

        return name;
    }

    /**
     * set name
     *
     * @param name The name
     **/
    public void setName(String name) {

        this.name = name;
    }

    /**
     * get  Thumbkey
     * @param Thumbkey The Thumbkey
     **/
    public String getThumbkey() {

        return thumbkey;
    }

    /**
     * set  Thumbkey
     *
     * @param Thumbkey The Thumbkey
     **/
    public void setThumbkey(String thumbkey) {

        this.thumbkey = thumbkey;
    }

    /**
     * get Zipkey
     *
     **/
    public String getZipkey() {

        return zipkey;
    }

    /**
     * set Zipkey
     *
     * @param zipkey The zipkey
     **/
    public void setZipkey(String zipkey) {

        this.zipkey = zipkey;
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
