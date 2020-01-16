package com.example.eventor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.navigation.NavigationView;
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

import static com.example.eventor.SignInActivity.SIGN_IN_PREF;


public class MainActivity extends AppCompatActivity {


    //firebase authentication variable
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    private UserFirebaseHelper userFirebaseHelper;



    //floating button for creat new event
    private FloatingActionButton fabCreateNewEvent;


    private Map<String, Boolean> eventsIdMap;


    private EventsListAdapter eventsListAdapter;
    private ArrayList<Event> eventsList;
    private ListView eventsListView;



    //private Toolbar toolbarMenu;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private Query getEventsQuery;
    private Query getEvent;
    private String userName;
    private String phoneNumber;


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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

        //get the current user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            //if current user not exist - got to SignInActivity
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        } else {
            //if the current user exist - stay here in MainActivity
            //TODO: get user details
            phoneNumber = currentUser.getPhoneNumber();
            userFirebaseHelper = new UserFirebaseHelper(/*phoneNumber*/);

            //get phoneNumber and userName from the device
            SharedPreferences sharedPref = getSharedPreferences(SIGN_IN_PREF, Context.MODE_PRIVATE);
            String phone = sharedPref.getString(getString(R.string.save_phone_number), phoneNumber);
            userName = sharedPref.getString(getString(R.string.save_user_name), phoneNumber);


            //ListView controller for exists events
            eventsListView = (ListView) findViewById(R.id.list_view_events);

            //list for exists events
            eventsIdMap = new HashMap<>();

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
            /*toolbarMenu = (Toolbar) findViewById(R.id.toolbar_menu);
            setSupportActionBar(toolbarMenu);*/
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            drawerLayout = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_logout:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            mAuth.signOut();
                            onStart();
                            break;
                    }
                    return true;
                }
            });



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
                eventsIdMap = new HashMap<>();
                //final boolean isManager = false;
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    eventsIdMap.put(data.getKey(), data.getValue(Boolean.class));
                }
                getEvent = databaseReference.child("Events");
                getEvent.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        eventsList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            String id = data.getKey();
                            if (eventsIdMap.containsKey(id)){
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
        currentEventIntent.putExtra(getString(R.string.intent_phone_number), phoneNumber);
        currentEventIntent.putExtra(getString(R.string.intent_user_name), userName);
        currentEventIntent.putExtra(getString(R.string.intent_is_manager), eventsIdMap.get(currentEvent.getId()));
        currentEventIntent.putExtra(getString(R.string.intent_current_event), currentEvent);

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
    /*@Override
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
    }*/



}