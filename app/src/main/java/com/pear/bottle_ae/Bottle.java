package com.pear.bottle_ae;

import android.location.Location;

/**
 * Created by wubowen on 2018/1/4.
 */

public class Bottle {

    // 普通瓶子
    public final static int STYLE_NORMAL    = 1;
    // VIP扔的瓶子
    public final static int STYLE_VIP       = 2;
    // 穷逼VIP，充话费送的那种
    public final static int STYLE_POOR_VIP  = 3;

    public final static double SPAN_LONGITUDE = 0.076545;
    public final static double SPAN_LATITUDE    = 0.054567;

    public int bottle_id;
    public String content;
    public int style;
    public BottleLocation location;

    public Bottle(String content, int style, double latitude, double longitude, String formatted_address) {
        this.content = content;
        switch (style) {
            case STYLE_NORMAL:
                this.style = STYLE_NORMAL;
                break;
            case STYLE_VIP:
                this.style = STYLE_VIP;
                break;
            case STYLE_POOR_VIP:
                this.style = STYLE_POOR_VIP;
                break;
            default:
                this.style = STYLE_NORMAL;
        }

        location = new BottleLocation(latitude, longitude, formatted_address);
    }


    public boolean whetherInArea(Location myLocation) {
        double distanceLongitude = Math.abs(myLocation.getLongitude() - this.location.longitude);
        double distanceLatitude  = Math.abs(myLocation.getLatitude() - this.location.latitude);
        return (distanceLatitude <= SPAN_LATITUDE) && (distanceLongitude <= SPAN_LONGITUDE);
    }
}
