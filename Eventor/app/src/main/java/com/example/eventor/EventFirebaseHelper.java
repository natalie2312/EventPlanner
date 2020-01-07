package com.example.eventor;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.Edits;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventFirebaseHelper {

    private final static String EVENTS = "Events";
    private final static String PRODUCTS_MAP = "productsMap";
    private final static String ID = "id";


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Map<String, Event> eventsMap;


    public EventFirebaseHelper() {
        eventsMap = new HashMap<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void insertNewEvent(final String eventId, final Event newEvent) {
        databaseReference.child(EVENTS).child(eventId).setValue(newEvent);
    }

    public void changeProduct(final String idEvent, final String thisProduct, final String productToChange/*, final Context context, final boolean isManager, final ListView productsListView, final ArrayAdapter adapter*/) {
        Query changeProductQuery = databaseReference.child(EVENTS);

        changeProductQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    String d = data.child(ID).getValue(String.class);
                    if (d.equals(idEvent)) {
                        ArrayList<String> productsList = new ArrayList<>();
                        for (DataSnapshot ref : data.child(PRODUCTS_MAP).getChildren()) {

                            String product = ref.getValue(String.class);
                            productsList.add(product);
                            if (product.equals(thisProduct)) {
                                productsList.set(productsList.indexOf(thisProduct), productToChange);
                                String k = ref.getRef().getKey();
                                ref.getRef().setValue(productToChange);
                            }
                            //data.child("ProductsList").getRef().setValue(productsList);
                        }
                        //ArrayAdapter adapter = new ProductsListAdapter(context, isManager, idEvent, productsList);
                        //productsListView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void addProduct(final String idEvent, final String product, final String howBring) {
        Query addProductQuery = databaseReference.child(EVENTS);

        addProductQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    String id = data.child(ID).getValue(String.class);
                    if (id.equals(idEvent)) {
                        //ArrayList<String> productsList = new ArrayList<>();
                        Map<String, String> productsMap = new HashMap<>();
                        productsMap = data.child(PRODUCTS_MAP).getValue(Map.class);
                        if (!productsMap.containsKey(product)) {
                            databaseReference.child(EVENTS).child(key).child(PRODUCTS_MAP).setValue(product);
                            databaseReference.child(EVENTS).child(key).child(PRODUCTS_MAP).child(product).setValue(howBring);

                        }
                        //for (DataSnapshot ref : data.child("productsList").getChildren()) {
                        /*for (DataSnapshot productsSnapshot : data.child("productsMap").getChildren()) {
                            String product = productsSnapshot.getValue(String.class);
                            productsList.add(product);
                            if (product.equals(thisProduct)) {
                                productsList.set(productsList.indexOf(thisProduct), productToChange);
                                String k = ref.getRef().getKey();
                                ref.getRef().setValue(productToChange);
                            }
                            //data.child("ProductsList").getRef().setValue(productsList);
                        }*/
                        //ArrayAdapter adapter = new ProductsListAdapter(context, isManager, idEvent, productsList);
                        //productsListView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void addProduct(final String idEvent, final String product) {
        addProduct(idEvent, product, "");
    }

    public void deleteProduct(String thisProduct) {
        String key = databaseReference.child(EVENTS).child(PRODUCTS_MAP).child(thisProduct).getKey();
        databaseReference.child(EVENTS).child(PRODUCTS_MAP).child(key).removeValue();
    }


    public void chageBringProduct(String eventId, String product) {
        Query chageBringProductQuery = databaseReference.child(EVENTS);
    }

/*
    public void addImage(final Context context, String eventId, Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageReference = storageReference.child("Images").child("EventImages").child(eventId + ".png");
        //StorageReference imageReference = storageReference.child("/Images/EventImages/" + eventId + ".png");


        if (imageBytes != null) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            UploadTask uploadTask = (UploadTask) imageReference.putBytes(imageBytes).
                    addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
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
*/

    public void getImage(String eventId) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference getImageReference = storageReference.child("Images").child("EventImages").child(eventId + ".png");

    }


}
