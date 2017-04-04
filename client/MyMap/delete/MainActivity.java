package com.project.sonia.mymap;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap gmap = null;
    private Marker mPos = null;
    private float speed;
    private double tar_lat,tar_lon;

    private Marker[] shops;


    private int pic_num = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_noogle);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ionAni.start();
    }

    Thread ionAni = new Thread(){
        @Override
        public void run(){
            try {
                while (!isInterrupted()) {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double cur_lat,cur_lon;
                            cur_lat = mPos.getPosition().latitude;
                            cur_lon = mPos.getPosition().longitude;

                            double dif_lat,dif_lon;
                            dif_lat = tar_lat-cur_lat;
                            dif_lon = tar_lon-cur_lon;
                            if(dif_lat<5E-5&&dif_lat>-5E-5
                                    &&dif_lon<1E-4&&dif_lon>-1E-4){
                                tPrint("stop");
                                if(pic_num!=0){
                                    pic_num = 0;
                                    mPos.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ion_1));
                                }
                            }else{
                                tPrint("move");
                                if(dif_lat<0){
                                    cur_lat -= Math.min(-dif_lat,5E-5);
                                }else{
                                    cur_lat += Math.min(dif_lat,5E-5);
                                }
                                if(dif_lon<0){
                                    cur_lon -= Math.min(-dif_lon,5E-5);
                                }else{
                                    cur_lon += Math.min(dif_lon,5E-5);
                                }
                                mPos.setPosition(new LatLng(cur_lat,cur_lon));
                                pic_num++;
                                pic_num %= 4;
                                if (mPos != null) {
                                    tPrint("pic num: "+pic_num);
                                    switch (pic_num) {
                                        case 0:
                                            mPos.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ion_1));
                                            break;
                                        case 1:
                                            mPos.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ion_2));
                                            break;
                                        case 2:
                                            mPos.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ion_3));
                                            break;
                                        case 3:
                                            mPos.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ion_4));
                                            break;
                                    }
                                }
                            }

                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                tPrint("Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            tPrint("Can't find style. Error: ");
        }
        // Position the map's camera near Sydney, Australia.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(-34, 151))      // Sets the center of the map to Mountain View
                .zoom(10)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

        gmap = googleMap;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(btnLocate);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, btnChange);

        shops = new Marker[10];

        googleMap.setOnMarkerClickListener(markerListener);

    }

    OnMarkerClickListener markerListener = new OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            int i = 0;
            for(i=0;i<10;i++){
                if((int)(marker.getTag())==i)
                    break;
            }
            if(i>=10)
                return false;

            tPrint("Shops "+i);
            return true;
        }
    };


    OnMyLocationButtonClickListener btnLocate = new OnMyLocationButtonClickListener() {

        @Override
        public boolean onMyLocationButtonClick() {

            tPrint("button clicked");
            displayMarkers();
            return true;
        }
    };

    public void displayMarkers(){
        tPrint("display markers");

        int a[] = new int[5];

        a[0] = R.drawable.marker_s_01;
        a[1] = R.drawable.marker_s_02;

        shops[0] = gmap.addMarker(new MarkerOptions()
                    .position(new LatLng(tar_lat + 2 * 1e-4, tar_lon + 2 * 1e-4))
                    .icon(BitmapDescriptorFactory.fromResource(a[0])));
        shops[0].setTag(0);

        shops[1] = gmap.addMarker(new MarkerOptions()
                .position(new LatLng(tar_lat - 3 * 1e-4, tar_lon - 1 * 1e-4))
                .icon(BitmapDescriptorFactory.fromResource(a[1])));
        shops[1].setTag(1);

    }

    LocationListener btnChange = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                speed = location.getSpeed();

                tar_lat = location.getLatitude();
                tar_lon = location.getLongitude();

                if (mPos == null) {
                    mPos = gmap.addMarker(new MarkerOptions()
                            .position(new LatLng(tar_lat, tar_lon))
                            .title("Your Location")
                            .anchor(0.5f,0.8f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ion_1)));
                    tPrint("before error");
                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(tar_lat, tar_lon))
                        .zoom(19)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
            }
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
    };


    private void tPrint(String str) {
        System.out.println("sonia: " + str);
    }


}

