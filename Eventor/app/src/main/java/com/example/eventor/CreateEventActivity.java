package com.example.eventor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class CreateEventActivity extends AppCompatActivity {





    private FloatingActionButton fabAddPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);



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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String phoneNumber = currentUser.getPhoneNumber();

        Event event = new Event("Tea Time", "01-01-2020", "Ra'anana");
        event.addProduct("Tea", "Omer");
        event.addProduct("Milk");
        event.addProduct("Ice", "Rafi");
        EventFirebaseHelper eventFirebaseHelper = new EventFirebaseHelper();
        eventFirebaseHelper.insertNewEvent(event.getId(), event);

        UserFirebaseHelper userFirebaseHelper = new UserFirebaseHelper(phoneNumber);
        userFirebaseHelper.addEvent(phoneNumber, event.getId(), true);
    }

}
