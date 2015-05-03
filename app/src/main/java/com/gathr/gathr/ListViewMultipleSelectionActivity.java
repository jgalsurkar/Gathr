package com.gathr.gathr;

import android.app.Activity;
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



public class ListViewMultipleSelectionActivity extends ActionBarActivity {
    Button button;
    ListView listView;
    ArrayAdapter<String> adapter;

    QueryDB DBconn = new QueryDB(this, AuthUser.fb_id, AuthUser.user_id);
    MyGlobals global = new MyGlobals(this);

    public String userId = AuthUser.user_id, output="", results, checkedId="";
    public String[] categoryName;
    public String[] categoryId;
    String from="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_multiple_selection);
        findViewsById();
        try {
            Intent i = getIntent();
            from = i.getStringExtra("from");

            String[] selectedCategoryId;
            String userCategoryId = i.getStringExtra("categoryId");

            if (userCategoryId != null)
                selectedCategoryId=userCategoryId.split(",");
            else{
                selectedCategoryId= new String[1];
                selectedCategoryId[0]="";}
            DBconn.executeQuery("SELECT Id,Name FROM CATEGORIES;");
            results = DBconn.getResults();
            JSONArray json;
            json = new JSONArray(results);
            int n = json.length();
            categoryId = new String[n];
            categoryName = new String[n];
            for(int j=0;j<n;j++)
            {
                categoryId[j] = json.getJSONObject(j).getString("Id");
                categoryName[j] = json.getJSONObject(j).getString("Name");
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, categoryName);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setAdapter(adapter);
            if(selectedCategoryId[0]!="")
            {
                for(int j=0;j<selectedCategoryId.length;j++)
                {
                    listView.setItemChecked(Integer.valueOf(selectedCategoryId[j])-1,true);
                }
                adapter.notifyDataSetChanged();
            }
        }catch(Exception e){
            global.errorHandler(e);
        }

    }

    private void findViewsById() {
        listView = (ListView) findViewById(R.id.list);
        button = (Button) findViewById(R.id.testbutton);
    }

    public void onSubmit(View v) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++)
        {
            // Item position in adapter

            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
            {
                // selectedItems.add(adapter.getItem(position));
                output = output+ adapter.getItem(position) + ", ";
                checkedId = checkedId+categoryId[position]+",";
            }
        }
        if (!output.equals(""))
            output = output.substring(0, output.length()-2);
        else output="";
        Intent intent;
        String to;
        if(from.equals("edit"))
            intent= new Intent(getApplicationContext(),EditProfile.class );
        else if(from.equals("event"))
            intent= new Intent(getApplicationContext(),CreateEvent.class );
        else  intent= new Intent(getApplicationContext(),MapsActivity.class );
        intent.putExtra("categoryId",checkedId);
        intent.putExtra("category",output);
        // start the ResultActivity
        setResult(Activity.RESULT_OK, intent);
        finish();
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