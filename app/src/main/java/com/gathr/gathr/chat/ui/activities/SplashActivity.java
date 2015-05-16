/**************************************************************************************************
 Title : SplashActivity.java
 Author : Gathr Team
 Purpose : Initializes the chatroom i.e. Determines where they go, logs the user in, etc.
 *************************************************************************************************/

package com.gathr.gathr.chat.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.gathr.gathr.classes.AuthUser;
import com.gathr.gathr.chat.ApplicationSingleton;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBSettings;
import com.quickblox.auth.QBAuth;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.gathr.gathr.R;
import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;

public class SplashActivity extends Activity {

    private static final String APP_ID = "21154", AUTH_KEY = "YEJHvsAAaz74X98", AUTH_SECRET = "ZrOvZMWUQK9tNtw";
    final Context c = this;
    final QBUser user = new QBUser(AuthUser.getLogin(this),  "Gathr_" + AuthUser.getFBId(this));
    private QBChatService chatService;

    class setupChat extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String[] params) {
            try {
                QBAuth.createSession();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                QBUsers.signUp(user);
            } catch (QBResponseException e) {
                e.printStackTrace();
            }
            try {
                QBUser user1 = QBUsers.signIn(user);
                user.setId(user1.getId());
                ((ApplicationSingleton) getApplication()).setCurrentUser(user);

            } catch (QBResponseException e) {
                e.printStackTrace();
            }
            try {
                chatService.login(user);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                chatService.startAutoSendPresence(30); // every 30 seconds
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            }

            // go to Dialogs screen
            Intent old = getIntent();

            final String eventId = old.getStringExtra("EventId");
            final String eventName = old.getStringExtra("EventName");
            QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
            customObjectRequestBuilder.eq("name",eventName + eventId);

            Bundle x = new Bundle();
            try {
                ArrayList<QBDialog> dialogs = QBChatService.getChatDialogs(null, customObjectRequestBuilder,x );
                QBDialog dialog;
                if(dialogs.size() < 1){
                    dialog = new QBDialog();
                    dialog.setName(eventName + eventId);
                    dialog.setType(QBDialogType.PUBLIC_GROUP);
                    dialog.setRoomJid(eventId);
                    QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
                    dialog = groupChatManager.createDialog(dialog);
                }else{
                    dialog = dialogs.get(0);
                }

                Log.i("Testing", "Chatroom successfully received");
                Intent i = new Intent(c, ChatActivity.class);
                i.putExtra("dialog", dialog);
                i.putExtra("eventId", eventId);
                i.putExtra("EventName",eventName);
                startActivity(i);

            } catch (QBResponseException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

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

        // create QB user (locally)
        //user.setLogin(USER_LOGIN);
        //user.setPassword(USER_PASSWORD);
        user.setFullName(AuthUser.getFullName(this));

        setupChat x = new setupChat();
        x.execute();
    }
}