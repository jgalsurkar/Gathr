/**************************************************************************************************
 Title : GathringArrayAdapter.java
 Author : Gathr Team
 Purpose : Custom array adapter to set up the "Gathring" list items to properly display them
 *************************************************************************************************/

package com.gathr.gathr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gathr.gathr.R;

public class GathringArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final String[] descriptions;

    //Constructor that initializes the gathring's name and description arrays
    public GathringArrayAdapter(Context context, String[] values, String[] descriptions) {
        super(context, R.layout.fragment_gathrings_list, values);
        this.context = context;
        this.values = values;
        this.descriptions = descriptions;
    }

    @Override
    //Sets the name and description of an event list item and returns it
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.fragment_gathrings_list, parent, false);//Inflate the proper view
        TextView textView = (TextView) rowView.findViewById(R.id.gathring_list_item); // get the gathring list item's name (view)
        textView.setText(values[position]); //Set the text with the proper name from the array

        TextView descrView = (TextView) rowView.findViewById(R.id.gathring_list_descr); //get the gathring list item's description (view)
        descrView.setText(descriptions[position]); //Set the text with the proper description from the array in the same position

        return rowView;
    }
}
