package com.example.mareu;

import android.view.View;
import android.widget.DatePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.mareu.di.DI;
import com.example.mareu.service.MeetingApiService;
import com.example.mareu.ui.meeting_list.MeetingListActivity;
import com.example.mareu.utils.DeleteViewAction;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.mareu.utils.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MeetingInstrumentedTest {

    private static int ITEMS_COUNT = 1;
    @Rule
    public ActivityTestRule<MeetingListActivity> mActivityRule = new ActivityTestRule<>(MeetingListActivity.class);
    private MeetingListActivity mActivity;
    private MeetingApiService service;

    public static Matcher<View> matchesDate(final int year, final int month, final int day) {
        return new BoundedMatcher<View, DatePicker>(DatePicker.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("matches date:");
            }

            @Override
            protected boolean matchesSafely(DatePicker item) {
                return (year == item.getYear() && month == item.getMonth() && day == item.getDayOfMonth());
            }
        };
    }

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
        assertThat(mActivity, notNullValue());

        service = DI.getNewInstanceApiService();
        assertTrue(service.getMeetings().isEmpty());
    }

    // test du changement d'activité pour ajouter une réunion
    @Test
    public void myMeetingList_addAction_showAddMeeting() {
        // Begin to display MeetingListActivity (who is empty)
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        // On click to addMeeting button
        onView(withId(R.id.add_meeting)).perform(click());
        // The AddMeetingActivity is displayed
        onView(withId(R.id.add_meeting_activity)).check(matches(isDisplayed()));
    }

    // test d'ajout d'une réunion
    @Test
    public void myMeeting_addAction_isDisplayedInTheList() {
        // Begin to display MeetingListActivity (who is empty)
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        // On click to addMeeting button
        onView(withId(R.id.add_meeting)).perform(click());
        // In the AddMeetingActivity
        onView(withId(R.id.add_meeting_activity)).check(matches(isCompletelyDisplayed()));
        // Add text in the edit text participant for delock the save button and close the keyboard
        onView(withId(R.id.add_participants)).perform(typeText("Someone"), closeSoftKeyboard());
        // On click to add meeting in the list
        onView(withId(R.id.add_save)).perform(click());
        // Launch the list
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        // Chek if the meeting list is no empty
        onView(withId(R.id.list_meetings)).check(matches(hasMinimumChildCount(1)));
    }

    //test de suppression d'une réunion
    @Test
    public void myMeetingList_deleteAction_shouldRemoveItem() {
        // Launch the list
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        // On click to addMeeting button
        onView(withId(R.id.add_meeting)).perform(click());
        // In the AddMeetingActivity
        onView(withId(R.id.add_meeting_activity)).check(matches(isCompletelyDisplayed()));
        // Add text in the edit text participant for delock the save button and close the keyboard
        onView(withId(R.id.add_participants)).perform(typeText("Someone"), closeSoftKeyboard());
        // On click to add meeting in the list
        onView(withId(R.id.add_save)).perform(click());
        // We remove the element at the position 1
        onView(withId(R.id.list_meetings)).check(withItemCount(ITEMS_COUNT));
        // Perform click on a delete button
        onView(withId(R.id.list_meetings))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, new DeleteViewAction()));
        // Then the numbre of element is 2
        onView(withId(R.id.list_meetings)).check(withItemCount(ITEMS_COUNT - 1));
    }

    //test de l'affichage de l'alert dialog
    @Test
    public void myAlertDialog_filterAction_isDisplayed() {
        // Launch the list
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        // Launch the alert dialog filter
        onView(withId(R.id.location_filter)).perform(click());
        //Check if the alert dialog is displayed
        onView(withId(R.id.alert_dialog)).check(matches(isDisplayed()));
    }

    //test du filtre par salle
    @Test
    public void myMeetingList_filterRoomAction_showOnlyTheMeetingRoomSelected() throws Exception {
        // Add first meeting
        // Launch the list
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        // On click to addMeeting button
        onView(withId(R.id.add_meeting)).perform(click());
        // In the AddMeetingActivity
        onView(withId(R.id.add_meeting_activity)).check(matches(isCompletelyDisplayed()));
        // On click spinner
        onView(withId(R.id.spinner_location)).perform(click());
        // Choose Room B
        onData(allOf(is(instanceOf(String.class)), is("Room B"))).perform(click());
        // Check if the spinner contains the value Room B
        onView(withId(R.id.spinner_location)).check(matches(withSpinnerText(containsString("Room B"))));
        // Add text in the edit text participant for delock the save button and close the keyboard
        onView(withId(R.id.add_participants)).perform(typeText("Someone"), closeSoftKeyboard());
        // On click to add meeting in the list
        onView(withId(R.id.add_save)).perform(click());
        //Add second meeting
        onView(withId(R.id.add_meeting)).perform(click());
        onView(withId(R.id.add_meeting_activity)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.spinner_location)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Room C"))).perform(click());
        onView(withId(R.id.spinner_location)).check(matches(withSpinnerText(containsString("Room C"))));
        onView(withId(R.id.add_participants)).perform(typeText("Someone else"), closeSoftKeyboard());
        onView(withId(R.id.add_save)).perform(click());
        // Launch the list
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        // Launch the alert dialog filter
        onView(withId(R.id.location_filter)).perform(click());
        // Choose a room
        onView(withId(R.id.spinner_location_dialog)).perform(click());
        // Choose Room B
        onData(allOf(is(instanceOf(String.class)), is("Room B"))).inRoot(isPlatformPopup()).perform(click());
        // Check if the spinner contains the value Room B
        onView(withId(R.id.spinner_location_dialog)).check(matches(withSpinnerText(containsString("Room B"))));
        // Click ok to the alert dialog
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        // Meeting list
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        //Check if room B meeting is alone in the list
        onView(withId(R.id.list_meetings)).check(matches(hasMinimumChildCount(1)));

    }

    // test du filtre par date
    @Test
    public void myMeetingList_filterDateAction_showOnlyTheMeetingDateSelected() throws Exception {
        // Add first meeting
        // Launch the list
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        // On click to addMeeting button
        onView(withId(R.id.add_meeting)).perform(click());
        // In the AddMeetingActivity
        onView(withId(R.id.add_meeting_activity)).check(matches(isCompletelyDisplayed()));
        // On click spinner
        onView(withId(R.id.spinner_location)).perform(click());
        // Choose Room B
        onData(allOf(is(instanceOf(String.class)), is("Room B"))).perform(click());
        // Check if the spinner contains the value Room B
        onView(withId(R.id.spinner_location)).check(matches(withSpinnerText(containsString("Room B"))));
        // Click to date button
        onView(withId(R.id.button_date)).perform(click());
        // Choose a date
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 10, 10));
        // Confirm the date
        onView(withText("OK")).perform(click());
        // Add text in the edit text participant for delock the save button and close the keyboard
        onView(withId(R.id.add_participants)).perform(typeText("Someone"), closeSoftKeyboard());
        // On click to add meeting in the list
        onView(withId(R.id.add_save)).perform(click());
        //Add second meeting
        onView(withId(R.id.add_meeting)).perform(click());
        onView(withId(R.id.add_meeting_activity)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.spinner_location)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Room C"))).perform(click());
        onView(withId(R.id.spinner_location)).check(matches(withSpinnerText(containsString("Room C"))));
        onView(withId(R.id.button_date)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 11, 11));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.add_participants)).perform(typeText("Someone else"), closeSoftKeyboard());
        onView(withId(R.id.add_save)).perform(click());
        //Add third meeting
        onView(withId(R.id.add_meeting)).perform(click());
        onView(withId(R.id.add_meeting_activity)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.spinner_location)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Room A"))).perform(click());
        onView(withId(R.id.spinner_location)).check(matches(withSpinnerText(containsString("Room A"))));
        onView(withId(R.id.button_date)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 10, 10));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.add_participants)).perform(typeText("Someone else again"), closeSoftKeyboard());
        onView(withId(R.id.add_save)).perform(click());
        // Launch the list
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        // Launch the alert dialog filter
        onView(withId(R.id.location_filter)).perform(click());
        // Click to date button
        onView(withId(R.id.dateButton_dialog)).perform(click());
        //Choose date
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 10, 10));
        // Confirm the date
        onView(withText("OK")).perform(click());
        // Click ok to the alert dialog
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
        // Meeting list
        onView(withId(R.id.list_meetings)).check(matches(isDisplayed()));
        //Check if there are only 2 meetings at this date
        onView(withId(R.id.list_meetings)).check(matches(hasMinimumChildCount(2)));
    }


}


