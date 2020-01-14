package com.example.eventor;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EventFirebaseHelper {

    private final static String EVENTS = "Events";
    private final static String PRODUCTS_MAP = "productsMap";
    private final static String PRODUCTS = "products";
    private final static String PRODUCT_NAME = "name";
    private final static String BRING_PRODUCT = "howBring";
    private final static String ID = "id";
    private static final String INVITED_MAP = "invitedMap";
    private static final String CONTACTS = "contacts";


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

    public void getInvitedMap(final String eventId, final ArrayList<Contact> contactsList) {
        Query getInvitedMap = databaseReference.child(EVENTS);
        getInvitedMap.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals(eventId)) {
                        contactsList.clear();
                        ArrayList<Contact> contacts = (ArrayList<Contact>) data.child(CONTACTS).getValue();
                        for (int i = 0; i < contacts.size(); i++) {
                            String name = data.child(CONTACTS).child(String.valueOf(i)).child("name").getValue(String.class);
                            String phoneNumber = data.child(CONTACTS).child(String.valueOf(i)).child("phoneNumber").getValue(String.class);
                            Boolean isSelected = data.child(CONTACTS).child(String.valueOf(i)).child("selected").getValue(Boolean.class);
                            Contact contact = new Contact(name, phoneNumber);
                            contact.setSelected(isSelected);
                            contactsList.add(contact);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    public void changeHowBring(final String eventId, final Product product) {
        Query changeHowBringQuery = databaseReference.child(EVENTS);
        changeHowBringQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    if (key.equals(eventId)) {
                        ArrayList<Product> prodList = new ArrayList<>();
                        prodList = (ArrayList<Product>) data.child(PRODUCTS).getValue();
                        for (int i = 0; i < prodList.size(); i++) {
                            String nameProd = data.child(PRODUCTS).child(String.valueOf(i)).child(PRODUCT_NAME).getValue(String.class);
                            String howBringProd = data.child(PRODUCTS).child(String.valueOf(i)).child(BRING_PRODUCT).getValue(String.class);
                            if (nameProd.equals(product.getName())) {
                                databaseReference.child(EVENTS).child(eventId).child(PRODUCTS).child(String.valueOf(i)).child(BRING_PRODUCT).setValue(product.getHowBring());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    public void changeProductName(final String eventId, final Product preChange, final Product postChange, final ArrayList<Product> productsList, final ProductsListAdapter adapter) {
        Query changeProductNameQuery = databaseReference.child(EVENTS);
        changeProductNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    if (key.equals(eventId)) {
                        if (data.hasChild(PRODUCTS)) {
                            productsList.clear();
                            ArrayList<Product> products = (ArrayList<Product>) data.child(PRODUCTS).getValue();
                            for (int i = 0; i < products.size(); i++) {
                                String nameProd = data.child(PRODUCTS).child(String.valueOf(i)).child(PRODUCT_NAME).getValue(String.class);
                                String howBringProd = data.child(PRODUCTS).child(String.valueOf(i)).child(BRING_PRODUCT).getValue(String.class);
                                Product p = new Product(nameProd);
                                p.setHowBring(howBringProd);
                                if (nameProd.equals(preChange.getName())) {
                                    p.setName(postChange.getName());
                                    p.setHowBring(postChange.getHowBring());
                                    databaseReference.child(EVENTS).child(eventId).child(PRODUCTS).child(String.valueOf(i)).child(PRODUCT_NAME).setValue(postChange.getName());
                                    databaseReference.child(EVENTS).child(eventId).child(PRODUCTS).child(String.valueOf(i)).child(BRING_PRODUCT).setValue(postChange.getHowBring());
                                }
                                productsList.add(p);
                            }
                            adapter.updateProducts(productsList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void deleteProducts(final String eventId, final ArrayList<Product> deleteProducts, final ArrayList<Product> productsList) {
        Query deleteProductsQuery = databaseReference.child(EVENTS);
        deleteProductsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    ArrayList<Product> allProducts = new ArrayList<>();
                    if (data.hasChild(PRODUCTS)) {
                        allProducts = (ArrayList<Product>) data.child(PRODUCTS).getValue();
                        for (int i = 0; i < allProducts.size(); i++) {
                            productsList.clear();
                            String nameProd = data.child(PRODUCTS).child(String.valueOf(i)).child(PRODUCT_NAME).getValue(String.class);
                            String howBringProd = data.child(PRODUCTS).child(String.valueOf(i)).child(BRING_PRODUCT).getValue(String.class);
                            Product product = new Product(nameProd);
                            product.setHowBring(howBringProd);
                            Iterator<Product> iterator = deleteProducts.iterator();
                            boolean toAdd = true;
                            while (iterator.hasNext()) {
                                Product p = iterator.next();
                                if (nameProd.equals(p.getName())) {
                                    toAdd = false;
                                }
                            }
                            if (toAdd) {
                                productsList.add(product);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void addNewProduct(final String eventId, final Product newProduct, final ArrayList<Product> productsList, final ProductsListAdapter adapter) {
        Query addProductQuery = databaseReference.child(EVENTS);
        addProductQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    ArrayList<Product> allProducts = new ArrayList<>();
                    if (key.equals(eventId)) {
                        boolean exist = false;
                        if (data.hasChild(PRODUCTS)) {
                            allProducts = (ArrayList<Product>) data.child(PRODUCTS).getValue();
                            productsList.clear();
                            for (int i = 0; i < allProducts.size(); i++) {
                                String nameProd = data.child(PRODUCTS).child(String.valueOf(i)).child(PRODUCT_NAME).getValue(String.class);
                                String howBringProd = data.child(PRODUCTS).child(String.valueOf(i)).child(BRING_PRODUCT).getValue(String.class);
                                Product product = new Product(nameProd);
                                product.setHowBring(howBringProd);
                                productsList.add(product);
                                if (nameProd.equals(newProduct.getName())) {
                                    exist = true;
                                }
                            }
                        }
                        if (!exist) {
                            int size = productsList.size();
                            databaseReference.child(EVENTS).child(eventId).child(PRODUCTS).child(String.valueOf(size)).setValue(newProduct);
                            productsList.add(newProduct);
                        }
                        //adapter.updateProducts(productsList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    public void getProducts(final String eventId, final ArrayList<Product> productsList, final ProductsListAdapter adapter){
        Query getProductsQuery = databaseReference.child(EVENTS);
        getProductsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    ArrayList<Product> allProducts = new ArrayList<>();
                    if (key.equals(eventId)) {
                        boolean exist = false;
                        if (data.hasChild(PRODUCTS)) {
                            allProducts = (ArrayList<Product>) data.child(PRODUCTS).getValue();
                            productsList.clear();
                            for (int i = 0; i < allProducts.size(); i++) {
                                String nameProd = data.child(PRODUCTS).child(String.valueOf(i)).child(PRODUCT_NAME).getValue(String.class);
                                String howBringProd = data.child(PRODUCTS).child(String.valueOf(i)).child(BRING_PRODUCT).getValue(String.class);
                                Product product = new Product(nameProd);
                                product.setHowBring(howBringProd);
                                productsList.add(product);
                            }
                        }
                        adapter.updateProducts(productsList);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }



    public void changeEventDetails(final String eventId, final String changeText, final String detail) {
        Query changeEventDetailsQuery = databaseReference.child(EVENTS);
        changeEventDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String key = data.getKey();
                    if (key.equals(eventId)) {
                        if (data.hasChild(detail)) {
                            databaseReference.child(EVENTS).child(key).child(detail).setValue(changeText);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    public void getDetailValue(String eventId, String detail, final TextView tv){
        Query getDetailValue = databaseReference.child(EVENTS).child(eventId).child(detail);
        getDetailValue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String valueDetail = dataSnapshot.getValue(String.class);
                tv.setText(valueDetail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }
}
