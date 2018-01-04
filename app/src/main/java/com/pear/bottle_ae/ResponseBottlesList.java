package com.pear.bottle_ae;

import java.util.List;

/**
 * Created by wubowen on 2018/1/4.
 */

public class ResponseBottlesList {
    public String status;
    public String msg;

    public static class Data {
        BottleLocation centerLocation;
        BottleLocation spanLocation;
        List<Bottle> bottles;
    }

    public ResponseBottlesList.Data data;
}
