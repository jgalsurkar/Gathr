package com.gathr.gathr;

import android.text.format.Time;
import com.google.android.gms.maps.model.LatLng;

public class Event {

        public int id;
        public String name;
        public String description;
        public Time time;
        public int capacity;
        public int pop;
        public LatLng coordinates;

        public Event(String eventName, String eventDesc, int eventCap, int eventPop, Time eventTime, LatLng eventCoor){
            name = eventName;
            description = eventDesc;
            capacity = eventCap;
            pop = eventPop;
            time = eventTime;
            coordinates = eventCoor;
        }

        public Event(String n, String d, int c, int p, int hour, int min, double lat, double lon){
            name = n;
            description = d;
            capacity = c;
            pop = p;
            time = new Time();
            time.setToNow();
            time.hour = hour;
            time.minute = min;
            coordinates = new LatLng(lat, lon);
        }

        public Event(String n, String d, int c, int p, int hour, int min, String address){
            name = n;
            description = d;
            capacity = c;
            pop = p;
            time = new Time();
            time.setToNow();
            time.hour = hour;
            time.minute = min;
            // coordinates = new LatLng(lat, lon);
        }
}


