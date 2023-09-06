package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    ActionBar actionBar;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Home");
        }
        loadFragment(new HomeFragment());

        bottomNavigationView = findViewById(R.id.bottom_nav);
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
