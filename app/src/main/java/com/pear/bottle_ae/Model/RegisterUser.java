package com.pear.bottle_ae.Model;

/**
 * Created by jiayin on 2017/12/26.
 */

public class RegisterUser {
    private String username;
    private String nickname;
    private String gender;
    private String pwd;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd() {
        return pwd;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGender() {
        return gender;
    }

}
