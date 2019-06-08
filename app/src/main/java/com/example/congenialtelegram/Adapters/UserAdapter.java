package com.example.congenialtelegram.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.congenialtelegram.Models.UserModel;
import com.example.congenialtelegram.ProfileActivity;
import com.example.congenialtelegram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private ArrayList<UserModel> userModels;
    private Context context;

    public UserAdapter(ArrayList<UserModel> userModels){
        this.userModels = userModels;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.user_card, viewGroup, false);
        return new ViewHolder(view, context, userModels);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder viewHolder, final int i) {
        final String uid = userModels.get(i).getUid();
        final String userUid = FirebaseAuth.getInstance().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String author = (String) dataSnapshot.child("name").getValue();
                String profileImage = (String) dataSnapshot.child("profile_pic").getValue();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.authorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });

        Boolean bool = userModels.get(i).getFriend();
        if(bool){
            viewHolder.followButton.setVisibility(View.GONE);
            viewHolder.followButton.setEnabled(false);
            viewHolder.checkButton.setVisibility(View.VISIBLE);
            viewHolder.checkButton.setEnabled(true);
        }

        viewHolder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.followButton.setVisibility(View.GONE);
                viewHolder.followButton.setEnabled(false);
                viewHolder.checkButton.setVisibility(View.VISIBLE);
                viewHolder.checkButton.setEnabled(true);
                databaseReference.child(userUid).child("following").child(uid).setValue(uid);
                databaseReference.child(uid).child("followers").child(userUid).setValue(userUid);
                userModels.set(i,new UserModel(uid, true));
            }
        });

        viewHolder.checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.followButton.setVisibility(View.VISIBLE);
                viewHolder.followButton.setEnabled(true);
                viewHolder.checkButton.setVisibility(View.GONE);
                viewHolder.checkButton.setEnabled(false);
                userModels.set(i,new UserModel(uid, false));
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.child(userUid).child("following").child(uid).getRef().removeValue();
                        dataSnapshot.child(uid).child("followers").child(userUid).getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImageView;
        private TextView authorView;
        private ImageButton followButton;
        private ImageButton checkButton;

        public ViewHolder(@NonNull View itemView, Context context, ArrayList<UserModel> userModels) {
            super(itemView);
            authorView = itemView.findViewById(R.id.author);
            profileImageView = itemView.findViewById(R.id.profileImage);
            followButton = itemView.findViewById(R.id.followButton);
            checkButton = itemView.findViewById(R.id.checkButton);
            checkButton.setEnabled(false);
        }
    }
}
