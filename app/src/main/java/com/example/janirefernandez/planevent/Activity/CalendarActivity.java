package com.example.janirefernandez.planevent.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.desai.vatsal.mydynamiccalendar.MyDynamicCalendar;
import com.desai.vatsal.mydynamiccalendar.OnDateClickListener;

import com.desai.vatsal.mydynamiccalendar.OnEventClickListener;
import com.example.janirefernandez.planevent.Helper.Event;
import com.example.janirefernandez.planevent.Helper.FunctionsHelper;
import com.example.janirefernandez.planevent.Helper.SQLiteEvents;
import com.example.janirefernandez.planevent.R;


import java.util.Date;
import java.util.List;

public class CalendarActivity extends Activity {

    private Toolbar toolbar;
    private MyDynamicCalendar myCalendar;
    private SQLiteEvents db;
    private Button btnFollowedEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        btnFollowedEvents = (Button) findViewById(R.id.btnFollowedEvents);

        myCalendar = (MyDynamicCalendar) findViewById(R.id.myCalendar);

        myCalendar.showMonthView();

        myCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onClick(Date date) {
                Intent intent = new Intent(CalendarActivity.this, FollowedEventCompleteActivity.class);
            }

            @Override
            public void onLongClick(Date date) {
                Log.e("date", String.valueOf(date));
            }
        });

        //Customize calendar
        myCalendar.setCalendarBackgroundColor("#fbfbfb");
        myCalendar.setHeaderBackgroundColor("#ff6861");
        myCalendar.setHeaderTextColor("#ffffff");
        myCalendar.setNextPreviousIndicatorColor("#333333");
        myCalendar.setWeekDayLayoutBackgroundColor("#ff6861");
        myCalendar.setWeekDayLayoutTextColor("#fbfbfb");
        myCalendar.setExtraDatesOfMonthBackgroundColor("#5e6266");
        myCalendar.setExtraDatesOfMonthTextColor("#fbfbfb");
        myCalendar.setDatesOfMonthBackgroundColor("#999999");
        myCalendar.setDatesOfMonthTextColor("#fbfbfb");
        myCalendar.setCurrentDateTextColor("#bbff6861");
        myCalendar.setEventCellBackgroundColor("#a7f5e4");
        myCalendar.setEventCellTextColor("#333333");
        myCalendar.setBelowMonthEventTextColor("#425684");
        myCalendar.setBelowMonthEventDividerColor("#635478");
        myCalendar.setHolidayCellBackgroundColor("#654248");
        myCalendar.setHolidayCellTextColor("#d590bb");

        //Getting list of followed events
        db = new SQLiteEvents(getApplicationContext(), new FunctionsHelper().datebaseName(getApplicationContext()));
        List<Event> events = db.getEventTableDetails();

        //Adding Events to calendar
        
        myCalendar.deleteAllEvent();
        
        for (int i = 0; i < events.size(); i++) {
            String date = events.get(i).getDate();
            String year = date.substring(0, 4);
            String month = date.substring(5, 7);
            String day = date.substring(8, 10);
            String dateFormat = day + "-" + month + "-" + year;

            myCalendar.addEvent(dateFormat, "00:00", "00:00", events.get(i).getTitle());
        }

        // single event cell click listener
        myCalendar.setOnEventClickListener(new OnEventClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(CalendarActivity.this, FollowedEventCompleteActivity.class);
            }

            @Override
            public void onLongClick() {
                Intent intent = new Intent(CalendarActivity.this, FollowedEventCompleteActivity.class);
            }
        });


        btnFollowedEvents.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),FollowedEventsActivity.class);
                startActivity(i);
                finish();
            }

        });


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(i);
        finish();
    }


}
