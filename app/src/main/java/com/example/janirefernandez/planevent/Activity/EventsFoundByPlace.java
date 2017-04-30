package com.example.janirefernandez.planevent.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import com.example.janirefernandez.planevent.Helper.Event;
import com.example.janirefernandez.planevent.Helper.EventAdapterEventSearch;
import com.example.janirefernandez.planevent.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class EventsFoundByPlace extends Activity {

    private Button btnLinkToSearch;
    private ViewGroup layout;
    private ScrollView scrollView;
    private static String var;

    private ListView mListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_events);

        mListView = (ListView) findViewById(R.id.listViewEvents);
        String events = null;
        try{
             events = getIntent().getExtras().getString("eventsString"); //Other activity gives me this
        }
       catch(Exception e)
       {
           if(events==null)
               events =var;
       }
       var=events;
        try {
            JSONArray newJArray = new JSONArray(events);

            ArrayList<String> arrayList = new ArrayList<String>();
            ArrayList<Event> eventList = new ArrayList<>();
            String jsonObject;


            ArrayList<String> titleList = new ArrayList<String>();
            ArrayList<String> tagList = new ArrayList<String>();
            ArrayList<String> dateList = new ArrayList<String>();
            ArrayList<String> locationList = new ArrayList<String>();
            ArrayList<String> descriptionList = new ArrayList<String>();
            ArrayList<String> idList = new ArrayList<String>();

            for(int i = 0, count = newJArray.length(); i< count; i++) {

                jsonObject = newJArray.getJSONObject(i).getString("title");
                titleList.add(jsonObject.toString());
                jsonObject = newJArray.getJSONObject(i).getString("tag");
                tagList.add(jsonObject.toString());
                jsonObject = newJArray.getJSONObject(i).getString("date");
                dateList.add(jsonObject.toString());
                jsonObject = newJArray.getJSONObject(i).getString("description");
                descriptionList.add(jsonObject.toString());
                jsonObject = newJArray.getJSONObject(i).getString("place");
                locationList.add(jsonObject.toString());
                jsonObject = newJArray.getJSONObject(i).getString("unique_id");
                idList.add(jsonObject.toString());
            }


            final String[] titleArray = titleList.toArray(new String[titleList.size()]);
            final String[] tagArray = tagList.toArray(new String[tagList.size()]);
            final String[] dateArray = dateList.toArray(new String[dateList.size()]);
            final String[] locationArray = locationList.toArray(new String[locationList.size()]);
            final String[] descriptionArray = descriptionList.toArray(new String[descriptionList.size()]);
            final String[] idArray = idList.toArray(new String[idList.size()]);

            EventAdapterEventSearch customAdapter = new EventAdapterEventSearch(getApplicationContext(),titleArray,tagArray, dateArray, locationArray);
            mListView.setAdapter(customAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    Intent intent = new Intent(EventsFoundByPlace.this, EventCompleteActivity.class);
                    intent.putExtra("titleListEvent", titleArray[position]);
                    intent.putExtra("dateListEvent", dateArray[position]);
                    intent.putExtra("locationListEvent", locationArray[position]);
                    intent.putExtra("tagListEvent", tagArray[position]);
                    intent.putExtra("descriptionListEvent",descriptionArray[position]);
                    intent.putExtra("idListEvent",idArray[position]);
                    startActivity(intent);

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(getApplicationContext(),
                SearchEventsActivity.class);
        startActivity(i);
        finish();
    }
}
