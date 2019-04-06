package com.example.shraddha.igram;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class updateComplaint extends AppCompatActivity implements View.OnClickListener {

    Calendar mDate;
    int day, month, year, hour, minute;
    String format;
    private TextView dateTextView;
    private EditText description;
    private Button pickUpDate, pickUpTime;
    private Spinner complaintType;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_complaint);

        databaseReference = FirebaseDatabase.getInstance().getReference("complaint");

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        pickUpDate = (Button) findViewById(R.id.pickDateButton);
        pickUpTime = (Button) findViewById(R.id.pickTimeButton);
        description = (EditText) findViewById(R.id.describe);
        complaintType = (Spinner) findViewById(R.id.handymanSpinner);

        List<String> list = Arrays.asList(getResources().getStringArray(R.array.complaint_type));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setTextColor(Color.GRAY);
                    tv.setClickable(false);
                    v = tv;
                } else {
                    v = super.getDropDownView(position, null, parent);
                }
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        complaintType.setAdapter(dataAdapter);
        complaintType.setSelection(list.indexOf(getIntent().getStringExtra("handyman")));

        mDate = Calendar.getInstance();
        day = mDate.get(Calendar.DAY_OF_MONTH);
        month = mDate.get(Calendar.MONTH);
        year = mDate.get(Calendar.YEAR);
        hour = mDate.get(Calendar.HOUR);
        minute = mDate.get(Calendar.MINUTE);

        dateTextView.setText(getIntent().getStringExtra("dateOfcomplaint"));
        pickUpDate.setText(getIntent().getStringExtra("datePref1"));
        pickUpTime.setText(getIntent().getStringExtra("timePref1"));
        description.setText(getIntent().getStringExtra("describe"));
        pickUpTime.setOnClickListener(this);
        pickUpDate.setOnClickListener(this);
        findViewById(R.id.updateButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pickDateButton:
                DatePickerDialog datePickerDialog = new DatePickerDialog(updateComplaint.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        pickUpDate.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
                break;

            case R.id.pickTimeButton:
                TimePickerDialog timePickerDialog = new TimePickerDialog(updateComplaint.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay == 0) {
                            hourOfDay += 12;
                            format = "AM";
                        } else if (hourOfDay == 12) {
                            format = "PM";
                        } else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            format = "PM";
                        } else {
                            format = "AM";
                        }
                        pickUpTime.setText(hourOfDay + ":" + String.format("%02d", minute) + " " + format);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
                break;

            case R.id.updateButton:
                updateComplaint();
                break;

            case R.id.cancelButton:
                startActivity(new Intent(this, tracker.class));
                break;
        }
    }

    private void updateComplaint() {
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        dateTextView.setText(currentDateTimeString);

        String dateOfComplaint = dateTextView.getText().toString().trim();
        String handyman = complaintType.getSelectedItem().toString().trim();
        String datePref1 = pickUpDate.getText().toString().trim();
        String timePref1 = pickUpTime.getText().toString().trim();
        String describe = description.getText().toString().trim();
        String status = getIntent().getStringExtra("status");
        String complaintId = getIntent().getStringExtra("complaintId");
        String studentId = getIntent().getStringExtra("studentId");
        String hostel = getIntent().getStringExtra("hostel");
        String room = getIntent().getStringExtra("room");

        String timeHour[] = timePref1.split(":");
        int firstHour = Integer.parseInt(timeHour[0]);

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("dd/M/yyyy").parse(datePref1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (TextUtils.isEmpty(describe)) {
            Toast.makeText(this, "The Description should not be Empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (datePref1.equals("Pick Date")) {
            Toast.makeText(this, "Select a preferred Date", Toast.LENGTH_SHORT).show();
            return;
        } else if (dayOfWeek == 1 || dayOfWeek == 7) {
            Toast.makeText(this, "Oops!! It's Weekend :( Select another Date.", Toast.LENGTH_SHORT).show();
            return;
        } else if (timePref1.equals("Pick Time")) {
            Toast.makeText(this, "Select a preferred Time", Toast.LENGTH_SHORT).show();
            return;
        } else if ((timePref1.indexOf("AM") != -1 && firstHour < 9) || (timePref1.indexOf("PM") != -1 && firstHour > 5)) {
            Toast.makeText(this, "Oops!! We can't provide service at that time :(", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(describe)) {
            Toast.makeText(this, "The Description should not be Empty", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (getIntent().getStringExtra("handyman").equals(handyman)) {
                newComplaint complaint = new newComplaint(dateOfComplaint, handyman, datePref1, timePref1, describe, status, complaintId, studentId, hostel, room, datePref1 + "_" + timePref1);
                databaseReference.child(handyman).child(getIntent().getStringExtra("complaintId")).setValue(complaint);
            } else {
                databaseReference.child(getIntent().getStringExtra("handyman")).child(getIntent().getStringExtra("complaintId")).removeValue();
                newComplaint complaint = new newComplaint(dateOfComplaint, handyman, datePref1, timePref1, describe, status, complaintId, studentId, hostel, room, datePref1 + "_" + timePref1);
                databaseReference.child(handyman).child(getIntent().getStringExtra("complaintId")).setValue(complaint);
            }
            Toast.makeText(this, "Complaint Updated Sucessfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, tracker.class));
        }
    }

}
