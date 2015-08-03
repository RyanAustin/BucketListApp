package com.ryanaustin.bucketlist;
import java.io.Serializable;

/**
 * User: Ryan
 * Date: 7/20/2015
 * Time: 6:22 AM
 */
public class Locations implements Serializable {

    private long rowID;
    private String location;
    private String latitude;
    private String longitude;
    private int visited;

    public Locations() {

    }

    public Locations(long rowID, String location, String latitude, String longitude, int visited) {
        this.rowID = rowID;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visited = visited;
    }

    public long getRowID() {
        return rowID;
    }

    public void setRowID(long rowID) {
        this.rowID = rowID;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getVisited() {
        return visited;
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }


}