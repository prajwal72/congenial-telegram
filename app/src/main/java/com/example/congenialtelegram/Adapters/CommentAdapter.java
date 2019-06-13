package com.example.congenialtelegram.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.congenialtelegram.ImageViewActivity;
import com.example.congenialtelegram.Models.CommentModel;
import com.example.congenialtelegram.ProfileActivity;
import com.example.congenialtelegram.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private ArrayList<CommentModel> commentModels;
    private Context context;

    public CommentAdapter(ArrayList<CommentModel> commentModels) {
        this.commentModels = commentModels;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.comment_card, viewGroup, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.authorView.setText(commentModels.get(i).getAuthor());
        viewHolder.commentView.setText(commentModels.get(i).getComment());

        final String uid = commentModels.get(i).getUid();
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

        final String profileUrl = commentModels.get(i).getProfileImageUrl();
        if(profileUrl != null){
            String url = commentModels.get(i).getProfileImageUrl();
            Uri uri = Uri.parse(url);
            Glide.with(context)
                    .load(uri)
                    .into(viewHolder.imageView);
        }
        else{
            Glide.with(context)
                    .load(R.drawable.profile_pic)
                    .into(viewHolder.imageView);
        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileUrl != null){
                    Intent intent = new Intent(context, ImageViewActivity.class);
                    intent.putExtra("url", profileUrl);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView authorView;
        private TextView commentView;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            authorView = itemView.findViewById(R.id.author);
            commentView = itemView.findViewById(R.id.commentText);
            imageView = itemView.findViewById(R.id.profileImage);
        }
    }
}
