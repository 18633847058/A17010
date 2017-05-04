package com.yang.eric.a17010.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yang.eric.a17010.utils.Constants;
import com.yang.eric.a17010.utils.StringUtils;

import java.util.UUID;

public class SplashActivity extends AppCompatActivity {

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        String uniqueID = UUID.randomUUID().toString();
        SharedPreferences sp = getSharedPreferences(Constants.SETTINGS, MODE_PRIVATE);
        if(sp.getString(Constants.IMEI, "").isEmpty()) {
            sp.edit().putString(Constants.IMEI,
                    StringUtils.getMd5(uniqueID)).apply();
        }
        if(!sp.getBoolean(Constants.ISLOGIN, false) &&
                sp.getLong(Constants.CHECK_COED,0) == 0) {
            Intent intent = new Intent(this, LoginActivity.class);
//            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
	}



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
