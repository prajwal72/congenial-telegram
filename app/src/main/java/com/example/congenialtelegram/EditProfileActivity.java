package com.example.congenialtelegram;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.congenialtelegram.Models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Random;

public class EditProfileActivity extends AppCompatActivity {

    private static final int REQ_CODE_IMAGE_INPUT = 1;
    private Button updateCoverButton;
    private Button updateProfilePictureButton;
    private Button updateProfileButton;
    private EditText nameEdit;
    private EditText aboutEdit;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        updateCoverButton = findViewById(R.id.coverPic);
        updateProfileButton = findViewById(R.id.updateButton);
        updateProfilePictureButton = findViewById(R.id.profilePic);
        nameEdit = findViewById(R.id.name);
        aboutEdit = findViewById(R.id.about);
        progressBar = findViewById(R.id.progressBar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(uid);

        setData();

        updateProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, ChangeProfilePictureActivity.class));
            }
        });

        updateCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void setData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                nameEdit.setText(name);

                String about = (String) dataSnapshot.child("about").getValue();
                if(about != null)
                    aboutEdit.setText(about);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void chooseImage() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , REQ_CODE_IMAGE_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_IMAGE_INPUT && data != null){
            uploadImage(data);
        }
    }


    private void uploadImage(@Nullable Intent data) {
        final Uri selectedImage;

        if(data != null)
            selectedImage = data.getData();
        else
            selectedImage = null;

        if(selectedImage != null){
            final String randomString;
            Random random =  new Random();
            long rand = random.nextLong();
            randomString = Long.toString(rand);

            final StorageReference newReference = FirebaseStorage.getInstance().getReference().child("cover_pic").child(uid).child(randomString);

            progressBar.setVisibility(View.VISIBLE);
            nameEdit.setEnabled(false);
            aboutEdit.setEnabled(false);

            newReference.putFile(selectedImage)
                    .addOnSuccessListener(EditProfileActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            newReference.getDownloadUrl().addOnSuccessListener(EditProfileActivity.this, new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Date date = new Date();
                                    PostModel postModel = new PostModel(randomString, firebaseUser.getUid(), null, uri.toString(), date);
                                    databaseReference.child("posts").child(randomString).setValue(postModel);
                                    databaseReference.child("cover_pic").setValue(uri.toString());
                                }
                            });
                            Toast.makeText(EditProfileActivity.this, "Cover Picture Changed Successfully", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            nameEdit.setEnabled(true);
                            aboutEdit.setEnabled(true);
                        }
                    })
                    .addOnFailureListener(EditProfileActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Error while changing Cover Picture", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            nameEdit.setEnabled(true);
                            aboutEdit.setEnabled(true);
                        }
                    });
        }
    }

    private void updateProfile(){
        String name = nameEdit.getText().toString().trim();
        String about = aboutEdit.getText().toString().trim();
        databaseReference.child("name").setValue(name);
        databaseReference.child("about").setValue(about);

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        firebaseUser.updateProfile(request);

        Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
    }
}
