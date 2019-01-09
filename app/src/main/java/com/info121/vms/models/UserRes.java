package com.info121.vms.models;

import com.orm.SugarRecord;

/**
 * Created by KZHTUN on 2/27/2018.
 */

public class UserRes extends SugarRecord {

    private String username;
    private String userpassword;

    public UserRes() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }
}
