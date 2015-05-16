package com.gathr.gathr;

        import android.content.Context;
        import android.support.v4.app.ActionBarDrawerToggle;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.ActionBarActivity;
        import android.view.Gravity;
        import android.view.View;
        import android.widget.ImageButton;
        import android.widget.TextView;

/**
 * Created by Aarsh,Hanna on 5/10/2015.
 */
public class ActionBarActivityPlus extends ActionBarActivity{


    public void setActionBar(Integer id)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        actionBar.setCustomView(mActionBarView);
        TextView title= (TextView)mActionBarView.findViewById(R.id.title);
        title.setText(id);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    public void setActionBar(String str)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
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
