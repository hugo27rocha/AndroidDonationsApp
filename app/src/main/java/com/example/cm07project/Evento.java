
package com.example.cm07project;

import android.widget.EditText;

public class Evento {


    String name;
    String des;
    String date;
    String streetadress;
    String state;
    String country;
    String id;
    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;

    public Evento(String name, String des, String date, String streetadress, String state, String country, String id,String uid) {
        this.name = name;
        this.des = des;
        this.date = date;
        this.streetadress = streetadress;
        this.state = state;
        this.country = country;
        this.id = id;
        this.uid=uid;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStreetadress() {
        return streetadress;
    }

    public void setStreetadress(String streetadress) {
        this.streetadress = streetadress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}