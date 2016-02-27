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

import java.util.List;

import de.fluchtwege.trakttvsample.R;
import de.fluchtwege.trakttvsample.model.PopularFilm;
import de.fluchtwege.trakttvsample.model.QueryResultFilm;
import de.fluchtwege.trakttvsample.net.DataManager;
import de.fluchtwege.trakttvsample.ui.adapter.FilmsAdapter;
import de.fluchtwege.trakttvsample.ui.adapter.PopularFilmsAdapter;
import de.fluchtwege.trakttvsample.ui.adapter.QueryResultFilmsAdapter;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

public class FilmListViewModel {

	private static final int FIRST_PAGE = 0;
	private int pagesLoaded = FIRST_PAGE;

	//is it an issue that we are exposing these members for databing?
	@NonNull
	public final DataManager dataManager = DataManager.getInstance();

	@NonNull
	public final ObservableField<FilmsAdapter> adapter = new ObservableField<>();

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

	private Subscription subscription;
	private boolean isLoadingPopularFilms = false;


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
		isLoadingPopularFilms = true;
		dataManager.getPopularFilms(pagesLoaded)
				.subscribeOn(schedulerIO)
				.observeOn(schedulerMain)
				.subscribe(new Subscriber<List<PopularFilm>>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e("There was a problem loading the films" + e);
						setLoadingViews(false);
						isLoadingPopularFilms = false;
					}

					@Override
					public void onNext(List<PopularFilm> filmList) {
						Timber.d(" get PopularFilms on Next()");
						onPopularFilms(filmList);
					}
				});
	}

	private void onPopularFilms(List<PopularFilm> filmList) {
		setLoadingViews(false);
		if (pagesLoaded == 0) {
			adapter.set(new PopularFilmsAdapter());
		}
		pagesLoaded++;
		final PopularFilmsAdapter popularFilmsAdapter = (PopularFilmsAdapter) adapter.get();
		popularFilmsAdapter.addFilms(filmList);
		popularFilmsAdapter.notifyItemInserted(adapter.get().getItemCount());
		isLoadingPopularFilms = false;
	}

	public void getSearchResult(final String query) {
		if (subscription != null) {
			subscription.unsubscribe();
		}
		subscription = dataManager.getSearchResult(query)
				.subscribeOn(schedulerIO)
				.observeOn(schedulerMain)
				.cache()
				.subscribe(new Subscriber<List<QueryResultFilm>>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						Timber.e("There was a problem searching the films" + e);
						setLoadingViews(false);
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
		setLoadingViews(false);
		adapter.set(new QueryResultFilmsAdapter());
		final QueryResultFilmsAdapter queryResultFilmsAdapter = (QueryResultFilmsAdapter) adapter.get();
		queryResultFilmsAdapter.addFilms(films);
		queryResultFilmsAdapter.notifyDataSetChanged();
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
		if (subscription != null) {
			subscription.unsubscribe();
			subscription = null;
		}
		searchBarVisibility.set(EditText.GONE);
		pagesLoaded = FIRST_PAGE;
		getPopularFilms();
	}


	public OnScrollListener scrollListener = new OnScrollListener() {

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			if (dy > 0) {
				if (!isLoadingPopularFilms && hasReachedBottomOfList()) {
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
		if (adapter.get() instanceof QueryResultFilmsAdapter || searchBarVisibility.get() == EditText.VISIBLE) {
			hideSearchBarAndLoadPopularFilms();
			return true;
		}
		return false;
	}


}
