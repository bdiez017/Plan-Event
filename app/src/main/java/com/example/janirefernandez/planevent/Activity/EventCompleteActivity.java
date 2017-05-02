package com.example.janirefernandez.planevent.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.janirefernandez.planevent.Helper.FunctionsHelper;
import com.example.janirefernandez.planevent.Helper.SQLiteEvents;
import com.example.janirefernandez.planevent.R;
import com.google.api.services.calendar.Calendar;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by JanireFernandez on 04/04/2017.
 */

public class EventCompleteActivity extends Activity {

    private TextView titleEventComplete;
    private TextView descriptionEventComplete;
    private TextView locationEventComplete;
    private TextView dateEventComplete;
    private TextView tagEventComplete;
    private Button btnFollow;
    private SQLiteEvents db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_complete);

        titleEventComplete = (TextView) findViewById(R.id.titleEventComplete);
        descriptionEventComplete = (TextView) findViewById(R.id.descriptionEventComplete);
        tagEventComplete = (TextView) findViewById(R.id.tagEventComplete);
        locationEventComplete = (TextView) findViewById(R.id.locationEventComplete);
        dateEventComplete = (TextView) findViewById(R.id.dateEventComplete);
        btnFollow = (Button) findViewById(R.id.btnFollow);

        titleEventComplete.setText(getIntent().getExtras().getString("titleListEvent"));
        dateEventComplete.setText(getIntent().getExtras().getString("dateListEvent"));
        locationEventComplete.setText(getIntent().getExtras().getString("locationListEvent"));
        tagEventComplete.setText(getIntent().getExtras().getString("tagListEvent"));
        descriptionEventComplete.setText(getIntent().getExtras().getString("descriptionListEvent"));


        // SQLite database handler
        db = new SQLiteEvents(getApplicationContext(), new FunctionsHelper().datebaseName(getApplicationContext()));


        btnFollow.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                //Store the event in the SQLite
                String title = titleEventComplete.getText().toString();
                String date = dateEventComplete.getText().toString();
                String place = locationEventComplete.getText().toString();
                String tag = tagEventComplete.getText().toString();
                String description = descriptionEventComplete.getText().toString();
                String unique_id = getIntent().getExtras().getString("idListEvent");

                createEventInCalendar(date, title, description, place);

                //inserting row in events table
                //db.deleteEvents();
                db.addEvent(title, place, date, description, tag, unique_id);

            }

        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),
                EventsFoundByPlace.class);
        startActivity(i);
        finish();
    }

    private void createEventInCalendar(String date, String title, String desc, String place) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, desc);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, place);
        startActivity(intent);
        
    }


}
