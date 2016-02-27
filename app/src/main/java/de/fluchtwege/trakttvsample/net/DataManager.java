package de.fluchtwege.trakttvsample.net;

import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.trakttvsample.model.Film;
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
	public List<Film> loadPopularFilms(final int pagesLoaded) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return createListOf10Films(pagesLoaded);
	}

	private Film loadSearchResult(String query) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return create1Film(query);

	}

	private Film create1Film(String query) {
		return new Film(query + " - That is The Title", "1999", "This is a film that has a strange name. But it is not so strange.",
				"http://ste.india.com/sites/default/files/2014/12/17/303980-film-700.jpg");
	}

	@VisibleForTesting
	public List<Film> createListOf10Films(int pagesLoaded) {
		ArrayList<Film> films = new ArrayList<>();
		int counter = 0;

		for (int i = 0; i < 10; i += 2) {

			films.add(new Film("Der Film", "1932 : " + (pagesLoaded + "," + counter), "Dieser Film ist der absolut beste Film überhaupt. Man kann ihn mehrere male sehen ohne ihn zu verstehen. " +
					"Er ist so witzig wie nur möglich. Action gepaart mit Spannung und beste Unterhaltung. 23 Sterne.", "http://ste.india.com/sites/default/files/2014/12/17/303980-film-700.jpg"));

			films.add(new Film("Der Andere Film", "1965", "ddshf jksdhf ksjhdf ksjdfh ksjdhf ksjdhfkjsdhf ksjdhf skdjfhsjkdfh ksjdfh sdjkfh sdjkf " +
					"asdhaj khd kajshd kajshd kasjhd kasjhd kjashd kjads " +
					"h adjshdk aufnsjdfh sdjf xcdnscjdsksdf sdjhf sjdhf sdjf s",
					"http://ste.india.com/sites/default/files/2014/12/17/303980-film-700.jpg"));
			counter+=2;
		}
		return films;
	}


	public Observable<List<Film>> getPopularFilms(final int pagesLoaded) {
		return Observable.create(new Observable.OnSubscribe<List<Film>>() {

			@Override
			public void call(Subscriber<? super List<Film>> subscriber) {
				if (!subscriber.isUnsubscribed()) {
					final List<Film> listOf10Films = loadPopularFilms(pagesLoaded);
					subscriber.onNext(listOf10Films);
					subscriber.onCompleted();
					Timber.d("getPopularFilms page: " + pagesLoaded + " call()");
				}
			}
		});
	}

	public Observable<Film> getSearchResult(final String query) {
		return Observable.create(new Observable.OnSubscribe<Film>() {

			@Override
			public void call(Subscriber<? super Film> subscriber) {
				if (!subscriber.isUnsubscribed()) {
					final Film searchResult = loadSearchResult(query);
					subscriber.onNext(searchResult);
					subscriber.onCompleted();
					Timber.i("getSearchResult query: " +query + "call()");
				}
			}
		});
	}


}
