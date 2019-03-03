package com.example.ynote.ynote;

public class User {

    public String About,URI, name;

    public User(){

    }

    public User(String About, String URI, String name) {
        this.About = About;
        this.URI = URI;
        this.name = name;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String About) {
        this.About = About;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
