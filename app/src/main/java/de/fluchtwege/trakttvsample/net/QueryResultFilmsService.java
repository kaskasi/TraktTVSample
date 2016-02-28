package de.fluchtwege.trakttvsample.net;

import java.util.List;

import de.fluchtwege.trakttvsample.model.QueryResultFilm;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface QueryResultFilmsService {

	//https://api-v2launch.trakt.tv/search?query=batman&type=movie
	@Headers({
			"Content-Type:" + DataManager.CONTENT_TYPE,
			"trakt-api-version: " + DataManager.TRAKT_API_VERSION,
			"trakt-api-key: " + DataManager.TRAKT_API_KEY
	})
	@GET("search")
	Observable<List<QueryResultFilm>> listQueryResultFilms(@Query("query") String query, @Query("type") String type, @Query("page") int pagesLoaded, @Query("limit") int pageSize);
}
