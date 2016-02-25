package de.fluchtwege.trakttvsample;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.fluchtwege.trakttvsample.model.Film;
import de.fluchtwege.trakttvsample.net.DataManager;
import de.fluchtwege.trakttvsample.ui.adapter.FilmAdapter;
import de.fluchtwege.trakttvsample.viewmodel.FilmListViewModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FilmListViewModelTest {

	@Test
	public void When_viewpager_is_initialized_the_10_most_popular_films_are_loaded() {
		final LinearLayoutManager manager = new LinearLayoutManager(mock(Context.class));
		final FilmListViewModel viewModel = spy(new FilmListViewModel(manager));
		viewModel.init();
		verify(viewModel, times(1)).loadPopularFilms();
	}

	@Test
	public void When_10_most_popular_films_are_loaded_on_start_they_are_shown_in_list() {
		final List<Film> films = new ArrayList<>();
		final LinearLayoutManager manager = new LinearLayoutManager(mock(Context.class));
		final FilmListViewModel viewModel = spy(new FilmListViewModel(manager));
		viewModel.adapter = spy(new FilmAdapter(new ArrayList<Film>()));
		viewModel.onPopularFilmsloaded(films);

		verify(viewModel.adapter, times(1)).setFilms(films);
	}

	@Test
	public void When_user_reaches_the_end_of_list_while_scrolling_next_10_films_are_loaded()  {
		assertEquals(5, 2 + 2);
	}

	@Test
	public void When_next_10_films_are_laoded_the_entries_should_be_added_to_the_end_of_the_list()  {
		assertEquals(5, 2 + 2);
	}

	@Test
	public void When_user_presses_search_icon_in_toolbar_searchview_is_shown() {
		assertEquals(5, 2 + 2);
	}


	@Test
	public void When_user_enters_text_in_searchview_a_new_search_is_started() {
		assertEquals(5, 2 + 2);
	}

	//TODO: Wenn schon 2 Zeichen eingegeben sind wird Eingaben in das Suchfeld wird eine neue Suche gestartet (? nicht spezifiziert)

	@Test
	public void When_user_enters_text_in_searchview_and_an_old_search_is_running_old_search_is_stopped () {
		assertEquals(5, 2 + 2);
	}


}
