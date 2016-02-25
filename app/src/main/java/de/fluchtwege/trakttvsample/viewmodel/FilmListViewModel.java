package de.fluchtwege.trakttvsample.viewmodel;

import android.databinding.ObservableInt;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.model.Film;
import de.fluchtwege.trakttvsample.net.DataManager;
import de.fluchtwege.trakttvsample.ui.adapter.FilmAdapter;

public class FilmListViewModel {

	public final DataManager dataManager = DataManager.getInstance();
	public FilmAdapter adapter = new FilmAdapter(new ArrayList<Film>());
	public final LinearLayoutManager manager;

	public ObservableInt searchBarVisibility = new ObservableInt(EditText.GONE);

	public int menu = R.menu.menu_list;

	public String title = "Film Liste";

	public Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			if (item.getItemId() == R.id.action_search) {
				searchBarVisibility.set(EditText.VISIBLE);
			}
			return false;
		}
	};

	public TextWatcher watcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Log.i("TAG", s.toString());
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	public OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			Log.i("TAG", "scrolling");
		}
	};

	public FilmListViewModel(LinearLayoutManager manager) {
		this.manager = manager;
	}


	public void loadPopularFilms() {
		dataManager.loadPopularFilms(this, 0);

	}

	public void onPopularFilmsloaded(List<Film> filmList) {
		adapter.setFilms(filmList);
	}

	public void init() {
		loadPopularFilms();
	}
}
