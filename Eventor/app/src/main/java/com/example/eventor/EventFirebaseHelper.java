package com.example.eventor;

import android.content.Context;
import android.icu.text.Edits;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventFirebaseHelper {


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Map<String, Event> eventsMap;


    public EventFirebaseHelper() {
        eventsMap = new HashMap<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void insertNewEvent(final String eventId, final Event newEvent) {
        databaseReference.child("Events").child(eventId).setValue(newEvent);
    }

    public void changeProduct(final String idEvent, final String thisProduct, final String productToChange/*, final Context context, final boolean isManager, final ListView productsListView, final ArrayAdapter adapter*/) {
        Query changeProductQuery = databaseReference.child("Events");
        changeProductQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    String d = data.child("id").getValue(String.class);
                    if (d.equals(idEvent)) {
                        ArrayList<String> productsList = new ArrayList<>();
                        for (DataSnapshot ref : data.child("productsMap").getChildren()) {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    public void addProduct(final String idEvent, final String product, final String howGet){
        Query addProductQuery = databaseReference.child("Events");
        addProductQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    String id = data.child("id").getValue(String.class);
                    if (id.equals(idEvent)) {
                        //ArrayList<String> productsList = new ArrayList<>();
                        Map<String, String> productsMap = new HashMap<>();
                        productsMap = data.child("productsMap").getValue(Map.class);
                        if (!productsMap.containsKey(product)){
                            databaseReference.child("Events").child(key).child("productsMap").setValue(product);
                            databaseReference.child("Events").child(key).child("productsMap").child(product).setValue(howGet);
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
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    public void addProduct(final String idEvent, final String product){
        addProduct(idEvent, product, "");
    }

    public void deleteProduct(String thisProduct, String productToChange) {
        String key = databaseReference.child("Events").child("productsMap").child(thisProduct).getKey();
        databaseReference.child("Events").child("productsList").child(key).removeValue();
    }



}
