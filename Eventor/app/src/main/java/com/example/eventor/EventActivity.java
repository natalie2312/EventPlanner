package com.example.eventor;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private boolean isManager;
    private String phoneNumber;
    private String userName;
    private String idEvent;

    private EventFirebaseHelper eventFirebaseHelper;
    private TextView nameEventTextView;
    private TextView locationEventTextView;
    private TextView dateEventTextView;
    private ListView productsListView;

    private ArrayList<Product> productsList;
    //private Map<String, String> productsMap;
    private ProductsListAdapter productsListAdapter;

    private Event currentEvent;
    private ArrayList<Contact> contactsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        productsList = new ArrayList<>();
        //toolbar controller
        Toolbar toolbarMenu = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbarMenu);

        contactsList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        phoneNumber = currentUser.getPhoneNumber();
        eventFirebaseHelper = new EventFirebaseHelper();

        currentEvent = (Event) getIntent().getSerializableExtra(getString(R.string.intent_current_event));
        //getInvitedContacts();
        eventFirebaseHelper.getInvitedMap(currentEvent.getId(), contactsList);
        userName = getIntent().getStringExtra(getString(R.string.intent_user_name));
        String phoneNumber = getIntent().getStringExtra(getString(R.string.intent_phone_number));
        isManager = getIntent().getBooleanExtra(getString(R.string.intent_is_manager), false);

        nameEventTextView = (TextView) findViewById(R.id.name_event_text_view);
        nameEventTextView.setText(currentEvent.getName());

        locationEventTextView = (TextView) findViewById(R.id.location_event_text_view);
        locationEventTextView.setText(currentEvent.getLocation());

        dateEventTextView = (TextView) findViewById(R.id.date_event_text_view);
        dateEventTextView.setText(currentEvent.getDate());

        productsListView = (ListView) findViewById(R.id.list_view_products);
        if (currentEvent.getProducts() != null) {
            productsList = currentEvent.getProducts();
        }
        productsListAdapter = new ProductsListAdapter(this, isManager, userName, productsList, true);



        productsListView.setAdapter(productsListAdapter);
        //////////////////////////////////////////////////////////////////////////////////////////////////
        eventFirebaseHelper.getProducts(currentEvent.getId(), productsList, productsListAdapter);
        eventFirebaseHelper.getDetailValue(currentEvent.getId(), "name", nameEventTextView);
        eventFirebaseHelper.getDetailValue(currentEvent.getId(), "location", locationEventTextView);
        eventFirebaseHelper.getDetailValue(currentEvent.getId(), "date", dateEventTextView);
        //////////////////////////////////////////////////////////////////////////////////////////////////
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (productsList.get(position).getHowBring().equals(userName)) {
                    productsList.get(position).setHowBring("");
                } else if (productsList.get(position).getHowBring().equals("")) {
                    productsList.get(position).setHowBring(userName);
                }
                productsListAdapter.updateProducts(productsList);

                //EventFirebaseHelper eventFirebaseHelper = new EventFirebaseHelper();
                //eventFirebaseHelper.addProduct(currentEvent.getId(), productsList.get(position), productsList);
                eventFirebaseHelper.changeHowBring(currentEvent.getId(), productsList.get(position));

            }
        });


        if (isManager) {
            //permission only for managers.
            productsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    editItem(view, position);
                    return true;
                }
            });
            nameEventTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editEventDetails((TextView) v, "name");
                    return false;
                }
            });
            locationEventTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editEventDetails((TextView) v, "location");
                    return false;
                }
            });
            dateEventTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editEventDetails((TextView) v, "date");
                    return false;
                }
            });
        }
    }


    /**
     * This function delete item prom the products list
     *
     * @param productRow the row of product
     * @param position   the position of product
     */

    private void deleteItems(View productRow, final int position) {
        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteDialog.setTitle("Delete Item")
                .setMessage("Are you sure do you want delete " + productsList.get(position) + "?")
                .setView(container)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ArrayList<Product> deleteProducts = new ArrayList<>();
                        //deleteProducts.add(productsList.get(position));
                        //eventFirebaseHelper.deleteProduct(currentEvent.getId(), deleteProducts, productsList);
                        //productsList.remove(position);
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


    /**
     * This function edit item of products list
     *
     * @param productRow the row of product
     * @param position   the position of product
     */

    private void editItem(final View productRow, final int position) {
        final AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.setMargins(100,100,100,100);
        final EditText editItemEditText = new EditText(this);
        //editItemEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editItemEditText.setLayoutParams(params);
        container.addView(editItemEditText);
        editDialog.setTitle("Edit Item")
                .setView(container)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = editItemEditText.getText().toString();
                        Product product = new Product(item);
                        if (item.isEmpty()) {
                            return;
                        } else {
                            Product preProduct = productsList.get(position);
                            Product postProduct = new Product(item);
                            eventFirebaseHelper.changeProductName(currentEvent.getId(), preProduct, postProduct, productsList, productsListAdapter);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setCancelable(false)
                .create();
        editDialog.show();
    }

    /**
     * This function change exist product in other product
     *
     * @param productRow the row of product
     * @param position   the position of product
     * @param newItem    new product to change
     */

    private void changeItem(View productRow, int position, Product newItem) {
        TextView tv = (TextView) productRow.findViewById(R.id.item_product_text_view);
        productsList.set(position, newItem);
        //tv.setText(productsList.get(position));
    }


    /**
     * This method return products list from productsMap
     *
     * @param productsMap to do list
     * @return ArrayList of products
     */
    public ArrayList<String> getProductsList(Map<String, String> productsMap) {
        ArrayList<String> products = new ArrayList<>();
        if (productsMap != null) {
            for (String product : productsMap.keySet()) {
                products.add(product);
            }
        }
        return products;
    }


    /*private void bringProduct(String eventId, Product product) {
        EventFirebaseHelper eventFirebaseHelper = new EventFirebaseHelper();
        eventFirebaseHelper.addProduct(eventId, product, productsList);
    }*/


    //toolbar controller with logout button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_create_new_event, menu);
        if (isManager) {
            MenuItem addProduct = menu.findItem(R.id.add_product_item);
            addProduct.setVisible(true);
            MenuItem deleteProducts = menu.findItem(R.id.delete_products_items);
            deleteProducts.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.beck_to_events_list_item:
                backToEventsList();
                break;
            case R.id.show_selected_contacts_item:
                if (!contactsList.isEmpty()) {
                    showSelectedContacts(contactsList);
                } else {
                    Toast.makeText(this, "Your list is empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.add_product_item:
                addProduct();
                break;
            case R.id.delete_products_items:
                Toast.makeText(this, "This feature not available yet", Toast.LENGTH_SHORT).show();
                break;
            //startActivity(new Intent(this, ShowSelectedGroup.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private void backToEventsList() {
        finish();
    }


    private void addProduct() {

        final AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.setMargins(100,100,100,100);
        final EditText editItemEditText = new EditText(this);
        //editItemEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editItemEditText.setLayoutParams(params);
        container.addView(editItemEditText);
        editDialog.setTitle("Add product")
                .setView(container)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = editItemEditText.getText().toString();
                        if (item.isEmpty()) {
                            return;
                        } else {
                            Product p = new Product(item);
                            eventFirebaseHelper.addNewProduct(currentEvent.getId(), p, productsList, productsListAdapter);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setCancelable(false)
                .create();
        editDialog.show();

        productsListAdapter.updateProducts(productsList);
    }


    private void showSelectedContacts(ArrayList<Contact> selectedContacts) {
        Intent showContactsIntent = new Intent(this, ShowSelectedGroupActivity.class);
        showContactsIntent.putExtra("show_selected_contacts", selectedContacts);
        startActivity(showContactsIntent);
    }


    private void getInvitedContacts() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();
        Query getInvitedMap = databaseReference.child("Events");
        getInvitedMap.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey().equals(currentEvent.getId())) {
                        Map<String, Contact> invitedMap = (Map<String, Contact>) data.child("invitedMap").getValue();
                        for (String key : invitedMap.keySet()) {
                            String name = data.child("invitedMap").child(key).child("name").getValue(String.class);
                            String phoneNumber = data.child("invitedMap").child(key).child("phoneNumber").getValue(String.class);
                            Boolean isSelected = data.child("invitedMap").child(key).child("selected").getValue(Boolean.class);
                            Contact contact = new Contact(name, phoneNumber);
                            contact.setSelected(isSelected);
                            contactsList.add(contact);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void editEventDetails(final TextView tv, final String detail) {
        final AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.setMargins(100,100,100,100);
        final EditText editItemEditText = new EditText(this);
        //editItemEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editItemEditText.setLayoutParams(params);
        editItemEditText.setText(tv.getText());
        container.addView(editItemEditText);
        editDialog.setTitle("Edit " + detail)
                .setView(container)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = editItemEditText.getText().toString();
                        if (item.isEmpty()) {
                            return;
                        } else {
                            eventFirebaseHelper.changeEventDetails(currentEvent.getId(), editItemEditText.getText().toString(), detail);
                            tv.setText(item);
                            //productsListAdapter.updateProducts(productsList);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setCancelable(false)
                .create();
        editDialog.show();
    }





}
