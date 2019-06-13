package com.example.congenialtelegram;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.congenialtelegram.Fragments.Dashboard;
import com.example.congenialtelegram.Fragments.Profile;
import com.example.congenialtelegram.Fragments.Settings;
import com.example.congenialtelegram.Fragments.Users;
import com.example.congenialtelegram.Models.NotifyModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "string";
    private ValueEventListener valueEventListener;
    private String userUid;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        createNotificationChannel();
        checkForMessages();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userUid = FirebaseAuth.getInstance().getUid();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Intent intent = getIntent();
        if(intent.getExtras() == null)
            loadFragment(Dashboard.newInstance());
        else {
            loadFragment(Profile.newInstance());
            bottomNavigationView.setSelectedItemId(R.id.profile);
        }
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

    private void checkForMessages() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        assert userUid != null;
        valueEventListener = databaseReference.child(userUid).child("notify").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    NotifyModel notify = data.getValue(NotifyModel.class);
                    String uid = data.getKey();
                    assert notify != null;
                    String userName = notify.getUserName();
                    Boolean bool = notify.getNotify();
                    if(bool){
                        Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                        intent.putExtra("name", userName);
                        intent.putExtra("uid", uid);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notifications)
                                .setContentTitle("You have unread messages from ".concat(userName))
                                .setContentText("")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        Random random = new Random();
                        int id = random.nextInt();
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(id, builder.build());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if(valueEventListener != null && userUid != null)
            databaseReference.child(userUid).child("notify").removeEventListener(valueEventListener);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
