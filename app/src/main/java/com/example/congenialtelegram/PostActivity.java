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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.congenialtelegram.Models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Random;

public class PostActivity extends AppCompatActivity {

    private static final int REQ_CODE_IMAGE_INPUT = 1;
    private EditText captionEdit;
    private Button imageButton;
    private Button postButton;
    private Button discardButton;
    private ImageView imageView;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private String uid;
    private DatabaseReference databaseReference;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        captionEdit = findViewById(R.id.caption);
        imageButton = findViewById(R.id.uploadImageButton);
        postButton = findViewById(R.id.postButton);
        discardButton = findViewById(R.id.discardButton);
        imageView = findViewById(R.id.image);
        progressBar = findViewById(R.id.progressBar);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(uid);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostActivity.this.finish();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postUpdate();
                startActivity(new Intent(PostActivity.this, MainActivity.class));
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

            final StorageReference newReference = FirebaseStorage.getInstance().getReference().child("posts").child(uid).child(randomString);

            progressBar.setVisibility(View.VISIBLE);
            captionEdit.setEnabled(false);

            newReference.putFile(selectedImage)
                    .addOnSuccessListener(PostActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Glide.with(PostActivity.this)
                                    .load(selectedImage)
                                    .into(imageView);

                            newReference.getDownloadUrl().addOnSuccessListener(PostActivity.this, new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url = uri.toString();
                                }
                            });
                            progressBar.setVisibility(View.INVISIBLE);
                            captionEdit.setEnabled(true);
                        }
                    })
                    .addOnFailureListener(PostActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostActivity.this, "Error while adding Image", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            captionEdit.setEnabled(true);
                        }
                    });
        }
    }

    private void postUpdate(){
        final String randomString;
        Random random =  new Random();
        long rand = random.nextLong();
        randomString = Long.toString(rand);

        String caption = captionEdit.getText().toString().trim();
        if(caption.equals("") && url == null)
            PostActivity.this.finish();
        else{
            Date date = new Date();
            PostModel postModel = new PostModel(randomString, firebaseUser.getUid(), caption, url, date);
            databaseReference.child("posts").child(randomString).setValue(postModel);
        }
    }
}
