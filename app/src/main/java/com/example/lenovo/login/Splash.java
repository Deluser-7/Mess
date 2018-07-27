package com.example.lenovo.login;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Splash extends AppCompatActivity {

    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            finish();
            startActivity(new Intent(Splash.this,Login.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(runnable,2000);
    }
}
