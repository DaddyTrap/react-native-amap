package com.dianwoba.rctamap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.GroundOverlay;
import com.amap.api.maps2d.model.GroundOverlayOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.geocoder.StreetNumber;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ReadableMap;

import java.util.List;

import javax.annotation.Nullable;

import static android.content.ContentValues.TAG;

/**
 * Created by daddytrap on 16-12-3.
 */

public class AMapGroundOverlay extends AMapFeature {
    private GroundOverlayOptions groundOverlayOptions;
    private GroundOverlay groundOverlay;
    private float width;
    private float height;

    private LatLng position;

    private boolean anchorIsSet;
    private float anchorX;
    private float anchorY;

    private final Context context;

    private BitmapDescriptor imageDecriptor;

    private float rotation = 0.0f;

    private boolean visibility = true;
    private float transparency = 1.0f;
    private float zIndex;

    private final DraweeHolder mLogoHolder;
    private DataSource<CloseableReference<CloseableImage>> dataSource;
    private final ControllerListener<ImageInfo> mLogoControllerListener =
            new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(
                        String id,
                        @Nullable final ImageInfo imageInfo,
                        @Nullable Animatable animatable) {
                    CloseableReference<CloseableImage> imageReference = null;
                    try {
                        imageReference = dataSource.getResult();
                        if (imageReference != null) {
                            CloseableImage image = imageReference.get();
                            if (image != null && image instanceof CloseableStaticBitmap) {
                                CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) image;
                                Bitmap bitmap = closeableStaticBitmap.getUnderlyingBitmap();
                                if (bitmap != null) {
                                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                    imageDecriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                                }
                            }
                        }
                    } finally {
                        dataSource.close();
                        if (imageReference != null) {
                            CloseableReference.closeSafely(imageReference);
                        }
                    }
                    update();
                }
            };

    public AMapGroundOverlay(Context context) {
        super(context);
        this.context = context;
        mLogoHolder = DraweeHolder.create(createDraweeHierarchy(), context);
        mLogoHolder.onAttach();
        Log.i(TAG, "AMapGroundOverlay: constructing gol");
    }

    private GenericDraweeHierarchy createDraweeHierarchy() {
        return new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setFadeDuration(0)
                .build();
    }

    public void setCoordinate(ReadableMap coordinate) {
        position = new LatLng(coordinate.getDouble("latitude"), coordinate.getDouble("longitude"));
        if (groundOverlay != null) {
            groundOverlay.setPosition(position);
        }
        update();
    }

    public void setAnchor(double x, double y) {
        anchorIsSet = true;
        anchorX = (float) x;
        anchorY = (float) y;
        if (groundOverlayOptions != null) {
            groundOverlayOptions.anchor(anchorX, anchorY);
        }
        update();
    }


    public void setImage(String uri) {
        Log.i(TAG, "setImage: Setting image");
        if (uri == null) {
            imageDecriptor = null;
            update();
        } else if (uri.startsWith("http://") || uri.startsWith("https://") ||
                uri.startsWith("file://")) {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(uri))
                    .build();

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setControllerListener(mLogoControllerListener)
                    .setOldController(mLogoHolder.getController())
                    .build();
            mLogoHolder.setController(controller);
        } else {
            imageDecriptor = getBitmapDescriptorByName(uri);
            update();
        }
    }

    public void setWidth(float w) {
        this.width = w;
        update();
    }

    public GroundOverlayOptions getGroundOverlayOptions() {
        if (groundOverlayOptions == null) {
            groundOverlayOptions = createGroundOverlayOptions();
        }
        return groundOverlayOptions;
    }

    private BitmapDescriptor getIcon() {
        if (imageDecriptor != null) {
            // use local image as a marker
            return imageDecriptor;
        } else {
            // render the default marker pin
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        }
    }

    private GroundOverlayOptions createGroundOverlayOptions() {
        GroundOverlayOptions options = new GroundOverlayOptions().position(position, width);
        if (anchorIsSet) options.anchor(anchorX, anchorY);
        // TODO: 16/5/19
//        if (calloutAnchorIsSet) options.infoWindowAnchor(calloutAnchorX, calloutAnchorY);
        // TODO: 16/5/19
//        options.rotation(rotation);
        // TODO: 16/5/19
//        options.flat(flat);
        options.image(getIcon());
        return options;
    }

    public void update() {
        Log.i(TAG, "update: GroundOverlay");
        if (groundOverlay == null) {
            return;
        }

        groundOverlay.setImage(getIcon());
        groundOverlay.setDimensions(width);
        groundOverlay.setPosition(position);
        if (anchorIsSet) {
            groundOverlayOptions.anchor(anchorX, anchorY);
        } else {
            groundOverlayOptions.anchor(0.5f, 1.0f);
        }
    }

    @Override
    public void addToMap(AMap map) {
        this.groundOverlay = map.addGroundOverlay(getGroundOverlayOptions());
    }

    @Override
    public void removeFromMap(AMap map) {
        groundOverlay.remove();
        groundOverlay = null;
    }

    @Override
    public Object getFeature() {
        return groundOverlay;
    }

    private int getDrawableResourceByName(String name) {
        return getResources().getIdentifier(
                name,
                "drawable",
                getContext().getPackageName());
    }

    private BitmapDescriptor getBitmapDescriptorByName(String name) {
        return BitmapDescriptorFactory.fromResource(getDrawableResourceByName(name));
    }
}
