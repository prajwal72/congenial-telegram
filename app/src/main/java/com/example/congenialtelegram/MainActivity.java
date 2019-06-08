package com.example.congenialtelegram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.congenialtelegram.Fragments.Dashboard;
import com.example.congenialtelegram.Fragments.Profile;
import com.example.congenialtelegram.Fragments.Settings;
import com.example.congenialtelegram.Fragments.Users;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(Dashboard.newInstance());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()){
                    case R.id.dashboard:
                        fragment = Dashboard.newInstance();
                        loadFragment(fragment);
                        return true;
                    case R.id.users:
                        fragment = Users.newInstance();
                        loadFragment(fragment);
                        return true;
                    case R.id.profile:
                        fragment = Profile.newInstance();
                        loadFragment(fragment);
                        return true;
                    case R.id.settings:
                        fragment = Settings.newInstance();
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
