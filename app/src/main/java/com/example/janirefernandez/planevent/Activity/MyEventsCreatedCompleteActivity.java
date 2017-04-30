package com.example.janirefernandez.planevent.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.janirefernandez.planevent.App.AppConfig;
import com.example.janirefernandez.planevent.App.AppController;
import com.example.janirefernandez.planevent.Helper.FunctionsHelper;
import com.example.janirefernandez.planevent.Helper.SQLiteEvents;
import com.example.janirefernandez.planevent.Helper.SQLiteEventsCreated;
import com.example.janirefernandez.planevent.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JanireFernandez on 24/04/2017.
 */

public class MyEventsCreatedCompleteActivity  extends Activity {

    private TextView titleEventComplete;
    private TextView descriptionEventComplete;
    private TextView locationEventComplete;
    private TextView dateEventComplete;
    private TextView tagEventComplete;
    private Button btnDelete;
    private SQLiteEventsCreated db;
    private ProgressDialog pDialog;
    private static final String TAG = MyEventsCreatedActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myevents_created_complete);

        titleEventComplete = (TextView) findViewById(R.id.titleMyEventComplete);
        descriptionEventComplete = (TextView) findViewById(R.id.descriptionMyEventComplete);
        tagEventComplete = (TextView) findViewById(R.id.tagMyEventComplete);
        locationEventComplete = (TextView) findViewById(R.id.locationMyEventComplete);
        dateEventComplete= (TextView) findViewById(R.id.dateMyEventComplete);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        titleEventComplete.setText(getIntent().getExtras().getString("titleListEvent"));
        dateEventComplete.setText(getIntent().getExtras().getString("dateListEvent"));
        locationEventComplete.setText(getIntent().getExtras().getString("locationListEvent"));
        tagEventComplete.setText(getIntent().getExtras().getString("tagListEvent"));
        descriptionEventComplete.setText(getIntent().getExtras().getString("descriptionListEvent"));


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteEventsCreated(getApplicationContext(),new FunctionsHelper().datebaseName(getApplicationContext()));
        btnDelete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                //Store the event in the SQLite
                String title = titleEventComplete.getText().toString();

                //llamada base de datos externa

                deleteEvent(title);

                //deleting row in events table
                db.deleteEvent(title);
                Intent i = new Intent(getApplicationContext(),MyEventsCreatedActivity.class);
                startActivity(i);
                finish();


            }

        });
    }



    @Override
    public void onBackPressed(){
        Intent i = new Intent(getApplicationContext(),
                MyEventsCreatedActivity.class);
        startActivity(i);
        finish();
    }

    private void deleteEvent(final String title ) {

        // Tag used to cancel the request
        String tag_string_cre = "req_delete_event";

        pDialog.setMessage("Deleting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_DELETE_EVENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Create Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getApplicationContext(), getText(R.string.eventDeletedSuccesfully), Toast.LENGTH_LONG).show();
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Create event Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to create event
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", title);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_cre);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
