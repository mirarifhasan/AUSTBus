package com.example.austbus.Model;


public class Bus {

    public String busName, busRoute, startingTime, departureTime, routeToAUST;
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

    public Bus(String busName, String routeToAUST, int busID, String startingTime, String departureTime) {
        this.busName = busName;
        this.routeToAUST = routeToAUST;
        this.busID = busID;
        this.startingTime = startingTime;
        this.departureTime = departureTime;
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

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getRouteToAUST() {
        return routeToAUST;
    }

    public void setRouteToAUST(String routeToAUST) {
        this.routeToAUST = routeToAUST;
    }
}
