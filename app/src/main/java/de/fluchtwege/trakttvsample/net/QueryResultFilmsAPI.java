package de.fluchtwege.trakttvsample.net;

import java.util.List;

import de.fluchtwege.trakttvsample.model.QueryResultFilm;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class QueryResultFilmsAPI {

	public Observable<List<QueryResultFilm>> getQueryResultFilms(String query, int page) {
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(logging);
		Retrofit retrofit = new Retrofit.Builder()
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(httpClient.build())
				.baseUrl("https://api-v2launch.trakt.tv/")
				.build();

		QueryResultFilmsService queryResultFilmsService = retrofit.create(QueryResultFilmsService.class);
		return queryResultFilmsService.listQueryResultFilms(query, DataManager.TYPE_MOVIE, page  + DataManager.PAGE_OFFSET, DataManager.PAGE_SIZE);
	}
}
