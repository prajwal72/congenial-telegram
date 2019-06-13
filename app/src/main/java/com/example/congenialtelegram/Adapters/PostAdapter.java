package com.example.congenialtelegram.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.congenialtelegram.ImageViewActivity;
import com.example.congenialtelegram.MainActivity;
import com.example.congenialtelegram.Models.CommentModel;
import com.example.congenialtelegram.Models.PostModel;
import com.example.congenialtelegram.ProfileActivity;
import com.example.congenialtelegram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<PostModel> postModels;
    private ArrayList<CommentModel> commentModels;
    private Context context;
    private CommentAdapter commentAdapter;
    private String userUid;

    public PostAdapter(ArrayList<PostModel> postModels){
        this.postModels = postModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.post, viewGroup, false);
        return new ViewHolder(view, context, postModels);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
        userUid = FirebaseAuth.getInstance().getUid();

        setProfilePicture(viewHolder, index);
        setAuthor(viewHolder, index);
        setDate(viewHolder, index);
        setCaption(viewHolder, index);
        setImage(viewHolder, index);
        setInfoBar(viewHolder, index);
        setLikeButton(viewHolder, index);
        setCommentButton(viewHolder, index);
        setShareButton(viewHolder, index);
    }

    private void setProfilePicture(ViewHolder viewHolder, final int index) {
        String profileImage = postModels.get(index).getProfileImageUrl();
        if(profileImage != null){
            Uri uri = Uri.parse(profileImage);
            Glide.with(context)
                    .load(uri)
                    .into(viewHolder.profileImageView);
        }
        else{
            Glide.with(context)
                    .load(R.drawable.profile_pic)
                    .into(viewHolder.profileImageView);
        }

        viewHolder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postModels.get(index).getProfileImageUrl() != null){
                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putExtra("url", postModels.get(index).getProfileImageUrl());
                    context.startActivity(intent);
                }
            }
        });
    }

    private void setAuthor(ViewHolder viewHolder, int index) {
        String author = postModels.get(index).getAuthor();
        final String uid = postModels.get(index).getUid();

        viewHolder.authorView.setText(author);
        viewHolder.authorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userUid = FirebaseAuth.getInstance().getUid();
                if(!uid.equals(userUid)){
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("follows", true);
                    context.startActivity(intent);
                }
            }
        });
    }

    private void setDate(ViewHolder viewHolder, int index) {
        Date date = postModels.get(index).getDate();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        String strDate = formatter.format(date);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        String strTime = timeFormatter.format(date);
        String time = strDate.concat(" at ").concat(strTime);

        viewHolder.dateView.setText(time);
    }

    private void setCaption(ViewHolder viewHolder, int index) {
        if(postModels.get(index).getCaption() != null)
            viewHolder.captionView.setText(postModels.get(index).getCaption());
        else
            viewHolder.captionView.setText(null);
    }


    private void setImage(ViewHolder viewHolder, final int index) {
        if(postModels.get(index).getImageUrl() != null){
            String url = postModels.get(index).getImageUrl();
            Uri uri = Uri.parse(url);
            Glide.with(context)
                    .load(uri)
                    .into(viewHolder.imageView);
        }
        else{
            Glide.with(context)
                    .load((Bitmap) null)
                    .into(viewHolder.imageView);
        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postModels.get(index).getImageUrl() != null){
                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putExtra("url", postModels.get(index).getImageUrl());
                    context.startActivity(intent);
                }
            }
        });
    }

    private void setInfoBar(ViewHolder viewHolder, int index) {
        int numberOfLikes = postModels.get(index).getNumberOfLikes();
        int numberOfComments = postModels.get(index).getNumberOfComments();
        String likes = Integer.toString(numberOfLikes);
        String comments = Integer.toString(numberOfComments);

        if(numberOfLikes == 0 && numberOfComments == 0)
            viewHolder.infoView.setText(null);
        else if(numberOfLikes == 0){
            if(numberOfComments > 1)
                viewHolder.infoView.setText(comments.concat(" comments"));
            else
                viewHolder.infoView.setText(comments.concat(" comment"));
        }
        else if(numberOfComments == 0){
            if(numberOfLikes > 1)
                viewHolder.infoView.setText(likes.concat(" likes"));
            else
                viewHolder.infoView.setText(likes.concat(" like"));
        }
        else{
            if(numberOfComments == 1 && numberOfLikes == 1)
                viewHolder.infoView.setText(likes.concat(" like  ").concat(comments).concat(" comment"));
            else if(numberOfComments == 1)
                viewHolder.infoView.setText(likes.concat(" likes  ").concat(comments).concat(" comment"));
            else if(numberOfLikes == 1)
                viewHolder.infoView.setText(likes.concat(" like  ").concat(comments).concat(" comments"));
            else
                viewHolder.infoView.setText(likes.concat(" likes  ").concat(comments).concat(" comments"));
        }

    }

    private void setLikeButton(final ViewHolder viewHolder, final int index) {
        Map<String, Boolean> likes = postModels.get(index).getLikes();
        if(likes.containsKey(userUid)){
            viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
        }else{
            viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
        }

        final boolean[] liked = new boolean[1];
        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = postModels.get(index).getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(uid);
                String id = postModels.get(index).getId();
                DatabaseReference postRef = databaseReference.child("posts").child(id);
                postRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        PostModel p = mutableData.getValue(PostModel.class);
                        if(p == null) {
                            return Transaction.success(mutableData);
                        }

                        if(p.likes.containsKey(userUid)){
                            p.numberOfLikes = p.numberOfLikes - 1;
                            p.likes.remove(userUid);
                            liked[0] = false;
                        }
                        else {
                            p.numberOfLikes = p.numberOfLikes + 1;
                            assert userUid != null;
                            p.likes.put(userUid, true);
                            liked[0] = true;
                        }

                        mutableData.setValue(p);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        Map<String, Boolean> likes = postModels.get(index).getLikes();
                        if(liked[0]){
                            viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
                            int numberOfLikes = postModels.get(index).getNumberOfLikes() + 1;
                            postModels.get(index).setNumberOfLikes(numberOfLikes);
                            assert userUid != null;
                            likes.put(userUid, true);
                        }
                        else{
                            viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
                            int numberOfLikes = postModels.get(index).getNumberOfLikes() - 1;
                            postModels.get(index).setNumberOfLikes(numberOfLikes);
                            likes.remove(userUid);
                        }
                        postModels.get(index).setLikes(likes);
                        setInfoBar(viewHolder, index);
                    }
                });
            }
        });
    }

    private void setCommentButton(final ViewHolder viewHolder, final int index) {
        final String uid = postModels.get(index).getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(uid);
        commentModels = new ArrayList<>();

        viewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view = inflater.inflate(R.layout.layout_comment, null);
                builder.setView(view);
                final Dialog dialog = builder.create();
                dialog.setContentView(R.layout.layout_comment);
                dialog.show();

                final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                final EditText commentEdit = view.findViewById(R.id.comment);
                Button postButton = view.findViewById(R.id.postButton);

                final String id = postModels.get(index).getId();
                final DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference();
                commentModels.clear();
                commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DataSnapshot data = dataSnapshot.child(uid).child("posts").child(id).child("comments");
                        for(DataSnapshot ds: data.getChildren()){
                            CommentModel comment = ds.getValue(CommentModel.class);
                            assert comment != null;
                            String authorUid = comment.getUid();
                            String author = (String) dataSnapshot.child(authorUid).child("name").getValue();
                            String profileUrl = (String) dataSnapshot.child(authorUid).child("profile_pic").getValue();
                            comment.setAuthor(author);
                            comment.setProfileImageUrl(profileUrl);
                            commentModels.add(comment);
                        }

                        Collections.sort(commentModels, new Comparator<CommentModel>() {
                            @Override
                            public int compare(CommentModel o1, CommentModel o2) {
                                return o1.getDate().compareTo(o2.getDate());
                            }
                        });
                        commentAdapter = new CommentAdapter(commentModels);
                        recyclerView.setAdapter(commentAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                postButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String comment = commentEdit.getText().toString().trim();
                        if(!comment.equals("")){
                            final String randomString;
                            Random random =  new Random();
                            long rand = random.nextLong();
                            randomString = Long.toString(rand);
                            final Date date = new Date();

                            commentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    CommentModel commentModel = new CommentModel(randomString, userUid, comment, date);
                                    String author = (String) dataSnapshot.child(userUid).child("name").getValue();
                                    String profileUrl = (String) dataSnapshot.child(userUid).child("profile_pic").getValue();
                                    databaseReference.child("posts").child(id).child("comments").child(randomString).setValue(commentModel);
                                    commentModel.setAuthor(author);
                                    commentModel.setProfileImageUrl(profileUrl);
                                    commentModels.add(commentModel);
                                    commentEdit.setText("");
                                    commentAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            String id = postModels.get(index).getId();
                            DatabaseReference postRef = databaseReference.child("posts").child(id);
                            postRef.runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                    PostModel p = mutableData.getValue(PostModel.class);
                                    if(p == null) {
                                        return Transaction.success(mutableData);
                                    }
                                    Date date = new Date();

                                    p.numberOfComments = p.numberOfComments + 1;
                                    p.lastModifiedDate = date;
                                    mutableData.setValue(p);
                                    return Transaction.success(mutableData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                    int numberOfComments = postModels.get(index).getNumberOfComments() + 1;
                                    postModels.get(index).setNumberOfComments(numberOfComments);
                                    setInfoBar(viewHolder, index);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void setShareButton(ViewHolder viewHolder, int index) {
        final String caption = postModels.get(index).getCaption();
        final String imageUrl = postModels.get(index).getImageUrl();
        if(imageUrl != null) {
            final StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            try {
                final File localFile = File.createTempFile("images", ".jpg");
                viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Opening Menu");
                        progressDialog.show();
                        storageRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                progressDialog.dismiss();
                                String message = caption;
                                Uri uri = FileProvider.getUriForFile(context, "com.example.congenialtelegram.provider", localFile);
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                share.putExtra(Intent.EXTRA_STREAM, uri);
                                share.setType("image/*");
                                share.putExtra(Intent.EXTRA_TEXT, message);

                                context.startActivity(Intent.createChooser(share, "Share"));
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = caption;
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.setType("text/*");
                    share.putExtra(Intent.EXTRA_TEXT, message);

                    context.startActivity(Intent.createChooser(share, "Share"));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView authorView;
        private TextView dateView;
        private TextView captionView;
        private ImageView imageView;
        private ImageView profileImageView;
        private TextView infoView;
        private ImageButton likeButton;
        private ImageButton commentButton;
        private ImageButton shareButton;

        private ViewHolder(@NonNull View itemView, Context context, ArrayList<PostModel> postModels) {
            super(itemView);
            authorView = itemView.findViewById(R.id.author);
            dateView = itemView.findViewById(R.id.date);
            captionView = itemView.findViewById(R.id.caption);
            imageView = itemView.findViewById(R.id.image);
            profileImageView = itemView.findViewById(R.id.profileImage);
            infoView = itemView.findViewById(R.id.info);
            likeButton = itemView.findViewById(R.id.like);
            commentButton = itemView.findViewById(R.id.comment);
            shareButton = itemView.findViewById(R.id.share);
        }
    }
}
