package com.example.cm07project;

public class People {
    /** Represents People on Firebase DB*/
    public String eventid;
    public String name;
    public String email;


    public People( String fullname, String eventid) {
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public People(String eventid, String name, String email ) {
        this.eventid = eventid;
        this.name = name;
        this.email = email;
    }

    public People() {
    }
}
