package com.lcp.imandroid.model;

/**
 * Created by linchenpeng on 2018/1/23.
 */

public class LoginResult {
    private int code;
    private String message;
    private User user;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
