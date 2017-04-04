package com.project.sonia.mymap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }

    public void open_share(View view){
        Intent intent =new Intent(MainMenu.this,ChooseActivity.class);
        startActivity(intent);
        finish();
    }

    public void open_hunt(View view){
        Intent intent =new Intent(MainMenu.this,MapActivity.class);
        startActivity(intent);
        finish();
    }

    public void open_setting(View view){
        Intent intent =new Intent(MainMenu.this,SettingActivity.class);
        startActivity(intent);
        finish();
    }
}
