package de.fluchtwege.trakttvsample;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FilmListViewModelTest {

	@Test
	public void When_app_is_started_the_10_most_popular_films_are_loaded() {
		assertEquals(5, 2 + 2);
	}

	@Test
	public void When_10_most_popular_films_are_loaded_on_start_they_are_shown_in_list() {
		assertEquals(5, 2 + 2);
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
