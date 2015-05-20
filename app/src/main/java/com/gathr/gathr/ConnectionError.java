/**************************************************************************************************
 Title : ConnectionError.java
 Author : Gathr Team
 Purpose : Activity that is started when the user has no internet connection. A button is present
           for the user to try testing the connection again, which will send them to the
           MainActivity if it is successful
 *************************************************************************************************/

package com.gathr.gathr;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
    /*Called when the "Try Again" button is clicked.
      Returns the user to the Main Activity(Page) if the user has internet connection*/
    public void tryAgain(View view){
        if(global.isNetworkAvailable(this)) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }else{//Otherwise display the following message
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

}
