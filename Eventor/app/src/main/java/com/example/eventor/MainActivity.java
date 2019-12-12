package com.example.eventor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {



    private FirebaseAuth mAuth;
    private FloatingActionButton fab;
    private Button logoutButton;


    private ArrayAdapter eventsAdapter;
    private ArrayList eventsList;
    private ListView eventsListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        } else {

            Bundle bundle = getIntent().getExtras();
            //String userName = bundle.getString("userName");
            String phoneNumber = currentUser.getPhoneNumber();
            //Toast.makeText(MainActivity.this, userName, Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, phoneNumber, Toast.LENGTH_LONG).show();

            eventsListView = (ListView) findViewById(R.id.list_view_events);
            eventsList = getListEvents();
            eventsAdapter = new ArrayAdapter(MainActivity.this, R.layout.item_exist_event, R.id.name_event, eventsList);
            eventsListView.setAdapter(eventsAdapter);
            eventsAdapter.setNotifyOnChange(true);


            eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this,String.valueOf(position), Toast.LENGTH_LONG).show();
                    openThisEvent();
                }
            });



            logoutButton = (Button) findViewById(R.id.button_logout);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    onStart();
                }
            });


            logoutButton = (Button) findViewById(R.id.button_logout);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    onStart();
                }
            });

            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    startActivity(new Intent(MainActivity.this, CreateEventActivity.class));
                }
            });
        }


    }




    private ArrayList getListEvents(){
        String [] eventsNames = {"Birthday", "BBQ", "Valentine's day", "Hnucka", "Birthday", "BBQ", "Valentine's day", "Hnucka", "Birthday", "BBQ"};
        return new ArrayList(Arrays.asList(eventsNames));
        //return null;
    }

    private void openThisEvent(){
        startActivity(new Intent(MainActivity.this, EventActivity.class));
    }


}