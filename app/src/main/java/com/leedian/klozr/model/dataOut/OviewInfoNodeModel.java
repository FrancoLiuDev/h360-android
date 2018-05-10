package com.leedian.klozr.model.dataOut;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

/**
 * Model for a infonode
 *
 * @author Franco
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
                           "n",
                           "x",
                           "y",
                           "r",
                           "info"
                   })
public class OviewInfoNodeModel {
    @JsonProperty("n")
    private String n;
    @JsonProperty("x")
    private String x;
    @JsonProperty("y")
    private String y;
    @JsonProperty("r")
    private String r;
    @JsonProperty("info")
    private String info;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public OviewInfoNodeModel() {

    }

    /**
     * @param r
     * @param n
     * @param y
     * @param info
     * @param x
     */
    public OviewInfoNodeModel(String n, String x, String y, String r, String info) {

        this.n = n;
        this.x = x;
        this.y = y;
        this.r = r;
        this.info = info;
    }

    /**
     * @return The n
     */
    @JsonProperty("n")
    public String getN() {

        return n;
    }

    /**
     * @param n The n
     */
    @JsonProperty("n")
    public void setN(String n) {

        this.n = n;
    }

    /**
     * @return The x
     */
    @JsonProperty("x")
    public String getX() {

        return x;
    }

    /**
     * @param x The x
     */
    @JsonProperty("x")
    public void setX(String x) {

        this.x = x;
    }

    /**
     * @return The y
     */
    @JsonProperty("y")
    public String getY() {

        return y;
    }

    /**
     * @param y The y
     */
    @JsonProperty("y")
    public void setY(String y) {

        this.y = y;
    }

    /**
     * @return The r
     */
    @JsonProperty("r")
    public String getR() {

        return r;
    }

    /**
     * @param r The r
     */
    @JsonProperty("r")
    public void setR(String r) {

        this.r = r;
    }

    /**
     * @return The info
     */
    @JsonProperty("info")
    public String getInfo() {

        return info;
    }

    /**
     * @param info The info
     */
    @JsonProperty("info")
    public void setInfo(String info) {

        this.info = info;
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
