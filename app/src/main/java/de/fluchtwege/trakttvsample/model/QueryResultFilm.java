package de.fluchtwege.trakttvsample.model;

public class QueryResultFilm {

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
