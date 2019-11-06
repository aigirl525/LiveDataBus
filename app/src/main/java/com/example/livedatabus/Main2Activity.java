package com.example.livedatabus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        LiveDataBus.getInstance().getChannel("key_test",Boolean.class)
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        Toast.makeText(Main2Activity.this,""+aBoolean,Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void sendMsg(View v){
        LiveDataBus.getInstance().getChannel("key_test").setValue(true);
    }
}
