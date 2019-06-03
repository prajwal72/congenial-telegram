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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        final EditText nameView = findViewById(R.id.signup_name);
        final EditText emailView = findViewById(R.id.signup_email);
        final EditText passwordView = findViewById(R.id.signup_password);
        final EditText confirmPasswordView = findViewById(R.id.confirm_signup_password);
        Button signUpButton = findViewById(R.id.signup_button);
        TextView signInButton = findViewById(R.id.back_button);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String name = nameView.getText().toString().trim();
                String email = emailView.getText().toString().trim();
                String password1 = passwordView.getText().toString().trim();
                String password2 = confirmPasswordView.getText().toString().trim();

                SignUp(name, email, password1, password2);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
            }
        });
    }

    private void SignUp(final String name, String email, String password1, String password2) {
        if(!validate(name, email, password1, password2))
            return;

        firebaseAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if(firebaseUser == null)
                                return;
                            String uid = firebaseUser.getUid();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                            databaseReference.child(uid).child("profile_pic").setValue("null");
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            firebaseUser.updateProfile(request);

                            verifyEmail();
                            startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
                        }
                        else{
                            Toast.makeText(CreateAccountActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validate(String name, String email, String password1, String password2) {
        if(name.length() == 0){
            Toast.makeText(this,"Name cannot be empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!email.contains("@") || !email.contains(".")){
            Toast.makeText(this,"Invalid Email", Toast.LENGTH_LONG).show();
            return false;
        }

        if(password1.length() < 8){
            Toast.makeText(this,"Password should be minimum 8 characters", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!password1.equals(password2)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void verifyEmail() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null)
            firebaseUser.sendEmailVerification();
    }
}
