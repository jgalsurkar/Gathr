/**************************************************************************************************
 Title : Event.java
 Author : Gathr Team
 Purpose : Class representing a Gathring. Used to instantiate a Gathring through a JSON object
 *************************************************************************************************/
package com.gathr.gathr.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Event {

    public String id, name, description, time, capacity, pop, date, address, city, state, status, event_organizer, categories, categoriesId; //separated by a comma
    public Double latitude, longitude;

    public Event(String JSON){
        try {
            JSONArray jsonArray = new JSONArray(JSON);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONObjtoEvent(jsonObject);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public Event(JSONObject jsonObject){
        JSONObjtoEvent(jsonObject);
    }
    public void JSONObjtoEvent(JSONObject jsonObject){
        try {
            id = jsonObject.getString("Id");
            name = jsonObject.getString("Name");
            description = jsonObject.getString("Desc");
            address = jsonObject.getString("Address");
            city = jsonObject.getString("City");
            state = jsonObject.getString("State");
            time = jsonObject.getString("Time");
            date = jsonObject.getString("Date");
            capacity = jsonObject.getString("Capacity");
            pop = jsonObject.getString("Population");
            status = jsonObject.getString("Status");
            event_organizer = jsonObject.getString("Organizer").trim();
            latitude = Double.parseDouble(jsonObject.getString("Latitude"));
            longitude = Double.parseDouble(jsonObject.getString("Longitude"));
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            categories = jsonObject.getString("Categories");
            categoriesId = jsonObject.getString("CategoriesId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}