package com.example.congenialtelegram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class ChangeProfilePictureActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private static final int REQ_CODE_IMAGE_INPUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);

        Button uploadButton = findViewById(R.id.uploadButton);
        Button removeButton = findViewById(R.id.removeButton);
        profileImageView = findViewById(R.id.profileImage);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = firebaseUser.getUid();
                databaseReference.child(uid).child("profile_pic").setValue("null");
                Glide.with(ChangeProfilePictureActivity.this)
                        .load(R.drawable.profile_pic)
                        .into(profileImageView);
                Toast.makeText(ChangeProfilePictureActivity.this, "Profile Picture Removed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_IMAGE_INPUT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_IMAGE_INPUT && data != null){
            uploadImage(data);
        }
    }


    private void uploadImage(@Nullable Intent data) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        Uri selectedImage = null;

        if(data != null)
            selectedImage = data.getData();

        if(selectedImage != null){
            final String uid = firebaseUser.getUid();
            String randomString;
            Random random =  new Random();
            StorageReference newReference = storageReference.child("profile_pic").child(uid);
            long rand = random.nextLong();
            randomString = Long.toString(rand);

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Changing Profile Picture");
            progressDialog.show();

            final Uri finalSelectedImage = selectedImage;
            newReference.child(randomString).putFile(selectedImage)
                    .addOnSuccessListener(ChangeProfilePictureActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Glide.with(ChangeProfilePictureActivity.this)
                                    .load(finalSelectedImage)
                                    .into(profileImageView);
                            progressDialog.dismiss();
                            Toast.makeText(ChangeProfilePictureActivity.this, "Profile Picture Changed Successfully", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(ChangeProfilePictureActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangeProfilePictureActivity.this, "Error while changing Profile Picture", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(ChangeProfilePictureActivity.this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage(" "+(int)progress+"%");
                        }
                    });

            newReference.getDownloadUrl().addOnSuccessListener(ChangeProfilePictureActivity.this, new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    databaseReference.child(uid).child("profile_pic").setValue(uri);
                }
            });

        }
    }
}
