/**************************************************************************************************
 Title : MainActivity.java
 Author : Gathr Team
 Purpose : AMain Activity which ensures successful login
 *************************************************************************************************/

package com.gathr.gathr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.facebook.android.Facebook;
import com.gathr.gathr.classes.AuthUser;

public class MainActivity extends FragmentActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Facebook fb = new Facebook(getString(R.string.facebook_app_id));
        fb.setAccessToken(null);
        fb.setAccessExpires(0);
        //SharedPreferences sp = getPreferences(MODE_PRIVATE);
        //String access_token = sp.getString("access_token", null);
        //long expires = sp.getLong("access_expires",0);
        //if(access_token!= null)

        //if (expires!=0)


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
        AuthUser.loadUser(this);
        if(AuthUser.getUserId(this).equals("")){
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

    @Override
         public void onBackPressed() {

             return;
         }
}