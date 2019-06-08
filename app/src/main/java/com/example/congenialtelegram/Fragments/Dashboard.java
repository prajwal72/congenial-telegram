package com.example.congenialtelegram.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.congenialtelegram.Adapters.PostAdapter;
import com.example.congenialtelegram.Models.PostModel;
import com.example.congenialtelegram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Dashboard extends Fragment {

    private ArrayList<PostModel> posts;
    private RecyclerView recyclerView;
    private ArrayList<String> uids;

    public Dashboard() {
        // Required empty public constructor
    }

    public static Dashboard newInstance() {
        return new Dashboard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        posts = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getPosts();

        return  view;
    }

    private void getPosts() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String userUid = firebaseUser.getUid();
        uids = new ArrayList<>();
        uids.add(userUid);

        databaseReference.child(userUid).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String uid = (String) ds.getValue();
                    uids.add(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(String id: uids){
                    DataSnapshot data = dataSnapshot.child(id).child("posts");
                    for(DataSnapshot ds: data.getChildren()){
                        PostModel post = ds.getValue(PostModel.class);
                        posts.add(post);
                    }
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
