code part related to socket io


MyMap\app\src\main\java\com\project\sonia\mymap
DragActivity.java
MapActivity.java



import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.Scanner;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


// Upload

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

mSocket.emit("new_record", obj);



// Download

mSocket = IO.socket(ip_add);
// Receiving an object
mSocket.on("new_result", new Emitter.Listener() {
    @Override
    public void call(Object... args) {
        JSONObject obj = (JSONObject)args[0];
        try {
            boolean up_result = obj.getBoolean("up_result");

            if(up_result==true) {
                Toast.makeText(getApplicationContext(), "Succeed",
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(DragActivity.this, MainMenu.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "Failed",
                        Toast.LENGTH_LONG).show();
            }
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


// Upload
JSONObject obj = new JSONObject();
try {
    obj.put("type", type);
    obj.put("tar_lat", tar_lat);
    obj.put("tar_lon", tar_lon);

    mSocket.emit("new_request", obj);
} catch (JSONException e) {
    e.printStackTrace();
}



// Download
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