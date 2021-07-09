package com.codepath.instagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.instagram.EndlessRecyclerViewScrollListener;
import com.codepath.instagram.Post;
import com.codepath.instagram.PostsAdapter;
import com.codepath.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public final int REQUEST_CODE = 20;
    public static final String TAG = "MainActivity";
    public static final int LOAD_LIMIT = 20;
    private SwipeRefreshLayout swipeContainer;

    private RecyclerView mRvPosts;
    protected PostsAdapter mAdapter;
    protected List<Post> mAllPosts;

    private EndlessRecyclerViewScrollListener scrollListener;

    private int mCurrentPosition = 0;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPosition = 0;
                queryPosts(mCurrentPosition);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRvPosts = view.findViewById(R.id.rvPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRvPosts.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi();
            }
        };
        // Adds the scroll listener to RecyclerView
        mRvPosts.addOnScrollListener(scrollListener);

        // Initialize the posts into the PostsAdapter
        mAllPosts = new ArrayList<>();
        mAdapter = new PostsAdapter(getContext(), mAllPosts);

        // set the adapter on the recycler view
        mRvPosts.setAdapter(mAdapter);
        mRvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        mCurrentPosition = 0;
        queryPosts(mCurrentPosition);
    }

    private void queryPosts(int requestedStartPosition) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        mCurrentPosition += LOAD_LIMIT;
        query.setLimit(LOAD_LIMIT);
        // starts reading posts after the position
        query.setSkip(requestedStartPosition);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                mAdapter.clear();
                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                mAllPosts.addAll(posts);
                mAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    public void loadNextDataFromApi() {
        queryPosts(mCurrentPosition);
        mAdapter.notifyItemRangeInserted(mCurrentPosition, LOAD_LIMIT);
    }
}