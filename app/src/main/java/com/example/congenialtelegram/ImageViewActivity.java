package com.example.congenialtelegram;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

public class ImageViewActivity extends AppCompatActivity {
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Intent intent = getIntent();
        String url = intent.getExtras().getString("url");
        ImageView imageView = findViewById(R.id.image);
        final ImageView backButton = findViewById(R.id.backButton);
        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        backButton.setEnabled(false);

        Uri uri = Uri.parse(url);
        Glide.with(ImageViewActivity.this)
                .load(uri)
                .into(imageView);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 0){
                    flag = 1;
                    backButton.setVisibility(View.VISIBLE);
                    backButton.setEnabled(true);
                }
                else{
                    flag = 0;
                    backButton.setVisibility(View.INVISIBLE);
                    backButton.setEnabled(false);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageViewActivity.this.finish();
            }
        });
    }
}
