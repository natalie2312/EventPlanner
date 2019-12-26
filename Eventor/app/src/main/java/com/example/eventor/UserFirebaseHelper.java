package com.example.eventor;

import android.content.Context;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Map;

public class UserFirebaseHelper {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    private String userName;
    private String phoneNumber;
    private User mUser;





    public UserFirebaseHelper(String phoneNumber){
        this.phoneNumber = phoneNumber;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        getUserFromDB();
    }



    public void insertNewUser(final User newUser){

        final String phoneNumber = newUser.getPhoneNumber();

        Query insertUserQuery = databaseReference.child("Users");
        insertUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userExist = false;
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    User user = data.getValue(User.class);
                    if (user.getPhoneNumber().equals(phoneNumber)){
                        userExist = true;
                        break;
                    }
                }
                if (!userExist){
                    databaseReference.child("Users").child(newUser.getPhoneNumber()).setValue(newUser, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null){
                                //insert userId as phoneNumber success
                                //users.put(phoneNumber, newUser);
                            } else {
                                //insert userId as phoneNumber failed
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void addEvent(String phoneNumber, final String idEvent, boolean isManager){
        //String phone = user.getUserPhoneNumber();
        databaseReference.child("Users").child(phoneNumber).child("userEventsList").child(idEvent).setValue(isManager);
    }


    public void getUserNameFromDB(final String phoneNumber){
        Query query = databaseReference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("Users").child(phoneNumber).child("userName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void getUserFromDB(){
        Query query = databaseReference;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.child("Users").child(phoneNumber).getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }


    //public String getUserName() {
    //    return userName;
    //}





    public User getUser() {
        return mUser;
    }
}
