/**************************************************************************************************
 Title : ListViewMultipleSelectionActivity.java
 Author : Gathr Team
 Purpose : List View of categories for the user to select and add/remove
 *************************************************************************************************/

package com.gathr.gathr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.gathr.gathr.classes.MyGlobals;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;

import org.json.JSONArray;

public class ListViewMultipleSelectionActivity extends ActionBarActivityPlus {
    Button button;
    ListView listView;
    ArrayAdapter<String> adapter;

    QueryDB DBconn = new QueryDB(this);
    MyGlobals global = new MyGlobals(this);

    public String output="", checkedId="";
    public Class<?> from;
    public String[] categoryName, categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_multiple_selection);
        setActionBar("Pick Categories", true);

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
                            from = (Class<?>)i.getSerializableExtra("from");
                            String[] selectedCategoryId;
                            String userCategoryId = i.getStringExtra("categoryId");

                            if (userCategoryId != null) {
                                selectedCategoryId = userCategoryId.split(",");
                            }else{
                                selectedCategoryId= new String[1];
                                selectedCategoryId[0]="";
                            }
                            JSONArray json = new JSONArray(results);
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
        DBconn.executeQuery("SELECT Id,Name FROM CATEGORIES;", new getCategories());
    }

    public void onSubmit(View v) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++){
            int position = checked.keyAt(i);
            if (checked.valueAt(i)){
                output = output+ adapter.getItem(position) + ", ";
                checkedId = checkedId+categoryId[position]+",";
            }
        }
        if (!output.equals(""))
            output = output.substring(0, output.length()-2);

        Intent intent = new Intent(getApplicationContext(), from );

        intent.putExtra("categoryId",checkedId);
        intent.putExtra("category",output);

        // start the ResultActivity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }



}