package com.gathr.gathr;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.android.Facebook;
import com.facebook.widget.LoginButton;
import com.gathr.gathr.R;

/**
 * Created by Anya on 3/15/2015.
 */
public class StartingPlace extends Activity {
    Facebook fb;
    ImageView pic;
    TextView welcome;
    SharedPreferences sp;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String APP_ID = getString(R.string.facebook_app_id);
        LoginButton authButton = (LoginButton)findViewById(R.id.authButton);
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

    }
}
