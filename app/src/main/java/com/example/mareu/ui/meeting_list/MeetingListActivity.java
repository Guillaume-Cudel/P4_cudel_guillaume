package com.example.mareu.ui.meeting_list;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.mareu.R;
import com.example.mareu.model.Meeting;
import com.example.mareu.service.MeetingApiService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeetingListActivity extends AppCompatActivity {

    @BindView(R.id.container)
    ViewPager mViewPager;
    PageAdapter mPagerAdapter;
    private MeetingApiService mApiService;
    private Meeting meeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);
        ButterKnife.bind(this);

        mPagerAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

    }


    @OnClick(R.id.add_meeting)
    void addMeeting() {
        Intent secondeActivite = new Intent(MeetingListActivity.this, AddMeetingActivity.class);
        startActivityForResult(secondeActivite, MeetingFragment.REQUEST_MEETING);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}


