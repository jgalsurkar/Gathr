/**************************************************************************************************
 Title : GCoder.java
 Author : Gathr Team
 Purpose : Converts address to geocoordinates and vice versa
 *************************************************************************************************/

package com.gathr.gathr.classes;

import android.content.Context;
import android.location.Geocoder;
import android.location.Address;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import com.google.android.gms.maps.model.LatLng;
import java.util.Locale;

public class GCoder {

    private android.location.Geocoder coder;

    public GCoder(Context c){
        coder = new Geocoder(c, Locale.getDefault());
    }

    //Converts an address to Lat/Lon Object
    public LatLng addressToCoor(String address){
        List<Address> queryResults;
        LatLng result = new LatLng(0,0);
        try {
            queryResults = coder.getFromLocationName(address,1);
            if(queryResults.size() < 1)
                return new LatLng(0,0);
            result = new LatLng(queryResults.get(0).getLatitude(), queryResults.get(0).getLongitude());
        } catch (IOException e){
            Log.i("error", e.toString());
        }
        return result;
    }

    //Converts a Latitude and Longitude to an Address
    public String coorToAddress(double lat, double lon){
        List<Address> queryResults;
        String address;
        try {
            queryResults = coder.getFromLocation(lat, lon, 1);
            address = queryResults.get(0).getAddressLine(0);
        } catch( IOException e){
            Log.i("error", e.toString());
            address = "ERROR RETRIEVING ADDRESS";
        }
        return address;
    }
}