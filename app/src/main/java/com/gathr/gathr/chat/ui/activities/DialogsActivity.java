package com.gathr.gathr.chat.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.gathr.gathr.chat.ApplicationSingleton;
import com.gathr.gathr.chat.core.GroupChatManagerImpl;
import com.gathr.gathr.chat.ui.adapters.DialogsAdapter;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.request.QBRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.gathr.gathr.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogsActivity extends Activity {
    private String eventId, eventName;

    private ListView dialogsListView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs_activity);
        final Context c = this;
        dialogsListView = (ListView) findViewById(R.id.roomsList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        Intent i = getIntent();
        eventId = "179"/*i.getStringExtra("EventId")*/;
        eventName = "The First One To"/*i.getStringExtra("EventName")*/;

        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        customObjectRequestBuilder.addRule("data[Event_id]","eq",eventId);
        Log.i("Testing",eventId);
        QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args) {


                Log.i("Testing", "Chatroom successfully received");
                Intent i = new Intent(c, ChatActivity.class);
                QBDialog dialog = dialogs.get(0);
                i.putExtra("dialog", dialog);
                startActivity(i);

            }

            @Override
            public void onError(List<String> errors) {
                //AlertDialog.Builder dialog = new AlertDialog.Builder(DialogsActivity.this);
                //dialog.setMessage("Get Chatroom errors: " + errors).create().show();
                Log.i("Testing", "Room does not exist, creating new room");

                QBDialog dialog = new QBDialog();
                dialog.setName(eventName);
                dialog.setType(QBDialogType.GROUP);
                dialog.setRoomJid(eventId);
                QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
                groupChatManager.createDialog(dialog, new QBEntityCallbackImpl<QBDialog>() {
                    @Override
                    public void onSuccess(QBDialog dialog, Bundle args) {
                        Log.i("Testing", "New room created");
                        Intent i = new Intent(c, ChatActivity.class);
                        i.putExtra("dialog", dialog);
                        startActivity(i);

                    }

                    @Override
                    public void onError(List<String> errors) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(DialogsActivity.this);
                        dialog.setMessage("Create Chatroom errors: " + errors).create().show();
                    }
                });

            }
        });
    }


    void buildListView(List<QBDialog> dialogs){
        final DialogsAdapter adapter = new DialogsAdapter(dialogs, DialogsActivity.this);
        dialogsListView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);

        // choose dialog
        //
        dialogsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBDialog selectedDialog = (QBDialog)adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.EXTRA_DIALOG, (QBDialog)adapter.getItem(position));

                // group
                if(selectedDialog.getType().equals(QBDialogType.GROUP)){
                    bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.GROUP);

                    // private
                } else {
                    bundle.putSerializable(ChatActivity.EXTRA_MODE, ChatActivity.Mode.PRIVATE);
                }

                // Open chat activity
                //
                ChatActivity.start(DialogsActivity.this, bundle);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rooms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {

            // go to New Dialog activity
            //
            Intent intent = new Intent(DialogsActivity.this, NewDialogActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}