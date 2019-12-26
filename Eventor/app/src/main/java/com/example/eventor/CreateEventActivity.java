package com.example.eventor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.eventor.SignInActivity.SIGN_IN_PREF;

public class CreateEventActivity extends AppCompatActivity {


    private String userName;
    private String phoneNumber;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FloatingActionButton fabAddPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        phoneNumber = currentUser.getPhoneNumber();

        //get phoneNumber and userName from the device
        SharedPreferences sharedPref = getSharedPreferences(SIGN_IN_PREF, Context.MODE_PRIVATE);
        String phone = sharedPref.getString(getString(R.string.save_phone_number), phoneNumber);
        userName = sharedPref.getString(getString(R.string.save_user_name), phoneNumber);


        saveEvent();


        //button and listener for contacts list
        fabAddPerson = (FloatingActionButton) findViewById(R.id.fab_add_person);
        fabAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactList();
            }
        });
    }


    /**
     * This method open the contact list to create order list for the event.
     */
    private void openContactList(){

    }





    private void saveEvent(){

        Event event = new Event("Tea Time", "01-01-2020", "Ra'anana");
        event.addProduct("Tea", userName);
        event.addProduct("Milk");
        event.addProduct("Ice", "Rafi");
        EventFirebaseHelper eventFirebaseHelper = new EventFirebaseHelper();
        eventFirebaseHelper.insertNewEvent(event.getId(), event);

        UserFirebaseHelper userFirebaseHelper = new UserFirebaseHelper(phoneNumber);
        userFirebaseHelper.addEvent(phoneNumber, event.getId(), true);
    }

}
