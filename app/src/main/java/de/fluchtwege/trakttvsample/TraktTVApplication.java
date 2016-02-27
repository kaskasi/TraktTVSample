package de.fluchtwege.trakttvsample;

import android.app.Application;

import timber.log.Timber;

public class TraktTVApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Timber.plant(new Timber.DebugTree());
	}
}
