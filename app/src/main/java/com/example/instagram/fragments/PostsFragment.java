package com.example.instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.Post;
import com.example.instagram.PostsAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final String TAG = "PostsFragment";
    private RecyclerView rvPosts;
    protected PostsAdapter postsAdapter;
    protected List<Post> allPosts;
    private SwipeRefreshLayout swipeContainer;

    //Elements for info movie
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    private TextView tvUsernameDetails;
    private TextView createdAt;
    private ImageView ivImageDetails;
    private ImageView ivProfileDetails;
    private TextView tvDescriptionDetails;
    private TextView tvLikesDetails;

    public PostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsFragment newInstance(String param1, String param2) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);

        allPosts = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(postsAdapter);

        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();

        //We create a pop-up for the post details
        postsAdapter.setOnItemClickListener(new PostsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

                dialogBuilder = new AlertDialog.Builder(getContext());
                final View detailsView = getLayoutInflater().inflate(R.layout.activity_post_detail, null);

                tvUsernameDetails = detailsView.findViewById(R.id.tvUsernameDetails);
                createdAt = detailsView.findViewById(R.id.createdAt);
                ivImageDetails = detailsView.findViewById(R.id.ivImageDetails);
                ivProfileDetails = detailsView.findViewById(R.id.ivUserDetails);
                tvDescriptionDetails = detailsView.findViewById(R.id.tvDescriptionDetails);
                tvLikesDetails = detailsView.findViewById(R.id.tvLikedBy);

                tvDescriptionDetails.setText(allPosts.get(position).getDescription());
                tvUsernameDetails.setText(allPosts.get(position).getUser().getUsername());
                String createdAtAgo = "Created " + calculateTimeAgo(allPosts.get(position).getCreatedAt()) + " ago";
                createdAt.setText(createdAtAgo);
                ParseFile image = allPosts.get(position).getImage();
                if (image != null) {
                    Glide.with(getContext()).load(image.getUrl()).apply(RequestOptions.bitmapTransform(new RoundedCorners(50))).into(ivImageDetails);
                }
                ParseFile imageUser = allPosts.get(position).getUser().getParseFile("photoProfile");
                if(imageUser != null){
                    Glide.with(getContext()).load(imageUser.getUrl()).circleCrop().into(ivProfileDetails);
                }
                int likes = allPosts.get(position).getLikes();
                String likesString = "Liked by "+likes;
                tvLikesDetails.setText(likesString);

                dialogBuilder.setView(detailsView);
                dialog = dialogBuilder.create();
                dialog.show();
            }
        });

        //Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        //Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts();
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    //Method to get the Posts
    protected void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Post post : posts){
                    Log.i(TAG, "Posts: "+post.getDescription() + " username: " + post.getUser().getUsername());
                }
                allPosts.clear();
                allPosts.addAll(posts);
                postsAdapter.notifyDataSetChanged();
            }
        });
    }

    //method to calculate how long ago the post was created
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