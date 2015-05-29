package com.example.parsechating;

import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Application;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Parse.enableLocalDatastore(this);
		ParseObject.registerSubclass(Message.class);
		Parse.initialize(this, "i9p5TJVVcxt0xjy9LWT4WXRje0eUMQWNY3EHX6IU", "mAACoGucBU5DLeWZMTqfAD9EyvBeFlUFAE85mM0C");
		
	}

}
