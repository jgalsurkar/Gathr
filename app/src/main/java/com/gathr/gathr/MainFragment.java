package com.gathr.gathr;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.Arrays;

public class MainFragment extends Fragment{

    // String results;
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
                    final String user_fname = user.getFirstName();
                    final String user_lname = user.getLastName();
                    final String user_dob = user.getBirthday();
                    // user_loc = user.getLocation();

                    QueryDB DBconn = new QueryDB(getActivity(), "login.php?fid=" + user_fid, true );//user_fid);
                    try {
                        class login implements DatabaseCallback {
                            public void onTaskCompleted(String results) {
                                Intent i;
                                if(results.charAt(0) == 'N'){
                                    i = new Intent(getActivity().getApplicationContext(), EditProfile.class);
                                }else{
                                    i = new Intent(getActivity().getApplicationContext(), MapsActivity.class);
                                }

                                AuthUser.user_id = results.substring(1);
                                AuthUser.fb_id = user_fid;
                                AuthUser.user_fname = user_fname;
                                AuthUser.user_lname = user_lname;

                                SharedPreferences settings = getActivity().getSharedPreferences("AuthUser", 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("userid", AuthUser.user_id);
                                editor.putString("fbid",  AuthUser.fb_id);
                                editor.putString("fname", AuthUser.user_fname);
                                editor.putString("lname", AuthUser.user_lname);
                                editor.commit();

                                startActivity(i);
                            }
                        }
                        DBconn.executeQuery("('" + user_fid + "', '" + user_email + "', '" + user_fname + "', '" + user_lname + "', '" + user_dob + "', '" + user_gender + "')", new login());

                        //results = DBconn.getResults();


                    }catch(GathrException e){
                        Log.i("Exception", e.error);
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