package com.gathr.gathr;

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

import com.facebook.android.Facebook;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.android.Facebook;
import com.facebook.android.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends FragmentActivity
        //
        //implements MainFragment.OnFragmentInteractionListener
{
    Facebook fb;
    ImageView pic;
    TextView welcome;
    SharedPreferences sp;
    private com.gathr.gathr.MainFragment mainFragment;

    private static final String TAG = "MainFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String APP_ID = getString(R.string.facebook_app_id);
        fb = new Facebook(APP_ID);
        sp = getPreferences(MODE_PRIVATE);
        String access_token = sp.getString("access_token", null);
        long expires = sp.getLong("access_expires",0);
        if(access_token!= null)
        {
            fb.setAccessToken(access_token);
        }
        if (expires!=0)
        {fb.setAccessExpires(expires);}

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.anya.facebooklogin",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
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
    }




}
