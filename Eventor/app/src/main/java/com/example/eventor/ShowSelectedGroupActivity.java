package com.example.eventor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class ShowSelectedGroupActivity extends AppCompatActivity {


    private ListView selectedContactsListView;
    private ArrayList<Contact> selectedContactsList;
    private ContactsListAdapter contactsListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_selected_group);

        Toolbar toolbarMenu = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbarMenu);

        selectedContactsList = new ArrayList<>();
        selectedContactsList = (ArrayList<Contact>) getIntent().getSerializableExtra("show_selected_contacts");

        selectedContactsListView = findViewById(R.id.show_contacts_list_view);
        contactsListAdapter = new ContactsListAdapter(this, selectedContactsList, false);
        selectedContactsListView.setAdapter(contactsListAdapter);
    }









    //toolbar controller with logout button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_show_selected_contacts, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.beck_to_create_new_event_item:
                backToCreateNewEvent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void backToCreateNewEvent(){
        finish();
    }
}
