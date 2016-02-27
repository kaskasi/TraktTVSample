package de.fluchtwege.trakttvsample.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.databinding.FilmListBinding;
import de.fluchtwege.trakttvsample.viewmodel.FilmListViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FilmListActivity extends AppCompatActivity {

	private FilmListViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.film_list);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		viewModel = new FilmListViewModel(new LinearLayoutManager(this));
		FilmListBinding binding = DataBindingUtil.setContentView(this, R.layout.film_list);
		binding.setViewModel(viewModel);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		viewModel.initWithSchedulers(Schedulers.io(), AndroidSchedulers.mainThread());
	}

	@Override
	public void onBackPressed() {
		if (!viewModel.onBackPressed()) {
			super.onBackPressed();
		}
	}
}
