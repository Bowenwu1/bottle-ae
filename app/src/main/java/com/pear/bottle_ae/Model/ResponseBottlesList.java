package com.pear.bottle_ae.Model;

import java.util.List;

/**
 * Created by wubowen on 2018/1/4.
 */

public class ResponseBottlesList {
    public String status;
    public String msg;

    public static class Data {
        public BottleLocation centerLocation;
        public BottleLocation spanLocation;
        public List<Bottle> bottles;
    }

    public ResponseBottlesList.Data data;
}
