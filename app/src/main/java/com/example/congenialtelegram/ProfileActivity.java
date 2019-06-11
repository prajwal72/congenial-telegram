package com.example.congenialtelegram;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.congenialtelegram.Adapters.PostAdapter;
import com.example.congenialtelegram.Models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static com.example.congenialtelegram.R.drawable.ic_check;
import static com.example.congenialtelegram.R.drawable.ic_person_add;

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
    private ImageView followButton;
    private ImageView messageButton;
    private String uid;
    private boolean bool;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        uid = Objects.requireNonNull(intent.getExtras()).getString("uid");
        bool = intent.getExtras().getBoolean("follows");


        recyclerView = findViewById(R.id.recyclerView);
        coverView = findViewById(R.id.coverPic);
        profilePicView = findViewById(R.id.profilePic);
        nameView = findViewById(R.id.name);
        aboutView = findViewById(R.id.about);
        followerView = findViewById(R.id.followers);
        followingView = findViewById(R.id.following);
        followButton = findViewById(R.id.followButton);
        messageButton = findViewById(R.id.messageButton);
        userUid = FirebaseAuth.getInstance().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        setIntro();

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bool)
                    unfollow();
                else
                    follow();

                bool = !bool;
            }
        });


        posts = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
        recyclerView.setNestedScrollingEnabled(false);

        getPosts();
    }

    private void setIntro() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String url = (String) dataSnapshot.child(uid).child("cover_pic").getValue();
                if(url != null){
                    Uri uri = Uri.parse(url);
                    coverView.setBackground(null);
                    Glide.with(ProfileActivity.this)
                            .load(uri)
                            .into(coverView);
                    coverView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ImageViewActivity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                        }
                    });
                }
                else {
                    Glide.with(ProfileActivity.this)
                            .load(R.drawable.header)
                            .into(coverView);
                }

                final String profileUrl = (String) dataSnapshot.child(uid).child("profile_pic").getValue();
                if(profileUrl != null){
                    Uri uri = Uri.parse(profileUrl);
                    Glide.with(ProfileActivity.this)
                            .load(uri)
                            .into(profilePicView);
                    profilePicView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, ImageViewActivity.class);
                            intent.putExtra("url", profileUrl);
                            startActivity(intent);
                        }
                    });
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

        if(bool){
            followButton.setImageDrawable(getResources().getDrawable(ic_check));
        }

    }

    private void getPosts() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot data = dataSnapshot.child(uid).child("posts");
                String author = (String) dataSnapshot.child(uid).child("name").getValue();
                String profileImage = (String) dataSnapshot.child(uid).child("profile_pic").getValue();
                for(DataSnapshot ds: data.getChildren()){
                    PostModel post = ds.getValue(PostModel.class);
                    assert post != null;
                    post.setAuthor(author);
                    post.setProfileImageUrl(profileImage);
                    posts.add(post);
                }
                Collections.sort(posts, new Comparator<PostModel>() {
                    @Override
                    public int compare(PostModel o1, PostModel o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
                PostAdapter postAdapter = new PostAdapter(posts);
                recyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void follow(){
        followButton.setImageDrawable(getResources().getDrawable(ic_check));

        databaseReference.child(userUid).child("following").child(uid).setValue(uid);
        databaseReference.child(uid).child("followers").child(userUid).setValue(userUid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long number = (long) dataSnapshot.child(userUid).child("numberfollowing").getValue();
                databaseReference.child(userUid).child("numberfollowing").setValue(number + 1);
                long number2 = (long) dataSnapshot.child(uid).child("numberfollowers").getValue();
                databaseReference.child(uid).child("numberfollowers").setValue(number2 + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void unfollow(){
        followButton.setImageDrawable(getResources().getDrawable(ic_person_add));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.child(userUid).child("following").child(uid).getRef().removeValue();
                dataSnapshot.child(uid).child("followers").child(userUid).getRef().removeValue();
                long number = (long) dataSnapshot.child(userUid).child("numberfollowing").getValue();
                databaseReference.child(userUid).child("numberfollowing").setValue(number - 1);
                long number2 = (long) dataSnapshot.child(uid).child("numberfollowers").getValue();
                databaseReference.child(uid).child("numberfollowers").setValue(number2 - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
