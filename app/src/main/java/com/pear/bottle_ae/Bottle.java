package com.pear.bottle_ae;

/**
 * Created by wubowen on 2018/1/3.
 */

public class Bottle {
    public String Content;
    public int style;

    public static class BottleLocation {
        public String formatted_address;
        public float latitude;
        public float longitude;
    }
    public Bottle.BottleLocation location;

    // 普通瓶子
    public static int STYLE_NORMAL    = 1;
    // VIP扔的瓶子
    public static int STYLE_VIP       = 2;
    // 穷逼VIP，充话费送的那种
    public static int STYLE_POOR_VIP  = 3;
}
