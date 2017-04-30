package com.example.janirefernandez.planevent.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.janirefernandez.planevent.Helper.FunctionsHelper;
import com.example.janirefernandez.planevent.Helper.SQLiteEvents;
import com.example.janirefernandez.planevent.R;

/**
 * Created by JanireFernandez on 24/04/2017.
 */

public class FollowedEventCompleteActivity extends Activity {

    private TextView titleEventComplete;
    private TextView descriptionEventComplete;
    private TextView locationEventComplete;
    private TextView dateEventComplete;
    private TextView tagEventComplete;
    private Button btnUnfollow;
    private SQLiteEvents db;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followed_event_complete);

        titleEventComplete = (TextView) findViewById(R.id.titleFollowEventComplete);
        descriptionEventComplete = (TextView) findViewById(R.id.descriptionFollowEventComplete);
        tagEventComplete = (TextView) findViewById(R.id.tagFollowEventComplete);
        locationEventComplete = (TextView) findViewById(R.id.locationFollowEventComplete);
        dateEventComplete= (TextView) findViewById(R.id.dateFollowEventComplete);
        btnUnfollow = (Button) findViewById(R.id.btnUnfollow);

        titleEventComplete.setText(getIntent().getExtras().getString("titleListEvent"));
        dateEventComplete.setText(getIntent().getExtras().getString("dateListEvent"));
        locationEventComplete.setText(getIntent().getExtras().getString("locationListEvent"));
        tagEventComplete.setText(getIntent().getExtras().getString("tagListEvent"));
        descriptionEventComplete.setText(getIntent().getExtras().getString("descriptionListEvent"));


        // SQLite database handler
        db = new SQLiteEvents(getApplicationContext(),new FunctionsHelper().datebaseName(getApplicationContext()));
        btnUnfollow.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                //Store the event in the SQLite
                String title = titleEventComplete.getText().toString();
                //deleting row in events table
                db.deleteEvent(title);
                Intent i = new Intent(getApplicationContext(),FollowedEventsActivity.class);
                startActivity(i);
                finish();


            }

        });
    }



    @Override
    public void onBackPressed(){
        Intent i = new Intent(getApplicationContext(),
                FollowedEventsActivity.class);
        startActivity(i);
        finish();
    }
}
