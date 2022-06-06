package com.example.natour21.map;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.drawing.MapSnapshot;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapSnapshotUtil {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void takeMapSnapshot(Context context, GeoPoint geoPoint, MapSnapshot.MapSnapshotable callback, int width, int height){

        Marker marker = new Marker(new MapView(context));
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        List<Overlay> mOverlay = new ArrayList<>();
        mOverlay.add(marker);

        MapTileProviderBase mapTileProviderBase = new MapTileProviderBasic(context);
        Rect rect = new Rect(0, 0, width, height);

        Projection mProjection = new Projection(14.00, rect, geoPoint, 0, 0, 0, true, true, MapView.getTileSystem(), 0, 0);

        final MapSnapshot mapSnapshot = new MapSnapshot(callback,
                MapSnapshot.INCLUDE_FLAG_UPTODATE, mapTileProviderBase, mOverlay, mProjection);
        executorService.submit(mapSnapshot);
    }
}
