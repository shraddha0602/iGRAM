package com.example.shraddha.igram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class register extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText nameEdittext, rollEditText, passwordEditText, emailEditText, roomEditText;
    private RadioGroup hostel;
    private RadioButton radioButton;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private String token_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameEdittext = (EditText) findViewById(R.id.namEditText);
        rollEditText = (EditText) findViewById(R.id.rollEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        hostel = (RadioGroup) findViewById(R.id.hostelRadioGroup);
        roomEditText = (EditText) findViewById(R.id.roomEditText);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        findViewById(R.id.loginbutton).setEnabled(false);

        findViewById(R.id.registerButton).setOnClickListener(this);
        findViewById(R.id.loginbutton).setOnClickListener(this);
    }

    private void registerUser() {
        String name, roll, password, email, room, rb;
        int selectedId;

        if (TextUtils.isEmpty(nameEdittext.getText().toString().trim())) {
            Toast.makeText(register.this, "Name is required", Toast.LENGTH_SHORT).show();
            nameEdittext.requestFocus();
            return;
        } else
            name = nameEdittext.getText().toString().trim();

        if (TextUtils.isEmpty(rollEditText.getText().toString().trim())) {
            Toast.makeText(register.this, "Roll Number is required", Toast.LENGTH_SHORT).show();
            rollEditText.requestFocus();
            return;
        } else
            roll = rollEditText.getText().toString().trim();

        if (hostel.getCheckedRadioButtonId() == -1) {
            Toast.makeText(register.this, "Hostel is required", Toast.LENGTH_SHORT).show();
            return;
        } else {
            selectedId = hostel.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            rb = radioButton.getText().toString().trim();
        }

        if (TextUtils.isEmpty(roomEditText.getText().toString().trim())) {
            Toast.makeText(register.this, "Room Number is required", Toast.LENGTH_SHORT).show();
            roomEditText.requestFocus();
            return;
        } else
            room = roomEditText.getText().toString().trim();

        if (TextUtils.isEmpty(emailEditText.getText().toString().trim())) {
            Toast.makeText(register.this, "Email is required", Toast.LENGTH_SHORT).show();
            emailEditText.requestFocus();
            return;
        } else
            email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(passwordEditText.getText().toString().trim())) {
            Toast.makeText(register.this, "Password is required", Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        } else
            password = passwordEditText.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.requestFocus();
            emailEditText.setError("Invaild Email");
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(register.this, "Minimum length of password should be 6 characters", Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        }

        progressDialog.setMessage("Registering user...");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if (task.isSuccessful()) {
                            Toast.makeText(register.this, "Registered Sucessfully", Toast.LENGTH_SHORT).show();
                            token_id = FirebaseInstanceId.getInstance().getToken();
                            String currentUser_Id = mAuth.getCurrentUser().getUid();
                            databaseReference.child(currentUser_Id).child("tokenId").setValue(token_id);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(register.this, "You are already registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        findViewById(R.id.loginbutton).setEnabled(true);
    }

    private void loginUser() {
        String name = nameEdittext.getText().toString().trim();
        String roll = rollEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String room = roomEditText.getText().toString().trim();
        int selectedId = hostel.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        String rb = radioButton.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                @Override
                                public void onSuccess(GetTokenResult getTokenResult) {
                                    token_id = FirebaseInstanceId.getInstance().getToken();
                                    String currentUser_Id = mAuth.getCurrentUser().getUid();

                                    databaseReference.child(currentUser_Id).child("tokenId").setValue(token_id);
                                }
                            });
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException)
                                Toast.makeText(register.this, "Invalid User", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(register.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        token_id = FirebaseInstanceId.getInstance().getToken();
        registerInfo info = new registerInfo(name, room, rb, roll, email, password, token_id);
        FirebaseUser user = mAuth.getCurrentUser();

        databaseReference.child(user.getUid()).setValue(info);
        Toast.makeText(this, "Welcome!!", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(), tracker.class);
        startActivity(i);
        setContentView(R.layout.activity_tracker);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerButton:
                registerUser();
                break;
            case R.id.loginbutton:
                loginUser();
                break;
        }
    }
}
