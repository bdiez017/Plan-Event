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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.example.janirefernandez.planevent.Helper.FunctionsHelper;
import com.example.janirefernandez.planevent.Helper.Geolocation;
import com.example.janirefernandez.planevent.Helper.SQLiteEvents;
import com.example.janirefernandez.planevent.Helper.SQLiteEventsCreated;
import com.example.janirefernandez.planevent.Helper.SQLiteHandlerUsers;
import com.example.janirefernandez.planevent.Helper.SessionManager;
import com.example.janirefernandez.planevent.R;
import com.example.janirefernandez.planevent.App.AppConfig;
import com.example.janirefernandez.planevent.App.AppController;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.text.SimpleDateFormat;

public class CreateEventActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private static final String TAG = CreateEventActivity.class.getSimpleName();
    private Button btnCreate;
    private Button btnLinkToMain;
    private ImageButton btnLocation;
    private EditText inputTitle;
    private EditText inputPlace;
    private EditText inputDate;
    private EditText inputDescription;
    private Spinner tagSpinner;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandlerUsers db;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private boolean permissionIsGranted = false;

    private String placeLocation;
    private SQLiteEventsCreated dbEventsCreated;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Represent your location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);


        inputTitle = (EditText) findViewById(R.id.titleEvent);
        inputPlace = (EditText) findViewById(R.id.placeEvent);
        inputDate = (EditText) findViewById(R.id.dateEvent);
        inputDescription = (EditText) findViewById(R.id.descriptionEvent);
        btnLocation = (ImageButton) findViewById(R.id.locationButton);



        //changing theme of the spinner
        tagSpinner = (Spinner) findViewById(R.id.tagSpinnerCreateEvent);
        ArrayAdapter<String> spinnerTagArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.tag_spinner_background, getResources().getStringArray(R.array.tag));
        spinnerTagArrayAdapter.setDropDownViewResource(R.layout.tag_spinner_background);
        tagSpinner.setAdapter(spinnerTagArrayAdapter);


        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnLinkToMain = (Button) findViewById(R.id.btnLinkToMainScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler for users
        db = new SQLiteHandlerUsers(getApplicationContext());
        // SQLite database handler for events Created
        dbEventsCreated = new SQLiteEventsCreated(getApplicationContext(),new FunctionsHelper().datebaseName(getApplicationContext()));

        // Button to create an event
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = inputTitle.getText().toString().trim();
                String place = inputPlace.getText().toString().trim();
                String date = inputDate.getText().toString().trim();
                String description = inputDescription.getText().toString().trim();
                String tag = tagSpinner.getSelectedItem().toString();

                if (!title.isEmpty() && !place.isEmpty() && !date.isEmpty() && !description.isEmpty() && !tag.isEmpty()) {

                    if(new FunctionsHelper().checkDate(date)){
                        createEvent(title, place, date, description, tag);


                    }else{
                        Toast.makeText(getApplicationContext(),
                                getText(R.string.dateNotValid), Toast.LENGTH_LONG)
                                .show();
                    }

                } else {

                    if(title.isEmpty())
                        inputTitle.setError(getText(R.string.enter_title));
                    if(place.isEmpty())
                        inputPlace.setError(getText(R.string.enter_place));
                    if(date.isEmpty())
                        inputDate.setError(getText(R.string.enter_date));
                    if(description.isEmpty())
                        inputDescription.setError(getText(R.string.enter_description));

                    if(tag.isEmpty())
                    {
                        TextView errorText = (TextView)tagSpinner.getSelectedView();
                        errorText.setError("");
                    }
                    Toast.makeText(getApplicationContext(),
                            getText(R.string.enterAllDetails), Toast.LENGTH_LONG)
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

    private void createEvent(final String title, final String place,
                             final String date, final String description, final String tag) {

        // Tag used to cancel the request
        String tag_string_cre = "req_create_event";

        pDialog.setMessage("Creating ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_CREATE_EVENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Create Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getApplicationContext(), getText(R.string.eventCreatedSuccesfully), Toast.LENGTH_LONG).show();

                        //addEvent in  SQLite database for myCreatedEvent
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        dbEventsCreated.addEvent(title,place,date,description,tag,null);
                        // Launch main activity
                        Intent intent = new Intent(
                                CreateEventActivity.this,
                                MainActivity.class);
                        startActivity(intent);
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
                params.put("place", place);
                params.put("date", date);
                params.put("description", description);
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

    @Override
    protected void onStart() {
        super.onStart();
        //Connect the client
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
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
         placeLocation = getLocationName(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApliCLient connection has been suspend");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApliCLient connection has failed");
    }

    private void findViewsById() {
        inputDate = (EditText) findViewById(R.id.dateEvent);
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
