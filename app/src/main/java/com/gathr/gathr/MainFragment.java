package com.gathr.gathr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.android.Util;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Anya on 2/24/2015.
 */
public class MainFragment extends Fragment{
    Facebook fb;
    ImageView pic;
    TextView welcome;

    private MainFragment mainFragment;
    private static final String TAG = "MainFragment";
    private ProfilePictureView profilePictureView;
    private TextView userNameView,dob, userInfoTextView,location;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        // Find the user's profile picture custom view
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);

        // Find the user's name view
        userNameView = (TextView) view.findViewById(R.id.user_name);
        dob = (TextView)view.findViewById(R.id.DateOfBirth);

        userInfoTextView = (TextView) view.findViewById(R.id.userInfoTextView);
        location = (TextView)view.findViewById(R.id.location);

        LoginButton authButton = (LoginButton)view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_likes","user_about_me","user_location","email"/*,"publish_actions"*/,"user_birthday","user_friends"));

        authButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {

                    userNameView.setText("Welcome, " + user.getName());
                    profilePictureView.setProfileId(user.getId());
                    profilePictureView.setVisibility(View.VISIBLE);
                    // Example: typed access (birthday)
                    // - requires user_birthday permission
                    //dob.setText(user.getBirthday());

                    //dob.setVisibility(View.VISIBLE);

                    userInfoTextView.setVisibility(View.VISIBLE);
                    userInfoTextView.setText(buildUserInfoDisplay(user));
                   // user.getLocation().getProperty("name");
                    //location.setVisibility(View.VISIBLE);

                } else {
                    userNameView.setText("You are not logged in.");
                    profilePictureView.setVisibility(View.INVISIBLE);
                    dob.setVisibility(View.INVISIBLE);
                    userInfoTextView.setVisibility(View.INVISIBLE);
                }
            }

        });

        Session.getActiveSession().getPermissions();

        return view;
    }
    /*public interface OnFragmentInteractionListener {
    }*/
    public String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: typed access (birthday)
        // - requires user_birthday permission
        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
        userInfo.append(String.format("Location: %s\n\n",
                user.getLocation().getProperty("name")));

        // Example: access via property name (locale)
        // - no special permissions required
        userInfo.append(String.format("Locale: %s\n\n",
                user.getProperty("locale")));

        // Example: access via key for array (languages)
        // - requires user_likes permission
        /*JSONArray languages = (JSONArray) user.getProperty("languages");
        if (languages.length() > 0) {
            ArrayList<String> languageNames = new ArrayList<String>();
            for (int i = 0; i < languages.length(); i++) {
                JSONObject language = languages.optJSONObject(i);
                // Add the language name to a list. Use JSON
                // methods to get access to the name field.
                languageNames.add(language.optString("name"));
            }
            userInfo.append(String.format("Languages: %s\n\n",
                    languageNames.toString()));
        }
*/
        return userInfo.toString();
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
                        .setPermissions(Arrays.asList("user_likes","user_about_me","user_location","email"/*,"publish_actions"*/,"user_birthday","user_friends"))
                        .setCallback(callback));
            } else {
            //    Session.openActiveSession(getActivity(),this,true);

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
