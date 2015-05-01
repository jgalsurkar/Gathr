package com.gathr.gathr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.facebook.android.Facebook;

public class MainActivity extends FragmentActivity{
     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MyGlobals().checkInternet(this);

        Facebook fb = new Facebook(getString(R.string.facebook_app_id));
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        String access_token = sp.getString("access_token", null);
        long expires = sp.getLong("access_expires",0);
        if(access_token!= null)
            fb.setAccessToken(access_token);
        if (expires!=0)
            fb.setAccessExpires(expires);

         /*
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.gathr.gathr", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) { }
        */
        SharedPreferences settings = getSharedPreferences("AuthUser", 0);
        AuthUser.user_id = settings.getString("userid", "");
        AuthUser.fb_id = settings.getString("fbid", "");
        AuthUser.user_fname = settings.getString("fname", "");
        AuthUser.user_lname = settings.getString("lname", "");
        if(AuthUser.user_id.equals("")){
          //  if (savedInstanceState == null) {
          getSupportFragmentManager().beginTransaction().add(android.R.id.content, new MainFragment()).commit();
            //} else {

                // Or set the fragment from restored state info
              //  mainFragment = (com.gathr.gathr.MainFragment) getSupportFragmentManager()
              //          .findFragmentById(android.R.id.content);
            //}
        }else{
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
            finish();
        }
    }
}
