package com.example.janirefernandez.planevent.Activity;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.janirefernandez.planevent.App.AppConfig;
import com.example.janirefernandez.planevent.App.AppController;
import com.example.janirefernandez.planevent.Helper.FunctionsHelper;
import com.example.janirefernandez.planevent.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.Subject;

public class SearchEventsActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private static final String TAG = SearchEventsActivity.class.getSimpleName();
    private Button btnSearch;
    private Button btnLinkToMain;
    private ImageButton btnLocation;
    private EditText inputPlace;
    private EditText inputDate;
    private ProgressDialog pDialog;
    private Spinner tagSpinner;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private boolean permissionIsGranted = false;

    private String placeLocation;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_event);

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);


        inputPlace = (EditText) findViewById(R.id.placeEventSearch);
        inputDate = (EditText) findViewById(R.id.dateEventSearch);
        btnSearch = (Button) findViewById(R.id.btnSearchByPlace);
        btnLinkToMain = (Button) findViewById(R.id.btnLinkToMainScreenAfterSearch);
        btnLocation = (ImageButton) findViewById(R.id.locationButtonSearch);


        //changing theme of the spinner
        tagSpinner = (Spinner) findViewById(R.id.tagSpinner);
        ArrayAdapter<String> spinnerTagArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.tag_spinner_background, getResources().getStringArray(R.array.tag));
        spinnerTagArrayAdapter.setDropDownViewResource(R.layout.tag_spinner_background);
        tagSpinner.setAdapter(spinnerTagArrayAdapter);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Button to search events
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String place = inputPlace.getText().toString().trim();
                String date = inputDate.getText().toString().trim();
                String tag = tagSpinner.getSelectedItem().toString();

                if (!date.isEmpty() || !place.isEmpty() || !tag.isEmpty()) {

                    if (!date.isEmpty()) {
                        if (new FunctionsHelper().checkDate(date)) {
                            searchEvents(place, date, tag);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getText(R.string.dateNotValid), Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        searchEvents(place, date, tag);
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            getText(R.string.enterDetailsSearch), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


        //ImageButton to set Location
        btnLocation.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                inputPlace.setText(placeLocation);

            }
        });

        // Link to Main Screen
        btnLinkToMain.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        //Code for DatePicker
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        findViewsById();
        setDateTimeField();

    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(i);
        finish();
    }

    private void searchEvents(final String place, final String date, final String tag) {

        // Tag used to cancel the request
        String tag_string_cre = "req_search_events";

        pDialog.setMessage(getText(R.string.searching));
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SEARCH_EVENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Response: " + response.toString());
                hideDialog();

                try {
                    //Create a JSON object so to have access to the attributes
                    JSONObject jObj = new JSONObject(response.toString());
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        String events= "";
                        JSONArray arrayj = jObj.getJSONArray("events");

                        for(int i=0; i< arrayj.length() ; i++)
                        {
                            events += "Title: "+arrayj.getJSONObject(i).getString("title") +"\t"
                                    +"Place: "+ arrayj.getJSONObject(i).getString("place") +"\t"
                                    +"Date: "+ arrayj.getJSONObject(i).getString("date") +"\t"
                                    +"Description: "+ arrayj.getJSONObject(i).getString("description") +"\n";
                        }

                        //Change to another layout
                        Intent intent = new Intent(SearchEventsActivity.this, EventsFoundByPlace.class);
                        intent.putExtra("events",events);
                        /*---------------------------------------------------------------------------------------------------*/
                        String arrayList = arrayj.toString();
                        intent.putExtra("eventsString",arrayList);
                        /*---------------------------------------------------------------------------------------------------*/

                        startActivity(intent);
                        finish();
                    } else {
                        // Error occurred in searching an event. Get the error
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
                Log.e(TAG, "Search event Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }

        }) {

            @Override
            protected Map<String, String> getParams() {

                //Posting params to Search event url
                Map<String, String> params = new HashMap<String, String>();
                params.put("place", place);
                params.put("date", date);
                params.put("tag", tag);
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


    //Location Code
    @Override
    protected void onStart() {
        super.onStart();
        //Connect the client
        mGoogleApiClient.connect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            } else {
                permissionIsGranted = true;
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public String getLocationName(double lattitude, double longitude) {

        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {

            List<Address> addresses = gcd.getFromLocation(lattitude, longitude, 10);

            for (Address adrs : addresses) {
                if (adrs != null) {

                    String city = adrs.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                        System.out.println("city ::  " + cityName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.toString());
        placeLocation = (getLocationName(location.getLatitude(), location.getLongitude()));

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApliCLient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApliCLient connection has failed");
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SearchEvents Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    //Funtions for DatePicker

    private void findViewsById() {
        inputDate = (EditText) findViewById(R.id.dateEventSearch);
        inputDate.setInputType(InputType.TYPE_NULL);
        inputDate.requestFocus();
    }

    private void setDateTimeField() {
        inputDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                inputDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }


    @Override
    public void onClick(View view) {
        if(view == inputDate) {
            fromDatePickerDialog.show();
        }
    }


}




