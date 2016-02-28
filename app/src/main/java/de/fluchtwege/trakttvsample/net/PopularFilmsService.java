package de.fluchtwege.trakttvsample.net;

import java.util.List;

import de.fluchtwege.trakttvsample.model.Movie;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface PopularFilmsService {

	//https://api-v2launch.trakt.tv/movies/popular?page={page}&limit={limit}
	@Headers({
			"Content-Type:" + DataManager.CONTENT_TYPE,
			"trakt-api-version: " + DataManager.TRAKT_API_VERSION,
			"trakt-api-key: " + DataManager.TRAKT_API_KEY
	})
	@GET("popular")
	Observable<List<Movie>> listPopularFilms(@Query("page") int page, @Query("limit") int limit, @Query("extended") String extension);

}
