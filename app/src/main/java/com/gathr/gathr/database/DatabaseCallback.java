/**************************************************************************************************
 Title : DatabaseCallback.java
 Author : Gathr Team
 Purpose : Interface for determining what to do with database results
 *************************************************************************************************/
package com.gathr.gathr.database;

public interface DatabaseCallback {
    void onTaskCompleted(String results);
}