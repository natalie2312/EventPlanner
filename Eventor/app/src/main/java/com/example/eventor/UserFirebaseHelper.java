package com.example.eventor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserFirebaseHelper {

    private static final String USERS = "Users";
    private static final String USER_EVENTS_LIST = "userEventsList";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;







    public UserFirebaseHelper(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }



    public void insertNewUser(final User newUser){

        final String phoneNumber = newUser.getPhoneNumber();

        Query insertUserQuery = databaseReference.child(USERS);
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
                    databaseReference.child(USERS).child(newUser.getPhoneNumber()).setValue(newUser, new DatabaseReference.CompletionListener() {
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


    public void addEvent(final String phoneNumber, final String idEvent, final boolean isManager){
        Query addEventQuery = databaseReference.child(USERS);
        addEventQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    String currentPhoneNumber = data.getKey();
                    if (currentPhoneNumber.equals(phoneNumber)){
                        databaseReference.child(USERS).child(phoneNumber).child(USER_EVENTS_LIST).child(idEvent).setValue(isManager);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

}
