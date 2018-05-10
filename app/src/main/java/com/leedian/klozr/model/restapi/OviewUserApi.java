package com.leedian.klozr.model.restapi;
import com.leedian.klozr.model.dataIn.AuthCredentials;
import com.leedian.klozr.model.dataOut.UserInfoModel;

import okhttp3.Response;

/**
 * Oview User Api
 *
 * @author Franco
 */
public class OviewUserApi
        extends HttpApiBase
{
    private UserInfoModel userModel;

    /**
     * Get user infomation
     *
     * @return UserInfoModel
     */
    public UserInfoModel getUserModel() {

        return userModel;
    }

    /**
     * requestSignIn
     *
     * @param credentials
     * @return boolean
     * @throws Exception
     */
    public boolean requestSignIn(AuthCredentials credentials) throws Exception {

        boolean  isSuccessCall;
        Response response;
        String   body;

        try {
            isSuccessCall = getApiRequest().loginRequest(credentials).connectSync();
            response = getApiRequest().getResponse();

            if (!isSuccessCall) {
                this.ParseResponseCode(response.header(RESPONSE_CODE, UNKNOWN_ERROR_CODE));
                return false;
            } else {
                body = response.body().string();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        debugAssert(body != null);

        try {
            String jsonDataNode = "data";
            userModel = UserInfoModel.getModelFromJsonNode(body, jsonDataNode);
            userModel.setUser(credentials.getUsername());
            userModel.setBrand(UrlMessagesConstants.StrHttpServiceBrandName);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }
}
