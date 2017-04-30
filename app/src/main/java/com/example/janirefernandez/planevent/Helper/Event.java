package com.example.janirefernandez.planevent.Helper;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by JanireFernandez on 03/04/2017.
 */

public class Event {

    private String title;
    private String place;
    private String  date;
    private String description;
    private String tag;
    private String unique_id;


    public void setTitle(String title)
    {
        this.title=title;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setPlace(String place)
    {
        this.place=place;
    }
    public String getPlace()
    {
        return place;
    }

    public void setDescription(String description)
    {
        this.description=description;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDate(String date)
    {
        this.date=date;
    }

    public String getDate()
    {
        return this.date;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public String getTag()
    {
        return  this.tag;
    }

    public void setUnique_id(String unique_id){
        this.unique_id = unique_id;
    }

    public String getUnique_id()
    {
        return unique_id;
    }
}
