package com.yarik.salaryshare.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.viewpagerindicator.CirclePageIndicator;
import com.yarik.salaryshare.R;
import com.yarik.salaryshare.adapters.CustomPagerAdapter;
import com.yarik.salaryshare.application.ParseApplication;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Dialog progressDialog;
    private ViewPager viewPager_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initializeReferences();
        CirclePageIndicator login_indicator = (CirclePageIndicator) findViewById(R.id.login_indicator);
        viewPager_.setPageTransformer(false, new FadePageTransfomer());

        PagerAdapter adapter = new CustomPagerAdapter(MainActivity.this);
        viewPager_.setAdapter(adapter);

        login_indicator.setSnap(true);
        login_indicator.setViewPager(viewPager_);

/* TODO: get facebook hash keys
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.yarik.salaryshare",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        */
        // Check if there is a currently logged in user
        // and it's linked to a Facebook account.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            // Go to the NavigationDrawer
            showNavigation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onLoginClick(View v) {
        progressDialog = ProgressDialog.show(MainActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "email");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    Log.d(ParseApplication.TAG, "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(ParseApplication.TAG, "User signed up and logged in through Facebook!");
                    showNavigation();
                } else {
                    Log.d(ParseApplication.TAG, "User logged in through Facebook!");
                    showNavigation();
                }
            }
        });

    }

    private void showNavigation() {
        // Start an intent for the dispatch activity
        Intent intent = new Intent(MainActivity.this, NavigationDrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void initializeReferences() {
        viewPager_ = (ViewPager) findViewById(R.id.login_viewpager);
    }

    public class FadePageTransfomer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            view.setAlpha(1 - Math.abs(position));
        }
    }
}

