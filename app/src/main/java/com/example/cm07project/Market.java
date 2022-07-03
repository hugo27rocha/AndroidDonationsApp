package com.example.cm07project;

public class Market {

    /** Represents Market on Firebase DB*/

    public String eventid;
    public String item;

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Market(String eventid, String item) {
        this.eventid = eventid;
        this.item = item;
    }
}
