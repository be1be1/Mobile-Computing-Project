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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.Scanner;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap gmap = null;
    private Marker mPos = null;
    private float speed;
    private double tar_lat,tar_lon;

    private Marker[] shops;


    private int pic_num = 0;

    private LinearLayout info_clo,menu_clo;
    private ImageView m_subtype;
    private TextView m_tag[];
    private TextView m_name,m_location,m_description;

    private int download_fre = 10;
    private int download_per = 10;
    private double pre_lat =-1,pre_lon=-1;

    private int cur_type = 1;
    private int[] l_subtype;
    private String[] l_name,l_location,l_description,l_tags;
    private double[] l_lat,l_lon;

    private int num_result;

    private int pic[];

    private Button btnMenu;

    private String FILE_NAME = null;
    private String ip_add;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_noogle);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ionAni.start();

        data_init();




    }

    private void ip_load(){

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
        ip_add = sb.toString();
        tPrint(ip_add);

        try {
            mSocket = IO.socket(ip_add);

            mSocket.on("new_data", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject)args[0];
                    try {
                        num_result = obj.getInt("num_result");
                        JSONArray datas = obj.getJSONArray("result_data");
                        JSONObject tempd;

                        for(int i=0;i<num_result;i++){
                            tempd = (JSONObject) datas.get(i);
                            l_subtype[i] = tempd.getInt("sub_type");

                            l_name[i] = tempd.getString("name");
                            l_location[i] = tempd.getString("location");
                            l_description[i] = tempd.getString("description");
                            l_tags[i] = tempd.getString("tags");

                            l_lat[i] = tempd.getDouble("m_lat");
                            l_lon[i] = tempd.getDouble("m_lon");
                        }

                        displayMarkers();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void data_init(){
        FILE_NAME = getResources().getString(R.string.ip_file_name);
        ip_load();
        info_clo = (LinearLayout)findViewById(R.id.infoClo);
        menu_clo = (LinearLayout)findViewById(R.id.menuClo);
        m_subtype = (ImageView)findViewById(R.id.mSubtype);
        m_tag = new TextView[3];
        m_tag[0] = (TextView)findViewById(R.id.mTag1);
        m_tag[1] = (TextView)findViewById(R.id.mTag2);
        m_tag[2] = (TextView)findViewById(R.id.mTag3);
        m_name = (TextView)findViewById(R.id.mName);
        m_location = (TextView)findViewById(R.id.mLocation);
        m_description = (TextView)findViewById(R.id.mDescription);

        btnMenu = (Button)findViewById(R.id.btnMenu);

        cur_type = 1;

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

        l_subtype = new int[10];

        l_name = new String[10];
        l_location = new String[10];
        l_description = new String[10];
        l_tags = new String[10];

        l_lat = new double[10];
        l_lon = new double[10];

    }

    Thread ionAni = new Thread(){
        @Override
        public void run(){
            try {
                while (!isInterrupted()) {
                    Thread.sleep(200);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double cur_lat,cur_lon;
                            if(mPos==null) return;
                            cur_lat = mPos.getPosition().latitude;
                            cur_lon = mPos.getPosition().longitude;

                            double dif_lat,dif_lon;
                            dif_lat = tar_lat-cur_lat;
                            dif_lon = tar_lon-cur_lon;
                            if(dif_lat<5E-5&&dif_lat>-5E-5
                                    &&dif_lon<5E-5&&dif_lon>-5E-5){
                                tPrint("stop");
                                if(pic_num!=0){
                                    pic_num = 0;
                                    mPos.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ion_1));
                                }
                            }else{
                                tPrint("move");
                                if(dif_lat<0){
                                    cur_lat -= Math.min(-dif_lat,1E-5);
                                }else{
                                    cur_lat += Math.min(dif_lat,1E-5);
                                }
                                if(dif_lon<0){
                                    cur_lon -= Math.min(-dif_lon,1E-5);
                                }else{
                                    cur_lon += Math.min(dif_lon,1E-5);
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

            show_info_window(i);

            return false;
        }
    };



    private void show_info_window(int i){
        btnMenu.setVisibility(View.INVISIBLE);
        String[] tmp_tag = l_tags[i].split(",");
        for(int j=0;j<3;j++){
            if(tmp_tag[j].compareTo("")==0){
                m_tag[j].setVisibility(View.INVISIBLE);
            }else{
                m_tag[j].setVisibility(View.VISIBLE);
                m_tag[j].setText(tmp_tag[j]);
            }
        }

        m_name.setText(l_name[i]);
        m_location.setText(l_location[i]);
        m_description.setText(l_description[i]);
        m_subtype.setImageResource(pic[l_subtype[i]]);

        info_clo.setVisibility(View.VISIBLE);

    }


    OnMyLocationButtonClickListener btnLocate = new OnMyLocationButtonClickListener() {

        @Override
        public boolean onMyLocationButtonClick() {

            tPrint("button clicked");
            return true;
        }
    };

    private void download_records(int type){
        tPrint("downloading");

        pre_lon = tar_lon;
        pre_lat = tar_lat;
        Toast.makeText(getApplicationContext(), "Hunting",
                Toast.LENGTH_LONG).show();

        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
            obj.put("tar_lat", tar_lat);
            obj.put("tar_lon", tar_lon);

            mSocket.emit("new_request", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // type, tar_lat, tar_lon

        // name,location,tags,description
        // lat,lon
        // subtype


    }

    private void displayMarkers(){
        tPrint("display markers");

        for(int i=0;i<10;i++){
            if(shops[i]!=null)
                shops[i].remove();
        }

        for(int i=0;i<num_result;i++){
            shops[i] = gmap.addMarker(new MarkerOptions()
                    .position(new LatLng(l_lat[i], l_lon[i]))
                    .icon(BitmapDescriptorFactory.fromResource(pic[l_subtype[i]])));
            shops[i].setTag(i);
        }

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
                    mPos.setTag(94);
                    tPrint("before error");
                }

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(tar_lat, tar_lon))
                        .zoom(19)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                if(info_clo.getVisibility()!=View.VISIBLE)
                    gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

                if(download_per>=download_fre){
                    double t1 = tar_lat-pre_lat;
                    double t2 = tar_lon-pre_lon;
                    if(Math.abs(t1)+Math.abs(t2)>1e-3) {
                        download_records(cur_type);
                    }
                    download_per = 0;
                }

                download_per++;
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

    public void close_info(View view){
        tPrint("close info");
        info_clo.setVisibility(View.INVISIBLE);
        btnMenu.setVisibility(View.VISIBLE);
    }

    public void close_menu(View view){
        tPrint("close menu");
        menu_clo.setVisibility(View.INVISIBLE);
        btnMenu.setVisibility(View.VISIBLE);
    }

    public void show_menu(View view){
        tPrint("close menu");
        menu_clo.setVisibility(View.VISIBLE);
        btnMenu.setVisibility(View.INVISIBLE);
    }

    public void open_input(View view){
        int type = 0;
        if(view.getId()==R.id.btnAm){ // academic
            type = 1;
        }else if(view.getId()==R.id.btnFm){ // food
            type = 2;
        }else if(view.getId()==R.id.btnPm){ // pokemon
            type = 3;
        }
        download_records(type);
        download_per = 0;
        menu_clo.setVisibility(View.INVISIBLE);
        btnMenu.setVisibility(View.VISIBLE);
    }

    public void open_menu(View view){
        ionAni.interrupt();
        Intent intent =new Intent(MapActivity.this,MainMenu.class);
        startActivity(intent);
        finish();
    }


    private void tPrint(String str) {
        System.out.println("sonia: " + str);
    }


}

