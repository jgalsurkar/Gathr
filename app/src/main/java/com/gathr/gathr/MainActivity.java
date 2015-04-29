package com.gathr.gathr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends FragmentActivity
        //
        //implements MainFragment.OnFragmentInteractionListener
{
    Facebook fb;
    SharedPreferences sp;
    String access_token;
    private com.gathr.gathr.MainFragment mainFragment;
    MyGlobals global = new MyGlobals();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        global.checkInternet(this);

        super.onCreate(savedInstanceState);
        String APP_ID = getString(R.string.facebook_app_id);
        fb = new Facebook(APP_ID);
        sp = getPreferences(MODE_PRIVATE);
        access_token = sp.getString("access_token", null);
        long expires = sp.getLong("access_expires",0);
        if(access_token!= null)
            fb.setAccessToken(access_token);
        if (expires!=0)
            fb.setAccessExpires(expires);

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.gathr.gathr",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {

        }

        SharedPreferences settings = getSharedPreferences("AuthUser", 0);
        AuthUser.user_id = settings.getString("userid", "");
        AuthUser.fb_id = settings.getString("fbid", "");
        AuthUser.user_fname = settings.getString("fname", "");
        AuthUser.user_lname = settings.getString("lname", "");

        if(AuthUser.user_id == ""){
            if (savedInstanceState == null) {

                // Add the fragment on initial activity setup
                mainFragment = new com.gathr.gathr.MainFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, mainFragment)
                        .commit();
            } else {

                // Or set the fragment from restored state info
                mainFragment = (com.gathr.gathr.MainFragment) getSupportFragmentManager()
                        .findFragmentById(android.R.id.content);
            }
        }else{
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
        }
    }





}
