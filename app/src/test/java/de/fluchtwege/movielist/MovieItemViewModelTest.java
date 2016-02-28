package de.fluchtwege.movielist;

import org.junit.Test;

import de.fluchtwege.movielist.model.Movie;
import de.fluchtwege.movielist.viewmodel.MovieItemViewModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class MovieItemViewModelTest {

	//TODO: add robolectric for testing views
	@Test
	public void Every_entry_shows_film_title() {
		MovieItemViewModel viewModel = new MovieItemViewModel(mock(Movie.class));
		assertEquals("url", viewModel.getImageUrl());
	}

	@Test
	public void Every_entry_shows_film_production_year() {
		assertEquals(5, 2 + 2);
	}

	@Test
	public void Every_entry_shows_overview() {
		assertEquals(5, 2 + 2);
	}

	@Test
	public void Overview_should_not_be_truncated() {
		assertEquals(5, 2 + 2);
	}

	@Test
	public void Every_entry_shows_film_image() {
		assertEquals(5, 2 + 2);
	}


}
