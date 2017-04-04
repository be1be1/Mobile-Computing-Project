package com.project.sonia.mymap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class SettingActivity extends AppCompatActivity {

    private String FILE_NAME = null;

    private EditText eTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_ip);

        FILE_NAME = getResources().getString(R.string.ip_file_name);
        eTxt = (EditText)findViewById(R.id.editText);
    }

    public void ip_update(View view){

        String data = eTxt.getText().toString();

        FileOutputStream out = null;
        PrintStream ps = null;
        try {
            out = super.openFileOutput(FILE_NAME, Activity.MODE_PRIVATE);
            ps = new PrintStream(out);
            ps.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                    ps.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void ip_load(View view){
        String ip_add;
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
    }

    public void open_menu(View view){
        Intent intent =new Intent(SettingActivity.this,MainMenu.class);
        startActivity(intent);
        finish();
    }

    private void tPrint(String str) {
        System.out.println("sonia: " + str);
    }
}
