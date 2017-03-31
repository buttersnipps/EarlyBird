package com.example.soutrikbarua.earlybird;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;



/**
 * Created by soutrikbarua on 2017-03-31.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
