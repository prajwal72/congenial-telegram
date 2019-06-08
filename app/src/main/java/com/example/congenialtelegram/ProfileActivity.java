package com.example.congenialtelegram;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.congenialtelegram.Adapters.PostAdapter;
import com.example.congenialtelegram.Models.PostModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ArrayList<PostModel> posts;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ImageView coverView;
    private ImageView profilePicView;
    private TextView nameView;
    private TextView aboutView;
    private TextView followerView;
    private TextView followingView;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        uid = intent.getExtras().getString("uid");

        recyclerView = findViewById(R.id.recyclerView);
        coverView = findViewById(R.id.coverPic);
        profilePicView = findViewById(R.id.profilePic);
        nameView = findViewById(R.id.name);
        aboutView = findViewById(R.id.about);
        followerView = findViewById(R.id.followers);
        followingView = findViewById(R.id.following);

        posts = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
        recyclerView.setNestedScrollingEnabled(false);

        setIntro();

        getPosts();
    }

    private void setIntro() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = (String) dataSnapshot.child(uid).child("cover_pic").getValue();
                if(url != null){
                    Uri uri = Uri.parse(url);
                    coverView.setBackground(null);
                    Glide.with(ProfileActivity.this)
                            .load(uri)
                            .into(coverView);
                }
                else {
                    Glide.with(ProfileActivity.this)
                            .load(R.drawable.header)
                            .into(coverView);
                }

                String profileUrl = (String) dataSnapshot.child(uid).child("profile_pic").getValue();
                if(profileUrl != null){
                    Uri uri = Uri.parse(profileUrl);
                    Glide.with(ProfileActivity.this)
                            .load(uri)
                            .into(profilePicView);
                }
                else {
                    Glide.with(ProfileActivity.this)
                            .load(R.drawable.profile_pic)
                            .into(profilePicView);
                }

                String name = (String) dataSnapshot.child(uid).child("name").getValue();
                nameView.setText(name);

                String about = (String) dataSnapshot.child(uid).child("about").getValue();
                if(about != null)
                    aboutView.setText(about);

                long numberOfFollowers = (long) dataSnapshot.child(uid).child("numberfollowers").getValue();
                String followers = Long.toString(numberOfFollowers).concat(" Followers");
                followerView.setText(followers);

                long numberOfFollowing = (long) dataSnapshot.child(uid).child("numberfollowing").getValue();
                String following = Long.toString(numberOfFollowing).concat(" Following");
                followingView.setText(following);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getPosts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot data = dataSnapshot.child(uid).child("posts");
                for(DataSnapshot ds: data.getChildren()){
                    PostModel post = ds.getValue(PostModel.class);
                    posts.add(post);
                }
                PostAdapter postAdapter = new PostAdapter(posts);
                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
