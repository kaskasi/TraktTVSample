package de.fluchtwege.movielist.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import de.fluchtwege.movielist.R;
import de.fluchtwege.movielist.databinding.MovieListBinding;
import de.fluchtwege.movielist.viewmodel.MovieListViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieListActivity extends AppCompatActivity {

	private MovieListViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_list);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		viewModel = new MovieListViewModel(new LinearLayoutManager(this), Schedulers.io(), AndroidSchedulers.mainThread());
		MovieListBinding binding = DataBindingUtil.setContentView(this, R.layout.movie_list);
		binding.setViewModel(viewModel);
		viewModel.resetToPopularFilms();
	}

	@Override
	protected void onPause() {
		if (viewModel != null) {
			viewModel.tearDown();
		}
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		if (!viewModel.onBackPressed()) {
			super.onBackPressed();
		}
	}
}
