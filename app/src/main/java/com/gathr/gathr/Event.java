package com.gathr.gathr;

import com.google.android.gms.maps.model.LatLng;

public class Event {

        public String id;
        public String name;
        public String description;
        public String time;
        public int capacity;
        public int pop;
        public LatLng coordinates;
        public String date;

        public Event(String n, String d, int c, int p, String eventTime, double lat, double lon){
            name = n;
            description = d;
            capacity = c;
            pop = p;
            time = eventTime;//new Time();
            //time.setToNow();
            //time.hour = hour;
            //time.minute = min;
            coordinates = new LatLng(lat, lon);
        }


}


