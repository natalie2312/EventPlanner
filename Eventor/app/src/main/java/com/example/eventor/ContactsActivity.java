package com.example.eventor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;





import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;




public class ContactsActivity extends AppCompatActivity {


    public static final int REQUEST_READ_CONTACTS = 79;
    private ListView contactsListView;
    private ArrayList<Contact> mobileArray;
    private ArrayList<Contact> selectedContacts;
    private FloatingActionButton createGroupFab;
    private ContactsListAdapter contactsAdapter;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbarMenu = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbarMenu);

        mobileArray = new ArrayList<>();
        selectedContacts = new ArrayList<>();
        createGroupFab = findViewById(R.id.create_group_fab);
        createGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                //---set the data to pass back---
                data.putExtra("selected_contacts_list", selectedContacts);
                setResult(RESULT_OK, data);
                //---close the activity---
                finish();
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArray = getAllContacts();
        } else {
            requestPermission();
        }
        contactsListView = findViewById(R.id.contacts_list_view);
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mobileArray.get(position).isSelected()){
                    mobileArray.get(position).setSelected(false);
                    if (selectedContacts.contains(mobileArray.get(position))){
                        selectedContacts.remove(mobileArray.get(position));
                    }
                } else {
                    mobileArray.get(position).setSelected(true);
                    if (!selectedContacts.contains(mobileArray.get(position))){
                        selectedContacts.add(mobileArray.get(position));
                    }
                }
                contactsAdapter.update(mobileArray);
            }
        });
        contactsAdapter = new ContactsListAdapter(this, mobileArray, true);
        contactsListView.setAdapter(contactsAdapter);
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                    finish();
                }
                return;
            }
        }
    }
    private ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contactsList = new ArrayList();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                Contact contact = new Contact();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                contact.setName(name);
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    String phoneNo = "";
                    while (pCur.moveToNext()) {
                        phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    String uniPhoneNumber = (String) uniPhone(onlyDigits(phoneNo),972);
                    contact.setPhoneNumber(uniPhoneNumber);
                    pCur.close();
                }
                if (contact.getPhoneNumber() != null) {
                    contactsList.add(contact);
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return contactsList;
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






    private CharSequence onlyDigits(CharSequence charSequence) {
        String onlyDigits = "";
        int len = charSequence.length();
        for (int i = 0; i < len; i++) {
            Character c = charSequence.charAt(i);
            if ((c >= '0' && c <= '9') || c == '+') {
                onlyDigits += c;
            }
        }
        return onlyDigits;
    }


    private CharSequence uniPhone(CharSequence phoneNumber, int prefix) {
        String phone = "";
        switch (prefix) {
            case 972:
                String ilPrefix = "+972";
                Character shortPrefix = phoneNumber.charAt(0);
                if ((phoneNumber.length() == 10 || phoneNumber.length() == 9) && shortPrefix == '0'){
                    phone = ilPrefix+phoneNumber.subSequence(1, phoneNumber.length());
                } else if ((phoneNumber.length() == 13 || phoneNumber.length() == 12) && phoneNumber.subSequence(0,4).equals(ilPrefix)){
                    phone = (String) phoneNumber;
                }
                break;
            default:
                break;
        }
        return phone;
    }

}
