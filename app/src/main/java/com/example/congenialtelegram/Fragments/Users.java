package com.example.congenialtelegram.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.congenialtelegram.Adapters.PostAdapter;
import com.example.congenialtelegram.Adapters.UserAdapter;
import com.example.congenialtelegram.Models.PostModel;
import com.example.congenialtelegram.Models.UserModel;
import com.example.congenialtelegram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Users extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<UserModel> userModels;

    public Users() {
        // Required empty public constructor
    }

    public static Users newInstance() {
        return new Users();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        userModels = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getUsers();

        return view;
    }

    private void getUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String userUid = firebaseUser.getUid();

        final Map<String, Boolean> map = new HashMap<>();

        databaseReference.child(userUid).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String uid = (String) ds.getValue();
                    map.put(uid, true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot data = dataSnapshot.child("users");
                for(DataSnapshot ds: data.getChildren()){
                    String uid = (String) ds.getValue();
                    if(uid.equals(userUid))
                        continue;
                    if(map.containsKey(uid))
                        userModels.add(new UserModel(uid,true));
                    else
                        userModels.add(new UserModel(uid,false));
                    Log.e(" as",uid);
                }
                UserAdapter userAdapter = new UserAdapter(userModels);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
