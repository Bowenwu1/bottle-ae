package com.pear.bottle_ae;

/**
 * Created by wubowen on 2018/1/7.
 * 改变佳音的User定义，使其风格一致，减少model的冗余
 */

public class ResponseUser {
    private String status;
    private User data;

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return data;
    }
}
