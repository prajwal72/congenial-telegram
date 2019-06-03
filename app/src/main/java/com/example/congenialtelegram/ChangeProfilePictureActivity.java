package com.example.congenialtelegram;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class ChangeProfilePictureActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private String uid;

    private static final int REQ_CODE_IMAGE_INPUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);

        Button uploadButton = findViewById(R.id.uploadButton);
        Button removeButton = findViewById(R.id.removeButton);
        TextView backButton = findViewById(R.id.backButton);
        profileImageView = findViewById(R.id.profileImage);
        progressBar = findViewById(R.id.progressBar);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
            uid = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(uid).child("profile_pic");

        setImage();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangeProfilePictureActivity.this, MainActivity.class));
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.setValue("null");
                Glide.with(ChangeProfilePictureActivity.this)
                        .load(R.drawable.profile_pic)
                        .into(profileImageView);
                Toast.makeText(ChangeProfilePictureActivity.this, "Profile Picture Removed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setImage() {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = (String) dataSnapshot.getValue();
                if(url == "null"){
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                Uri uri = Uri.parse(url);
                Glide.with(ChangeProfilePictureActivity.this)
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.profile_pic)
                        .into(profileImageView);
                progressBar.setVisibility(View.INVISIBLE);
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
            String randomString;
            Random random =  new Random();
            long rand = random.nextLong();
            randomString = Long.toString(rand);

            final StorageReference newReference = FirebaseStorage.getInstance().getReference().child("profile_pic").child(uid).child(randomString);

            progressBar.setVisibility(View.VISIBLE);

            newReference.putFile(selectedImage)
                    .addOnSuccessListener(ChangeProfilePictureActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Glide.with(ChangeProfilePictureActivity.this)
                                    .load(selectedImage)
                                    .centerCrop()
                                    .placeholder(R.drawable.profile_pic)
                                    .into(profileImageView);

                            newReference.getDownloadUrl().addOnSuccessListener(ChangeProfilePictureActivity.this, new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    databaseReference.setValue(uri.toString());
                                }
                            });

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(ChangeProfilePictureActivity.this, "Profile Picture Changed Successfully", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(ChangeProfilePictureActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangeProfilePictureActivity.this, "Error while changing Profile Picture", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
