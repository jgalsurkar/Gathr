/**************************************************************************************************
 Title : MapsActivity.java
 Author : Gathr Team
 Purpose : Allows user to navigate map and browse events by selecting them on the map
 *************************************************************************************************/
package com.gathr.gathr;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import android.content.Context;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.gathr.gathr.classes.AuthUser;
import com.gathr.gathr.classes.Event;
import com.gathr.gathr.classes.GCoder;
import com.gathr.gathr.classes.MyGlobals;
import com.gathr.gathr.classes.SidebarGenerator;
import com.gathr.gathr.database.DatabaseCallback;
import com.gathr.gathr.database.QueryDB;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.List;

import android.support.v4.widget.DrawerLayout;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;

import org.json.JSONArray;


public class MapsActivity extends ActionBarActivityPlus implements GoogleMap.OnMarkerClickListener {

    //Class Objects//
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private LocationListener locationListener;
    private HashMap<Integer, Event> allEvents = new HashMap<Integer, Event>();
    private HashMap<String, Marker> allMarkers = new HashMap<String, Marker>();
    private QueryDB database= new QueryDB(this);
    private GCoder geocoder;
    MyGlobals global = new MyGlobals(this);
    private String raw_json;

    //Auto-Population Settings//
    private boolean autoRepop = true;
    private double camMilesToRepop = 5.0;
    private double camDistToRePop = milesToLat(camMilesToRepop);  //5 miles to repopulate
    private LatLng locationAlpha;
    private boolean startAtUserLocation = false;

    private boolean firstRun = true;
    private final GoogleMap.OnCameraChangeListener mOnCameraChangeListener =
            new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    if(autoRepop && startAtUserLocation){
                        double delta = getDistance(locationAlpha, cameraPosition.target);
                        if(delta > camDistToRePop){
                            locationAlpha = cameraPosition.target;
                            queryLocationEvents(locationAlpha, camMilesToRepop);
                        }
                    }

                    if(firstRun){
                        onMapLoad();
                        firstRun = false;
                    }
                }
            };

    //Overridden Functions//

    @Override
    public FragmentManager getFragmentManager() {
        return super.getFragmentManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   //Every app
        setContentView(R.layout.activity_maps);  //Sets up map
        global.checkInternet();

        setActionBar("Gathr");
        //Set up user location services
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); //Location manager handles location tasks
        locationListener = new MyLocationListener();   //Location listener listens to changes in location
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);  //Attach location listener to location manager
        geocoder = new GCoder(this);
        //Query database
        try {
            new SidebarGenerator((DrawerLayout)findViewById(R.id.drawer_layout), (ListView)findViewById(R.id.left_drawer),android.R.layout.simple_list_item_1,this);
            setUpMapIfNeeded();

            //Set up map

        }catch(Exception e){
            global.errorHandler(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        EventMsg(allEvents.get(marker.hashCode()), this);
        return true;
    }

    //Location Listener Nested Class//

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
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){}
    }

    //Message Box Functions//

    private void MsgBox(String Title, String Message){
        //Generates message box popup
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
        ;
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void EventMsg(final Event event,  final Context c){
        //Generates event message popup
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        MyGlobals globals = new MyGlobals();
        String time = globals.normalTime(event.time.toString());
        String eventInfo = globals.nDate(event.date) + " at " + time +  "\n" +event.description + "\n\nCapacity: " + event.pop + "/" + event.capacity;
        alertDialogBuilder.setTitle(event.name);
        alertDialogBuilder
                .setMessage(eventInfo)
                .setCancelable(true)
                .setPositiveButton("View Details", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(c, ViewGathring.class);
                        i.putExtra("eventId", (event.id));
                        startActivity(i);
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

    //Distance Calculation functions//

    private double getDistance(LatLng a, LatLng b){
        //Calculates distance between two LatLng objects
        return Math.sqrt(Math.pow(a.latitude - b.latitude, 2) + Math.pow(b.longitude - b.longitude, 2));
    }

    private double milesToLat(double miles){
        //Converts miles to latitudinal distance
        return (miles/3960.0)*180.0/Math.PI;
    }

    private double milesToLon(double miles, double lat){
        //Converts miles to longitudinal distance
        double r = 3960.0*Math.cos(lat*Math.PI/180);
        return (miles/r)*180.0/Math.PI;
    }

    //Event Search Functions//

    public void goToSearch(View v){
        //Activates search
        Intent i = new Intent(this, SearchEvents.class);
        startActivityForResult(i, 0);
    }

    private void makeSearch(String location, String categories, String time){
        //Runs search with parameters
        if(location.isEmpty())
            return;
        QueryDB DBConn = new QueryDB(this, "storeSearch.php");
        DBConn.executeQuery("("+ AuthUser.getUserId(this) +", '"+ DBConn.escapeString(location) +"', TIME(NOW()), DATE(NOW())) CATEGORIES " + categories); // replace 1, 5, 6, 7 with category names
        LatLng searchLatLng = geocoder.addressToCoor(location);
        if(searchLatLng.latitude != 0.0 & searchLatLng.longitude != 0.0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLatLng, 15));
            queryLocationEvents(mMap.getCameraPosition().target, camMilesToRepop);
        }
        else
            MsgBox("Sorry", "We couldn't find " + location + ". Try searching by address, city, zip code, or landmark");

    }

    private void queryLocationEvents(LatLng target, double miles){
        queryLocationEvents(target, miles, "");
    }

    private void queryLocationEvents(LatLng target, double miles, String categories){
        //Queries database for events within given miles of given LatLng coordinates of type categories (optional parameter)
        double latDiff = milesToLat(miles),
                lonDiff = milesToLon(miles, target.latitude);
        double latBoundary1 = target.latitude - latDiff,
                latBoundary2 = target.latitude + latDiff,
                lonBoundary1 = target.longitude - lonDiff,
                lonBoundary2 = target.longitude + lonDiff;

        //In case in southern or eastern hemisphere, swap boundaries as needed
        if(latBoundary2 < latBoundary1){
            double temp = latBoundary1;
            latBoundary1 = latBoundary2;
            latBoundary2 = temp;
        }
        if(lonBoundary2 < lonBoundary1){
            double temp = lonBoundary1;
            lonBoundary1 = lonBoundary2;
            lonBoundary2 = temp;
        }

        try{
            String areaRestriction = "Latitude > " + latBoundary1 + " AND Latitude < " + latBoundary2 + " AND Longitude > " + lonBoundary1 + " AND Longitude < " + lonBoundary2;
            String catRestriction = (categories == "")?"":" CATEGORIES " + categories;
            String query = "SELECT * FROM EVENTS WHERE " + areaRestriction + " AND Population < Capacity AND Status = 'OPEN' AND ((Time > TIME(NOW()) AND Date = DATE(NOW())) OR Date > DATE(NOW()))" + catRestriction;

            class getEvents implements DatabaseCallback {
                public void onTaskCompleted(final String r){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            raw_json = r;
                            Log.i("DEBUG",raw_json);
                            if(!r.contains("ERROR") && !r.isEmpty()) {
                                Log.i("DEBUG", "Now populating events with json: " + r);
                                populateEvents();
                            }
                        }
                    });
                }
            }
            Log.i("debug", query);
            database = new QueryDB(this);
            database.executeQuery(query, new getEvents());
        }  catch(Exception e) {
            Log.i("Error", "Failed to populate events at search location");
            global.errorHandler(e);
        }
    }

    //Event Population Functions//

    private void populateEvents(){
        //Populates map with event markers based on search made
        try{
            JSONArray json = new JSONArray(raw_json);
            int markerHash;
            Event thisEvent;
            Marker thisMarker;
            for (int i=0;i<json.length();i++){
                thisEvent = new Event(json.getJSONObject(i));
                if(allMarkers.containsKey(thisEvent.id))
                    continue;
                thisMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(thisEvent.latitude, thisEvent.longitude)));
                markerHash = thisMarker.hashCode();
                allEvents.put(markerHash, thisEvent);
                allMarkers.put(thisEvent.id, thisMarker);
            }
        }catch(Exception e){
            Log.i("Error", "Failed to populate events");
            global.errorHandler(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //If we get to this page from inside the app (they should be passing EventId)
                String category = data.getStringExtra("category");
                String categoryId = data.getStringExtra("categoryId");
                String location = data.getStringExtra("location");
                String time = data.getStringExtra("time");
                makeSearch(location, time, category);
            }
        }
    }

    //Initial Map Setup Functions//

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
        mMap.setOnCameraChangeListener(mOnCameraChangeListener);

    }

    private void onMapLoad(){
        //Runs once when Map fragment is fully loaded
        if (mMap != null) {
            //Set camera to user location if location provider available
            //Otherwise, set camera to NYC by default
            Intent i = getIntent();
            if (i.hasExtra("event_json")) {
                //Runs if and only if this activity was opened to solely to view an event on the map
                Event thisEvent = new Event(i.getStringExtra("event_json"));
                int thisId = Integer.parseInt(thisEvent.id);
                if (allMarkers.containsKey(thisId)) {
                    LatLng thisCoor = allMarkers.get(thisId).getPosition();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(thisCoor, 15));
                } else {
                    Marker newMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(thisEvent.latitude, thisEvent.longitude)));
                    int markerHash = newMarker.hashCode();
                    allEvents.put(markerHash, thisEvent);
                    allMarkers.put(thisEvent.id, newMarker);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newMarker.getPosition(), 15));
                }
            } else {
                String thisProvider;
                Location location;
                List<String> allProviders = locationManager.getAllProviders();
                for (int n = 0; n < allProviders.size(); n++) {
                    thisProvider = allProviders.get(n);
                    location = locationManager.getLastKnownLocation(thisProvider);
                    if (location != null) {
                        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));
                        startAtUserLocation = true;
                        break;
                    }
                }
                if (startAtUserLocation)
                    locationAlpha = mMap.getCameraPosition().target;
                else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.7127, -74.0059), 15));
                    locationAlpha = mMap.getCameraPosition().target;
                }
                queryLocationEvents(locationAlpha, camMilesToRepop);
            }
        }
    }

}