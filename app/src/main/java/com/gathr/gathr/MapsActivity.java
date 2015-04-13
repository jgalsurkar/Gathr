package com.gathr.gathr;

import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.content.Context;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.location.Geocoder;
import android.location.Address;
import java.util.List;

import android.support.v4.widget.DrawerLayout;

import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener {



    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private LocationListener locationListener;
    private HashMap<Integer, Event> allEvents;
    private QueryDB database;

    @Override
    public FragmentManager getFragmentManager() {
        return super.getFragmentManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //Query database
        database = new QueryDB(AuthUser.fb_id, AuthUser.user_id);
        database.executeQuery("SELECT * FROM EVENTS");

        super.onCreate(savedInstanceState);   //Every app
        setContentView(R.layout.activity_maps);  //Sets up map


        String[] titles = new String[]{"Map","My Profile","Gathrings","Friends","Settings","Notifications","Log Out"};
        Class<?>[] links = { MapsActivity.class, ProfileActivity.class, CreateEvent.class, CreateEvent.class, CreateEvent.class, CreateEvent.class, MainActivity.class};
        new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this, titles, links );

        //Set up user location services
       // locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); //Location manager handles location tasks
        //locationListener = new MyLocationListener();   //Location listener listens to changes in location
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);  //Attach location listener to location manager

        //Set up map
        setUpMapIfNeeded();

    }



    /*


    import math

# Distances are measured in miles.
# Longitudes and latitudes are measured in degrees.
# Earth is assumed to be perfectly spherical.

earth_radius = 3960.0
degrees_to_radians = math.pi/180.0
radians_to_degrees = 180.0/math.pi

def change_in_latitude(miles):
    "Given a distance north, return the change in latitude."
    return (miles/earth_radius)*radians_to_degrees

def change_in_longitude(latitude, miles):
    "Given a latitude and a distance west, return the change in longitude."
    # Find the radius of a circle around the earth at given latitude.
    r = earth_radius*math.cos(latitude*degrees_to_radians)
    return (miles/r)*radians_to_degrees

     */

    public void MsgBox(String Title, String Message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);


        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //WHAT YOU WANT TO HAPPEN WHEN THEY CLICK THE BUTTON
                    }
                })
        ;

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public HashMap<Integer, Event> populateEvents(){
        HashMap<Integer, Event> result = new HashMap<Integer, Event>();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        //get json parser
        //use to populate events

        JSONArray json;
        String raw_json = database.getResults();
        String event_name, event_desc;
        int event_pop, event_cap;
        double event_lat, event_lon;
        try {
            Event thisEvent;
            Marker thisMarker;
            int markerHash;
            json = new JSONArray(raw_json);
            String event_address;

            List<Address> addresses;
            for (int i=0;i<json.length();i++)
            {
                event_name = json.getJSONObject(i).getString("Name");
                event_desc = json.getJSONObject(i).getString("Desc");
                event_pop = Integer.parseInt(json.getJSONObject(i).getString("Population"));
                event_cap = Integer.parseInt(json.getJSONObject(i).getString("Capacity"));
                //event_time = json.getJSONObject(i).getString("Time");
                event_lat = Double.parseDouble(json.getJSONObject(i).getString("Latitude"));
                event_lon = Double.parseDouble(json.getJSONObject(i).getString("Longitude"));

                thisEvent = new Event(event_name, event_desc, event_cap, event_pop, 0, 0, event_lat, event_lon);
                thisMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(event_lat, event_lon)));
                markerHash = thisMarker.hashCode();
                result.put(markerHash, thisEvent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    private void setUpMap() {
        //Set user location
        mMap.setMyLocationEnabled(true);

        //Marker click listener
        mMap.setOnMarkerClickListener(this);

        //Populate events
        allEvents = populateEvents();

        //Set camera to user location

        //Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        //LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,15));
    }



    @Override
    public boolean onMarkerClick(final Marker marker) {

        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        EventMsg(allEvents.get(marker.hashCode()));

        return true;
    }


    public void EventMsg(Event event){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        String time;
        if(event.time.hour > 12)
            time = (event.time.hour%13+1) + ":" + event.time.minute + "pm";
        else
            time = (event.time.hour == 0 ? "12" : event.time.hour) + ":" + event.time.minute + "am";

        String eventInfo = time +  "\nCapacity: " + Integer.toString(event.pop) + "/" + Integer.toString(event.capacity);

        alertDialogBuilder.setTitle(event.name);
        alertDialogBuilder
                .setMessage(eventInfo)
                .setCancelable(true)
                .setPositiveButton("Join Event", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //WHAT YOU WANT TO HAPPEN WHEN THEY CLICK THE BUTTON
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //WHAT YOU WANT TO HAPPEN WHEN THEY CLICK THE BUTTON
                    }
                })
        ;

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
            //   mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("I AM HERE"));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}
    }
}
