package com.example.eventor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;


//custom ArrayAdapter for exists events
public class EventsListAdapter extends ArrayAdapter<Event> {

    private Context context;
    private ArrayList<Event> events;
    private LayoutInflater inflater;



    //constructor
    public EventsListAdapter(@NonNull Context context, @NonNull ArrayList events) {
        //super(context, resource, objects);
        super(context, R.layout.item_exist_event, events);
        this.context = context;
        this.events = events;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View eventRow = convertView;
        if (eventRow == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            eventRow = inflater.inflate(R.layout.item_exist_event, parent, false);
        }

        TextView nameEventTextView = (TextView) eventRow.findViewById(R.id.name_event);
        TextView locationEventTextView = (TextView) eventRow.findViewById(R.id.location_event);
        TextView dateEventTextView = (TextView) eventRow.findViewById(R.id.date_event);

        Event event = events.get(position);
        nameEventTextView.setText(event.getName());
        locationEventTextView.setText(event.getLocation());
        dateEventTextView.setText(event.getDate());


        return eventRow;
    }
}
