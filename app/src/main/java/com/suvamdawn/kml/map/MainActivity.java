package com.suvamdawn.kml.map;

import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static android.support.constraint.Constraints.TAG;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,GoogleMap.OnCameraMoveStartedListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.this, R.raw.style_json_dark));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }else{
                // for default camera focus
                mMap = googleMap;
                mMap.animateCamera(CameraUpdateFactory.zoomTo(19), 5000, null);
                LatLng def = new LatLng(12.9716, 77.5946); //bangalore
                CameraUpdate locations = CameraUpdateFactory.newLatLngZoom(def, 19);
                mMap.animateCamera(locations);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                //fetch KML Layer from local file
                KmlLayer layer = new KmlLayer(googleMap, R.raw.blore_bldgs_trial_with_title, getApplicationContext());
                //add KML Layer on map
                layer.addLayerToMap();
                //KML layer click listener
                layer.setOnFeatureClickListener(new Layer.OnFeatureClickListener() {
                    @Override
                    public void onFeatureClick(com.google.maps.android.data.Feature feature) {
                        Toast.makeText(getApplicationContext(),"Name: "+feature.getProperty("name")+"\nDescription: "+feature.getProperty("description"),Toast.LENGTH_LONG).show();
                    }
                });
                //fetch name and description from local KML Layer file
                for (KmlContainer containers : layer.getContainers()){
                    for (KmlPlacemark placemark : containers.getPlacemarks()) {
                        KmlPolygon polygon = (KmlPolygon) placemark.getGeometry();
                        for (LatLng latLng : polygon.getOuterBoundaryCoordinates()) {
                            builder.include(latLng);
                        }
                    }
                }
                //for camera focus
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels-50;
                int height = getResources().getDisplayMetrics().heightPixels-50;
                int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                mMap.moveCamera(cu);
                mMap.animateCamera(cu);
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
