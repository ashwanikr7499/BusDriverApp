package com.example.busdriverapp;

public class Driver {



    String uid;
    String bus_no;
    String latitude;
    String longitude;



    String phoneNo;
    public Driver(){

    }
    public String getUid() {
        return uid;
    }

    public String getBus_no() {
        return bus_no;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
    public String getPhoneNo() {
        return phoneNo;
    }
    public Driver(String uid, String bus_no, String latitude, String longitude,String phoneNo) {
        this.uid = uid;
        this.bus_no = bus_no;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNo=phoneNo;
    }




}
