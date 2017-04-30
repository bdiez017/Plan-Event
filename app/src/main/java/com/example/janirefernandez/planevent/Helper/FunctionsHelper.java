package com.example.janirefernandez.planevent.Helper;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JanireFernandez on 27/03/2017.
 */

public class FunctionsHelper {


    public boolean checkDate (String date) {

        boolean check = false;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            String dateSplt[] = date.split("-");

            if(dateSplt[0].length() == 4 && dateSplt[1].length() == 2 && dateSplt[2].length() ==2){
                check = true;
            }else{
                check = false;
            }

        } catch (ParseException e) {
            check = false;
        }
        return check;
    }

    public boolean checkMail(String email) {

        boolean check = false;

        Pattern patronEmail = Pattern.compile("^[_a-z0-9-]+(.[_a-z0-9-]+)*@[a-z0-9-]+(.[a-z0-9-]+)*(.[a-z]{2,4})$");
        //Pattern patronEmail = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
        Matcher mEmail = patronEmail.matcher(email.toLowerCase());
        if (mEmail.matches()){
            check = true;
        }
        return check;
    }


    public String datebaseName(Context context)
    {
        SQLiteHandlerUsers db = new SQLiteHandlerUsers(context);
        HashMap<String, String> database_name=  db.getUserDetails();
        return database_name.get("email").toString();
    }


}
