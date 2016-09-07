package com.yarik.salaryshare.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class ParseApplication extends Application {
    public static final String TAG = "SalaryShare";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "g6kp61Rv922peH1cPawHxtBki7aBxwCaJizMIyDO", "W9TttP3Ht1PYnv91E77WXWEZ4EXFVG8HljUWCEbd");
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);
    }
}
