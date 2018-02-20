package com.homanhuang.tomtomtest;

import android.support.annotation.NonNull;
import com.tomtom.online.sdk.map.TomtomMap;

/**
 * Created by Homan on 2/9/2018.
 */

public interface OnMapReadyCallback extends com.tomtom.online.sdk.map.OnMapReadyCallback {
    /**
     * Called when the map is ready to be used.
     */
    void onMapReady(@NonNull TomtomMap tomtomMap);
}
