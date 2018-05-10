package com.leedian.klozr.model.restapi;
import com.leedian.klozr.BuildConfig;

import okhttp3.Response;

/**
 * a base api call class
 *
 * @author Franco
 */
public class HttpApiBase {
    final static String      RESPONSE_CODE      = "code";
    final static String      UNKNOWN_ERROR_CODE = "99.99";
    private      HttpRequest apiRequest         = new HttpRequest();
    private int major_code;
    private int minor_code;
    private int code;

    /**
     * Get http response code
     *
     **/
    public int getCode() {

        return code;
    }

    /**
     * Set http response code
     *
     * @param code response code
     **/
    public void setCode(int code) {

        this.code = code;
    }

    /**
     * Get http Major code
     *
     **/
    public int getMajor_code() {

        return major_code;
    }

    /**
     * Get http Minor code
     *
     **/
    public int getMinor_code() {

        return minor_code;
    }

    /**
     * Get Api Request
     *
     **/
    HttpRequest getApiRequest() {

        return apiRequest;
    }

    /**
     * Get Http Response
     *
     **/
    Response getHttpResponse() throws Exception {

        Response response;
        response = apiRequest.getResponse();

        this.setCode(response.code());
        this.ParseResponseCode(response.header(RESPONSE_CODE, UNKNOWN_ERROR_CODE));

        return response;
    }

    /**
     * Parse Http Response Code
     *
     * @param codeStr response code
     **/
    void ParseResponseCode(String codeStr) {

        boolean  isResponseHeaderReturn;
        String[] errorStrings;
        isResponseHeaderReturn = !codeStr.equals(UNKNOWN_ERROR_CODE);

        if (!isResponseHeaderReturn) { return; }

        errorStrings = codeStr.split("\\.");
        debugAssert((errorStrings.length == 2));

        major_code = Integer.parseInt(errorStrings[0]);
        minor_code = Integer.parseInt(errorStrings[1]);
    }

    /**
     * Debug Assert help
     *
     * @param positive   positive condition
     **/
    void debugAssert(boolean positive) {

        if (BuildConfig.DEBUG && (!positive)) { throw new RuntimeException(); }
    }
}
