package com.example.shraddha.igram;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.Arrays;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import static com.example.shraddha.igram.R.array.complaint_type;

public class complaint extends AppCompatActivity implements View.OnClickListener {

    Calendar mDate;
    int day, month, year, hour, minute;
    String format;
    DatabaseReference databaseReference, databaseReferenceUsers;
    private TextView dateTextView;
    private EditText description;
    private Button pickUpDate, pickUpTime;
    private Spinner complaintType;
    private String hostel, room;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        dateTextView = (TextView) findViewById(R.id.dateTextView);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        dateTextView.setText(currentDateTimeString);


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

        pickUpDate.setOnClickListener(this);
        pickUpTime.setOnClickListener(this);
        findViewById(R.id.submitButton).setOnClickListener(this);

        mDate = Calendar.getInstance();
        day = mDate.get(Calendar.DAY_OF_MONTH);
        month = mDate.get(Calendar.MONTH);
        year = mDate.get(Calendar.YEAR);
        hour = mDate.get(Calendar.HOUR);
        minute = mDate.get(Calendar.MINUTE);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("complaint");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                registerInfo r = dataSnapshot.getValue(registerInfo.class);
                hostel = r.getHostel();
                room = r.getRoom();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pickDateButton:
                DatePickerDialog datePickerDialog = new DatePickerDialog(complaint.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        pickUpDate.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
                break;

            case R.id.pickTimeButton:
                TimePickerDialog timePickerDialog = new TimePickerDialog(complaint.this, new TimePickerDialog.OnTimeSetListener() {
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

            case R.id.submitButton:
                saveComplaint();
                break;
        }
    }

    private void saveComplaint() {
        String dateOfComplaint = dateTextView.getText().toString().trim();
        String handyman = complaintType.getSelectedItem().toString().trim();
        String datePref1 = pickUpDate.getText().toString().trim();
        String timePref1 = pickUpTime.getText().toString().trim();
        String describe = description.getText().toString().trim();

        String timeHour[] = timePref1.split(":");
        int firstHour = Integer.parseInt(timeHour[0]);
        //int firstMin = Integer.parseInt(timeHour[1]);

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("dd/M/yyyy").parse(datePref1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);


        if (datePref1.equals("Pick Date")) {
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
            String forOrder = datePref1 + "_" + timePref1;
            String id = databaseReference.push().getKey();
            newComplaint c = new newComplaint(dateOfComplaint, handyman, datePref1, timePref1, describe, "Pending", id, mAuth.getCurrentUser().getUid(), hostel, room, forOrder);
            databaseReference.child(handyman).child(id).setValue(c);
            databaseReference.orderByChild("timepref1");
            Toast.makeText(this, "Complaint Registered Sucessfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, tracker.class));
        }
    }
}
