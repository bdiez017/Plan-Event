package com.example.janirefernandez.planevent.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JanireFernandez on 06/04/2017.
 */

public class ArrayEvent {

    private List<Event> arrayEvent = new ArrayList<Event>();


    public List<Event> getArrayEvents()
    {
        if(arrayEvent == null)
        {
            arrayEvent = new ArrayList<Event>();
        }

        return arrayEvent;
    }

    public void addArrayEvents(Event e)
    {
        arrayEvent.add(e);
    }

}
