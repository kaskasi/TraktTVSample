package de.fluchtwege.trakttvsample.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.model.Movie;
import de.fluchtwege.trakttvsample.model.QueryResultFilm;
import de.fluchtwege.trakttvsample.net.DataManager;
import de.fluchtwege.trakttvsample.ui.adapter.MovieAdapter;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class MovieListViewModel {

	private static final int FIRST_PAGE = 0;
	private int pagesLoaded = FIRST_PAGE;

	boolean isSearchResult = false;

	//is it an issue that we are exposing these members for databing?
	@NonNull
	public final DataManager dataManager = DataManager.getInstance();

	@NonNull
	public final ObservableField<MovieAdapter> adapter = new ObservableField<>(new MovieAdapter());

	@NonNull
	public final LinearLayoutManager manager;

	public ObservableInt searchBarVisibility = new ObservableInt(EditText.GONE);

	@MenuRes
	public int menu = R.menu.menu_list;

	@StringRes
	public ObservableInt title = new ObservableInt(R.string.title_list);

	@NonNull
	private Scheduler schedulerIO;
	@NonNull
	private Scheduler schedulerMain;

	private Subscription subscription;
	private boolean isLoadingFilms = false;

	private String query;


	public MovieListViewModel(LinearLayoutManager manager) {
		this.manager = manager;
	}

	//All problems can be solved by another layer of indirection - david wheeler
	//"...except for the problem of too many layers of indirection." - kevlin henney
	public void initWithSchedulers(@NonNull final Scheduler schedulerIO, @NonNull final Scheduler schedulerMain) {
		this.schedulerIO = schedulerIO;
		this.schedulerMain = schedulerMain;
		getPopularFilms();
	}

	@VisibleForTesting
	public void getPopularFilms() {
		if (isSearchResult) {
			pagesLoaded = FIRST_PAGE;
			adapter.get().clear();
			adapter.get().notifyDataChanges();
		}
		isLoadingFilms = true;
		isSearchResult = false;
		dataManager.getPopularFilms(pagesLoaded)
				.subscribeOn(schedulerIO)
				.observeOn(schedulerMain)
				.subscribe(new Subscriber<List<Movie>>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e("There was a problem loading the films" + e);
						isLoadingFilms = false;
					}

					@Override
					public void onNext(List<Movie> filmList) {
						Timber.d(" get PopularFilms on Next()");
						onPopularFilms(filmList);
					}
				});
	}

	private void onPopularFilms(List<Movie> filmList) {
		isLoadingFilms = false;
		for (int i = 0; i < filmList.size(); i++) {
			Movie popularFilm = filmList.get(i);
			popularFilm.title = (String.valueOf((pagesLoaded * 10) + i)) + " " + popularFilm.title;
		}
		pagesLoaded++;
		adapter.get().addFilms(filmList);
		adapter.get().notifyItemInserted(adapter.get().getItemCount());
		isLoadingFilms = false;
	}

	public void getSearchResult(final String query) {
		if (!isSearchResult || !this.query.equals(query)) {
			pagesLoaded = FIRST_PAGE;
			adapter.get().clear();
			adapter.get().notifyDataChanges();
		}
		this.query = query;
		isLoadingFilms = true;
		isSearchResult = true;
		if (subscription != null) {
			subscription.unsubscribe();
		}
		//subscription =
		dataManager.getSearchResult(query, pagesLoaded)
				.subscribeOn(schedulerIO)
				.observeOn(schedulerMain)
				.debounce(1000, TimeUnit.MILLISECONDS)
				.subscribe(new Subscriber<List<QueryResultFilm>>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e("There was a problem searching the films" + e);
					}

					@Override
					public void onNext(List<QueryResultFilm> films) {
						Timber.d("getSearchResult query: " + query + " onNext()");
						onSearchResult(films);
						subscription = null;
					}
				});
	}

	private void onSearchResult(List<QueryResultFilm> films) {
		isLoadingFilms = false;
		pagesLoaded++;
		title.set(R.string.title_search);
		List<Movie> movies = filterMoviesFromSearchResults(films);
		adapter.get().addFilms(movies);
		adapter.get().notifyDataSetChanged();
	}

	private List<Movie> filterMoviesFromSearchResults(List<QueryResultFilm> films) {
		List<Movie> movies = new ArrayList<>();
		for (QueryResultFilm film : films) {
			movies.add(film.movie);
		}
		return movies;
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
			String query = s.toString().trim();
			if (query.length() > 0) {
				getSearchResult(query);
			} else if (query.length() == 0) {
				hideSearchBarAndLoadPopularFilms();
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	private void hideSearchBarAndLoadPopularFilms() {
		if (subscription != null) {
			subscription.unsubscribe();
			subscription = null;
		}
		title.set(R.string.title_list);
		searchBarVisibility.set(EditText.GONE);
		pagesLoaded = FIRST_PAGE;
		getPopularFilms();
	}


	public OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			if (dy > 0) {
				if (!isLoadingFilms && hasReachedBottomOfList()) {
					Timber.d("onScroll Last Item -> getPopularFilms()");
					if (isSearchResult) {
						getSearchResult(query);
					} else {
						getPopularFilms();
					}

				}
			}
		}
	};

	private boolean hasReachedBottomOfList() {
		final int visibleItemCount = manager.getChildCount();
		final int totalItemCount = manager.getItemCount();
		final int pastVisiblesItems = manager.findFirstVisibleItemPosition();

		return (visibleItemCount + pastVisiblesItems) >= totalItemCount;
	}

	public boolean onBackPressed() {
		if (isSearchResult || searchBarVisibility.get() == EditText.VISIBLE) {
			hideSearchBarAndLoadPopularFilms();
			return true;
		}
		return false;
	}


}
