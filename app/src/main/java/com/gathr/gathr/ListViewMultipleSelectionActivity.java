/**************************************************************************************************
 Title : ListViewMultipleSelectionActivity.java
 Author : Gathr Team
 Purpose : List View of categories for the user to select and add/remove
 *************************************************************************************************/

package com.gathr.gathr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.gathr.gathr.classes.MyGlobals;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;

import org.json.JSONArray;

public class ListViewMultipleSelectionActivity extends ActionBarActivity {
    Button button;
    ListView listView;
    ArrayAdapter<String> adapter;

    QueryDB DBconn = new QueryDB(this);
    MyGlobals global = new MyGlobals(this);

    public String output="", results, checkedId="";
    public String[] categoryName;
    public String[] categoryId;
    String from="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_multiple_selection);
        setActionBar(R.string.title_activity_list_view_multiple_selection);
        listView = (ListView) findViewById(R.id.list);
        button = (Button) findViewById(R.id.testbutton);
        final Context c = this;
        class getCategories implements DatabaseCallback {
            public void onTaskCompleted(final String results) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Intent i = getIntent();
                            from = i.getStringExtra("from");
                            String[] selectedCategoryId;
                            String userCategoryId = i.getStringExtra("categoryId");
                            Log.i("Results:", from +" "+userCategoryId);
                            if (userCategoryId != null) {
                                selectedCategoryId = userCategoryId.split(",");
                            }else{
                                selectedCategoryId= new String[1];
                                selectedCategoryId[0]="";
                            }
                            JSONArray json;
                            json = new JSONArray(results);
                            int n = json.length();
                            categoryId = new String[n];
                            categoryName = new String[n];
                            for (int j = 0; j < n; j++) {
                                categoryId[j] = json.getJSONObject(j).getString("Id");
                                categoryName[j] = json.getJSONObject(j).getString("Name");
                            }
                            adapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_multiple_choice, categoryName);
                            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            listView.setAdapter(adapter);
                            if (!selectedCategoryId[0].equals("")) {
                                for (int j = 0; j < selectedCategoryId.length; j++) {
                                    listView.setItemChecked(Integer.valueOf(selectedCategoryId[j]) - 1, true);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }catch (Exception e){
                            global.errorHandler(e);
                        }
                    }
                });

            }
        }
        try {
            DBconn.executeQuery("SELECT Id,Name FROM CATEGORIES;", new getCategories());
        }catch(Exception e){
            global.errorHandler(e);
        }

    }

    public void setActionBar(int id)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        actionBar.setCustomView(mActionBarView);
        ((ImageButton)mActionBarView.findViewById(R.id.btn_slide)).setVisibility(View.INVISIBLE);
        ((TextView)mActionBarView.findViewById(R.id.title)).setText(R.string.title_activity_list_view_multiple_selection);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }
    public void openSideBar(View view)
    {
        DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.drawer_layout);
        sidebar.openDrawer(Gravity.LEFT);
    }

    public void onSubmit(View v) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++)
        {
            int position = checked.keyAt(i);
            if (checked.valueAt(i))
            {
                output = output+ adapter.getItem(position) + ", ";
                checkedId = checkedId+categoryId[position]+",";
            }
        }
        if (!output.equals(""))
            output = output.substring(0, output.length()-2);
        else
            output="";
        Intent intent;

        if(from.equals("edit"))
            intent= new Intent(getApplicationContext(),EditProfile.class );
        else if(from.equals("event"))
            intent= new Intent(getApplicationContext(),CreateEvent.class );
        else
            intent= new Intent(getApplicationContext(),SearchEvents.class );

        intent.putExtra("categoryId",checkedId);
        intent.putExtra("category",output);
        // start the ResultActivity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }



}