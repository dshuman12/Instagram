package com.codepath.instagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("gg6wggLhLn8AjVoVNuJ24ce1F4v77YBBflaKbzbi")
                .clientKey("MlAPzrVNRs9YZMf3J7T2sdKLXQQdbhr6aetkJGWd")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
