package com.pear.bottle_ae;

/**
 * Created by wubowen on 2018/1/4.
 */

public class BottleLocation {
    public String formatted_address;
    public double latitude;
    public double longitude;
    public BottleLocation(double latitude, double longitude, String formatted_address) {
        this.formatted_address = formatted_address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
