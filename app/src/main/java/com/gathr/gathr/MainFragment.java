package com.gathr.gathr;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.Arrays;

public class MainFragment extends Fragment{

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

                    String user_fid = user.getId();
                    String user_email = user.asMap().get("email").toString();
                    String user_gender= user.getProperty("gender").toString();
                    String user_fname = user.getFirstName();
                    String user_lname = user.getLastName();
                    String user_dob= user.getBirthday();

                    QueryDB DBconn = new QueryDB(user_fid);
                    DBconn.executeQuery("INSERT USER('"+user_fid+"', '"+ user_email+"', '"+user_fname+"', '"+user_lname+"', '"+user_dob+"', '"+user_gender+"')");
                    results = DBconn.getResults();

                    if (results.contains("ERROR")){
                        Log.i("Log In: ",results);
                    }else{
                        AuthUser.user_id = results;
                        AuthUser.fb_id = user_fid;
                        AuthUser.user_fname = user_fname;
                        AuthUser.user_lname = user_lname;
                    }
                    Intent i = new Intent(getActivity().getApplicationContext(), MapsActivity.class);
                    Log.i(TAG,"USER: "+AuthUser.user_id);
                    i.putExtra("userId", AuthUser.user_id);
                    startActivity(i);
                    //Intent i = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
                    //startActivity(i);

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