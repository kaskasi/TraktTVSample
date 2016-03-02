package de.fluchtwege.movielist.viewmodel;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import de.fluchtwege.movielist.R;
import de.fluchtwege.movielist.model.Movie;
import de.fluchtwege.movielist.model.MovieQuery;
import de.fluchtwege.movielist.net.RequestsAPI;
import de.fluchtwege.movielist.ui.MovieAdapter;
import rx.Scheduler;
import rx.Subscriber;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;


//All problems can be solved by another layer of indirection - david wheeler
//"...except for the problem of too many layers of indirection." - kevlin henney

public class MovieListViewModel {

	private static final int FIRST_PAGE = 0;

	@MenuRes public int menu = R.menu.menu_list;
	@StringRes public ObservableInt title = new ObservableInt(R.string.title_list);

	@NonNull public ObservableInt searchBarVisibility = new ObservableInt(EditText.GONE);
	@NonNull public ObservableField<RecyclerView.OnScrollListener> scrollListener = new ObservableField<>();
	@NonNull public ObservableField<Toolbar.OnMenuItemClickListener> menuItemClickListener = new ObservableField<>();
	@NonNull public ObservableField<OnEditorActionListener> editorAction = new ObservableField<>();
	@NonNull public ObservableField<TextWatcher> textWatcher = new ObservableField<>();

	@Nullable private BehaviorSubject<String> queryChangedSubject;
	@Nullable private RequestsAPI requestsAPI;
	@Nullable private Scheduler schedulerIO;
	@Nullable private Scheduler schedulerMain;
	@Nullable private String query;
	@Nullable public LinearLayoutManager manager;
	@Nullable public ObservableField<MovieAdapter> adapter = new ObservableField<>(new MovieAdapter());

	private int pagesLoaded = FIRST_PAGE;
	private boolean isLoadingMovies = false;

	public void setUpViewModel(LinearLayoutManager manager, @NonNull final Scheduler schedulerIO, @NonNull final Scheduler schedulerMain) {
		this.manager = manager;
		this.schedulerIO = schedulerIO;
		this.schedulerMain = schedulerMain;
		requestsAPI = new RequestsAPI();
		setUpUILogic();
		clearAndShowPopularMovies();
	}

	@VisibleForTesting
	public void setUpUILogic() {
		editorAction.set(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					v.setText(new String());
					searchBarVisibility.set(EditText.GONE);
				}
				return false;
			}
		});

		menuItemClickListener.set(new Toolbar.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() == R.id.action_search) {
					registerQueryChangedSubject();
					searchBarVisibility.set(EditText.VISIBLE);
				}
				return false;
			}
		});

		scrollListener.set(new OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if (dy > 0) {
					if (!isLoadingMovies && hasReachedBottomOfList()) {
						Timber.d("onScroll Last Item -> getPopularMovies()");
						if (query != null) {
							query(query);
						} else {
							getPopularMovies();
						}

					}
				}
			}
		});

		textWatcher.set(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(final CharSequence s, int start, int before, int count) {
				queryChangedSubject.onNext(s.toString().trim());
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

		});

	}

	public void tearDownViewModel() {
		unregisterTextChangeSubject();
		manager = null;
		query = null;
		requestsAPI = null;
		adapter = null;
		schedulerIO = null;
		schedulerMain = null;
	}

	@VisibleForTesting
	public void clearAndShowPopularMovies() {
		unregisterTextChangeSubject();
		title.set(R.string.title_list);
		searchBarVisibility.set(EditText.GONE);
		pagesLoaded = FIRST_PAGE;
		adapter.get().clear();
		adapter.get().notifyDataChanges();
		query = null;
		getPopularMovies();
	}

	@VisibleForTesting
	public void getPopularMovies() {
		isLoadingMovies = true;
		requestsAPI.getPopularMovies(pagesLoaded)
				.subscribeOn(schedulerIO)
				.observeOn(schedulerMain)
				.subscribe(new Subscriber<List<Movie>>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e("There was a problem loading the movies" + e);
						isLoadingMovies = false;
					}

					@Override
					public void onNext(List<Movie> movies) {
						onMoviesLoaded(movies);
					}
				});
	}

	@VisibleForTesting
	public void query(@NonNull final String query) {
		this.query = query;
		isLoadingMovies = true;
		requestsAPI.queryMovies(query, pagesLoaded)
				.subscribeOn(schedulerIO)
				.observeOn(schedulerMain)
				.debounce(1000, TimeUnit.MILLISECONDS)
				.subscribe(new Subscriber<List<MovieQuery>>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e("There was a problem loading the movies" + e);
						isLoadingMovies = false;
					}

					@Override
					public void onNext(List<MovieQuery> movieQueries) {
						onMoviesLoaded(filterMoviesFromSearchResults(movieQueries));
					}
				});
	}

	private List<Movie> filterMoviesFromSearchResults(List<MovieQuery> movieQueries) {
		List<Movie> result = new ArrayList<>();
		for (MovieQuery movieQuery : movieQueries) {
			result.add(movieQuery.movie);
		}
		return result;
	}

	@VisibleForTesting
	public void onMoviesLoaded(final List<Movie> movies) {
		isLoadingMovies = false;
		pagesLoaded++;
		adapter.get().addMovies(movies);
		adapter.get().notifyDataChanges();
	}


	private void unregisterTextChangeSubject() {
		if (queryChangedSubject != null) {
			queryChangedSubject.unsubscribeOn(schedulerMain);
			queryChangedSubject = null;
		}
	}

	@VisibleForTesting
	public void registerQueryChangedSubject() {
		queryChangedSubject = BehaviorSubject.create();
		queryChangedSubject
				.subscribeOn(schedulerIO)
				.observeOn(schedulerMain)
				.debounce(500, TimeUnit.MILLISECONDS)
				.subscribe(new Subscriber<String>() {
					@Override
					public void onCompleted() {
						Timber.d("queryChangedSubject onCompleted");
					}

					@Override
					public void onError(Throwable e) {
						Timber.e(e, "queryChangedSubject onError " + e.getMessage());
					}

					@Override
					public void onNext(String query) {
						Timber.d("queryChangedSubject onNext : " + query);
						if (query != null && query.length() > 0) {
							adapter.get().clear();
							query(query);
						}
					}
				});
	}

	@VisibleForTesting
	public boolean hasReachedBottomOfList() {
		if (manager != null) {
			final int visibleItemCount = manager.getChildCount();
			final int totalItemCount = manager.getItemCount();
			final int pastVisiblesItems = manager.findFirstVisibleItemPosition();

			return (visibleItemCount + pastVisiblesItems) >= totalItemCount;
		}
		return false;
	}

	public boolean onBackPressed() {
		if (query != null || searchBarVisibility.get() == EditText.VISIBLE) {
			clearAndShowPopularMovies();
			return true;
		}
		return false;
	}
}
