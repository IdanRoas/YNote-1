package com.example.ynote.ynote;



import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.ynote.ynote.MapsActivity.polylinePoints;



public class NoteObj implements Parcelable {

    private List<LatLng> polylineP;
    private String text;
    private String type;
    private List<String> uri;
    private String title;
    private String userId;
    private double biggestRadius;
    private String date;

    public NoteObj() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeTypedList(polylineP);
        out.writeString(text);
        out.writeString(type);
        out.writeStringList(uri);
        out.writeString(title);
        out.writeString(userId);
        out.writeDouble(biggestRadius);
        out.writeString(date);
    }
    public static final Parcelable.Creator<NoteObj> CREATOR = new Parcelable.Creator<NoteObj>() {
        public NoteObj createFromParcel(Parcel in) {
            return new NoteObj(in);
        }

        public NoteObj[] newArray(int size) {
            return new NoteObj[size];
        }
    };

    private NoteObj(Parcel in) {
        polylineP = new ArrayList<>();
        in.readTypedList(polylineP, LatLng.CREATOR);
        text = in.readString();
        type = in.readString();
        uri = new ArrayList<>();
        in.readStringList(uri);
        title = in.readString();
        userId = in.readString();
        biggestRadius = in.readDouble();
        date = in.readString();
    }
    public NoteObj(List<LatLng> polylineP, String text, String type,List<String> uri, String title, String userId, double biggestRadius, String date) {
        this.polylineP = polylineP;
        this.text = text;
        this.type = type;
        this.uri = uri;
        this.title = title;
        this.userId = userId;
        this.biggestRadius = biggestRadius;
        this.date = date;
    }

    public NoteObj(List<String> uri ,String text) {
        this.uri = uri;
        this.text=text;
    }

    public List<LatLng> getPolylineP() {
        return polylineP;
    }

    public void setPolylineP(List<LatLng> polylineP) {
        this.polylineP = polylineP;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getUri() {
        return uri;
    }

    public void setUri(List<String> uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getBiggestRadius() {
        return biggestRadius;
    }

    public void setBiggestRadius(double biggestRadius) {
        this.biggestRadius = biggestRadius;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "NoteObj{" +
                "polylineP=" + polylineP +
                ", text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", uri=" + uri.toString() +
                ", title='" + title + '\'' +
                ", userId='" + userId + '\'' +
                ", biggestRadius=" + biggestRadius +
                ", date='" + date + '\'' +
                '}';
    }


}



