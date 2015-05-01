package com.gathr.gathr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

public class FriendArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] names;
    private final String[] images;

    public FriendArrayAdapter(Context context, String[] names, String[] images) {
        super(context, R.layout.friend_list, names);
        this.context = context;
        this.names = names;
        this.images = images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.friend_list, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.friend_list_name);
        textView.setText(names[position]);

        ProfilePictureView imgView = (ProfilePictureView) rowView.findViewById(R.id.friend_profile_pic);
        new MyGlobals(context).tip(images[position]);
        imgView.setCropped(true);
        imgView.setProfileId(images[position]);

        return rowView;
    }
}