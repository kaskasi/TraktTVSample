package de.fluchtwege.trakttvsample.net;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.fluchtwege.trakttvsample.model.Movie;
import de.fluchtwege.trakttvsample.model.QueryResultFilm;
import rx.Observable;

public class DataManager {

	public static final int PAGE_SIZE = 10;
	public static final String TYPE_MOVIE = "movie";
	public static final String TRAKT_API_VERSION = "2";
	public static final String TRAKT_API_KEY = "ad005b8c117cdeee58a1bdb7089ea31386cd489b21e14b19818c91511f12a086";
	public static final String CONTENT_TYPE = "application/json";
	public static final String EXTENSION = "full,images";

	//trakt api starts counting pages at 1
	public static final int PAGE_OFFSET = 1;

	private static DataManager instance;

	private DataManager() {
	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	public Observable<List<Movie>> getPopularFilms(final int pagesLoaded) {
		PopularFilmsAPI popularFilmsAPI = new PopularFilmsAPI();
		Observable<List<Movie>> popularFilms = popularFilmsAPI.getPopularFilms(pagesLoaded);
		return popularFilms;

		/*return Observable.create(new Observable.OnSubscribe<List<PopularFilm>>() {

			@Override
			public void call(Subscriber<? super List<PopularFilm>> subscriber) {
				if (!subscriber.isUnsubscribed()) {
					final List<PopularFilm> listOf10Films = loadPopularFilms(pagesLoaded);
					subscriber.onNext(listOf10Films);
					subscriber.onCompleted();
					Timber.d("getPopularFilms page: " + pagesLoaded + " call()");
				}
			}
		});*/
	}

	public Observable<List<QueryResultFilm>> getSearchResult(final String query, int pagesLoaded) {
		QueryResultFilmsAPI queryResultFilmsAPI = new QueryResultFilmsAPI();
		Observable<List<QueryResultFilm>> queryResultFilms = queryResultFilmsAPI.getQueryResultFilms(query, pagesLoaded);
		return queryResultFilms;

		/*return Observable.create(new Observable.OnSubscribe<List<QueryResultFilm>>() {

			@Override
			public void call(Subscriber<? super List<QueryResultFilm>> subscriber) {
				if (!subscriber.isUnsubscribed()) {
					final List<QueryResultFilm> searchResult = loadSearchResult(query);
					subscriber.onNext(searchResult);
					subscriber.onCompleted();
					Timber.i("getSearchResult query: " + query + "call()");
				}
			}
		});*/
	}
















/*

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
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return createListWith1Film(query);
	}

	private List<QueryResultFilm> createListWith1Film(String query) {
		List<QueryResultFilm> result = new ArrayList<>();
		result.add(
				new QueryResultFilm(query + " - That is The Title", "1999", "This is a movie that has a strange name. But it is not so strange.",
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
*/


}
