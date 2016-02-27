package de.fluchtwege.trakttvsample.viewmodel;

import android.databinding.ObservableBoolean;
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

import java.util.List;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.model.Film;
import de.fluchtwege.trakttvsample.net.DataManager;
import de.fluchtwege.trakttvsample.ui.adapter.FilmAdapter;
import rx.Scheduler;
import rx.Subscriber;
import timber.log.Timber;

public class FilmListViewModel {

	private static final int FIRST_PAGE = 0;
	private int pagesLoaded = FIRST_PAGE;

	//is it an issue that we are exposing these members for databing?
	@NonNull
	public final DataManager dataManager = DataManager.getInstance();
	@NonNull
	public FilmAdapter adapter = new FilmAdapter();
	@NonNull
	public final LinearLayoutManager manager;

	public ObservableInt searchBarVisibility = new ObservableInt(EditText.GONE);
	public ObservableBoolean showLoadoadingViews = new ObservableBoolean(false);

	@MenuRes
	public int menu = R.menu.menu_list;

	@StringRes
	public int title = R.string.title_list;

	@NonNull
	private Scheduler schedulerIO;
	@NonNull
	private Scheduler schedulerMain;


	public FilmListViewModel(LinearLayoutManager manager) {
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
		dataManager.getPopularFilms(pagesLoaded)
				.subscribeOn(schedulerIO)
				.observeOn(schedulerMain)
				.subscribe(new Subscriber<List<Film>>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e("There was a problem loading the films" + e);
						setLoadingViews(false);
					}

					@Override
					public void onNext(List<Film> filmList) {
						Timber.d(" get PopularFilms on Next()");
						onPopularFilms(filmList);
					}
				});
	}

	private void onPopularFilms(List<Film> filmList) {
		setLoadingViews(false);
		pagesLoaded++;
		adapter.addFilms(filmList);
		adapter.notifyItemInserted(adapter.getItemCount());
	}

	public void getSearchResult(final String query) {
		dataManager.getSearchResult(query)
				.subscribeOn(schedulerIO)
				.observeOn(schedulerMain)
				.subscribe(new Subscriber<Film>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e("There was a problem searching the films" + e);
						setLoadingViews(false);
					}

					@Override
					public void onNext(Film film) {
						Timber.d("getSearchResult query: " + query + " onNext()");
						onSearchResult(film);
					}
				});
	}

	private void onSearchResult(Film film) {
		setLoadingViews(false);
		adapter.clear();
		adapter.addFilm(film);
		adapter.notifyDataSetChanged();
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
			} else if (query.length() == 0) {
				hideSearchBarAndLoadPopularFilms();
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	private void hideSearchBarAndLoadPopularFilms() {
		searchBarVisibility.set(EditText.GONE);
		pagesLoaded = FIRST_PAGE;
		adapter.clear();
		adapter.notifyDataChanges();
		getPopularFilms();
	}


	public OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			if (dy > 0) {
				if (hasReachedBottomOfList()) {
					Timber.d("onScroll Last Item -> getPopularFilms()");
					getPopularFilms();
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
		if (searchBarVisibility.get() == EditText.VISIBLE) {
			hideSearchBarAndLoadPopularFilms();
			return true;
		}
		return false;
	}


}
