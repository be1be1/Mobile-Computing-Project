package com.project.sonia.mymap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InputActivity extends AppCompatActivity {

    private int type = 0;

    private Spinner sub_type;
    private EditText name,location,stime,etime,description,tags[];

    private long time_now, time_end;

    private String[] sub_type_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_input);

        sub_type_str = new String[18];
        for(int i=0;i<18;i++){
            sub_type_str[i] = "cry"+i;
        }

        Bundle bundle = this.getIntent().getExtras();
        type = bundle.getInt("type");

        name = (EditText)findViewById(R.id.eName);
        location = (EditText)findViewById(R.id.eLocation);
        stime = (EditText)findViewById(R.id.eTimeStart);
        etime = (EditText)findViewById(R.id.eTimeEnd);
        description = (EditText)findViewById(R.id.eDescription);
        tags = new EditText[3];
        tags[0] = (EditText)findViewById(R.id.eTag1);
        tags[1] = (EditText)findViewById(R.id.eTag2);
        tags[2] = (EditText)findViewById(R.id.eTag3);

        time_now=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sub_type = (Spinner)findViewById(R.id.eClass);
        //数据
        List<String> data_list = new ArrayList<String>();

        if(type==1){
            name.setHint("Name of Academic Activity");

            time_end = time_now+3600000;
            Date data_now =new Date(time_now);
            Date data_end = new Date(time_end);
            String time_str =format.format(data_now);
            String time_str_e = format.format(data_end);
            stime.setText(time_str);
            etime.setText(time_str_e);

            for(int i=0;i<5;i++)
                data_list.add(sub_type_str[i]);
        }else if(type==2){
            name.setHint("Name of Restaurant");
            time_end = time_now+86400000*5;
            Date data_now =new Date(time_now);
            Date data_end = new Date(time_end);
            String time_str =format.format(data_now);
            String time_str_e = format.format(data_end);
            stime.setText(time_str);
            etime.setText(time_str_e);

            for(int i=5;i<12;i++)
                data_list.add(sub_type_str[i]);
        }else if(type==3){
            name.setHint("Name of Pokemon");
            time_end = time_now+600000;
            Date data_now =new Date(time_now);
            Date data_end = new Date(time_end);
            String time_str =format.format(data_now);
            String time_str_e = format.format(data_end);
            stime.setText(time_str);
            etime.setText(time_str_e);

            for(int i=12;i<18;i++)
                data_list.add(sub_type_str[i]);
        }


        //适配器
        ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this, R.layout.snip_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.snip_item);
        //加载适配器
        sub_type.setAdapter(arr_adapter);


    }

    public void open_drag(View view){
        Intent intent =new Intent(InputActivity.this,DragActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("type", type);

        bundle.putString("name", name.getText().toString());
        tPrint(name.getText().toString());
        bundle.putString("description", description.getText().toString());
        tPrint(description.getText().toString());
        bundle.putString("location", location.getText().toString());
        tPrint(location.getText().toString());

        String tag_str = tags[0].getText().toString()+","
                +tags[1].getText().toString()+","
                +tags[2].getText().toString();
        bundle.putString("tag", tag_str);
        tPrint(tag_str);

        String stime_str = stime.getText().toString();
        String etime_str = etime.getText().toString();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long slong,elong;
        try {
            slong = format.parse(stime_str).getTime();
            elong = format.parse(etime_str).getTime();
        } catch (ParseException e) {
            slong = time_now;
            elong = time_end;
            tPrint("parse failed");
        }
        bundle.putLong("s_time", slong);
        bundle.putLong("e_time", elong);
        tPrint(slong+" "+elong);

        String tmp_str = sub_type.getSelectedItem().toString();
        tPrint(tmp_str);
        int i = 0;
        for(i=0;i<18;i++){
            if(tmp_str.compareTo(sub_type_str[i])==0)
                break;
        }
        tPrint("sub type "+i);
        bundle.putInt("sub_type", i);

        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void tPrint(String str) {
        System.out.println("sonia: " + str);
    }
}
