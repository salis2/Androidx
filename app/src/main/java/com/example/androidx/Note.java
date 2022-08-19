package com.example.androidx;

import io.realm.Realm;
import io.realm.RealmObject;

public class Note extends RealmObject {

    String title;
    String description;
    long createdTime;
    String location;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

//    String title;
//    String description;
//    long createdTime;
//
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public long getCreatedTime() {
//        return createdTime;
//    }
//
//    public void setCreatedTime(long createdTime) {
//        this.createdTime = createdTime;
//    }
}