package com.project.sonia.mymap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;


public class DragActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap gmap = null;
    private Marker mPos = null;
    private double tar_lat,tar_lon;
    private Marker dragM = null;
    private double m_lat,m_lon;

    private int type,sub_type;
    private long stime,etime;
    private String description, name, location, tags;

    private int pic[];

    private String FILE_NAME = null;
    private String ip_add;

    SocketIO socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noogle_drag);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = this.getIntent().getExtras();
        type = bundle.getInt("type");
        sub_type = bundle.getInt("sub_type");
        stime = bundle.getLong("s_time");
        etime = bundle.getLong("e_time");
        name = bundle.getString("name");
        description = bundle.getString("description");
        tags = bundle.getString("tags");
        location = bundle.getString("location");

        pic = new int[18];
        pic[0] = R.drawable.marker_s_01;
        pic[1] = R.drawable.marker_s_02;
        pic[2] = R.drawable.marker_s_03;
        pic[3] = R.drawable.marker_s_04;
        pic[4] = R.drawable.marker_s_05;
        pic[5] = R.drawable.marker_s_06;
        pic[6] = R.drawable.marker_s_07;
        pic[7] = R.drawable.marker_s_08;
        pic[8] = R.drawable.marker_s_09;
        pic[9] = R.drawable.marker_s_10;
        pic[10] = R.drawable.marker_s_11;
        pic[11] = R.drawable.marker_s_12;
        pic[12] = R.drawable.marker_s_13;
        pic[13] = R.drawable.marker_s_14;
        pic[14] = R.drawable.marker_s_15;
        pic[15] = R.drawable.marker_s_16;
        pic[16] = R.drawable.marker_s_17;
        pic[17] = R.drawable.marker_s_18;

        FILE_NAME = getResources().getString(R.string.ip_file_name);
        ip_load();

        Toast.makeText(getApplicationContext(), "Long Push to Drag the Marker",
                Toast.LENGTH_LONG).show();

    }

    private void ip_load(){
        FILE_NAME = getResources().getString(R.string.ip_file_name);
        FileInputStream in = null;
        Scanner s = null;
        StringBuffer sb = new StringBuffer();
        try {
            in = super.openFileInput(FILE_NAME);
            s = new Scanner(in);
            while (s.hasNext()) {
                sb.append(s.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (in != null) {
            try {
                in.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ip_add = sb.toString();
        tPrint("Loaded: " + ip_add);

        String host = ip_add;//"http://192.168.56.1:1234";
        try {
            socket = new SocketIO(host);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        socket.connect(new IOCallback() {
            @Override
            public void onMessage(JSONObject json, IOAcknowledge ack) {
               tPrint("Server said(JSON): " + json);
            }

            @Override
            public void onMessage(String data, IOAcknowledge ack) {
                tPrint("Server said(Str): " + data);
            }

            @Override
            public void onError(SocketIOException socketIOException) {
                tPrint("an Error occured");
                socketIOException.printStackTrace();
            }

            @Override
            public void onDisconnect() {
                System.out.println("Connection terminated.");
            }

            @Override
            public void onConnect() {
                tPrint("Connection established");
            }

            @Override
            public void on(String event, IOAcknowledge ack, Object... args) {
                tPrint("Server triggered event '" + event + "'");
                if (event.compareTo("upload_result") == 0) {
                    JSONObject obj = (JSONObject) args[0];
                    Boolean up_result;
                    try {
                        up_result = obj.getBoolean("result");
                    } catch (JSONException e) {
                        tPrint("Mal Result");
                        up_result = false;
                    }
                    tPrint("Upload Result: "+up_result);
                    if(up_result){
                        quit_record(null);
                    }
                }
            }
        });
    }

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
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        gmap = googleMap;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //gmap.setMyLocationEnabled(true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, btnChange);
        gmap.setOnMarkerDragListener(dragListen);



    }

    OnMarkerDragListener dragListen = new OnMarkerDragListener() {
        @Override
        public void onMarkerDragStart(Marker marker) {
        }

        @Override
        public void onMarkerDrag(Marker marker) {
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            if((int)(marker.getTag())==0){
                m_lat = marker.getPosition().latitude;
                m_lon = marker.getPosition().longitude;
                tPrint(m_lat+" "+m_lon);
            }
        }
    };

    LocationListener btnChange = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {

                tar_lat = location.getLatitude();
                tar_lon = location.getLongitude();

                if (mPos == null) {
                    mPos = gmap.addMarker(new MarkerOptions()
                            .position(new LatLng(tar_lat, tar_lon))
                            .title("Your Location")
                            .anchor(0.5f,0.8f)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ion_1)));
                }

                if(dragM==null){
                    dragM = gmap.addMarker(new MarkerOptions()
                            .position(new LatLng(tar_lat + 2e-4, tar_lon + 2e-4))
                            .title("activity location")
                            .icon(BitmapDescriptorFactory.fromResource(pic[sub_type]))
                            .draggable(true));
                    dragM.setTag(0);
                    m_lat = dragM.getPosition().latitude;
                    m_lon = dragM.getPosition().longitude;
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

    public void share_record(View view){
        Toast.makeText(getApplicationContext(), "Uploading Records",
                Toast.LENGTH_LONG).show();

        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
            obj.put("sub_type", sub_type);
            obj.put("stime", stime);
            obj.put("etime", etime);
            obj.put("name", name);
            obj.put("description", description);
            obj.put("tags", tags);
            obj.put("location", location);
            obj.put("m_lat", m_lat);
            obj.put("m_lon", m_lon);

            socket.emit("upload", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void quit_record(View view){

        tPrint("not die yet");
        socket.disconnect();
        Intent intent = new Intent(DragActivity.this, MainMenu.class);
        startActivity(intent);
        finish();

    }


    private void tPrint(String str) {
        System.out.println("sonia: " + str);
    }


}

