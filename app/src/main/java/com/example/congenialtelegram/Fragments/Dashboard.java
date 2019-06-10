package com.example.congenialtelegram.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Collections;
import java.util.Comparator;

public class Dashboard extends Fragment {

    private ArrayList<PostModel> posts;
    private RecyclerView recyclerView;
    private ArrayList<String> uids;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        posts = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPosts();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getPosts();

        return  view;
    }

    private void getPosts() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        assert firebaseUser != null;
        final String userUid = firebaseUser.getUid();
        uids = new ArrayList<>();

        databaseReference.child(userUid).child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uids.add(userUid);
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String uid = (String) ds.getValue();
                    uids.add(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for(String id: uids){
                    DataSnapshot data = dataSnapshot.child(id).child("posts");
                    String author = (String) dataSnapshot.child(id).child("name").getValue();
                    String profileImage = (String) dataSnapshot.child(id).child("profile_pic").getValue();
                    for(DataSnapshot ds: data.getChildren()){
                        PostModel post = ds.getValue(PostModel.class);
                        assert post != null;
                        post.setAuthor(author);
                        post.setProfileImageUrl(profileImage);
                        posts.add(post);
                    }
                }
                Collections.sort(posts, new Comparator<PostModel>() {
                    @Override
                    public int compare(PostModel o1, PostModel o2) {
                        return o2.getLastModifiedDate().compareTo(o1.getLastModifiedDate());
                    }
                });
                PostAdapter postAdapter = new PostAdapter(posts);
                recyclerView.setAdapter(postAdapter);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
