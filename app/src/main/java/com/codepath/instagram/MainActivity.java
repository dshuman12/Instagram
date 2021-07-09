package com.codepath.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final int REQUEST_CODE = 20;
    public static final String TAG = "MainActivity";
    public static final int LOAD_LIMIT = 20;
    private SwipeRefreshLayout swipeContainer;

    private RecyclerView mRvPosts;
    protected PostsAdapter mAdapter;
    protected List<Post> mAllPosts;

    private EndlessRecyclerViewScrollListener scrollListener;

    private int mCurrentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
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

        mRvPosts = findViewById(R.id.rvPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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
        mAdapter = new PostsAdapter(this, mAllPosts);

        // set the adapter on the recycler view
        mRvPosts.setAdapter(mAdapter);
        mRvPosts.setLayoutManager(new LinearLayoutManager(this));
        mCurrentPosition = 0;
        queryPosts(mCurrentPosition);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.imLogout) {
            // Logout is tapped, logout user and send back to login screen
            ParseUser.logOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        else if (item.getItemId() == R.id.imPost) {
            // Logout is tapped, logout user and send back to login screen
            Intent i = new Intent(this, PostActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
