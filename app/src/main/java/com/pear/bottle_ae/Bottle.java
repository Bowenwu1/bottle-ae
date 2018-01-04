package com.pear.bottle_ae;

/**
 * Created by wubowen on 2018/1/3.
 */

public class Bottle {
    public String content;
    public int style;
    public int openers_count;
    public String created_at;

    public static class BottleLocation {
        public String formatted_address;
        public float latitude;
        public float longitude;
    }
    public Bottle.BottleLocation location;

    // 普通瓶子
    public static int STYLE_NORMAL    = 0;
    // VIP扔的瓶子
    public static int STYLE_VIP       = 1;
    // 穷逼VIP，充话费送的那种
    public static int STYLE_POOR_VIP  = 2;
}
