package com.gathr.gathr;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class ListViewMultipleSelectionActivity extends ActionBarActivity {
    Button button;
    ListView listView;
    ArrayAdapter<String> adapter;
    QueryDB DBconn = new QueryDB(AuthUser.fb_id, AuthUser.user_id);
    public String userId = AuthUser.user_id, output="", results, checkedId="";
    public String[] categoryName;
    public String[] categoryId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_multiple_selection);
        findViewsById();

        Intent i = getIntent();
        String userCategoryId = i.getStringExtra("categoryId");

        String[] selectedCategoryId = userCategoryId.split(",");

        DBconn.executeQuery("SELECT Id,Name FROM CATEGORIES;");
        results = DBconn.getResults();
        if (!results.contains("ERROR"))
        {
            JSONArray json;
            try {
                json = new JSONArray(results);
                int n = json.length();
                categoryId = new String[n];
                categoryName = new String[n];
                for(int j=0;j<n;j++)
                {
                    categoryId[j] = json.getJSONObject(j).getString("Id");
                    categoryName[j] = json.getJSONObject(j).getString("Name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, categoryName);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
        if(selectedCategoryId[0]!="" )
        {
            for(int j=0;j<selectedCategoryId.length;j++)
            {
                listView.setItemChecked(Integer.valueOf(selectedCategoryId[j])-1,true);
            }
            adapter.notifyDataSetChanged();
        }

    }

    private void findViewsById() {
        listView = (ListView) findViewById(R.id.list);
        button = (Button) findViewById(R.id.testbutton);
    }

    public void onSubmit(View v) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        //ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++)
        {
            // Item position in adapter

            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
            {
               // selectedItems.add(adapter.getItem(position));
                output = output+ adapter.getItem(position) + ", ";//selectedItems.get(i)+", ";
                checkedId = checkedId+categoryId[position]+",";
            }
        }
        output = output.substring(0, output.length()-2);
//        checkedId = checkedId.substring(0, checkedId.length()-1);
        Log.i("Check:","Checked: "+checkedId);
        Intent intent = new Intent(getApplicationContext(), EditProfile.class);
        intent.putExtra("categoryId",checkedId);
        intent.putExtra("category",output);
        // start the ResultActivity
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_view_multiple_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}