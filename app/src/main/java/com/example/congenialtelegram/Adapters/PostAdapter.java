package com.example.congenialtelegram.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.congenialtelegram.Models.PostModel;
import com.example.congenialtelegram.ProfileActivity;
import com.example.congenialtelegram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<PostModel> postModels;
    private Context context;

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
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int index) {
        Date date = postModels.get(index).getDate();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        String strDate = formatter.format(date);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        String strTime = timeFormatter.format(date);
        String time = strDate.concat(" at ").concat(strTime);

        final String userUid = FirebaseAuth.getInstance().getUid();

        final String uid = postModels.get(index).getUid();
        final String id = postModels.get(index).getId();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(uid);

        String author = postModels.get(index).getAuthor();
        String profileImage = postModels.get(index).getProfileImageUrl();
        viewHolder.authorView.setText(author);
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

        viewHolder.dateView.setText(time);
        if(postModels.get(index).getCaption() != null)
            viewHolder.captionView.setText(postModels.get(index).getCaption());
        else
            viewHolder.captionView.setText(null);

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

        Map<String, Boolean> likes = postModels.get(index).getLikes();
        if(likes.containsKey(userUid)){
            viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
        }

        final boolean[] liked = new boolean[1];
        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference postRef = databaseReference.child("posts").child(id);
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
                            p.likes.put(userUid, true);
                            liked[0] = true;
                        }

                        mutableData.setValue(p);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        Map<String, Boolean> likes = postModels.get(index).getLikes();
                        if(liked[0] == true){
                            viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
                            int numberOfLikes = postModels.get(index).getNumberOfLikes() + 1;
                            postModels.get(index).setNumberOfLikes(numberOfLikes);
                            likes.put(userUid, true);
                        }
                        else{
                            viewHolder.likeButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
                            int numberOfLikes = postModels.get(index).getNumberOfLikes() - 1;
                            postModels.get(index).setNumberOfLikes(numberOfLikes);
                            likes.remove(userUid);
                        }
                        postModels.get(index).setLikes(likes);
                    }
                });
            }
        });
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

        public ViewHolder(@NonNull View itemView, Context context, ArrayList<PostModel> postModels) {
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
