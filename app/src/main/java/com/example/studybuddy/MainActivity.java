package com.example.studybuddy;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    ActionBar actionBar;
    private DatabaseReference RootRef;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        if (currentUser == null)
        {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        actionBar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.bottom_nav);

        Intent intent = getIntent();
        String fragmentTag = intent.getStringExtra("FragmentToLoad");

        if (savedInstanceState == null) {
            if ("ChatFragment".equals(fragmentTag)) {
                bottomNavigationView.setSelectedItemId(R.id.chat);
                actionBar.setTitle("Chat");
                loadFragment(new ChatFragment());
            } else if ("ProfileFragment".equals(fragmentTag)) {
                bottomNavigationView.setSelectedItemId(R.id.profile);
                actionBar.setTitle("Profile");
                loadFragment(new ProfileFragment());
            } else {
                actionBar.setTitle("Home");
                loadFragment(new HomeFragment());
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                actionBar.setTitle("Home");
                loadFragment(new HomeFragment());
            } else if (id == R.id.calendar) {
                actionBar.setTitle("Calendar");
                loadFragment(new CalendarFragment());
            } else if (id == R.id.add) {
                actionBar.setTitle("Add");
                loadFragment(new AddFragment());
            } else if (id == R.id.chat) {
                actionBar.setTitle("Chat");
                loadFragment(new ChatFragment());
            } else if (id == R.id.profile) {
                actionBar.setTitle("Profile");
                loadFragment(new ProfileFragment());
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
