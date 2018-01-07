package com.pear.bottle_ae.Model;

/**
 * Created by hp on 2017/12/25.
 */

public class User {
    private String status;

    public String getStatus() {
        return status;
    }
        private data data;


    public User.data getData() {
        return data;
    }

    public static class data {
        private int user_id;
        private String username;
        private String nickname;
        private String gender;

        public int getUser_id() {
            return user_id;
        }

        public String getUsername() {
            return username;
        }

        public String getGender() {
            return gender;
        }

        public String getNickname() {
            return nickname;
        }
    }
}
