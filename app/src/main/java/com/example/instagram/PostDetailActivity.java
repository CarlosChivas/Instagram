package com.example.instagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

public class PostDetailActivity extends AppCompatActivity {

    private TextView tvUsernameDetails;
    private ImageView ivImageDetails;
    private TextView tvDescriptionDetails;
    private TextView tvCreatedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        ivImageDetails = (ImageView) findViewById(R.id.ivImageDetails);
        tvUsernameDetails = (TextView) findViewById(R.id.tvUsernameDetails);
        tvDescriptionDetails = (TextView) findViewById(R.id.tvDescriptionDetails);
        tvCreatedAt = (TextView) findViewById(R.id.createdAt);

        Post postDetails = Parcels.unwrap(getIntent().getParcelableExtra("post"));
        tvUsernameDetails.setText(postDetails.getUser().getUsername());
        tvCreatedAt.setText(calculateTimeAgo(postDetails.getCreatedAt()));
        Glide.with(PostDetailActivity.this).load(postDetails.getImage().getUrl()).into(ivImageDetails);
        tvDescriptionDetails.setText(postDetails.getDescription());

        //ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        //getSupportActionBar().setTitle(bookDetails.getTitle()); // set the top title
        //String title = actionBar.getTitle().toString(); // get the title
        //actionBar.hide(); // or e
        //Log.i("MovieTrailerActivity", videoId);


    }

    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}