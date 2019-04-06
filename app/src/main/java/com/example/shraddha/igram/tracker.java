package com.example.shraddha.igram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class tracker extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "password";
    FloatingActionButton fab;
    DatabaseReference databaseReference, databaseReferenceUsers;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private complaintAdapter adapter;
    private List<newComplaint> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        list = new ArrayList<>();
        adapter = new complaintAdapter(list, this);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference("complaint");
        databaseReference.keepSynced(true);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(tracker.this, complaint.class));
            }
        });

        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                registerInfo student = dataSnapshot.getValue(registerInfo.class);
                ((TextView) findViewById(R.id.studentEmail)).setText(student.getEmail());
                ((TextView) findViewById(R.id.studentName)).setText(student.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(tracker.this, "Oops!! Something went wrong.", Toast.LENGTH_SHORT);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        newComplaint c = dataSnapshot1.getValue(newComplaint.class);
                        if (c.getStudentId().equals(user.getUid()))
                            list.add(c);
                        else
                            continue;
                    }
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(tracker.this, "Oops!! something went wrong.", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, settings.class));
                break;
            case R.id.logout:
                editor.putBoolean(KEY_REMEMBER, false);
                editor.remove(KEY_PASS);
                editor.remove(KEY_USERNAME);
                editor.apply();
                databaseReferenceUsers.child("tokenId").removeValue();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
        return true;
    }
}
