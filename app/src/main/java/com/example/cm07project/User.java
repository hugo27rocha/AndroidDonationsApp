package com.example.cm07project;

public class User {

    public String firstname;
    public String lastname;
    public Boolean org;
    public String email;

    public void setOrg(Boolean org) {
        this.org = org;
    }

    public Boolean getOrg() {
        return org;
    }

    public User(){
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String firstname, String lastname, String email,Boolean org){
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.org = org;

    }

    public String getName(){
        return this.firstname;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
