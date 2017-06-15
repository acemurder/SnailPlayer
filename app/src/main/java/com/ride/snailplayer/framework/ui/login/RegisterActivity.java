package com.ride.snailplayer.framework.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ride.snailplayer.R;

public class RegisterActivity extends AppCompatActivity {

    public static void launchActivity(Activity startingActivity) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeBasic();
        Intent intent = new Intent(startingActivity, RegisterActivity.class);
        ActivityCompat.startActivity(startingActivity, intent, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
