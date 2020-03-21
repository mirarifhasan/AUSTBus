package com.example.austbus.Model;


public class Bus {

    public String busName, busRoute;

    public Bus(String busName, String busRoute) {
        this.busName = busName;
        this.busRoute = busRoute;
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
