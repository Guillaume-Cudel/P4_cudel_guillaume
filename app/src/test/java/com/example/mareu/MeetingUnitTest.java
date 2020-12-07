package com.example.mareu;

import com.example.mareu.di.Di;
import com.example.mareu.model.Meeting;
import com.example.mareu.service.DummyMeetingGenerator;
import com.example.mareu.service.MeetingApiService;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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

    @Before
    public void setup() {
        service = Di.getNewInstanceApiService();
    }

    @Test
    public void getMeetingWithSuccess() {
        List<Meeting> meetings = service.getMeetings();
        List<Meeting> expectedMeetings = DummyMeetingGenerator.DUMMY_MEETINGS;
        assertThat(meetings, IsIterableContainingInAnyOrder.containsInAnyOrder(expectedMeetings.toArray()));
    }

    @Test
    public void addMeetingWithSuccess() {
        Meeting meetingAdded = new Meeting(1, 1, "Loin", "10/10/20",
                "01h00", "Aquaponey", "Pleins de mamies");
        service.createMeeting(meetingAdded);
        Meeting controlMeeting2 = service.getMeetings().get(0);
        assertTrue(service.getMeetings().contains(controlMeeting2));
    }

    @Test
    public void deleteMeetingWithSuccess() {
        Meeting meetingAdded = new Meeting(1, 1, "Loin", "10/10/20",
                "01h00", "Aquaponey", "Pleins de mamies");
        service.createMeeting(meetingAdded);
        Meeting meetingToDelete = service.getMeetings().get(0);
        service.deleteMeeting(meetingToDelete);
        assertFalse(service.getMeetings().contains(meetingToDelete));
    }
}