package com.alexandru.developer.aplicatie_studenti.view_pager;

import android.Manifest;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexandru on 6/30/14.
 */
public class Course implements Parcelable {
    public String name;
    public String fullName;
    public String type;
    public String location;
    public String time;
    public String prof;
    public String info;

    private final int NUM_ELEM = 7;

    public Course(String name, String fullName, String type, String location,
                  String time, String prof, String info) {
        this.name = name;
        this.fullName = fullName;
        this.type = type;
        this.info = info;
        this.time = time;
        this.location = location;
        this.prof = prof;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[] {name, fullName, type, info, time, location, prof});
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {

        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public Course(Parcel in){
        String[] data = new String[NUM_ELEM];
        in.readStringArray(data);
        this.name = data[0];
        this.fullName = data[1];
        this.type = data[2];
        this.info= data[3];
        this.time= data[4];
        this.location = data[5];
        this.prof = data[6];
    }

}
