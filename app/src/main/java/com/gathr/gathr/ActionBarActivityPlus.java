package com.gathr.gathr;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class ActionBarActivityPlus extends ActionBarActivity{
    public void setActionBar(Integer id){
        setActionBar(id, false);
    }
    public void setActionBar(String id){
        setActionBar(id, false);
    }
    public void setActionBar(Integer id, Boolean hide){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        actionBar.setCustomView(mActionBarView);

        if(hide)
          ((ImageButton)mActionBarView.findViewById(R.id.btn_slide)).setVisibility(View.INVISIBLE);

        TextView title= (TextView)mActionBarView.findViewById(R.id.title);
        title.setText(id);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    public void setActionBar(String str, Boolean hide)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);

        if(hide)
            ((ImageButton)mActionBarView.findViewById(R.id.btn_slide)).setVisibility(View.INVISIBLE);

        actionBar.setCustomView(mActionBarView);
        TextView title= (TextView)mActionBarView.findViewById(R.id.title);
        title.setText(str);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    public void openSideBar(View view)
    {
        DrawerLayout sidebar = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(sidebar.isDrawerOpen(Gravity.START)) {
            sidebar.closeDrawer(Gravity.START);
        }else {
            sidebar.openDrawer(Gravity.START);
        }
    }

}
