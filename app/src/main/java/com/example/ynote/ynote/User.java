package com.example.ynote.ynote;

public class User {

    private String About, URI, name, Score;

    public User() {

    }

    public User(String about, String URI, String name, String score) {
        About = about;
        this.URI = URI;
        this.name = name;
        Score = score;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
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

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }
}



