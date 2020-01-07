package com.example.eventor;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


//custom ArrayAdapter for exists events
public class EventsListAdapter extends ArrayAdapter<Event> {

    private Context context;
    private ArrayList<Event> events;
    private LayoutInflater inflater;
    private ImageView eventImageView;


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
        if (eventRow == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            eventRow = inflater.inflate(R.layout.item_exist_event, parent, false);
        }
        if (events.get(position).getHasImage()) {
            eventImageView = eventRow.findViewById(R.id.event_image_view);
            getImage(events.get(position).getId(), eventImageView);
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


    private void getImage(String eventId, final ImageView imageEvent) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageEventReference = storageReference.child("Images").child("EventImages").child(eventId + ".png");
        imageEventReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri.toString()).into(imageEvent);
            }
        });
    }




}
