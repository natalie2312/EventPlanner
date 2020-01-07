package com.example.eventor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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


    private ArrayAdapter<String> adapter;
    //private ArrayList<String> itemList;
    private ArrayList<String> productsList;

    private Map<String, String> productsMap;

    private ListView productsListView;
    private ProductsListAdapter productsListAdapter;

    private ImageView imageEvent;
    private SelectImageDialogFragment selectImageDialogFragment;


    private String eventNameStr;
    private String dateStr;
    private String locationStr;

    private FloatingActionButton fabAddPerson;


    private Event newEvent;
    private EventFirebaseHelper eventFirebaseHelper;
    private Bitmap eventBitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        eventFirebaseHelper = new EventFirebaseHelper();

        selectImageDialogFragment = new SelectImageDialogFragment(this);
        selectImageDialogFragment.setOnChosenImageListener(new SelectImageDialogFragment.OnChosenImageListener() {
            @Override
            public void onChosenImage(Bitmap chosenImage) {
                imageEvent.setImageBitmap(chosenImage);
                eventBitmap = chosenImage;
            }
        });
        imageEvent = (ImageView) findViewById(R.id.image_event);
        imageEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageDialogFragment.show(getSupportFragmentManager(), "Choose Image");
            }
        });

        saveButton = findViewById(R.id.finish);
        productsList = new ArrayList<>();
        productsMap = new HashMap<>();

        eventName = (EditText) findViewById(R.id.event_name);
        date = (EditText) findViewById(R.id.date);
        location= (EditText) findViewById(R.id.location);
        contanerEvents = (ListView) findViewById(R.id.container_events);
        newEvent = new Event(eventNameStr, dateStr, locationStr);

        ref = FirebaseDatabase.getInstance().getReference("Events");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eventNameStr = eventName.getText().toString();
                if (TextUtils.isEmpty(eventNameStr)){
                    eventName.setError("required");
                    return;
                }


                dateStr = date.getText().toString();
                if (TextUtils.isEmpty(dateStr) ) {
                    date.setError("required");
                    return;
                }

                locationStr = location.getText().toString();
                if(TextUtils.isEmpty(locationStr)) {
                    location.setError("required");
                    return;
                }
                Event event = new Event(eventNameStr, dateStr, locationStr);
                event.setProductsMap(productsMap);
                saveEvent(event);

                clear_form();
                moveToEventActivity(event);
            }
        });

        Add = findViewById(R.id.Add);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItem(newEvent);
            }
        });



        adapter = new ArrayAdapter<String>(CreateEventActivity.this, R.layout.product_item, R.id.item_title, productsList);
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

    private void AddItem(final Event event){
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
                        //EventFirebaseHelper eventFirebaseHelper = new EventFirebaseHelper();
                        eventFirebaseHelper.addProduct(event.getId(), item);
                        productsList.add(item);
                        adapter.notifyDataSetChanged();
                        productsMap.put(item, "");
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void moveToEventActivity(Event event){
        Intent currentEventIntent= new Intent(CreateEventActivity.this, EventActivity.class);
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
/**/
    private void saveEvent(Event event){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String phoneNumber = currentUser.getPhoneNumber();


        EventFirebaseHelper eventFirebaseHelper = new EventFirebaseHelper();
        eventFirebaseHelper.insertNewEvent(event.getId(), event);


        UserFirebaseHelper userFirebaseHelper = new UserFirebaseHelper(phoneNumber);
        userFirebaseHelper.addEvent(phoneNumber, event.getId(), true);
        if (eventBitmap != null){
            addImage(event.getId(), eventBitmap);
        }
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
                        String productToRemove = productsList.get(position);
                        productsMap.remove(productToRemove);
                        productsList.remove(position);
                        adapter.notifyDataSetChanged();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectImageDialogFragment.onActivityResult(requestCode, resultCode, data);
    }





    public void addImage(final String eventId, Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference imageReference = storageReference.child("Images").child("EventImages").child(eventId + ".png");
        //StorageReference imageReference = storageReference.child("/Images/EventImages/" + eventId + ".png");


        if (imageBytes != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            UploadTask uploadTask = (UploadTask) imageReference.putBytes(imageBytes);
            uploadTask.
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //progressDialog.dismiss();
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase.getReference();
                            //Task<Uri> url = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            databaseReference.child("Events").child(eventId).child("hasImage").setValue(true);
                            Toast.makeText(CreateEventActivity.this, "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //progressDialog.dismiss();
                            Toast.makeText(CreateEventActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }


    }


}
