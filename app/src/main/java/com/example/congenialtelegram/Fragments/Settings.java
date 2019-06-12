package com.example.congenialtelegram.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.congenialtelegram.LoginActivity;
import com.example.congenialtelegram.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends Fragment {

    private FirebaseAuth firebaseAuth;
    private Context context;

    public Settings() {
        // Required empty public constructor
    }

    public static Settings newInstance() {
        return new Settings();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView changePassword = view.findViewById(R.id.changePassword);
        TextView logOut = view.findViewById(R.id.logOut);
        context = container.getContext();

        firebaseAuth = FirebaseAuth.getInstance();

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Toast.makeText(context, "Logged Out", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        return view;
    }

    private void updatePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.change_password_dialog, null);
        builder.setView(v);
        final Dialog dialog = builder.create();
        dialog.setContentView(R.layout.change_password_dialog);
        dialog.show();

        final EditText passwordEdit = dialog.findViewById(R.id.password);
        final EditText confirmPasswordEdit = dialog.findViewById(R.id.confirmPassword);
        TextView updateButton = dialog.findViewById(R.id.updateButton);
        TextView cancelButton = dialog.findViewById(R.id.cancel);

        assert updateButton != null;
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert passwordEdit != null;
                String password = passwordEdit.getText().toString().trim();
                assert confirmPasswordEdit != null;
                String confirmPassword = confirmPasswordEdit.getText().toString().trim();
                boolean bool = validate(password, confirmPassword);
                if(bool){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    firebaseUser.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Password successfully changed", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Password changing Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        assert cancelButton != null;
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private boolean validate(String password1, String password2) {
        if(password1.length() < 6){
            Toast.makeText(context,"Password should be minimum 6 characters", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!password1.equals(password2)){
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
