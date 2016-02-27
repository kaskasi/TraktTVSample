package de.fluchtwege.trakttvsample.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.List;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.model.Film;
import de.fluchtwege.trakttvsample.net.DataManager;
import de.fluchtwege.trakttvsample.ui.adapter.FilmAdapter;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class FilmListViewModel {

	private static final int FIRST_PAGE = 0;
	private int pagesLoaded = FIRST_PAGE;

	public final DataManager dataManager = DataManager.getInstance();
	public FilmAdapter adapter = new FilmAdapter();
	public final LinearLayoutManager manager;

	public ObservableInt searchBarVisibility = new ObservableInt(EditText.GONE);
	public ObservableBoolean showLoadoadingViews = new ObservableBoolean(false);

	@MenuRes
	public int menu = R.menu.menu_list;

	@StringRes
	public int title = R.string.title_list;


	public FilmListViewModel(LinearLayoutManager manager) {
		this.manager = manager;
	}

	public void init() {
		getPopularFilms();
	}

	@VisibleForTesting
	public void getPopularFilms() {
		dataManager.getPopularFilms(pagesLoaded)
				.observeOn(Schedulers.immediate())
				.subscribeOn(Schedulers.immediate())
				.subscribe(new Subscriber<List<Film>>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						setLoadingViews(false);
						//Log.e("TAG", "There was a problem loading the films" + e);
					}

					@Override
					public void onNext(List<Film> filmList) {
						Log.i("TAG", "on Next()");
						setLoadingViews(false);
						pagesLoaded++;
						adapter.addFilms(filmList);
						adapter.notifyItemInserted(adapter.getItemCount());
					}

				});
	}

	public void getSearchResult(String query) {
		dataManager.getSearchResult(query)
				.observeOn(Schedulers.immediate())
				.subscribeOn(Schedulers.immediate())
				.subscribe(new Subscriber<Film>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						setLoadingViews(false);
						//Log.e("TAG", "There was a problem loading the films" + e);
					}

					@Override
					public void onNext(Film film) {
						Log.i("TAG", "on Next()");
						setLoadingViews(false);
						adapter.clear();
						adapter.addFilm(film);
						adapter.notifyDataSetChanged();
						manager.notify();
					}

				});
	}

	private void setLoadingViews(boolean visible) {
		showLoadoadingViews.set(visible);
	}

	public Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			if (item.getItemId() == R.id.action_search) {
				searchBarVisibility.set(EditText.VISIBLE);
			}
			return false;
		}
	};

	public OnEditorActionListener editorAction = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				searchBarVisibility.set(EditText.GONE);
			}
			return false;
		}
	};


	public TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			final String query = s.toString().trim();
			if (query.length() > 0) {

				getSearchResult(query);
			} else {
				pagesLoaded = FIRST_PAGE;
				adapter.clear();
				adapter.notifyDataSetChanged();
				getPopularFilms();
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	public OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			if (dy > 0) {
				final int visibleItemCount = manager.getChildCount();
				final int totalItemCount = manager.getItemCount();
				final int pastVisiblesItems = manager.findFirstVisibleItemPosition();

				if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
					Log.v("TAG", "Last Item Wow !");
					getPopularFilms();
				}

			}
		}
	};


}
