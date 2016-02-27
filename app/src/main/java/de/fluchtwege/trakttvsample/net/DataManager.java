package de.fluchtwege.trakttvsample.net;

import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.trakttvsample.model.PopularFilm;
import de.fluchtwege.trakttvsample.model.QueryResultFilm;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public class DataManager {

	private static DataManager instance;

	private DataManager() {

	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	//Until there is network implemented we need to fake this
	public List<PopularFilm> loadPopularFilms(final int pagesLoaded) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return createListOf10Films(pagesLoaded);
	}

	private List<QueryResultFilm> loadSearchResult(String query) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return createListWith1Film(query);
	}

	private List<QueryResultFilm> createListWith1Film(String query) {
		List<QueryResultFilm> result = new ArrayList<>();
		result.add(
		  new QueryResultFilm(query + " - That is The Title", "1999", "This is a film that has a strange name. But it is not so strange.",
				"http://ste.india.com/sites/default/files/2014/12/17/303980-film-700.jpg"));
		return result;
	}

	@VisibleForTesting
	public List<PopularFilm> createListOf10Films(int pagesLoaded) {
		ArrayList<PopularFilm> films = new ArrayList<>();
		for (int i = 0; i < 10; i += 1) {
			films.add(new PopularFilm("Der Film", "1932 : " + (pagesLoaded + "," + i), "sdhsjk"));
		}
		return films;
	}


	public Observable<List<PopularFilm>> getPopularFilms(final int pagesLoaded) {
		return Observable.create(new Observable.OnSubscribe<List<PopularFilm>>() {

			@Override
			public void call(Subscriber<? super List<PopularFilm>> subscriber) {
				if (!subscriber.isUnsubscribed()) {
					final List<PopularFilm> listOf10Films = loadPopularFilms(pagesLoaded);
					subscriber.onNext(listOf10Films);
					subscriber.onCompleted();
					Timber.d("getPopularFilms page: " + pagesLoaded + " call()");
				}
			}
		});
	}

	public Observable<List<QueryResultFilm>> getSearchResult(final String query) {
		return Observable.create(new Observable.OnSubscribe<List<QueryResultFilm>>() {

			@Override
			public void call(Subscriber<? super List<QueryResultFilm>> subscriber) {
				if (!subscriber.isUnsubscribed()) {
					final List<QueryResultFilm> searchResult = loadSearchResult(query);
					subscriber.onNext(searchResult);
					subscriber.onCompleted();
					Timber.i("getSearchResult query: " + query + "call()");
				}
			}
		});
	}


}
