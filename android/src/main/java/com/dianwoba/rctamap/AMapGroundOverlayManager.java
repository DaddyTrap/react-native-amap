package com.dianwoba.rctamap;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import javax.annotation.Nullable;

/**
 * Created by daddytrap on 16-12-7.
 */

public class AMapGroundOverlayManager extends ViewGroupManager<AMapGroundOverlay> {

    @Override
    public String getName() {
        return "AMapGroundOverlay";
    }

    @Override
    protected AMapGroundOverlay createViewInstance(ThemedReactContext reactContext) {
        return new AMapGroundOverlay(reactContext);
    }

    @ReactProp(name = "coordinate")
    public void setCoordinate(AMapGroundOverlay view, ReadableMap map) {
        view.setCoordinate(map);
    }

    @ReactProp(name = "anchor")
    public void setAnchor(AMapGroundOverlay view, ReadableMap map) {
        // should default to (0.5, 1) (bottom middle)
        double x = map != null && map.hasKey("x") ? map.getDouble("x") : 0.5;
        double y = map != null && map.hasKey("y") ? map.getDouble("y") : 1.0;
        view.setAnchor(x, y);
    }

    @ReactProp(name = "image")
    public void setImage(AMapGroundOverlay view, @Nullable String source) {
        view.setImage(source);
    }

    @ReactProp(name = "rotation", defaultFloat = 0.0f)
    public void setMarkerRotation(AMapGroundOverlay view, float rotation) {
        view.setRotation(rotation);
    }

    @ReactProp(name = "width", defaultFloat = 0.0f)
    public void setWidth(AMapGroundOverlay view, float width) {
        view.setWidth(width);
    }

}
