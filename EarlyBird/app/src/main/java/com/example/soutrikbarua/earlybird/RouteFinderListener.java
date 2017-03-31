package com.example.soutrikbarua.earlybird;

import java.util.List;

/**
 * Created by soutrikbarua on 2017-03-31.
 */

public interface RouteFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);

}
