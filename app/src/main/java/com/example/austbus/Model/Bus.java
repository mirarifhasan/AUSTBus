package com.example.austbus.Model;


public class Bus {

    public String busName, busRoute;
    public int busID;

    public Bus(String busName, String busRoute, int busID) {
        this.busName = busName;
        this.busRoute = busRoute;
        this.busID = busID;
    }

    public Bus(int busID, String busName) {
        this.busID = busID;
        this.busName = busName;
    }

    public int getBusID() {
        return busID;
    }

    public void setBusID(int busID) {
        this.busID = busID;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(String busRoute) {
        this.busRoute = busRoute;
    }
}
