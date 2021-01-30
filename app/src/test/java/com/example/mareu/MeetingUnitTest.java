package com.example.mareu;

import com.example.mareu.di.DI;
import com.example.mareu.model.Meeting;
import com.example.mareu.service.DummyMeetingGenerator;
import com.example.mareu.service.MeetingApiService;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MeetingUnitTest {

    private MeetingApiService service;
    private Date dateTest = new Date();

    @Before
    public void setup() {
        service = DI.getNewInstanceApiService();
    }

    @Test
    public void getMeetingWithSuccess() {
        List<Meeting> meetings = service.getMeetings();
        List<Meeting> expectedMeetings = DummyMeetingGenerator.DUMMY_MEETINGS;
        assertThat(meetings, IsIterableContainingInAnyOrder.containsInAnyOrder(expectedMeetings.toArray()));
    }

    @Test
    public void addMeetingWithSuccess() {
        assertTrue(service.getMeetings().isEmpty());
        Meeting meetingAdded = new Meeting(1, 1, "Loin", "10/10/20",
                "01h00", "Aquagym", "Pleins de mamies", dateTest);
        service.createMeeting(meetingAdded);
        assertEquals(1, service.getMeetings().size());
        assertTrue(service.getMeetings().contains(meetingAdded));
    }

    @Test
    public void deleteMeetingWithSuccess() {
        assertTrue(service.getMeetings().isEmpty());
        Meeting meetingAdded = new Meeting(1, 1, "Loin", "10/10/20",
                "01h00", "Chaussures", "Pleins de clients", dateTest);
        service.createMeeting(meetingAdded);
        assertEquals(1, service.getMeetings().size());
        service.deleteMeeting(meetingAdded);
        assertFalse(service.getMeetings().contains(meetingAdded));
        assertTrue(service.getMeetings().isEmpty());
    }

    @Test
    public void verifyIfDatesAndRoomAreGood() throws ParseException {
        // Create dates and rooms
        String room1 = "Room B";
        String room2 = "Room A";
        String date1 = "01/01/2021 14:00";
        String date2 = "01/01/2021 14:20";
        String date3 = "01/01/2021 15:00";
        String date4 = "01/01/2021 13:30";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date startDate1 = sdf.parse(date1);
        Date startDate2 = sdf.parse(date2);
        Date startDate3 = sdf.parse(date3);
        Date startDate4 = sdf.parse(date4);
        // Meetings list is empty
        assertTrue(service.getMeetings().isEmpty());
        // Create a meeting
        Meeting meeting1 = new Meeting(1, 1, room1, date1,
                "12h00", "Chaussures", "Pleins de clients", startDate1);

        service.createMeeting(meeting1);
        // Tests
        assertFalse(service.getMeetings().isEmpty());
        assertTrue(service.getMeetings().contains(meeting1));
        // Same room and date don't riding a date meeting
        assertTrue(service.verifyIfIsNotPossible(room1, startDate2));
        // Same room and the date is ok
        assertFalse(service.verifyIfIsNotPossible(room1, startDate3));
        // Same room and date before the meeting
        assertTrue(service.verifyIfIsNotPossible(room1, startDate4));
        // It's false because there is an other room
        assertFalse(service.verifyIfIsNotPossible(room2, startDate2));
    }
}