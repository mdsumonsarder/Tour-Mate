package com.dualbrotech.tourmate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dualbrotech.tourmate.R;

import com.dualbrotech.tourmate.Models.Event;

import java.util.ArrayList;

/**
 * Created by Arif Rahman on 1/23/2018.
 */

public class EventAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Event> events;

    public EventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.single_event_item,parent,false);

        TextView tv_eventName = view.findViewById(R.id.tv_eventName);
        TextView tv_eventDate = view.findViewById(R.id.tv_eventDate);
        TextView tv_eventBudget = view.findViewById(R.id.tv_expenseAmount);
        TextView tv_rank = view.findViewById(R.id.expenseRankTV);

        tv_eventName.setText(events.get(position).getEventName());
        tv_rank.setText(String.valueOf(position+1));
        tv_eventDate.setText(events.get(position).getEventDate());
        tv_eventBudget.setText(events.get(position).getEventBudget());

        return view;
    }
}
