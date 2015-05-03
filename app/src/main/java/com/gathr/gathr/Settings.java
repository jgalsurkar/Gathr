package com.gathr.gathr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.util.Arrays;


public class Settings extends ActionBarActivity {

    MyGlobals global = new MyGlobals(this);
    Context context = this;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);



        LoginButton authButton = (LoginButton)findViewById(R.id.authButton);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, global.titles, global.links );


    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            SharedPreferences settings = this.getSharedPreferences("AuthUser", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("notifications", true);
            editor.commit();
        } else {
            SharedPreferences settings = this.getSharedPreferences("AuthUser", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("notifications", false);
            editor.commit();
        }
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Toast.makeText(this, "Logging in, Please Wait!", Toast.LENGTH_SHORT).show();
        } else if (state.isClosed()) {
            Toast.makeText(this, "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
        }


    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override

        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);

            // For scenarios where the main activity is launched and user
            // session is not null, the session state change notification
            // may not be triggered. Trigger it if it's open/closed.
            //Session session = Session.getActiveSession();

            if (session != null &&
                    (session.isOpened() || session.isClosed()) ) {
                onSessionStateChange(session, session.getState(), null);

                SharedPreferences settings = context.getSharedPreferences("AuthUser", Context.MODE_PRIVATE);
                settings.edit().clear().commit();



                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);


            }

            uiHelper.onResume();

        }
    };

    private UiLifecycleHelper uiHelper;
   // @Override
   // public void onCreate(Bundle savedInstanceState) {
     //   super.onCreate(savedInstanceState);
       // uiHelper = new UiLifecycleHelper(this, callback);
        //uiHelper.onCreate(savedInstanceState);
 //   }
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
