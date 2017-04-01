package com.example.soutrikbarua.earlybird;

import java.util.List;

/**
 * Created by soutrikbarua on 2017-03-31.
 */

public interface RouteFinderListener {
    void onRouteFinderStart();
    void onRouteFinderSuccess(List<Route> route);

}
