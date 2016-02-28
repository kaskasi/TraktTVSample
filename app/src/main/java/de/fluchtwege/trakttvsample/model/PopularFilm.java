package de.fluchtwege.trakttvsample.model;

//TODO: generate pojo from http response
public class PopularFilm extends Film {
	public Movie movie;

	public String getImageUrl() {
		return movie.images.poster.thumb;
	}

	public String getTitle() {
		return movie.title;
	}

	public Integer getYear() {
		return movie.year;
	}

	public String getOverview() {
		return movie.overview;
	}
}
