package de.fluchtwege.movielist.net;

import java.util.List;

import de.fluchtwege.movielist.model.Movie;
import de.fluchtwege.movielist.model.MovieQuery;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class RequestsAPI {

	public static final int PAGE_SIZE = 10;
	public static final String TYPE_MOVIE = "movie";
	public static final String EXTENSION = "full,images";
	public static final String URL_POPULAR_MOVIES = "https://api-v2launch.trakt.tv/movies/";
	public static final String URL_QUERY_MOVIES = "https://api-v2launch.trakt.tv/";

	//api starts counting pages at 1
	private static final int PAGE_OFFSET = 1;

	private boolean withLogging = false;

	public Observable<List<MovieQuery>> queryMovies(final String query, final int page) {
		Retrofit retrofit = new Retrofit.Builder()
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(buildHttpClient())
				.baseUrl(URL_QUERY_MOVIES)
				.build();

		Requests request = retrofit.create(Requests.class);
		return request.queryMovies(query, TYPE_MOVIE, page + PAGE_OFFSET, PAGE_SIZE);
	}

	private OkHttpClient buildHttpClient() {
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		if (withLogging) {
			HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
			logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
			httpClient.addInterceptor(logging);
		}
		return httpClient.build();
	}

	public Observable<List<Movie>> getPopularMovies(final int page) {
		Retrofit retrofit = new Retrofit.Builder()
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create())
				.client(buildHttpClient())
				.baseUrl(URL_POPULAR_MOVIES)
				.build();

		Requests popularFilmsService = retrofit.create(Requests.class);
		return popularFilmsService.listPopularMovies(page + PAGE_OFFSET, PAGE_SIZE, EXTENSION);
	}
}
