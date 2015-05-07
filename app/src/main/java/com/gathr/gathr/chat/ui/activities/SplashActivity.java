package com.gathr.gathr.chat.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.gathr.gathr.AuthUser;
import com.gathr.gathr.chat.ApplicationSingleton;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.gathr.gathr.R;

import org.jivesoftware.smack.SmackException;

import java.util.List;

public class SplashActivity extends Activity {

    private static final String APP_ID = "21154";
    private static final String AUTH_KEY = "YEJHvsAAaz74X98";
    private static final String AUTH_SECRET = "ZrOvZMWUQK9tNtw";
    //
    private static final String USER_LOGIN = "dmcalumpit"/*AuthUser.user_fname + AuthUser.user_id*/;
    private static final String USER_PASSWORD = "test12345"/*AuthUser.fb_id*/;
    private static final String USER_FULLNAME = "Dustin Calumpit"/*AuthUser.user_fname + " " + AuthUser.user_lname*/;

    static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;

    private QBChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Init Chat
        //
        QBChatService.setDebugEnabled(true);
        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
        if (!QBChatService.isInitialized()) {
            QBChatService.init(this);
        }
        chatService = QBChatService.getInstance();


        // create QB user
        //
        final QBUser user = new QBUser();
        user.setLogin(USER_LOGIN);
        user.setPassword(USER_PASSWORD);
        user.setFullName(USER_FULLNAME);


        QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle args) {

                // save current user
                //
                user.setId(session.getUserId());
                ((ApplicationSingleton) getApplication()).setCurrentUser(user);

                // login to Chat
                //
                loginToChat(user);
            }

            @Override
            public void onError(List<String> errors) {
                //AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                //dialog.setMessage("create session errors: " + errors).create().show();
                signUp();
            }
        });
    }

    private void signUp(){
        final  QBUser user = new QBUser();
        user.setLogin(AuthUser.user_fname + AuthUser.user_id);
        user.setPassword(AuthUser.fb_id);
        user.setFullName(AuthUser.user_fname + " " + AuthUser.user_lname);

        QBUsers.signUp(user, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(final QBUser user, Bundle args) {

                QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>(){
                    @Override
                    public void onSuccess(QBSession session, Bundle args) {

                        // save current user
                        //
                        user.setId(session.getUserId());
                        ((ApplicationSingleton)getApplication()).setCurrentUser(user);

                        // login to Chat
                        //
                        loginToChat(user);
                    }

                    @Override
                    public void onError(List<String> errors) {
                        //AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                        //dialog.setMessage("create session errors: " + errors).create().show();
                        signUp();
                    }
                });

            }

            @Override
            public void onError(List<String> errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                dialog.setMessage("Fail to sign up: " + errors).create().show();
            }
        });
    }

    private void loginToChat(final QBUser user){

        chatService.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {

                // Start sending presences
                //
                try {
                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                }

                // go to Dialogs screen
                //
                Intent intent = new Intent(SplashActivity.this, DialogsActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(List errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                dialog.setMessage("chat login errors: " + errors).create().show();
            }
        });
    }
}