package com.leedian.klozr.model.dataOut;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.leedian.klozr.utils.JacksonConverter;

/**
 * Model for a user login profile
 *
 * @author Franco
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public class UserInfoModel {
    private static JacksonConverter converter = new JacksonConverter<UserInfoModel>(UserInfoModel.class);
    @JsonProperty("session")
    private String session;
    @JsonProperty("user")
    private String user;
    @JsonProperty("brand")
    private String brand;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public UserInfoModel() {

    }

    /**
     * convert model from  Json Node
     *
     * @param json The json
     * @param node The node name
     * @return UserInfoModel
     **/
    public static UserInfoModel getModelFromJsonNode(String json, String node) throws Exception {

        String nodeJson = converter.getJsonStringFromJsonNodeName(node, json);
        return (UserInfoModel) converter.getJsonObject(nodeJson);
    }

    /**
     * get user Session
     *
     * @return String
     **/
    @JsonProperty("session")
    public String getSession() {

        return session;
    }

    /**
     * set user Session
     **/
    @JsonProperty("session")
    public void setSession(String session) {

        this.session = session;
    }

    /**
     * get Brand
     *
     * @return String
     **/
    @JsonProperty("brand")
    public String getBrand() {

        return brand;
    }

    /**
     * set Brand
     *
     * @param  brand brand name
     **/
    @JsonProperty("brand")
    public void setBrand(String brand) {

        this.brand = brand;
    }

    /**
     * get user
     *
     * @return  String
     **/
    @JsonProperty("user")
    public String getUser() {

        return user;
    }

    /**
     * set user
     *
     * @return  String
     **/
    @JsonProperty("user")
    public void setUser(String user) {

        this.user = user;
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
