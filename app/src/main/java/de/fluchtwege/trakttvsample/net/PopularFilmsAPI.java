package de.fluchtwege.trakttvsample.net;

import java.util.List;

import de.fluchtwege.trakttvsample.model.Movie;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class PopularFilmsAPI {

	public Observable<List<Movie>> getPopularFilms(int page) {
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(logging);
		Retrofit retrofit = new Retrofit.Builder()
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(httpClient.build())
				.baseUrl("https://api-v2launch.trakt.tv/movies/")
				.build();

		PopularFilmsService popularFilmsService = retrofit.create(PopularFilmsService.class);
		return popularFilmsService.listPopularFilms(page + DataManager.PAGE_OFFSET, DataManager.PAGE_SIZE, DataManager.EXTENSION);
	}
}
