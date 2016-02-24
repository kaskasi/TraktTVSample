package de.fluchtwege.trakttvsample.ui.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.fluchtwege.trakttvsample.R;

public class TraktFilmListFragment extends Fragment {

	public TraktFilmListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_trakt, container, false);
	}
}
