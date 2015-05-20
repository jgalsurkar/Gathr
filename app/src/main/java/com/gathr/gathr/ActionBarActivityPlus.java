/**************************************************************************************************
 Title : ActionBarActivityPlus.java
 Author : Gathr Team
 Purpose : An Activity Subclass that makes actionbar customized for our application

 *************************************************************************************************/
package com.gathr.gathr;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class ActionBarActivityPlus extends ActionBarActivity{

    public void setActionBar(String title){ //If we only provide a string (title)
        setActionBar(title, false);
    } //If we only provide a string we dont want to hide the sidebar button

    public void setActionBar(String str, Boolean hide) //hide represents whether or not the sidebar button is
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);

        //If we want to hide the sidebarbutton
        if(hide)
            (mActionBarView.findViewById(R.id.btn_slide)).setVisibility(View.INVISIBLE);

        //Set the title
        actionBar.setCustomView(mActionBarView);
        TextView title= (TextView)mActionBarView.findViewById(R.id.title);
        title.setText(str);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    //This function opens/closes the sidebar when the sidebar button is clicked
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
