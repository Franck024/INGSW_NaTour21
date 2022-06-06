package com.example.natour21.map;





import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.natour21.AddressAutoSearchComponent;
import com.example.natour21.PermissionUtils;
import com.example.natour21.R;
import com.example.natour21.exceptions.ProviderDisabledException;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;


public class MapActivity extends AppCompatActivity implements java.util.Observer {


    private MapView map;
    private ImageButton btnDeletePath, btnSend, btnCenterOnUserLocation;
    private ProgressBar autoSearchProgressBar;
    private ScrollView scrollView;
    private List<PolylineMarkerPair> polylineMarkerPairs = new LinkedList<PolylineMarkerPair>();
    private final int MAX_RESULTS = 5;
    private final int DEFAULT_COLOR = Color.rgb(0, 80, 0);
    private final double DEFAULT_ZOOM = 17.5;
    private final int DEFAULT_REQUIRED_LOCATION_ACCURACY = 60;
    private AddressAutoSearchComponent addressAutoSearchComponent;

    private enum MapMode {
        MARKER_INSERT,
        VISUALIZE;
    }
    private class PolylineMarkerPair{
        private Polyline polyline;
        private List<Marker> markers;

        public PolylineMarkerPair(){
            this.polyline = new Polyline();
            this.markers = new LinkedList<Marker>();
        }

        public PolylineMarkerPair(Polyline polyline, List<Marker> markers) {
            this.polyline = polyline;
            this.markers = markers;
        }

        public Polyline getPolyline() {
            return polyline;
        }

        public List<Marker> getMarkers() {
            return markers;
        }

        public void setPolyline(Polyline polyline) {
            this.polyline = polyline;
        }

        public void setMarkers(List<Marker> markers) {
            this.markers = markers;
        }

        public void setPolylineColor(int rgb){
            polyline.getOutlinePaint().setColor(rgb);
        }
    }

    private MapMode mapMode = MapMode.MARKER_INSERT;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.map_activity);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        GeoPoint puntoIniziale = null;
        if (bundle != null && bundle.containsKey("PUNTO_INIZIALE_LAT") && bundle.containsKey("PUNTO_INIZIALE_LONG")){
            puntoIniziale = new GeoPoint(bundle.getDouble("PUNTO_INIZIALE_LAT"),
                    bundle.getDouble("PUNTO_INIZIALE_LONG"));
        }
        btnSend = findViewById(R.id.btnSendPath);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (polylineMarkerPairs == null || polylineMarkerPairs.size() != 1) return;
                PolylineMarkerPair mainPolylineMarkerPair = polylineMarkerPairs.get(0);
                if (mainPolylineMarkerPair == null) return;
                List<GeoPoint> geoPoints = mainPolylineMarkerPair
                        .getPolyline().getActualPoints();
                Bundle bundle = new Bundle();
                geoPoints = MapConverter.geoPointListEPSG3857ToGeoPointListEPSG4326(geoPoints);
                bundle.putString("GEO_POINT_LIST", MapConverter
                        .geoPointsToString(geoPoints));
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        btnDeletePath = findViewById(R.id.btnDeleteAll);
        btnDeletePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (polylineMarkerPairs == null || polylineMarkerPairs.size() != 1 ||
                mapMode == MapMode.VISUALIZE) return;
                clearPolyLineMarkerPair(polylineMarkerPairs.get(0));
                //Questo metodo dovrebbe essere chiamato solo in modalità di modifica mappa
                //in cui abbiamo un singolo tracciato.
                //Ergo, dobbiamo garantire la presenza di un nuovo tracciato.
                PolylineMarkerPair newPolylineMarkerPair = new PolylineMarkerPair();
                polylineMarkerPairs.add(newPolylineMarkerPair);
                newPolylineMarkerPair.setPolylineColor(DEFAULT_COLOR);
                map.getOverlays().add(newPolylineMarkerPair.getPolyline());

            }
        });
        btnCenterOnUserLocation = findViewById(R.id.btnCenterOnUserLocation);
        btnCenterOnUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.shouldAskForPermissions()) {
                    ActivityResultLauncher<String> requestPermissionLauncher =
                            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                                if (isGranted) {
                                    try{
                                        getUserLocation();
                                    }
                                    catch(SecurityException | ProviderDisabledException e){
                                        Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG);
                                    }
                                } else {
                                    Toast.makeText(ctx, "Autorizzazione negatata.", Toast.LENGTH_SHORT);
                                    Log.w("Permessi", "Negati");
                                    finish();
                                }
                            });
                    if (ContextCompat.checkSelfPermission(
                            ctx, Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                        try{
                            getUserLocation();
                        }
                        catch(SecurityException | ProviderDisabledException e){
                            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG);
                        }
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }
                else try{
                    getUserLocation();
                }
                catch(SecurityException | ProviderDisabledException e){
                    Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        });
        scrollView = new ScrollView(ctx);
        LinearLayout myLayout = findViewById(R.id.linearLayout);

        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                zoomOntoGeoPoint(addressAutoSearchComponent.getChosenGeoPoint(position));
            }
        };

        RelativeLayout autoSearchRelativeLayout = findViewById(R.id.mapRelativeLayout);

        addressAutoSearchComponent = new AddressAutoSearchComponent(ctx,
                onItemClickListener,
                MAX_RESULTS);

        AutoCompleteTextView addressAutoCompleteTextView = addressAutoSearchComponent
                .getAddressAutoCompleteTextView();

        addressAutoSearchComponent
                .getAddressAutoCompleteTextView()
                .setLayoutParams(new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));


        int addressAutoCompleteTextViewId = View.generateViewId();
        addressAutoCompleteTextView.setId(addressAutoCompleteTextViewId);

        RelativeLayout.LayoutParams progressBarLayoutParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        progressBarLayoutParams.addRule(RelativeLayout.ALIGN_TOP, addressAutoCompleteTextViewId);
        progressBarLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, addressAutoCompleteTextViewId);
        progressBarLayoutParams.addRule(RelativeLayout.ALIGN_RIGHT, addressAutoCompleteTextViewId);

        autoSearchProgressBar = new ProgressBar(ctx);
        autoSearchProgressBar.setLayoutParams(progressBarLayoutParams);
        autoSearchProgressBar.setIndeterminate(true);
        autoSearchProgressBar.setVisibility(View.INVISIBLE);

        if (mapMode.equals(MapMode.MARKER_INSERT)) polylineMarkerPairs.add(new PolylineMarkerPair());
        //else polylineMarkerPairs.addAll(inputPolylineMarkerPairs);

        autoSearchRelativeLayout.addView(addressAutoCompleteTextView);
        autoSearchRelativeLayout.addView(autoSearchProgressBar);
        myLayout.addView(scrollView);
        map = new MapView(ctx);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        MapView.LayoutParams mapParams = new MapView.LayoutParams(
                MapView.LayoutParams.MATCH_PARENT,
                MapView.LayoutParams.MATCH_PARENT,
                null, 0, 0, 0);
        map.invalidate();
        map.getOverlays().add(new Overlay() {
            @Override
            public void draw(Canvas c, MapView osmv, boolean shadow) {

            }


            @Override
            public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
                Projection projection = mapView.getProjection();
                GeoPoint geoPoint = (GeoPoint) projection.fromPixels((int)e.getX(), (int)e.getY());
                if (mapMode == MapMode.MARKER_INSERT){
                    Marker marker = new Marker(map);
                    marker.setInfoWindow(null);
                    marker.setPosition(geoPoint);
                    PolylineMarkerPair mainPolylineMarkerPair = polylineMarkerPairs.get(0);
                    mainPolylineMarkerPair.getMarkers().add(marker);
                    map.getOverlays().add(marker);
                    mainPolylineMarkerPair.getPolyline().addPoint(geoPoint);
                    map.invalidate();
                }
                return true;
            }

        });

        for (PolylineMarkerPair p : polylineMarkerPairs){
            p.setPolylineColor(DEFAULT_COLOR);
            map.getOverlays().add(p.getPolyline());
        }
        if (puntoIniziale != null) {
            Marker marker = new Marker(map);
            marker.setInfoWindow(null);
            marker.setPosition(puntoIniziale);
            PolylineMarkerPair mainPolylineMarkerPair = polylineMarkerPairs.get(0);
            mainPolylineMarkerPair.getMarkers().add(marker);
            map.getOverlays().add(marker);
            mainPolylineMarkerPair.getPolyline().addPoint(puntoIniziale);
            map.invalidate();
        }

        myLayout.addView(map, mapParams);
    }

    @Override
    public void update(Observable observable, Object o) {
        Log.i("Map", "Observable update received");
        if (!(o instanceof Boolean)) return;
        if (autoSearchProgressBar == null) return;
        Boolean isSearchingAddress = (Boolean) o;
        runOnUiThread( () -> autoSearchProgressBar.setVisibility
                (isSearchingAddress ? View.VISIBLE : View.INVISIBLE));
    }

    private void getUserLocation() throws SecurityException, ProviderDisabledException {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            throw new ProviderDisabledException("Abilitare il seguente provider:" + LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 150, 1, new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location){
                if ( location.getAccuracy() < DEFAULT_REQUIRED_LOCATION_ACCURACY ){
                    locationManager.removeUpdates(this);
                    zoomOntoLocation(location);
                }
            }
        });
    }
    private void zoomOntoLocation(Location location){
        if (map != null){
            GeoPoint startPoint = new GeoPoint(location);
            IMapController mapController = map.getController();
            mapController.setZoom(DEFAULT_ZOOM);
            mapController.setCenter(startPoint);

        }
    }

    private void zoomOntoGeoPoint(GeoPoint geoPoint){
        IMapController mapController = map.getController();
        mapController.setZoom(DEFAULT_ZOOM);
        mapController.animateTo(geoPoint);
    }


    private void clearMarkersFromMap(List<Marker> markers){
        for (Marker m : markers){
            map.getOverlays().remove(m);
        }
    }

    private void clearPolyLineMarkerPair(PolylineMarkerPair polylineMarkerPair){
        Polyline polyline = polylineMarkerPair.getPolyline();
        map.getOverlays().remove(polyline);
        clearMarkersFromMap(polylineMarkerPair.getMarkers());
        polylineMarkerPairs.remove(polylineMarkerPair);
    }

    private Gpx generateGPX(PolylineMarkerPair polylineMarkerPair){
        Gpx.Builder GPXbuilder = new Gpx.Builder();
        Track.Builder trackBuilder = new Track.Builder();
        TrackSegment.Builder trackSegmentBuilder = new TrackSegment.Builder();
        TrackPoint.Builder trackPointBuilder = new TrackPoint.Builder();
        List<Marker> markers = polylineMarkerPair.getMarkers();
        List<TrackPoint> trackPoints = new LinkedList<TrackPoint>();
        for (Marker m: markers){
            GeoPoint geoPoint = m.getPosition();
            trackPointBuilder.setLatitude(geoPoint.getLatitude());
            trackPointBuilder.setLongitude(geoPoint.getLongitude());
            trackPoints.add(trackPointBuilder.build());
        }
        trackSegmentBuilder.setTrackPoints(trackPoints);
        LinkedList<TrackSegment> trackSegments = new LinkedList<TrackSegment>();
        trackSegments.add(trackSegmentBuilder.build());
        LinkedList<Track> tracks = new LinkedList<Track>();
        trackBuilder.setTrackSegments(trackSegments);
        tracks.add(trackBuilder.build());
        GPXbuilder.setTracks(tracks);
        return GPXbuilder.build();
    }

    private PolylineMarkerPair generatePolylineMarkerPairFromGeoPoints(List<GeoPoint> geopoints){
        Polyline polyline = new Polyline();
        List<Marker> markers = new LinkedList<Marker>();
        for(GeoPoint gp : geopoints){
            polyline.addPoint(gp);
            Marker m = new Marker(map);
            m.setPosition(gp);
            markers.add(m);
        }
        return new PolylineMarkerPair(polyline, markers);
    }

    private void addPolylineMarkerPairToMap(PolylineMarkerPair polylineMarkerPair){
        Polyline polyline = polylineMarkerPair.getPolyline();
        List<Marker> markers = polylineMarkerPair.getMarkers();
        map.getOverlays().add(polyline);
        for (Marker m : markers) map.getOverlays().add(m);
    }



    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}
