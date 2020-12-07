package com.example.mareu.utils;

import android.view.View;
import android.widget.DatePicker;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.PickerActions;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;

public class StartViewAction {

    public static ViewAction clickViewWithId(final int id){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on specific button";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View button = view.findViewById(id);
                //findViewById(R.id.item_list_delete_button);
                // Maybe check for null
                button.performClick();

            }
        };
    }

}
