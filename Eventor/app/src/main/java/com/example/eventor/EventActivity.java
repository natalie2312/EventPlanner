package com.example.eventor;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    private ArrayList<String> productsList;
    private Map<String, String> productsMap;
    private ProductsListAdapter productsListAdapter;

    private Event currentEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        phoneNumber = currentUser.getPhoneNumber();
        userName = "Omer";
        eventFirebaseHelper = new EventFirebaseHelper();

        Event currentEvent = (Event) getIntent().getSerializableExtra("currentEvent");
        //String userName = getIntent().getStringExtra("userName");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        isManager = getIntent().getBooleanExtra("isManager", false);
        //linearLayoutContainer = (LinearLayout) findViewById(R.id.linear_layout_container);

        nameEventTextView = (TextView) findViewById(R.id.name_event_text_view);
        nameEventTextView.setText(currentEvent.getName());
        locationEventTextView = (TextView) findViewById(R.id.location_event_text_view);
        locationEventTextView.setText(currentEvent.getLocation());
        dateEventTextView = (TextView) findViewById(R.id.date_event_text_view);
        dateEventTextView.setText(currentEvent.getDate());

        productsListView = (ListView) findViewById(R.id.list_view_products);


        //productsList = currentEvent.getProductsList();
        productsList = currentEvent.getProductsList();


        productsListAdapter = new ProductsListAdapter(this, isManager, "Omer", currentEvent);
        //productsListAdapter = new ProductsListAdapter(this, isManager, userName, productsList);
        productsListView.setAdapter(productsListAdapter);


        if (isManager){
            //permission only for managers.
            productsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    deleteItem(view, position);
                    return false;
                }
            });
            productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    editItem(view, position);
                }
            });
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




    private void editItem(final View productRow, final int position){
        final AlertDialog.Builder editDialog = new AlertDialog.Builder(this);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                        if (item.isEmpty()){
                            return;
                        } else {
                            changeItem(productRow, position, item);
                            //EventFirebaseHelper eventFirebaseHelper = new EventFirebaseHelper();
                            //eventFirebaseHelper.changeProduct(userName,productTextView.getText().toString(), editItemEditText.getText().toString());
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //signInFailureUI();
                        return;
                    }
                })
                .setCancelable(false)
                .create();
        editDialog.show();
    }

    private void changeItem(View productRow, int position, String newItem){
        TextView tv = (TextView) productRow.findViewById(R.id.item_product_text_view);
        productsList.set(position, newItem);
        tv.setText(productsList.get(position));
    }
}
