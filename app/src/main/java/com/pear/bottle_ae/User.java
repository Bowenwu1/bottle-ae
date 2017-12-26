package com.pear.bottle_ae;

/**
 * Created by hp on 2017/12/25.
 */

public class User {
    private detail detail;
    private String status;

    public String getStatus() {
        return status;
    }

    public User.detail getDetail() {
        return detail;
    }

    public static class detail{
        private int user_id;
        private String pwd;
        private String username;
        private String nickname;
        private String gender;

        public String getPwd() {
            return pwd;
        }

        public int getUser_id() { return  user_id;}


        public String getGender() {
            return gender;
        }

        public String getNickname() {
            return nickname;
        }

        public String getUsername() {
            return username;
        }


        public void setGender(String gender) {
            this.gender = gender;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
