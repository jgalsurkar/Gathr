package com.gathr.gathr;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.gathr.gathr.classes.MyGlobals;


public class ConnectionError extends ActionBarActivity {

    MyGlobals global = new MyGlobals(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_error);
    }

    public void tryAgain(View view){
        if(global.isNetworkAvailable(this)) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connection_error, menu);
        return true;
    }

}
