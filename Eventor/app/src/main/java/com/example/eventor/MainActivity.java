package com.example.eventor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    //firebase authentication variable
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    private UserFirebaseHelper userFirebaseHelper;



    //floating button for creat new event
    private FloatingActionButton fabCreateNewEvent;


    private ArrayAdapter eventsAdapter;
    private EventsListAdapter eventsListAdapter;
    private ArrayList<Event> eventsList;
    private ListView eventsListView;


    private Toolbar toolbarMenu;

    Query getEventsQuery;
    Query getEvent;
    private String userName;
    private String phoneNumber;

// Variable to save amount of contacts
public static final int REQUEST_READ_CONTACTS = 79;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase authentication
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    protected void onStart() {
        super.onStart();

        //get the current usee
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            //if current user not exist - got to SignInActivity
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        } else {
            //if the current user exist - stay here in MainActivity
            //TODO: get user details
            phoneNumber = currentUser.getPhoneNumber();
            userFirebaseHelper = new UserFirebaseHelper(phoneNumber);

            //ListView controller for exists events
            eventsListView = (ListView) findViewById(R.id.list_view_events);

            //list for exists events
            eventsList = new ArrayList();
            getListEvents(phoneNumber);

            //custom adapter for eventsListEview
            eventsListAdapter = new EventsListAdapter(MainActivity.this, eventsList);
            eventsListAdapter.setNotifyOnChange(true);

            //connection between listview to adapter
            eventsListView.setAdapter(eventsListAdapter);
            eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    openChosenEvent(position);
                    //finish();
                }
            });

            //toolbar controller
            toolbarMenu = (Toolbar) findViewById(R.id.toolbar_menu);
            setSupportActionBar(toolbarMenu);

            //create new event controller
            fabCreateNewEvent = (FloatingActionButton) findViewById(R.id.fab);
            fabCreateNewEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createNewEvent();
                }
            });
        }


    }


    /**
     * This method get from firebase DB the list of exists events for this current user
     */
    private void getListEvents(final String phoneNumber){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        getEventsQuery = databaseReference.child("Users").child(phoneNumber).child("userEventsList");
        getEventsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //final Map<String, Boolean> eventsIdMap = new HashMap<>();
                final ArrayList<String> eventsId = new ArrayList<>();
                final boolean isManager = false;
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    eventsId.add(data.getKey());
                    //eventsIdMap.put(data.getKey(), data.getValue(Boolean.class));
                }
                getEvent = databaseReference.child("Events");
                getEvent.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        eventsList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            String id = data.getKey();
                            if (eventsId.contains(id)){
                                Event event = data.getValue(Event.class);
                                eventsList.add(event);
                            }
                        }
                        refreshListEvent();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }



    /**
     * This method pass the user to CreateEventActivity for create new event
     */
    public void createNewEvent(){
        Intent newEventIntent = new Intent(MainActivity.this, CreateEventActivity.class);
        startActivity(newEventIntent);
    }

    /**
     * This method open chosen event
     */
    private void openChosenEvent(int position){
        Event currentEvent = eventsList.get(position);
        Intent currentEventIntent = new Intent(MainActivity.this, EventActivity.class);
        currentEventIntent.putExtra("phoneNumber", phoneNumber);
        currentEventIntent.putExtra("isManager", true);
        //currentEventIntent.putExtra("userName", userName);
        currentEventIntent.putExtra("currentEvent", currentEvent);
        startActivity(currentEventIntent);
    }


    /**
     * This method refresh the list events
     */
    private void refreshListEvent(){
        //custom adapter for eventsListView
        eventsListAdapter = new EventsListAdapter(MainActivity.this, eventsList);
        //connection between listview to adapter
        eventsListView.setAdapter(eventsListAdapter);
    }



    //toolbar controller with logout button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_item:
                mAuth.signOut();
                onStart();
        }
        return super.onOptionsItemSelected(item);
    }




}