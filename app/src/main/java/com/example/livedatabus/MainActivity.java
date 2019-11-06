package com.example.livedatabus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void sendMsg(View v){
        LiveDataBus.getInstance().getChannel("key_test").setValue(true);
        startActivity(new Intent(this,Main2Activity.class));
    }
}
