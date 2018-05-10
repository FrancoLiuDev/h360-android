package com.leedian.klozr.model.dataIn;
/**
 * Auth Credentials for login Api
 *
 * @author Franco
 */
public class AuthCredentials {
    private String username;
    private String password;

    /**
     * AuthCredentials
     *
     * @param username name
     * @param password password
     **/
    public AuthCredentials(String username, String password) {

        this.username = username;
        this.password = password;
    }

    /**
     * get user name
     *
     * @return String
     **/
    public String getUsername() {

        return username;
    }

    /**
     * get user password
     *
     * @return String
     **/
    public String getPassword() {

        return password;
    }
}
