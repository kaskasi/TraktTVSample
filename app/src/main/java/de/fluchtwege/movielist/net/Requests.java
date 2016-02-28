package de.fluchtwege.movielist.net;

import java.util.List;

import de.fluchtwege.movielist.model.Movie;
import de.fluchtwege.movielist.model.MovieQuery;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface Requests {

	String VALUE_API_KEY = "";
	String VALUE_VERSION = "2";
	String VALUE_CONTENT_TYPE = "application/json";

	String PARAM_QUERY = "query";
	String PARAM_TYPE = "type";
	String PARAM_PAGE = "page";
	String PARAM_LIMIT = "limit";
	String PARAM_EXTENDED = "extended";

	String HEADER_PARAM_CONTENT_TYPE = "Content-Type";
	String HEADER_PARAM_API_VERSION = "trakt-api-version";
	String HEADER_PARAM_API_KEY = "trakt-api-key";


	@Headers({
			HEADER_PARAM_CONTENT_TYPE + ":" + VALUE_CONTENT_TYPE,
			HEADER_PARAM_API_VERSION + ": " + VALUE_VERSION,
			HEADER_PARAM_API_KEY + ": " + VALUE_API_KEY
	})
	@GET("search")
	Observable<List<MovieQuery>> queryMovies(@Query(PARAM_QUERY) String query, @Query(PARAM_TYPE) String type, @Query(PARAM_PAGE) int pagesLoaded, @Query(PARAM_LIMIT) int pageSize);

	@Headers({
			HEADER_PARAM_CONTENT_TYPE + ":" + VALUE_CONTENT_TYPE,
			HEADER_PARAM_API_VERSION + ": " + VALUE_VERSION,
			HEADER_PARAM_API_KEY + ": " + VALUE_API_KEY
	})
	@GET("popular")
	Observable<List<Movie>> listPopularMovies(@Query(PARAM_PAGE) int page, @Query(PARAM_LIMIT) int limit, @Query(PARAM_EXTENDED) String extension);
}
