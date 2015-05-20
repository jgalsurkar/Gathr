/**************************************************************************************************
  Title : EditProfile.java
  Author : Gathr Team
  Purpose : Activity which allows the user to edit aspects of their profile such as their interests,
  description, and social network links
  *************************************************************************************************/

package com.gathr.gathr;

import android.content.Intent;
 import android.support.v4.widget.DrawerLayout;
 import android.os.Bundle;
 import android.view.Menu;
 import android.view.View;
 import android.view.WindowManager;
 import android.widget.EditText;
 import android.widget.ListView;
 import com.gathr.gathr.classes.AuthUser;
 import com.gathr.gathr.classes.MyGlobals;
 import com.gathr.gathr.classes.SidebarGenerator;
 import com.gathr.gathr.database.DatabaseCallback;
 import com.gathr.gathr.database.QueryDB;

 import org.json.JSONArray;

public class EditProfile extends ActionBarActivityPlus {

     private EditText about_me;

     MyGlobals global = new MyGlobals(this);
     QueryDB DBconn = new QueryDB(this);

     public String userId = AuthUser.getUserId(this), category, categoryId;
     String results;
     EditText my_interests;


     @Override
     protected void onCreate(Bundle savedInstanceState) {
         this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
         super.onCreate(savedInstanceState);
         //Sets the custom Action Bar
         setActionBar("Edit Profile");
         setContentView(R.layout.activity_edit_profile);
         //Create the instance of Side Bar
         new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this);


         try {
             //set the default values for all the fields
             about_me = (EditText) findViewById(R.id.about_me);
             final EditText instagram = (EditText) findViewById(R.id.instagram);
             instagram.setHint("@username");
             final EditText twitter = (EditText) findViewById(R.id.twitter);
             twitter.setHint("@username");
             final EditText facebook = (EditText) findViewById(R.id.facebook);
             facebook.setHint("username");
             my_interests = (EditText) findViewById(R.id.my_interests);
             Intent i = getIntent();
             category = i.getStringExtra("category");
             categoryId = i.getStringExtra("categoryId");
             my_interests.setText(category);
             //get the data from the database
             results = global.getUserJSON();
             if (!results.contains("ERROR")) {
                 JSONArray json = new JSONArray(results);
                 about_me.setText(json.getJSONObject(0).getString("About_Me"));
                 instagram.setText(json.getJSONObject(0).getString("Instagram"));
                 facebook.setText(json.getJSONObject(0).getString("Facebook"));
                 twitter.setText(json.getJSONObject(0).getString("Twitter"));
             }
         }  catch(Exception e){
             global.errorHandler(e);
         }
     }
     // opens the page with categories choice for the interests, passing the categories that have been selected before
     public void openCategory(View view){
         Intent i = new Intent(this, ListViewMultipleSelectionActivity.class);
         i.putExtra("categoryId",categoryId);
         i.putExtra("from",EditProfile.class);
         startActivityForResult(i,0);
     }

     //makes sure that result of selection comes back to the same activity with needed results
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         // Check which request we're responding to
         if (requestCode == 0) {
             // Make sure the request was successful
             if (resultCode == RESULT_OK) {
                 category = data.getStringExtra("category");
                 categoryId = data.getStringExtra("categoryId");
                 my_interests.setText(category);
             }
         }
     }
     //saves changes to the database
     public void saveChanges (View view) {
         String about_me = DBconn.escapeString(getElementText(R.id.about_me));
         String instagram = DBconn.escapeString(getElementText(R.id.instagram));
         if(!instagram.equals(""))
         {
             if (instagram.charAt(0)!='@')
                 instagram = "@"+ instagram;
         }
         String twitter = DBconn.escapeString(getElementText(R.id.twitter));
         if(!twitter.equals("")) {
             if (twitter.charAt(0) != '@')
                 twitter = "@" + twitter;
         }
         String facebook =DBconn.escapeString(getElementText(R.id.facebook));
         try{
             final Intent i = new Intent(this, Profile.class);
             class UpdateUser implements DatabaseCallback {
                 public void onTaskCompleted(final String results) {
                     startActivity(i);
                     finish();
                 }
             }
             //updates database values
             DBconn.executeQuery("UPDATE USERS " +
                     "SET `About_Me`='"+about_me+"',`Instagram`='"+instagram+"',`Facebook`='"+facebook+"',`Twitter`='"+twitter+"' "+
                     "where `Id` ="+userId+";",new UpdateUser());
             global.getUserJSON(1);
             DBconn.executeQuery("ADD INTERESTS " +categoryId+" FOR "+userId);
             // We dont really need to wait
         }catch(Exception e){
             global.errorHandler(e);
         }
     }
     public String getElementText(int viewId){
         return ((EditText)findViewById(viewId)).getText().toString();
     }
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
         return true;
     }
 }