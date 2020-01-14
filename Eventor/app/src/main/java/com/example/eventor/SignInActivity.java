package com.example.eventor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    private final static String TAG = "TAG";
    public final static String SIGN_IN_PREF = "SignInPreferences";
    private FirebaseAuth mAuth;
    private EditText editTextUserName;
    private EditText editTextPhoneNumber;

    private Button signInButton;
    private String userName;
    private String phoneNumber;
    private Spinner prefixPhoneSpinner;
    private String verificationId;
    private String[] prefix = {"+972", "+1", "+32"};
    private ArrayAdapter<String> prefixAdapter;
    private String chosenPrefix;




    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(SignInActivity.this, "Verification completed", Toast.LENGTH_LONG).show();

            String code = phoneAuthCredential.getSmsCode();
            String prov = phoneAuthCredential.getProvider();
            String sign = phoneAuthCredential.getSignInMethod();
            if (code != null){
                verifyCode(code);
            } else {

            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            signInFailureUI();
            Toast.makeText(SignInActivity.this, "Verification Failed", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s;

            AlertDialog.Builder codeDialog = new AlertDialog.Builder(SignInActivity.this);
            FrameLayout container = new FrameLayout(SignInActivity.this);
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(100,100,100,100);
            final EditText codeEditText = new EditText(getBaseContext());
            codeEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            codeEditText.setLayoutParams(params);
            container.addView(codeEditText);
            codeDialog.setTitle("Verification Code")
                    .setView(container)
                    .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String code = codeEditText.getText().toString();
                            if (code.length() < 6){
                                signInFailureUI();
                            } else {
                                verifyCode(code);
                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            signInFailureUI();
                        }
                    })
                    .setCancelable(false)
                    .create();
            codeDialog.show();
        }
    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        editTextUserName = (EditText) findViewById(R.id.edit_text_user_name);
        editTextPhoneNumber = (EditText) findViewById(R.id.edit_text_phone_number);
        prefixPhoneSpinner = (Spinner) findViewById(R.id.prefix_phone_spinner);
        prefixAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prefix);
        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefixPhoneSpinner.setAdapter(prefixAdapter);
        prefixPhoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenPrefix = prefix[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        signInButton = (Button) findViewById(R.id.button_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = editTextUserName.getText().toString();
                phoneNumber = chosenPrefix + editTextPhoneNumber.getText().toString();

                if (userName.equals("")) {
                    Toast.makeText(SignInActivity.this, "Please Enter user name", Toast.LENGTH_SHORT).show();
                } else if (phoneNumber.length() < 10) {
                    Toast.makeText(SignInActivity.this, "Please Enter phonr number", Toast.LENGTH_SHORT).show();
                } else {
                    signInButton.setClickable(false);
                    editTextPhoneNumber.setEnabled(false);
                    editTextUserName.setEnabled(false);
                    PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();
                    phoneAuthProvider.verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            SignInActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(SignInActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            signInSuccess();
                            saveUserOnTheDevice();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(SignInActivity.this, "signInWithCredential:failure", Toast.LENGTH_SHORT).show();
                                signInFailureUI();
                            }
                        }
                    }
                });
    }



    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            signInSuccess();
                            saveUserOnTheDevice();
                        } else {
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void signInFailureUI(){
        editTextPhoneNumber.setEnabled(true);
        editTextUserName.setEnabled(true);
        signInButton.setClickable(true);
    }

    private void signInSuccess(){
        User newUser = new User(userName, phoneNumber);
        UserFirebaseHelper userFirebaseHelper = new UserFirebaseHelper();
        userFirebaseHelper.insertNewUser(newUser);
        Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    /**
     * This method save phoneNumber and userName on the device
     */
    private void saveUserOnTheDevice(/*String phoneNumber, String userName*/){
        SharedPreferences sharedPref = getSharedPreferences("SignInPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.save_phone_number), phoneNumber);
        editor.putString(getString(R.string.save_user_name), userName);
        editor.commit();
    }




}
