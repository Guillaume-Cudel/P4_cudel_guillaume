package com.example.mareu.ui.meeting_list;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mareu.R;
import com.example.mareu.di.Di;
import com.example.mareu.model.Meeting;
import com.example.mareu.service.MeetingApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.mareu.ui.meeting_list.MeetingFragment.BUNDLE_EXTRA_COLOR;
import static com.example.mareu.ui.meeting_list.MeetingFragment.BUNDLE_EXTRA_DATE;
import static com.example.mareu.ui.meeting_list.MeetingFragment.BUNDLE_EXTRA_DATE_COMPARE;
import static com.example.mareu.ui.meeting_list.MeetingFragment.BUNDLE_EXTRA_HOUR;
import static com.example.mareu.ui.meeting_list.MeetingFragment.BUNDLE_EXTRA_ID;
import static com.example.mareu.ui.meeting_list.MeetingFragment.BUNDLE_EXTRA_LOCATION;
import static com.example.mareu.ui.meeting_list.MeetingFragment.BUNDLE_EXTRA_PARTICIPANTS;
import static com.example.mareu.ui.meeting_list.MeetingFragment.BUNDLE_EXTRA_SUBJECT;


public class AddMeetingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText mAddParticipants, mParticipants, mSubjectText;
    private Button mButtonAddParticipantsWithMail, mDateButton, mHourButton;
    private ImageButton mButtonColor;
    private Spinner mSpinnerLocation;
    private EditText mDateText, mHourText;
    private MaterialButton mSave;

    private MeetingApiService mApiService;
    private int mMeetingColor = 0;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int colorChoosed;
    private int spinnerRoom;
    private String[] room = {"Room A", "Room B", "Room C", "Room D", "Room E", "Room F", "Room G", "Room H", "Room I", "Room J"};
    private final int subjectMaxLenght = 25, participantMaxLenght = 15;
    private Date  dateToCompare;
    private String formatedDate, roomChoosed, subjectChoosed, participantsChoosed, dateChoosed, hourChoosed, dateAndHourChoosed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);
        mApiService = Di.getNewInstanceApiService();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        mAddParticipants = findViewById(R.id.add_participants);
        mParticipants = findViewById(R.id.participants);
        mButtonAddParticipantsWithMail = findViewById(R.id.button_participants);
        mButtonColor = findViewById(R.id.add_color);
        mSpinnerLocation = findViewById(R.id.spinner_location);
        mDateText = findViewById(R.id.select_date);
        mHourText = findViewById(R.id.select_hour);
        mDateButton = findViewById(R.id.button_date);
        mHourButton = findViewById(R.id.button_hour);
        mSubjectText = findViewById(R.id.add_subject);
        mSave = findViewById(R.id.add_save);

        mAddParticipants.setFilters(new InputFilter[]{new InputFilter.LengthFilter(participantMaxLenght)});
        mSubjectText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(subjectMaxLenght)});

        mDateText.setOnClickListener(this);
        mHourText.setOnClickListener(this);
        mSubjectText.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mDateButton.setOnClickListener(this);
        mHourButton.setOnClickListener(this);
        mButtonColor.setOnClickListener(this);
        mButtonAddParticipantsWithMail.setOnClickListener(this);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, room);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLocation.setAdapter(adapter1);

        init();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        final Calendar c = Calendar.getInstance();
         mYear = c.get(Calendar.YEAR);
         mMonth = c.get(Calendar.MONTH);
         mDay = c.get(Calendar.DAY_OF_MONTH);
         mHour = c.get(Calendar.HOUR_OF_DAY);
         mMinute = c.get(Calendar.MINUTE);
        switch (id) {
            case R.id.button_date :

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        formatedDate = formatter.format(selectedDate.getTime());
                        mDateText.setText(formatedDate);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                break;

            case R.id.button_hour:

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHourText.setText(mApiService.pad(hourOfDay) + ":" + mApiService.pad(minute));
                }
            }, mHour, mMinute, true);

            timePickerDialog.show();
            break;

            case R.id.button_participants:
                if (mAddParticipants != null) {
                    String participantsSaved = mAddParticipants.getEditableText().toString();
                    mParticipants.setText(mParticipants.getText().toString()+ participantsSaved + "@meeting.com; ");
                    mAddParticipants.getEditableText().clear();
                }
                break;

            case R.id.add_color:
                if (mMeetingColor >= 4) {
                    mMeetingColor = 0;
                    changeColor(mMeetingColor);
                } else {
                    mMeetingColor++;
                    changeColor(mMeetingColor);
                }
                break;

            case R.id.add_save:

                long idChoosed = System.currentTimeMillis();
                colorChoosed = mMeetingColor;
                roomChoosed = mSpinnerLocation.getSelectedItem().toString();
                spinnerRoom = mSpinnerLocation.getSelectedItemPosition();
                dateChoosed = mDateText.getEditableText().toString();
                hourChoosed = mHourText.getEditableText().toString();
                dateAndHourChoosed = dateChoosed + " " + hourChoosed;
                subjectChoosed = mSubjectText.getEditableText().toString();
                participantsChoosed = mParticipants.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy hh:mm");
                try {
                     dateToCompare = sdf.parse(dateAndHourChoosed);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                /*if (mApiService.verifyIfIsNotPossible(roomChoosed, dateToCompare)) {
                    AlertDialog.Builder buildeMeetingError;
                    buildeMeetingError = new AlertDialog.Builder(this);
                    buildeMeetingError.setTitle("Error to adding the meeting !");
                    buildeMeetingError.setMessage("There is already a meeting in this room at this time. Please choose an other room or an other time ");
                    buildeMeetingError.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog dialogMeetingError = buildeMeetingError.create();
                    dialogMeetingError.show();
                } else {
                    saveMeeting(idChoosed);
                }*/
                saveMeeting(idChoosed);
                break;
        }
    }

    private void saveMeeting(long idChoosed) {
        Intent intent = getIntent();
        intent.putExtra(BUNDLE_EXTRA_ID, idChoosed);
        intent.putExtra(BUNDLE_EXTRA_COLOR, colorChoosed);
        intent.putExtra(BUNDLE_EXTRA_LOCATION, roomChoosed);
        intent.putExtra(BUNDLE_EXTRA_DATE, dateChoosed);
        intent.putExtra(BUNDLE_EXTRA_DATE_COMPARE, dateToCompare);
        intent.putExtra(BUNDLE_EXTRA_HOUR, hourChoosed);
        intent.putExtra(BUNDLE_EXTRA_SUBJECT, subjectChoosed);
        intent.putExtra(BUNDLE_EXTRA_PARTICIPANTS, participantsChoosed);
        setResult(RESULT_OK, intent);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void init() {
        changeColor(mMeetingColor);
        Glide.with(this).load(mMeetingColor).apply(RequestOptions.circleCropTransform()).into(mButtonColor);
        mDateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSave.setEnabled(s.length() > 0);
            }
        });

        mAddParticipants.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {mButtonAddParticipantsWithMail.setEnabled(s.length() > 2);}
        });
    }

    public void changeColor(int color) {
        mMeetingColor = color;
        if (color == 1) {
            mButtonColor.setImageResource(R.drawable.ic_lens_cyan);
        } else if (color == 2) {
            mButtonColor.setImageResource(R.drawable.ic_lens_green);
        } else if (color == 3) {
            mButtonColor.setImageResource(R.drawable.ic_lens_red);
        } else if (color == 4) {
            mButtonColor.setImageResource(R.drawable.ic_lens_pink);
        } else if (color == 0) {
            mButtonColor.setImageResource(R.drawable.ic_lens);
        }
    }


}
