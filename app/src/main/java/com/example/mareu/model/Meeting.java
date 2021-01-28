package com.example.mareu.model;

import java.util.Date;
import java.util.Objects;

public class Meeting {

    private long id;
    private int color;
    private String location;
    private String date;
    private String hour;
    private String subject;
    private String participants;
    private Date dateToCompare;


    public Meeting(long id, int color, String location, String date, String hour, String subject, String participants, Date dateToCompare) {
        this.id = id;
        this.color = color;
        this.location = location;
        this.date = date;
        this.hour = hour;
        this.subject = subject;
        this.participants = participants;
        this.dateToCompare = dateToCompare;
        
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public Date toDate(){ return dateToCompare;}

    public void setDateToCompare(Date dateToCompare){
        this.dateToCompare = dateToCompare;
    }

}


