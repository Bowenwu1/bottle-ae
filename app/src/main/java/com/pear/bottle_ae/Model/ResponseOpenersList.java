package com.pear.bottle_ae.Model;

import java.util.List;

/**
 * Created by zhuojun on 2018/6/12.
 */

public class ResponseOpenersList {
    public String status;
    public String msg;

    public static class Data {
        public int openers_count;
        public List<Opener> openers;
    }

    public ResponseOpenersList.Data data;
}
