package com.gathr.gathr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.android.Util;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainFragment extends Fragment{
    String user_id;
    String user_email;
    String user_gender;
    String user_fname;
    String user_lname;
    String user_dob;
    String results;
    String id;
    private MainFragment mainFragment;
    private static final String TAG = "MainFragment";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.activity_main, container, false);
        // Find the user's profile picture custom view
        LoginButton authButton = (LoginButton)view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_about_me","email","user_birthday","user_friends"));

        authButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {


                    user_id = user.getId();
                    user_email = user.asMap().get("email").toString();
                    user_gender = user.getProperty("gender").toString();
                    user_fname = user.getFirstName();
                    user_lname = user.getLastName();
                    user_dob = user.getBirthday();


                    QueryDB DBconn = new QueryDB();

                    DBconn.executeQuery("SELECT Id, Facebook_Id, Email, First_Name, Last_Name, Birthday, Gender FROM USERS WHERE Facebook_Id = '"+user_id+"'");
                    results = DBconn.getResults();

                    if (results.contains("ERROR"))
                    {
                        String query = "INSERT INTO USERS " +
                                "( `Facebook_Id`, `Email`, `First_Name`, `Last_Name`, `Birthday`, `Gender`)" +
                                " VALUES " +
                                "('"+user_id+"', '"+ user_email+"', '"+user_fname+"', '"+user_lname+"', '"+user_dob+"', '"+user_gender+"')";
                        DBconn.executeQuery(query);
                        results = DBconn.getResults();
                    }
                    else {
                        JSONArray json;
                        try {
                            json = new JSONArray(results);
                            int n = json.length();
                            //String p = n+"";
                           // Log.i(TAG,"JSON: "+p);
                            AuthUser.id = json.getJSONObject(n-1).getString("Id");
                            AuthUser.user_id = json.getJSONObject(n-1).getString("Facebook_Id");
                            AuthUser.user_fname = json.getJSONObject(n-1).getString("First_Name");
                            AuthUser.user_lname = json.getJSONObject(n-1).getString("Last_Name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent i = new Intent(getActivity().getApplicationContext(), CreateEvent.class);
                    Log.i(TAG,"USER: "+user_id);
                    i.putExtra("userId", user_id);
                    startActivity(i);

                }
            }

        });

        Session.getActiveSession().getPermissions();


        return view;
    }

     private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
             }
    }
    private Session.StatusCallback callback = new Session.StatusCallback() {


        private void onClickLogin() {
            Session session = Session.getActiveSession();
            if (!session.isOpened() && !session.isClosed()) {
                session.openForRead(new Session.OpenRequest(getActivity())
                        .setPermissions(Arrays.asList("user_about_me","email","user_birthday","user_friends"))
                        .setCallback(callback));

            } else {
                Session.openActiveSession(getActivity(),true,callback);
            }
        }
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
            }

            uiHelper.onResume();
        }
           };

   private UiLifecycleHelper uiHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }
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


}
