package com.example.janirefernandez.planevent.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.janirefernandez.planevent.Helper.SQLiteHandlerUsers;
import com.example.janirefernandez.planevent.Helper.SessionManager;
import com.example.janirefernandez.planevent.R;

import java.util.HashMap;



public class MainActivity extends Activity {

    private Button btnLogout;
    private Button btnCreateEvent;
    private Button btnSearchEvent;
    private Button btnCalendar;
    private Button btnFollowedEvents;
    private Button btnMyEventsCreated;


    private SQLiteHandlerUsers db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnCreateEvent = (Button) findViewById(R.id.btnCreateEvent);
        btnSearchEvent = (Button) findViewById(R.id.btnSearchEvent);
        btnCalendar = (Button) findViewById(R.id.btnCalendar);
        btnFollowedEvents = (Button) findViewById(R.id.btnFollowedEvents);
        btnMyEventsCreated = (Button) findViewById(R.id.btnMyEventsCreated);
        // SqLite database handler
        db = new SQLiteHandlerUsers(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        // Create Event Button Click event
        btnCreateEvent.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),CreateEventActivity.class);
                startActivity(i);
                finish();
            }

        });

        // Search Events Button Click event
        btnCalendar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),CalendarActivity.class);
                startActivity(i);
                finish();
            }

        });

        btnSearchEvent.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),SearchEventsActivity.class);
                startActivity(i);
                finish();
            }

        });


        btnMyEventsCreated.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MyEventsCreatedActivity.class);
                startActivity(i);
                finish();
            }

        });


        // Logout Button Click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
               logoutUser();
            }

        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
