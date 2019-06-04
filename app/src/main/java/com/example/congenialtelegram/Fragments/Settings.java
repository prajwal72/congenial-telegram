package com.example.congenialtelegram.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.congenialtelegram.ChangeProfilePictureActivity;
import com.example.congenialtelegram.LoginActivity;
import com.example.congenialtelegram.MainActivity;
import com.example.congenialtelegram.R;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends Fragment {

    private FirebaseAuth firebaseAuth;

    public Settings() {
        // Required empty public constructor
    }

    public static Settings newInstance() {
        Settings fragment = new Settings();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button button = view.findViewById(R.id.button);
        Button button2 = view.findViewById(R.id.button2);

        firebaseAuth = FirebaseAuth.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangeProfilePictureActivity.class));
            }
        });

        return view;
    }

}
