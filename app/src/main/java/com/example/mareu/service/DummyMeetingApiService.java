package com.example.mareu.service;

import com.example.mareu.model.Meeting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DummyMeetingApiService implements MeetingApiService {

    private List<Meeting> meetings = DummyMeetingGenerator.generateMeetings();

    @Override
    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void deleteMeeting(Meeting meeting) {
        meetings.remove(meeting);
    }

    @Override
    public void createMeeting(Meeting meeting) {
        meetings.add(meeting);
    }

    @Override
    public List<Meeting> getMeetingsByDateAndRoom(String date, String room) {
        List<Meeting> filterMeetings = new ArrayList<Meeting>();
        for (int i = 0; i < meetings.size(); i++) {
            Meeting getMeeting = meetings.get(i);
            if (getMeeting.getDate().equals(date) && getMeeting.getLocation().equals(room)) {
                filterMeetings.add(getMeeting);
            }
            if (getMeeting.getDate().equals(date) && room.equals(" - ")) {
                filterMeetings.add(getMeeting);
            }
            if (date.equals("") && getMeeting.getLocation().equals(room)) {
                filterMeetings.add(getMeeting);
            }
        }
        return filterMeetings;
    }

    @Override
    public String pad(int input){
        String str = "";
        if (input >= 10){
            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);
        }
        return str;
    }


    @Override
    public boolean verifyIfIsNotPossible(String verifyRoom, Date verifyDate) {
        for (Meeting m : meetings) {
            if (m.getLocation().equals(verifyRoom)) {
                // heure de début d'une réunion existante
                Date startOldDate = m.toDate();

                // heure de fin de l'ancienne réunion (heure de début + 1h)
                Calendar endOldCalendar = Calendar.getInstance();
                endOldCalendar.setTime(startOldDate);
                endOldCalendar.add(Calendar.HOUR_OF_DAY, 1);
                Date endOldDate = endOldCalendar.getTime();

                // heure de fin de la nouvelle réunion (heure +1)
                Calendar endNewCalendar = Calendar.getInstance();
                endNewCalendar.setTime(verifyDate);
                endNewCalendar.add(Calendar.HOUR_OF_DAY, 1);
                Date endNewDate = endNewCalendar.getTime();

                if (verifyDate.after(startOldDate) && verifyDate.before(endOldDate)) {
                    return true;
                } else if (verifyDate.equals(endOldDate) || endNewDate.equals(startOldDate)) {
                    return false;
                }else if(verifyDate.before(startOldDate) && endNewDate.after(startOldDate)){
                    return true;
                }else {
                    return false;
                }
            }
        }
            return false;
        }

}








