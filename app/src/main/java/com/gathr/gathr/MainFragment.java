/**************************************************************************************************
 Title : MainFragment.java
 Author : Gathr Team
 Purpose : Ensures successful login and proper information is pulled from facebook during initial
 "account creation"
 *************************************************************************************************/

package com.gathr.gathr;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.gathr.gathr.classes.AuthUser;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;

import java.util.Arrays;

public class MainFragment extends Fragment{
    String id;

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
                    final String user_fid = user.getId();
                    final String user_email = user.asMap().get("email").toString();
                    final String user_gender = user.getProperty("gender").toString();
                    final String user_dob = user.getBirthday();

                    final String user_fname = user.getFirstName();
                    final String user_lname = user.getLastName();
                    // final Object user_loc = user.getProperty("current_location");

                    //user_loc.get
                    //  Log.i("tesT",user_loc.toString()).getLocation();
                    final String latitude = "-1";//Double.toString(user.getLocatgetProperty(""));
                    final String longitude = "0";//Double.toString(user_loc.getLongitude());

                    QueryDB DBconn = new QueryDB(getActivity(), "login.php?fid=" + user_fid );

                    class login implements DatabaseCallback {
                        public void onTaskCompleted(String results) {
                            Intent i;
                            if(results.charAt(0) == 'N'){
                                i = new Intent(getActivity().getApplicationContext(), EditProfile.class); //New User
                            }else{
                                i = new Intent(getActivity().getApplicationContext(), MapsActivity.class); //Existing User
                            }
                            AuthUser.setUser(getActivity(), results.substring(1), user_fid, user_fname, user_lname, latitude, longitude);
                            startActivity(i);
                            getActivity().finish();
                        }
                    }

                    try {
                        DBconn.executeQuery("('" + user_fid + "', '" + user_email + "', '" + user_fname + "', '" + user_lname + "', '" + user_dob + "', '" + user_gender + "', '" + latitude + "', '" + longitude + "')", new login());
                    }catch(Exception e){
                        Log.i("Exception", e.getMessage());
                    }
                }
            }

        });

        Session.getActiveSession().getPermissions();
        return view;
    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Toast.makeText(super.getActivity(), "Logging in, Please Wait!", Toast.LENGTH_SHORT).show();
        } else if (state.isClosed()) {
            Toast.makeText(super.getActivity(), "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
        }
    }
    private Session.StatusCallback callback = new Session.StatusCallback() {
        private void onClickLogin() {
            Session session = Session.getActiveSession();
            if (!session.isOpened() && !session.isClosed()) {

                session.openForRead(new Session.OpenRequest(getActivity())
                        .setPermissions(Arrays.asList("user_about_me", "email", "user_birthday", "user_friends"))
                        .setCallback(callback));

            } else {
                Session.openActiveSession(getActivity(), true, callback);

            }
        }
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
            // For scenarios where the main activity is launched and user
            // session is not null, the session state change notification
            // may not be triggered. Trigger it if it's open/closed.
            //Session session = Session.getActiveSession();
            if (session != null && (session.isOpened() || session.isClosed()) ) {
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