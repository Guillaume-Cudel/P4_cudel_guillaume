package com.example.mareu.ui.meeting_list;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mareu.R;
import com.example.mareu.di.Di;
import com.example.mareu.events.DeleteMeetingEvent;
import com.example.mareu.model.Meeting;
import com.example.mareu.service.MeetingApiService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MeetingFragment extends Fragment {

    public static final String BUNDLE_EXTRA_ID = "extra_id";
    public static final String BUNDLE_EXTRA_COLOR = "extra_color";
    public static final String BUNDLE_EXTRA_LOCATION = "extra_location";
    public static final String BUNDLE_EXTRA_DATE = "extra_date";
    public static final String BUNDLE_EXTRA_DATE_COMPARE = "extra_date_compare";
    public static final String BUNDLE_EXTRA_HOUR = "extra_hour";
    public static final String BUNDLE_EXTRA_SUBJECT = "extra_subject";
    public static final String BUNDLE_EXTRA_PARTICIPANTS = "extra_participants";
    public static final int REQUEST_MEETING = 1;

    private MeetingApiService mApiService;
    private List<Meeting> mMeetings;
    private RecyclerView mRecyclerView;
    private int selectedYear, selectedMonth, selectedDayOfMonth;
    private String[] rooms = {" - ", "Room A", "Room B", "Room C", "Room D", "Room E", "Room F", "Room G", "Room H", "Room I", "Room J"};


    public static MeetingFragment newInstance() {
        MeetingFragment fragment = new MeetingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiService = Di.getNewInstanceApiService();
        setHasOptionsMenu(true);

        final Calendar c = Calendar.getInstance();
        this.selectedYear = c.get(Calendar.YEAR);
        this.selectedMonth = c.get(Calendar.MONTH);
        this.selectedDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_meeting_list, container, false);
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
            return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.location_filter:

                final Calendar c = Calendar.getInstance();
                selectedYear = c.get(Calendar.YEAR);
                selectedMonth = c.get(Calendar.MONTH);
                selectedDayOfMonth = c.get(Calendar.DAY_OF_MONTH);

                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Filter of meeting :");

                final View customLayout = getLayoutInflater().inflate(R.layout.custom_filter_date_alert_dialog, null);
                final ArrayAdapter<String> adp = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, rooms);
                Spinner mSpinnerLocation = customLayout.findViewById(R.id.spinner_location_dialog);
                EditText mEditDate = customLayout.findViewById(R.id.editDate_dialog);
                Button mDateButton = customLayout.findViewById(R.id.dateButton_dialog);

                mSpinnerLocation.setAdapter(adp);
                builder.setView(customLayout);

                mDateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == mDateButton) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    Calendar selectedDate = Calendar.getInstance();
                                    selectedDate.set(year, month, dayOfMonth);
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                    String formatedDate = formatter.format(selectedDate.getTime());
                                    mEditDate.setText(formatedDate);
                                   // mEditDate.setText(dayOfMonth + "-" + month + "-" + year);
                                }
                            }, selectedYear, selectedMonth, selectedDayOfMonth);
                            datePickerDialog.show();
                        }
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Meeting> mFilterMeetings;
                        String roomChoosed = mSpinnerLocation.getSelectedItem().toString();
                        String dateOfEditDate = mEditDate.getText().toString();
                        mFilterMeetings = mApiService.getMeetingsByDateAndRoom(dateOfEditDate, roomChoosed);

                        mRecyclerView.setAdapter(new MyMeetingRecyclerViewAdapter(mFilterMeetings));
                        dialog.cancel();
                    }
                });

                builder.setNeutralButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRecyclerView.setAdapter(new MyMeetingRecyclerViewAdapter(mMeetings));
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initList() {
        mMeetings = mApiService.getMeetings();
        mRecyclerView.setAdapter(new MyMeetingRecyclerViewAdapter(mMeetings));
    }


    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onDeleteMeeting(DeleteMeetingEvent event) {
        mApiService.deleteMeeting(event.meeting);
        initList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEETING && resultCode == RESULT_OK) {
            long id = data.getExtras().getLong(BUNDLE_EXTRA_ID);
            int color = data.getExtras().getInt(BUNDLE_EXTRA_COLOR);
            String location = data.getExtras().getString(BUNDLE_EXTRA_LOCATION);
            String date = data.getExtras().getString(BUNDLE_EXTRA_DATE);
            Date dateCompare = (Date)data.getSerializableExtra(BUNDLE_EXTRA_DATE_COMPARE);
            String hour = data.getExtras().getString(BUNDLE_EXTRA_HOUR);
            String subject = data.getExtras().getString(BUNDLE_EXTRA_SUBJECT);
            String participants = data.getExtras().getString(BUNDLE_EXTRA_PARTICIPANTS);

            Meeting meeting = new Meeting(id, color, location, date, hour, subject, participants, dateCompare);

            // AJOUT ----------------
            if (mApiService.verifyIfIsNotPossible(location, dateCompare)) {
                AlertDialog.Builder buildeMeetingError;
                buildeMeetingError = new AlertDialog.Builder(requireActivity());
                buildeMeetingError.setTitle("Error to adding the meeting !");
                buildeMeetingError.setMessage("There is already a meeting in this room at this time. \n A meeting lasts 1 hour. \nPlease choose an other room or an other time ");
                buildeMeetingError.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialogMeetingError = buildeMeetingError.create();
                dialogMeetingError.show();
            } else {
                mApiService.createMeeting(meeting);
            }
            // FIN AJOUT ------------
           // mApiService.createMeeting(meeting);
        }
    }
}

