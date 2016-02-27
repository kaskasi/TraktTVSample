package de.fluchtwege.trakttvsample.viewmodel;

import de.fluchtwege.trakttvsample.model.PopularFilm;

public class PopularFilmViewModel {

	public final PopularFilm film;

	public PopularFilmViewModel(PopularFilm film) {
		this.film = film;
	}

	public String getTitle() { return film.getTitle(); }

	public String getYear() {
		return film.getYear();
	}

	public String getKey() { return film.getKey(); }
}
