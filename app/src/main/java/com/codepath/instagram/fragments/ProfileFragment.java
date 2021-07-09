package com.codepath.instagram.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath.instagram.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private TextView mTvUser;
    private TextView mTvName;
    private ImageView mIvProfilePhoto;
    private TextView mTvBio;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvName = view.findViewById(R.id.tvName);
        mTvUser = view.findViewById(R.id.tvUser);
        mIvProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        mTvBio = view.findViewById(R.id.tvBio);

        ParseUser currentUser = ParseUser.getCurrentUser();
        mTvUser.setText("@" + currentUser.getString("username"));
        mTvName.setText(currentUser.getString("name"));
        mTvBio.setText(currentUser.getString("bio"));

        ParseFile image = currentUser.getParseFile("profilePicture");
        if (image != null) {
            Log.i("ProfileFragment", image.toString() + image.getUrl());
            Glide.with(getContext()).load(image.getUrl()).into(mIvProfilePhoto);
        }
    }
}