package com.example.congenialtelegram.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.congenialtelegram.Models.PostModel;
import com.example.congenialtelegram.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        View view = LayoutInflater.from(context).inflate(R.layout.card, viewGroup, false);
        return new ViewHolder(view, context, postModels);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
        Date date = postModels.get(index).getDate();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        String strDate = formatter.format(date);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        String strTime = timeFormatter.format(date);
        String time = strDate.concat(" at ").concat(strTime);

        viewHolder.authorView.setText(postModels.get(index).getAuthor());
        viewHolder.dateView.setText(time);
        if(postModels.get(index).getCaption() != null)
            viewHolder.captionView.setText(postModels.get(index).getCaption());
        if(postModels.get(index).getImageUrl() != null){
            String url = postModels.get(index).getImageUrl();
            Uri uri = Uri.parse(url);
            Glide.with(context)
                    .load(uri)
                    .into(viewHolder.imageView);
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
        private TextView likeButton;
        private TextView commentButton;
        private TextView shareButton;

        public ViewHolder(@NonNull View itemView, Context context, ArrayList<PostModel> postModels) {
            super(itemView);
            authorView = itemView.findViewById(R.id.author);
            dateView = itemView.findViewById(R.id.date);
            captionView = itemView.findViewById(R.id.caption);
            imageView = itemView.findViewById(R.id.image);
            profileImageView= itemView.findViewById(R.id.profileImage);
            infoView = itemView.findViewById(R.id.info);
            likeButton = itemView.findViewById(R.id.like);
            commentButton = itemView.findViewById(R.id.comment);
            shareButton = itemView.findViewById(R.id.share);
        }
    }
}
