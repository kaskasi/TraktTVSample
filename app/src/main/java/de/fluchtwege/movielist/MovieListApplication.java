package de.fluchtwege.movielist;

import android.app.Application;

import timber.log.Timber;

public class MovieListApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Timber.plant(new Timber.DebugTree());
	}
}
