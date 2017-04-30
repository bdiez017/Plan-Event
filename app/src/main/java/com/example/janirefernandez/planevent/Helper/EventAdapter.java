package com.example.janirefernandez.planevent.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.janirefernandez.planevent.R;

import org.w3c.dom.Text;

/**
 * Created by JanireFernandez on 03/04/2017.
 */

public class EventAdapter extends BaseAdapter {

    Context context;
    String titleList[];
    String tagList[];
    String dateList[];
    int flags[];
    LayoutInflater inflter;

    public EventAdapter(Context applicationContext, String[] titleList, String[] tagList, String[] dateList) {
        this.context = context;
        this.titleList = titleList;
        this.tagList = tagList;
        this.dateList = dateList;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return titleList.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_event_view, null);
        TextView titleList = (TextView)view.findViewById(R.id.titleListEvent);
        TextView tagList = (TextView)view.findViewById(R.id.tagListEvent);
        TextView dateList = (TextView)view.findViewById(R.id.dateListEvent);

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        titleList.setText(this.titleList[i]);
        tagList.setText(this.tagList[i]);
        dateList.setText(this.dateList[i]);
//        icon.setImageResource(flags[i]);

        /**/
        return view;
    }


}
