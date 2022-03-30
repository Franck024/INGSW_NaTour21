package com.example.natour21.map;





import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.natour21.R;
import com.example.natour21.chat.stompclient.UserStompClient;
import com.example.natour21.entities.Messaggio;
import com.example.natour21.entities.Utente;
import com.example.natour21.exceptions.ProviderDisabledException;
import com.fasterxml.jackson.core.JsonProcessingException;

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

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.Track;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import io.ticofab.androidgpxparser.parser.domain.TrackSegment;


public class MapActivity extends AppCompatActivity {


    private MapView map;
    private AutoCompleteTextView addressAutoCompleteTextView;
    private Button btnSend;
    private Button btnDeletePath;
    private Geocoder geocoder;
    private boolean isSearchingAddress = false;
    private boolean isEditFromDropDown = false;
    private ScrollView scrollView;
    private TextWatcher textWatcher;
    private Handler delayedCallHandler = new Handler();
    private Runnable searchRunnable;
    private String inputAddress;
    private List<GeoPoint> shownAddressesGeopoints;
    private List<PolylineMarkerPair> polylineMarkerPairs = new LinkedList<PolylineMarkerPair>();
    private final int MAX_RESULTS = 5;
    private final int DEFAULT_COLOR = Color.rgb(0, 80, 0);
    private final double DEFAULT_ZOOM = 17.5;
    private final int DEFAULT_REQUIRED_LOCATION_ACCURACY = 60;

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

        //handle permissions first, before map is created. not depicted here


        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.map_activity);
        Intent intent = getIntent();
        btnSend = findViewById(R.id.btnSendPath);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (polylineMarkerPairs != null && (polylineMarkerPairs.size() == 1)){
                    PolylineMarkerPair mainPolylineMarkerPair = polylineMarkerPairs.get(0);
                    if (mainPolylineMarkerPair != null){
                        List<GeoPoint> geoPoints = mainPolylineMarkerPair
                                .getPolyline().getActualPoints();
                        Bundle bundle = new Bundle();
                        bundle.putString("GEO_POINT_LIST", MapConverter
                                .geoPointsToString(geoPoints));
                        intent.putExtras(bundle);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
        scrollView = new ScrollView(ctx);
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.linearlayout);
        addressAutoCompleteTextView = new AutoCompleteTextView(ctx);
        addressAutoCompleteTextView.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        addressAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                zoomOntoGeoPoint(shownAddressesGeopoints.get(position));
            }
        });

        searchRunnable =  new Runnable() {
            @Override
            public void run() {
                try {
                    isSearchingAddress = true;
                    List<Address> addressList = geocoder
                            .getFromLocationName(inputAddress, MAX_RESULTS);
                    List<String> stringAddresses = new LinkedList<String>();
                    ArrayList<GeoPoint> geopoints = new ArrayList<GeoPoint>();
                    for (Address a : addressList) {
                        String address = a.getAddressLine(0);
                        String result = address;
                        stringAddresses.add(result);
                        geopoints.add(new GeoPoint(a.getLatitude(), a.getLongitude()));
                    }
                    shownAddressesGeopoints = geopoints;
                    List<String> test = new LinkedList<String>();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                                    (ctx, android.R.layout.simple_list_item_1, stringAddresses);

                            arrayAdapter.getFilter().filter(null);
                            addressAutoCompleteTextView.setAdapter(arrayAdapter);
                            addressAutoCompleteTextView.showDropDown();
                            System.out.println(addressAutoCompleteTextView.isPopupShowing());
                            isSearchingAddress = false;
                        }
                    });
                } catch (IOException ioe) {
                    throw new RuntimeException(ioe.getMessage());
                }
            }
        };
        geocoder = new Geocoder(ctx);
        textWatcher = new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addressAutoCompleteTextView.dismissDropDown();
                addressAutoCompleteTextView.setAdapter(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (addressAutoCompleteTextView.isPerformingCompletion()) {
                    isEditFromDropDown = true;
                }
                delayedCallHandler.removeCallbacks(searchRunnable);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isEditFromDropDown){
                    isEditFromDropDown = false;
                    return;
                }
                inputAddress = addressAutoCompleteTextView.getText().toString();
                if (inputAddress.length() >= 5){

                    if (!isSearchingAddress){
                        delayedCallHandler.postDelayed(searchRunnable, 2000);
                    }

                }
            }
        };
        addressAutoCompleteTextView.addTextChangedListener(textWatcher);
        if (mapMode.equals(MapMode.MARKER_INSERT)) polylineMarkerPairs.add(new PolylineMarkerPair());
        //else polylineMarkerPairs.addAll(inputPolylineMarkerPairs);

        scrollView.addView(addressAutoCompleteTextView);
        myLayout.addView(scrollView);
        map = new MapView(ctx);
        map.setTileSource(TileSourceFactory.MAPNIK);


        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        org.osmdroid.views.MapView.LayoutParams mapParams = new org.osmdroid.views.MapView.LayoutParams(
                org.osmdroid.views.MapView.LayoutParams.MATCH_PARENT,
                org.osmdroid.views.MapView.LayoutParams.MATCH_PARENT,
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
        myLayout.addView(map, mapParams);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        //Questo metodo dovrebbe essere chiamato solo in modalità di modifica mappa
        //in cui abbiamo un singolo tracciato.
        //Ergo, dobbiamo garantire la presenza di un nuovo tracciato.
        PolylineMarkerPair newPolylineMarkerPair = new PolylineMarkerPair();
        polylineMarkerPairs.add(newPolylineMarkerPair);
        newPolylineMarkerPair.setPolylineColor(DEFAULT_COLOR);
        map.getOverlays().add(newPolylineMarkerPair.getPolyline());
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
