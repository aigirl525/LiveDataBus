package com.example.livedatabus.databinding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.livedatabus.DataBinderMapperImpl;
import com.example.livedatabus.R;

public class Main3Activity extends AppCompatActivity {
    // 编译完成后，在/app/build/generated/source/apt/debug下会新增一个packagename.databinding的文件夹
    // 命名方式：xml文件名 + BindingImpl
    // 或者是指定名称<data class="CustomDataBinding">
    CustomDataBindingImpl mBinging;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinging = DataBindingUtil.setContentView(this,R.layout.databinding);
        user = new User();
        user.setName("name");
        user.setPassword("pwd");
        mBinging.setUserInfo(user);
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                user.setName("changeName");
                mBinging.setUserInfo(user);
                return false;
            }
        });
        handler.sendEmptyMessageDelayed(1,5000L);
        mBinging.setBol(true);
        mBinging.setAdb(this);
    }
    public void onClickGetData(View v){
        Toast.makeText(this,"onClickGetData",Toast.LENGTH_SHORT).show();
    }
    public void changeUi(View view,Boolean ischange){
        if (ischange) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }
}
