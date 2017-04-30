package com.example.janirefernandez.planevent.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.janirefernandez.planevent.Helper.Event;
import com.example.janirefernandez.planevent.Helper.EventAdapterEventSearch;
import com.example.janirefernandez.planevent.Helper.FunctionsHelper;
import com.example.janirefernandez.planevent.Helper.SQLiteEvents;
import com.example.janirefernandez.planevent.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JanireFernandez on 06/04/2017.
 */

public class FollowedEventsActivity extends Activity {

    private ListView mListView;
    private SQLiteEvents db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_events);

        mListView = (ListView) findViewById(R.id.listViewEvents);

        // SQLite database handler
        db = new SQLiteEvents(getApplicationContext(),new FunctionsHelper().datebaseName(getApplicationContext()));
        List<Event> events =  db.getEventTableDetails();

        ArrayList<String> titleList = new ArrayList<String>();
        ArrayList<String> tagList = new ArrayList<String>();
        ArrayList<String> dateList = new ArrayList<String>();
        ArrayList<String> locationList = new ArrayList<String>();
        ArrayList<String> descriptionList = new ArrayList<String>();
        ArrayList<String> idList = new ArrayList<String>();


        for(int i = 0; i < events.size(); i++)
        {
            titleList.add(events.get(i).getTitle());
            tagList.add(events.get(i).getTag());
            dateList.add(events.get(i).getDate());
            descriptionList.add(events.get(i).getDescription());
            locationList.add(events.get(i).getPlace());
            idList.add(events.get(i).getUnique_id());
        }

        final String[] titleArray = titleList.toArray(new String[titleList.size()]);
        final String[] tagArray = tagList.toArray(new String[tagList.size()]);
        final String[] dateArray = dateList.toArray(new String[dateList.size()]);
        final String[] locationArray = locationList.toArray(new String[locationList.size()]);
        final String[] descriptionArray = descriptionList.toArray(new String[descriptionList.size()]);
        final String[] idArray = idList.toArray(new String[idList.size()]);


        EventAdapterEventSearch customAdapter = new  EventAdapterEventSearch(getApplicationContext(),titleArray,tagArray, dateArray, locationArray);
        mListView.setAdapter(customAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

               Intent intent = new Intent(FollowedEventsActivity.this, FollowedEventCompleteActivity.class);
                //Intent intent = new Intent(FollowedEventsActivity.this, EventCompleteActivity.class);
                intent.putExtra("titleListEvent", titleArray[position]);
                intent.putExtra("dateListEvent", dateArray[position]);
                intent.putExtra("locationListEvent", locationArray[position]);
                intent.putExtra("tagListEvent", tagArray[position]);
                intent.putExtra("descriptionListEvent",descriptionArray[position]);
                intent.putExtra("idListEvent",idArray[position]);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(i);
        finish();
    }

}
