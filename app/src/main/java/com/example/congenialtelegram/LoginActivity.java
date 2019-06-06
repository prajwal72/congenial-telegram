package com.example.congenialtelegram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText emailView = findViewById(R.id.login_email);
        final EditText passwordView = findViewById(R.id.login_password);
        Button signInButton = findViewById(R.id.login_button);
        TextView createAccountButton = findViewById(R.id.create_account_button);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = emailView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();

                signIn(email, password);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null && user.isEmailVerified())
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private void signIn(String email, String password){
        if(!validate(email, password))
            return;

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user != null && !user.isEmailVerified())
                                startActivity(new Intent(LoginActivity.this, EmailNotVerifiedActivity.class));
                            else if (user != null){
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                String uid = user.getUid();
                                databaseReference.child(uid).child("profile_pic").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String profilePic = (String) dataSnapshot.getValue();
                                        if(profilePic == null)
                                            startActivity(new Intent(LoginActivity.this, ChangeProfilePictureActivity.class));
                                        else
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        else
                            Toast.makeText(LoginActivity.this,"Incorrect email id or Password", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validate(String email, String password) {
        if(!email.contains("@") || !email.contains(".")){
            Toast.makeText(this,"Invalid Email", Toast.LENGTH_LONG).show();
            return false;
        }

        if(password.length() < 8){
            Toast.makeText(this,"Password should be minimum 8 characters", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
