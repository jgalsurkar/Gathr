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
import android.widget.Toast;

import com.facebook.android.Facebook;

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
    String access_token;
    private com.gathr.gathr.MainFragment mainFragment;
    MyGlobals global = new MyGlobals(this);
    private static final String TAG = "MainFragment";
    private static final String Token = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        global.checkInternet();

        super.onCreate(savedInstanceState);
        String APP_ID = getString(R.string.facebook_app_id);
        fb = new Facebook(APP_ID);
        sp = getPreferences(MODE_PRIVATE);
        access_token = sp.getString("access_token", null);
        long expires = sp.getLong("access_expires",0);
        if(access_token!= null)
        {
            // Log.i("TOKEN",access_token);
            fb.setAccessToken(access_token);
        }
        if (expires!=0)
        {fb.setAccessExpires(expires);}

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
