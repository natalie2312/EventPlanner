package com.example.eventor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference ref;

    private Button Add;
    private Button saveButton;
    private EditText eventName;
    private EditText date;
    private EditText location;

    private ListView contanerEvents;


    ArrayAdapter<String> adapter;
    ArrayList<String> itemList;

    private ArrayList<String> productsList;
    private Map<String, String> productsMap;

    private ListView productsListView;
    private ProductsListAdapter productsListAdapter;


    private String eventNameStr;
    private String dateStr;
    private String locationStr;

    private FloatingActionButton fabAddPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);


//        saveEvent();

        saveButton = findViewById(R.id.finish);
        itemList = new ArrayList<>();

        eventName = (EditText) findViewById(R.id.event_name);
        date = (EditText) findViewById(R.id.date);
        location= (EditText) findViewById(R.id.location);
        contanerEvents = (ListView) findViewById(R.id.container_events);

        ref = FirebaseDatabase.getInstance().getReference("Events");


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eventNameStr = eventName.getText().toString();
                dateStr = date.getText().toString();
                locationStr = location.getText().toString();

                if (TextUtils.isEmpty(eventNameStr)){
                    eventName.setError("required");
                    return;
                }

                if (TextUtils.isEmpty(dateStr) ) {
                    date.setError("required");
                    return;
                }
                if(TextUtils.isEmpty(locationStr)) {

                    location.setError("required");
                    return;

                }

                saveEvent();
                clear_form();
                moveToEventActivity();

            }
        });

        Add = findViewById(R.id.Add);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItem();
            }
        });



        adapter = new ArrayAdapter<String>(CreateEventActivity.this, R.layout.product_item, R.id.item_title, itemList);
        contanerEvents.setAdapter(adapter);

//        productsListAdapter = new ProductsListAdapter(this, isManager, "Omer", currentEvent);
        //productsListAdapter = new ProductsListAdapter(this, isManager, userName, productsList);
//        productsListView.setAdapter(productsListAdapter);

        contanerEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteItem(view, position);
                return false;
            }
        });

        //button and listener for contacts list
        fabAddPerson = (FloatingActionButton) findViewById(R.id.fab_add_person);
        fabAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactList();
            }
        });

    }

    private void AddItem(){
        //    EditText editText = new EditText(Main3Activity.this);
        //    contanerEvents.addView(editText);
        final EditText itemEdiText= new EditText(this);
        AlertDialog dialog= new AlertDialog.Builder(this)
                .setTitle("new Item:")
                .setView(itemEdiText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CreateEventActivity.this, itemEdiText.getText().toString(), Toast.LENGTH_SHORT).show();
                        //TODO: save to firebase
                        String item = String.valueOf(itemEdiText.getText());
                        itemList.add(item);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void moveToEventActivity(){


        Intent currentEventIntent= new Intent(CreateEventActivity.this, EventActivity.class);
        Event event = new Event(eventNameStr, dateStr, locationStr);

        currentEventIntent.putExtra(getString(R.string.intent_current_event) ,event);
        startActivity(currentEventIntent);
        finish();
    }

    /**
     * This method open the contact list to create order list for the event.
     */
    private void openContactList(){

    }

    private void clear_form() {
        eventName.getText().clear();
        location.getText().clear();
        date.getText().clear();
    }

    private void saveEvent(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String phoneNumber = currentUser.getPhoneNumber();

        Event event = new Event(eventNameStr, dateStr, locationStr);
//        event.addProduct("Tea", "Omer");
//        event.addProduct("Milk");
//        event.addProduct("Ice", "Rafi");
        EventFirebaseHelper eventFirebaseHelper = new EventFirebaseHelper();
        eventFirebaseHelper.insertNewEvent(event.getId(), event);

        UserFirebaseHelper userFirebaseHelper = new UserFirebaseHelper(phoneNumber);
        userFirebaseHelper.addEvent(phoneNumber, event.getId(), true);
    }

    private void deleteItem(View productRow, final int position){
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteDialog.setTitle("Delete Item")
                .setMessage("Are you sure do you want delete " + productsList.get(position) + "?")
                .setView(container)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        productsList.remove(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setCancelable(false)
                .create();
        deleteDialog.show();
    }

}
