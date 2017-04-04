package com.project.sonia.mymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_choice);
    }

    public void open_input(View view){
        int type = 0;
        if(view.getId()==R.id.btnA){ // academic
            type = 1;
        }else if(view.getId()==R.id.btnF){ // food
            type = 2;
        }else if(view.getId()==R.id.btnP){ // pokemon
            type = 3;
        }
        Intent intent =new Intent(ChooseActivity.this,InputActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("type",type);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void open_menu(View view){
        Intent intent =new Intent(ChooseActivity.this,MainMenu.class);
        startActivity(intent);
        finish();
    }

}
