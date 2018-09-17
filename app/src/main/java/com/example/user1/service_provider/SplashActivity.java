package com.example.user1.service_provider;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    Shared_pref_Login mshared_pref_login;
    int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mshared_pref_login=new Shared_pref_Login(SplashActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mshared_pref_login.isUserLogedOut())
                    startActivity(LoginActivity.class);
                else
                   startActivity(HomeActivity.class);

            }
        }, SPLASH_TIME_OUT);
    }

    void startActivity(Class name)
    {
        Intent intent = new Intent(SplashActivity.this, name);
        startActivity(intent);
        finish();
    }
}

