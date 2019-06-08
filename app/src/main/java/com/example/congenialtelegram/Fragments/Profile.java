package com.example.congenialtelegram.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.congenialtelegram.Adapters.PostAdapter;
import com.example.congenialtelegram.EditProfileActivity;
import com.example.congenialtelegram.Models.PostModel;
import com.example.congenialtelegram.PostActivity;
import com.example.congenialtelegram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Profile extends Fragment {

    private ArrayList<PostModel> posts;
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ImageView coverView;
    private ImageView profilePicView;
    private TextView nameView;
    private TextView aboutView;
    private TextView followerView;
    private TextView followingView;
    private Button editProfile;
    private Context context;
    private Button postButton;

    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance() {
        return new Profile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        coverView = view.findViewById(R.id.coverPic);
        profilePicView = view.findViewById(R.id.profilePic);
        nameView = view.findViewById(R.id.name);
        aboutView = view.findViewById(R.id.about);
        followerView = view.findViewById(R.id.followers);
        followingView = view.findViewById(R.id.following);
        editProfile = view.findViewById(R.id.editProfile);
        postButton = view.findViewById(R.id.postButton);
        context = view.getContext();
        posts = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PostActivity.class));
            }
        });

        setIntro();

        getPosts();

        return view;
    }

    private void setIntro() {
        final String uid = firebaseUser.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = (String) dataSnapshot.child(uid).child("cover_pic").getValue();
                if(url != null){
                    Uri uri = Uri.parse(url);
                    coverView.setBackground(null);
                    Glide.with(context)
                            .load(uri)
                            .into(coverView);
                }
                else {
                    Glide.with(context)
                            .load(R.drawable.header)
                            .into(coverView);
                }

                String profileUrl = (String) dataSnapshot.child(uid).child("profile_pic").getValue();
                if(profileUrl != null){
                    Uri uri = Uri.parse(profileUrl);
                    Glide.with(context)
                            .load(uri)
                            .into(profilePicView);
                }
                else {
                    Glide.with(context)
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
        final String uid = firebaseUser.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot data = dataSnapshot.child(uid).child("posts");
                for(DataSnapshot ds: data.getChildren()){
                    PostModel post = ds.getValue(PostModel.class);
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
}
