package com.gathr.gathr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GathringArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public GathringArrayAdapter(Context context, String[] values) {
        super(context, R.layout.activity_gathrings_list, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.activity_gathrings_list, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.gathring_list_item);
        textView.setText(values[position]);

        return rowView;
    }
}
