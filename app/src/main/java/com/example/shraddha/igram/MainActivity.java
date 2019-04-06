package com.example.shraddha.igram;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {

    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "password";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private EditText emailEditText, passwordEditText;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private CheckBox rememberCheckBox;
    private String token_id;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        findViewById(R.id.registerTextView).setOnClickListener(this);
        findViewById(R.id.loginButton).setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        rememberCheckBox = (CheckBox) findViewById(R.id.checkbox);

        if (sharedPreferences.getBoolean(KEY_REMEMBER, false))
            rememberCheckBox.setChecked(true);
        else
            rememberCheckBox.setChecked(false);

        emailEditText.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        passwordEditText.setText(sharedPreferences.getString(KEY_PASS, ""));

        emailEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        rememberCheckBox.setOnCheckedChangeListener(this);

        loginUser();

    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
            emailEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        }
        progressDialog.setMessage("Logging In...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                @Override
                                public void onSuccess(GetTokenResult getTokenResult) {
                                    token_id = FirebaseInstanceId.getInstance().getToken();
                                    String currentUser_Id = firebaseAuth.getCurrentUser().getUid();

                                    databaseReference.child(currentUser_Id).child("tokenId").setValue(token_id);
                                }
                            });
                            Intent i = new Intent(getApplicationContext(), tracker.class);
                            startActivity(i);
                            setContentView(R.layout.activity_tracker);
                        } else
                            Toast.makeText(MainActivity.this, "LogIn Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerTextView:
                startActivity(new Intent(this, register.class));
                break;
            case R.id.loginButton:
                loginUser();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        managePrefs();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        managePrefs();
    }

    private void managePrefs() {
        if (rememberCheckBox.isChecked()) {
            editor.putString(KEY_USERNAME, emailEditText.getText().toString().trim());
            editor.putString(KEY_PASS, passwordEditText.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        } else {
            editor.putBoolean(KEY_REMEMBER, false);
            editor.remove(KEY_PASS);
            editor.remove(KEY_USERNAME);
            editor.apply();
        }
    }
}
